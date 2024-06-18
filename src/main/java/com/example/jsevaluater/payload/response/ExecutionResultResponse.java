package com.example.jsevaluater.payload.response;

import com.example.jsevaluater.entity.enums.ExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExecutionResultResponse {
    private ExecutionStatus executionStatus;
    private LocalDateTime scheduledExecutionTime;
    private long executionDuration;
    private String stdout;
    private String stderr;
    private String message;
}
