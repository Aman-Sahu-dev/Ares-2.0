package com.ares.controlplane.service;

import ares.compute.ComputeEngineGrpc;
import ares.compute.TaskResult;
import ares.compute.TaskSpec;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
public class ComputeEngineClient {
    private static final Logger log = LoggerFactory.getLogger(ComputeEngineClient.class);
    private final ManagedChannel channel;
    private final ComputeEngineGrpc.ComputeEngineBlockingStub stub;
    private final long deadlineMs;

    public ComputeEngineClient(
            @Value("${ares.worker.host:localhost}") String host,
            @Value("${ares.worker.port:50051}") int port,
            @Value("${ares.worker.deadline-ms:30000}") long deadlineMs) {
        this.deadlineMs = deadlineMs;
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = ComputeEngineGrpc.newBlockingStub(channel);
        log.info("gRPC channel established to {}:{}", host, port);
    }

    public TaskResult execute(TaskSpec spec) {
        log.info("Dispatching task to Rust agent: runtime={}, entryPoint={}", spec.getRuntime(), spec.getEntryPoint());
        try {
            TaskResult result = stub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
                    .executeTask(spec);
            log.info("Task executed successfully: exitCode={}, executionTimeNs={}", result.getExitCode(), result.getExecutionTimeNs());
            return result;
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: status={}, description={}", e.getStatus(), e.getStatus().getDescription());
            throw new RuntimeException("Worker execution failed: " + e.getStatus().getDescription(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down gRPC channel");
        channel.shutdown();
    }
}