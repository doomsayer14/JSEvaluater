package com.example.jsevaluater.dto;

import com.example.jsevaluater.entity.enums.ExecutionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JSScriptFilter {
    private Boolean id;
    private Boolean scheduledTime;
    private ExecutionStatus executionStatus;
}
