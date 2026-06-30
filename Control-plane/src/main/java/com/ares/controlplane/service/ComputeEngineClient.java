package com.ares.controlplane.service;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ComputeEngineClient {
    private static final Logger log = LoggerFactory.getLogger(ComputeEngineClient.class);
    private final ManagedChannel channel;
    private final ComputeEngineGrpc.ComputeEngineBlockingStub stub;
    private final long deadlineMs;

    public ComputeEngineClient(
            @Value("${ares.worker.host:localhost}") String host,
            @Value("${ares.worker.port:50051}") int port,
            @Value("${ares.worker.deadline-ms:30000}") long deadlineMs
    ){
        this.deadlineMs = deadlineMs;
        this.channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();
        this.stub = ComputeEngineGrpc.newBlockingStub(channel);
        log.info("gRPC channel established to {} , {} ",host,port);
    }
}
