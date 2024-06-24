package com.example.jsevaluater.service;

import com.example.jsevaluater.dto.JSScriptFilter;
import com.example.jsevaluater.entity.JSScript;
import com.example.jsevaluater.entity.enums.ExecutionStatus;
import com.example.jsevaluater.exception.ActiveJSScriptDeletingException;
import com.example.jsevaluater.exception.InternalServerException;
import com.example.jsevaluater.exception.JSException;
import com.example.jsevaluater.exception.JSScriptNotFoundException;
import com.example.jsevaluater.payload.response.Response;
import com.example.jsevaluater.repository.JSScriptRepository;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.example.jsevaluater.repository.JSScriptRepository.JSScriptSpecs.*;

@Service
public class JSScriptService {

    private static final String PERMITTED_LANGUAGES = "js";

    private final JSScriptRepository jsScriptRepository;
    private final Engine engine;
    private final Context ctx;

    public JSScriptService(JSScriptRepository jsScriptRepository) {
        this.jsScriptRepository = jsScriptRepository;

        engine = Engine.newBuilder()
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        ctx = Context.newBuilder(PERMITTED_LANGUAGES).engine(engine).build();
    }

    public JSScript execute(String script) {
        JSScript jsScript = JSScript.builder()
                .executionStatus(ExecutionStatus.EXECUTING)
                .script(script)
                .build();
        jsScriptRepository.save(jsScript);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            //rewrite system out stream to byteArrayOutputStream to save the #eval output
            System.setOut(new PrintStream(byteArrayOutputStream));

            long before = System.currentTimeMillis();

            //actual evaluation of JS script
            ctx.eval("js", script);

            long after = System.currentTimeMillis();
            long executionTime = after - before;

            String stdout = byteArrayOutputStream.toString();

            //get system out stream back
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

            jsScript.setExecutionDuration(executionTime);
            jsScript.setExecutionStatus(ExecutionStatus.COMPLETED);
            jsScript.setStdout(stdout);
            jsScriptRepository.save(jsScript);
            return jsScript;

        } catch (PolyglotException | IllegalArgumentException | IllegalStateException e) {
            jsScript.setExecutionStatus(ExecutionStatus.FAILED);
            jsScript.setStderr(e.getMessage());
            jsScriptRepository.save(jsScript);

            throw new JSException("Script execution failed");

        } catch (IOException e) {
            jsScript.setExecutionStatus(ExecutionStatus.FAILED);
            jsScriptRepository.save(jsScript);
            throw new InternalServerException("IOException has been thrown.");
        }
    }


    public JSScript scheduleExecution(String script, LocalDateTime executionStartTime) {
        JSScript jsScript = JSScript.builder()
                .executionStatus(ExecutionStatus.QUEUED)
                .scheduledExecutionTime(executionStartTime)
                .script(script)
                .build();
        jsScriptRepository.save(jsScript);
        return jsScript;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    private void checkQueuedScripts() {
        List<JSScript> jsScriptList = jsScriptRepository
                .findAllByExecutionStatusAndScheduledExecutionTimeLessThan(ExecutionStatus.QUEUED, LocalDateTime.now());
        jsScriptList.forEach(jsScript -> execute(jsScript.getScript()));
    }

    public JSScript getJsScriptById(Long id) {
        return jsScriptRepository.findById(id)
                .orElseThrow(() -> new JSScriptNotFoundException("Script cannot be found for id: " + id));
    }

    public List<JSScript> getAllJSScripts(JSScriptFilter jsScriptFilter) {
        return jsScriptRepository.findAll(
                byExecutionStatus(jsScriptFilter.getExecutionStatus())
                        .and(orderByScheduledExecutionTime(jsScriptFilter.getScheduledTime())
                        .and(orderById(jsScriptFilter.getId()))
        );
    }

    public Response stopScript(Long id) {
        JSScript jsScript = getJsScriptById(id);
        try {
            ctx.interrupt(Duration.ZERO);
        } catch (TimeoutException e) {
            throw new InternalServerException("TimeoutException has been thrown.");
        }
        jsScript.setExecutionStatus(ExecutionStatus.FAILED);
        jsScriptRepository.save(jsScript);
        return Response.builder()
                .message("The script with id = " + jsScript.getId() + "has been stopped.")
                .build();

    }

    public void delete(String id) {
        JSScript jsScript = getJsScriptById(Long.parseLong(id));
        if (!jsScript.getExecutionStatus().equals(ExecutionStatus.FAILED) &&
                !jsScript.getExecutionStatus().equals(ExecutionStatus.COMPLETED)) {
            throw new ActiveJSScriptDeletingException("You are trying to delete script that is active right now.");
        }
        jsScriptRepository.delete(jsScript);
    }
}
