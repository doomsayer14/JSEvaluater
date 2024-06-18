package com.example.jsevaluater.mapper;

import com.example.jsevaluater.dto.JSScriptDto;
import com.example.jsevaluater.entity.JSScript;
import org.springframework.stereotype.Component;

@Component
public class JSScriptMapper {
    public JSScriptDto jsScriptToDto(JSScript jsScript) {
        return JSScriptDto.builder()
                .id(jsScript.getId())
                .script(jsScript.getScript())
                .executionDuration(jsScript.getExecutionDuration())
                .executionStatus(jsScript.getExecutionStatus())
                .scheduledExecutionTime(jsScript.getScheduledExecutionTime())
                .startTime(jsScript.getStartTime())
                .stderr(jsScript.getStderr())
                .stdout(jsScript.getStdout())
                .build();
    }
}
