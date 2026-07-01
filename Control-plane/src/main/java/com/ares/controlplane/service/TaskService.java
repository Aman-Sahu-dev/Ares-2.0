package com.ares.controlplane.service;

import ares.compute.TaskResult;
import ares.compute.TaskSpec;
import com.ares.controlplane.dto.TaskRequestDto;
import com.ares.controlplane.dto.TaskResponseDto;
import com.ares.controlplane.enums.Status;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final ComputeEngineClient worker;

    public TaskService(ComputeEngineClient worker) {
        this.worker = worker;
    }

    public TaskResponseDto dispatch(TaskRequestDto dto) {
        String taskId = UUID.randomUUID().toString();
        log.info("Task submitted: id={}, runtime={}, entryPoint={}", taskId, dto.runtime(), dto.entryPoint());

        TaskSpec spec = TaskSpec.newBuilder()
                .setRuntime(dto.runtime())
                .setEntryPoint(dto.entryPoint())
                .setByteCode(ByteString.copyFrom(dto.byteCode()))
                .setMemoryLimitBytes(dto.memoryLimitBytes())
                .setCpuShares(dto.cpuShares())
                .build();

        String stdout = "";
        String stderr = "";
        int exitCode = -1;
        Status status = Status.FAILED;
        long executionTimeNs = 0L;

        try {
            TaskResult result = worker.execute(spec);
            stdout = result.getStdout();
            stderr = result.getStderr();
            exitCode = result.getExitCode();
            status = Status.COMPLETED;
            executionTimeNs = result.getExecutionTimeNs();
            log.info("Task completed: id={}, exitCode={}, executionTimeNs={}",
                    taskId, exitCode, executionTimeNs);
        } catch (StatusRuntimeException e) {
            log.error("Task failed: id={}, error={}", taskId, e.getStatus().getDescription());
            stderr = e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "worker unreachable";
        }

        return new TaskResponseDto(
                taskId,
                dto.runtime(),
                dto.entryPoint(),
                status,
                stdout,
                stderr,
                exitCode,
                executionTimeNs
        );
    }
}