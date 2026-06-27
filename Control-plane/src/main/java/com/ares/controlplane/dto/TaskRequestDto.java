package com.ares.controlplane.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
public record TaskRequestDto(
    @NotBlank String runtime,
    @NotBlank String entryPoint,
    byte[] byteCode,
    @Min(1) Long memoryLimitBytes,
    @Min(1) Integer cpuShares
){
    public TaskRequestDto{
    if (memoryLimitBytes == null) memoryLimitBytes = 134_217_728l;
    if(cpuShares == null) cpuShares = 1024;
    if(byteCode == null ) byteCode = new byte[0];

    }

}
