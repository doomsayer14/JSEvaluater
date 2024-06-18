package com.example.jsevaluater.repository;

import com.example.jsevaluater.entity.JSScript;
import com.example.jsevaluater.entity.enums.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JSScriptRepository extends JpaRepository<JSScript, Long> {

    List<JSScript> findAllByExecutionStatus(ExecutionStatus executionStatus);

    List<JSScript> findAllByExecutionStatusAndScheduledExecutionTimeLessThan(ExecutionStatus executionStatus, LocalDateTime localDateTime);

    List<JSScript> findAllByOrderByIdDesc();

    List<JSScript> findAllByOrderByScheduledExecutionTimeDesc();


}
