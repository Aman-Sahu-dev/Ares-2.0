package com.ares.controlplane.dto;

public record TaskResponseDto(
        String stdout,
        String stderr,
        int exitCode,
        Long executionTimeNs
) {
}
