use std::process::Output;

use tonic::{Request, Response, Status};

use compute::execution_engine_server::ExecutionEngine;
use compute::{TaskRequest, TaskResponse, NodeStatus, Empty};

pub mod compute {
    tonic::include_proto!("compute");
}

#[derive(Default)]
pub struct AresWorker {}

#[tonic::async_trait]
impl ExecutionEngine for AresWorker {
    async fn execute_task(
        &self,
        request: Request<TaskRequest>,
    ) -> Result<Response<TaskResponse>, Status> {

        let req = request.into_inner();

        let output = if req.byte_code.is_empty() {
            std::process::Command::new("sh")
                .arg("-c")
                .arg(&req.entry_point)
                .output()
        } else {
            std::process::Command::new("sh")
                .arg("-c")
                .arg("echo 'byte code not yet implemented'")
                .output()
        };

        let result: Output = output.map_err(|e| {
            Status::internal(format!("Execution failed: {}", e))
        })?;

        Ok(Response::new(TaskResponse {
            task_id: req.task_id,
            stdout_output: result.stdout,
            stderr_output: result.stderr,
            execution_time_ms: 0,
            exit_code: if result.status.success() {
                "0".to_string()
            } else {
                "1".to_string()
            },
        }))
    }
}