package com.example.jsevaluater.entity;

import com.example.jsevaluater.entity.enums.ExecutionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class JSScript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String script;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus executionStatus;

    @Column
    private LocalDateTime scheduledExecutionTime;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private Long executionDuration;

    @Column
    private String stdout;

    @Column
    private String stderr;

}
