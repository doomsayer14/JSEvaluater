package com.example.jsevaluater.dto;

import com.example.jsevaluater.entity.enums.ExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JSScriptDto {
    private Long id;
    private String script;
    private ExecutionStatus executionStatus;
    private LocalDateTime scheduledExecutionTime;
    private LocalDateTime startTime;
    private long executionDuration;
    private String stdout;
    private String stderr;
}
