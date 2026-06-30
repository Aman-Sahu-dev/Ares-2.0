package com.ares.controlplane.service;
import com.ares.controlplane.dto.TaskRequestDto;
import com.ares.controlplane.dto.TaskResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final ComputeEngineClient worker;

    public TaskService(ComputeEngineClient worker){
        this.worker = worker;
    }
}   public TaskResponseDto dispatch(TaskRequestDto dto){
    String taskId = UUID.randomUUID().toString();
    log.
}
