package com.example.jsevaluater.repository;

import com.example.jsevaluater.entity.JSScript;
import com.example.jsevaluater.entity.JSScript_;
import com.example.jsevaluater.entity.enums.ExecutionStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JSScriptRepository extends JpaRepository<JSScript, Long>,
        JpaSpecificationExecutor<JSScript> {

    List<JSScript> findAllByExecutionStatusAndScheduledExecutionTimeLessThan(ExecutionStatus executionStatus, LocalDateTime localDateTime);

    interface JSScriptSpecs {
        static Specification<JSScript> orderById(Specification<JSScript> spec) {
            return (root, query, criteriaBuilder) -> {
                query.orderBy(criteriaBuilder.asc(root.get(JSScript_.id)));
                return spec.toPredicate(root, query, criteriaBuilder);
            };
        }

        static Specification<JSScript> orderByScheduledExecutionTime(Specification<JSScript> spec) {
            return (root, query, criteriaBuilder) -> {
                query.orderBy(criteriaBuilder.asc(root.get(JSScript_.scheduledExecutionTime)));
                return spec.toPredicate(root, query, criteriaBuilder);
            };
        }

        static Specification<JSScript> byExecutionStatus(ExecutionStatus executionStatus) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(JSScript_.executionStatus), executionStatus);
        }
    }
}
