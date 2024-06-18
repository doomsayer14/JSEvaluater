package com.example.jsevaluater.service;

import com.example.jsevaluater.dto.JSScriptDto;
import com.example.jsevaluater.entity.JSScript;
import com.example.jsevaluater.entity.comparator.JSScriptComparator;
import com.example.jsevaluater.entity.enums.ExecutionStatus;
import com.example.jsevaluater.exception.JSException;
import com.example.jsevaluater.exception.JSScriptNotFoundException;
import com.example.jsevaluater.mapper.JSScriptMapper;
import com.example.jsevaluater.payload.response.ExecutionResultResponse;
import com.example.jsevaluater.repository.JSScriptRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class JSScriptService {

    private static final String PERMITTED_LANGUAGES = "js";

    private final JSScriptRepository jsScriptRepository;
    private final Engine engine;
    private final Context ctx;
    private final JSScriptComparator jsScriptComparator;
    private final JSScriptMapper jsScriptMapper;

    public JSScriptService(JSScriptRepository jsScriptRepository, JSScriptComparator jsScriptComparator, JSScriptMapper jsScriptMapper) {
        this.jsScriptComparator = jsScriptComparator;
        this.jsScriptRepository = jsScriptRepository;
        this.jsScriptMapper = jsScriptMapper;

        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        ctx = Context.newBuilder(PERMITTED_LANGUAGES).engine(engine).build();
    }

    public ResponseEntity<ExecutionResultResponse> execute(String script) {
        JSScript jsScript = JSScript.builder()
                .executionStatus(ExecutionStatus.EXECUTING)
                .script(script)
                .build();
        jsScriptRepository.save(jsScript);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            System.setOut(new PrintStream(baos));

            long before = System.currentTimeMillis();
            ctx.eval("js", script);
            long after = System.currentTimeMillis();
            long executionTime = after - before;

            String stdout = baos.toString();
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

            jsScript.setExecutionDuration(executionTime);
            jsScript.setExecutionStatus(ExecutionStatus.COMPLETED);
            jsScriptRepository.save(jsScript);

            return ResponseEntity.ok(ExecutionResultResponse.builder()
                    .executionDuration(executionTime)
                    .executionStatus(ExecutionStatus.COMPLETED)
                    .stdout(stdout)
                    .build());
        } catch (PolyglotException | IllegalArgumentException | IllegalStateException e) {
            jsScript.setExecutionStatus(ExecutionStatus.FAILED);
            jsScript.setStderr(e.getMessage());
            jsScriptRepository.save(jsScript);

            throw new JSException("Script execution failed");

        } catch (IOException e) {
            jsScript.setExecutionStatus(ExecutionStatus.FAILED);
            jsScriptRepository.save(jsScript);
            return new ResponseEntity<>(ExecutionResultResponse.builder()
                    .executionStatus(ExecutionStatus.FAILED)
                    .message("IOException has been thrown.")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<ExecutionResultResponse> scheduleExecution(String script, LocalDateTime executionStartTime) {
        JSScript jsScript = JSScript.builder()
                .executionStatus(ExecutionStatus.QUEUED)
                .scheduledExecutionTime(executionStartTime)
                .script(script)
                .build();
        jsScriptRepository.save(jsScript);
        //scheduled call execute();
        return null;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void check() {
        List<JSScript> jsScriptList = jsScriptRepository
                .findAllByExecutionStatusAndScheduledExecutionTimeLessThan(ExecutionStatus.QUEUED, LocalDateTime.now());
        jsScriptList.forEach(jsScript -> execute(jsScript.getScript()));
    }

    public JSScript getJsScriptById(Long id) {
        return jsScriptRepository.findById(id)
                .orElseThrow(() -> new JSScriptNotFoundException("Script cannot be found for id: " + id));
    }

    public List<JSScriptDto> getAllJSScripts(ExecutionStatus executionStatus, String orderBy) {
        List<JSScript> jsScripts = new ArrayList<>();
        if (executionStatus != null) {
            jsScripts.addAll(jsScriptRepository.findAllByExecutionStatus(executionStatus));
        }
        if (NumberUtils.isCreatable(orderBy)) {
            return jsScriptRepository.findAllByOrderByIdDesc().stream()
                    .map(jsScriptMapper::jsScriptToDto)
                    .collect(Collectors.toList());;
        }
            jsScriptRepository.findAll
        }

        return jsScripts.stream()
                .map(jsScriptMapper::jsScriptToDto)
                .collect(Collectors.toList());
    }

    public ResponseEntity<ExecutionResultResponse> stopScript(Long id) {
        JSScript jsScript = getJsScriptById(id);
        try {
            ctx.interrupt(Duration.ZERO);
        } catch (TimeoutException e) {
            return new ResponseEntity<>(ExecutionResultResponse.builder()
                    .executionStatus(ExecutionStatus.FAILED)
                    .message("TimeoutException has been thrown.")
                    .build(), HttpStatus.BAD_REQUEST);
        }
        jsScript.setExecutionStatus(ExecutionStatus.FAILED);
        jsScriptRepository.save(jsScript);
        return ResponseEntity.ok(ExecutionResultResponse.builder()
                .executionStatus(jsScript.getExecutionStatus())
                .message("The script with id = " + jsScript.getId() + "has been stopped.")
                .build());

    }
}
