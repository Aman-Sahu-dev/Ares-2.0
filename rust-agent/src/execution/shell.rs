use async_trait::async_trait;
use std::sync::Arc;
use tokio::process::Command;
use tokio::sync::Semaphore;
use crate::proto::compute::{TaskResult, TaskSpec};
use super::{TaskExecutor, ExecutionError};

pub struct ShellExecutor{
    sem:Arc<Semaphore>
}
impl ShellExecutor{
    pub fn new(sem: Arc<Semaphore>)-> Self{
        Self { sem }
    }
}
#[async_trait]
impl TaskExecutor for ShellExecutor {
    async fn execute(&self,spec:TaskSpec)->Result<TaskResult,ExecutionError>{
        let _permit = self.sem.try_acquire().map_err(|_| ExecutionError::Throttled)?;
        let start = std::time::Instant::now();
        
        let output = Command::new("sh")
            .arg("-c")
            .arg(&spec.entry_point)
            .output()
            .await?;

        Ok(TaskResult {
            stdout: String::from_utf8_lossy(&output.stdout).into_owned(),
            stderr: String::from_utf8_lossy(&output.stderr).into_owned(),
            exit_code: output.status.code().unwrap_or(-1),
            execution_time_ns:start.elapsed().as_nanos() as u64,
        })
    }
    
}