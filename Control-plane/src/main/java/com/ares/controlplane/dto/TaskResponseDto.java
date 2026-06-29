package com.ares.controlplane.dto;

import com.ares.controlplane.enums.Status;

public record TaskResponseDto(
        String taskId,
        String runtime,
        String entryPoint,
        Status status,
        String stdout,
        String stderr,
        int exitCode,
        Long executionTimeNs
) {
}
