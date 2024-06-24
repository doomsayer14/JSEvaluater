package com.example.jsevaluater.controller;

import com.example.jsevaluater.dto.JSScriptDto;
import com.example.jsevaluater.dto.JSScriptFilter;
import com.example.jsevaluater.entity.JSScript;
import com.example.jsevaluater.entity.enums.ExecutionStatus;
import com.example.jsevaluater.mapper.JSScriptMapper;
import com.example.jsevaluater.payload.response.Response;
import com.example.jsevaluater.service.JSScriptService;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.OK)
    public JSScriptDto execute(@RequestParam String script) {
        JSScript jsScript = jsScriptService.execute(script);
        return jsScriptMapper.jsScriptToDto(jsScript);
    }

    @PostMapping("/scheduleExecution")
    @ResponseStatus(HttpStatus.CREATED)
    public JSScriptDto scheduleExecution(@RequestParam String script,
                                         @RequestParam LocalDateTime executionStartTime) {
        JSScript jsScript = jsScriptService.scheduleExecution(script, executionStartTime);
        return jsScriptMapper.jsScriptToDto(jsScript);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public JSScriptDto getJSScript(@PathVariable String id) {
        JSScript jsScript = jsScriptService.getJsScriptById(Long.parseLong(id));
        return jsScriptMapper.jsScriptToDto(jsScript);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<JSScriptDto> getAllJSScripts(@RequestBody JSScriptFilter jsScriptFilter) {
        return jsScriptService.getAllJSScripts(jsScriptFilter)
                .stream()
                .map(jsScriptMapper::jsScriptToDto)
                .toList();
    }

    @PostMapping("/stop/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response stopScript(@PathVariable String id) {
        return jsScriptService.stopScript(Long.parseLong(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeScript(@PathVariable String id) {
        jsScriptService.delete(id);
    }
}
