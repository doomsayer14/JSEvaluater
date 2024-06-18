package com.example.jsevaluater.controller;

import com.example.jsevaluater.dto.JSScriptDto;
import com.example.jsevaluater.entity.JSScript;
import com.example.jsevaluater.entity.enums.ExecutionStatus;
import com.example.jsevaluater.mapper.JSScriptMapper;
import com.example.jsevaluater.payload.response.ExecutionResultResponse;
import com.example.jsevaluater.service.JSScriptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/jsscripts")
public class JSScriptController {

    private final JSScriptService jsScriptService;
    private final JSScriptMapper jsScriptMapper;

    public JSScriptController(JSScriptService jsScriptService, JSScriptMapper jsScriptMapper) {
        this.jsScriptService = jsScriptService;
        this.jsScriptMapper = jsScriptMapper;
    }

    @PostMapping("/execute")
    public ResponseEntity<ExecutionResultResponse> execute(@RequestParam String script) {
        return jsScriptService.execute(script);
    }

    @PostMapping("/scheduleExecution")
    public ResponseEntity<ExecutionResultResponse> scheduleExecution(@RequestParam String script,
                                                                     @RequestParam LocalDateTime executionStartTime) {
        return jsScriptService.scheduleExecution(script, executionStartTime);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JSScriptDto> get(@PathVariable String id) {
        JSScript jsScript = jsScriptService.getJsScriptById(Long.parseLong(id));
        JSScriptDto jsScriptDto = jsScriptMapper.jsScriptToDto(jsScript);
        return ResponseEntity.ok(jsScriptDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<JSScriptDto>> getAll(@RequestParam ExecutionStatus executionStatus,
                                                    @RequestParam String orderBy) {
        List<JSScriptDto> jsScriptDtoList = jsScriptService.getAllJSScripts(executionStatus, orderBy);
        return ResponseEntity.ok(jsScriptDtoList);
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<ExecutionResultResponse> stopScript(@PathVariable String id) {
        return jsScriptService.stopScript(Long.parseLong(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JSScriptDto> removeScript(@PathVariable String id,
                                                    @RequestParam ExecutionStatus executionStatus) {
        return ResponseEntity.noContent(jsScriptService.delete(id, executionStatus));
    }
}
