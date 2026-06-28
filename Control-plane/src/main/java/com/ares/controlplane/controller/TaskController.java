package com.ares.controlplane.controller;

import com.ares.controlplane.dto.TaskRequestDto;
import com.ares.controlplane.dto.TaskResponseDto;
import com.ares.controlplane.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    public static final Logger log = LoggerFactory.getLogger(TaskController.class);
    public final TaskService taskService;

    public TaskController (TaskService taskService){
        this.taskService = taskService;
    }
    @PostMapping
    public ResponseEntity<TaskResponseDto> submitTask(@Valid @RequestBody TaskRequestDto dto){
    log.debug("task submission : runtime = {}, entrypoint = {}",dto.runtime(),dto.entryPoint());
    TaskResponseDto response = taskService.dispatch(dto);
    log.debug("task completed: exitCode = {}, executionTimeNs = {}",response.exitCode(),response.executionTimeNs());
    return ResponseEntity.ok(response);
    }
}
