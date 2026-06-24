use core::error;

use async_trait::async_trait;
use crate::proto::compute::{TaskSpec,TaskResult};

#[async_trait]
pub trait TaskExecutor: Send + Sync {
    async fn execute(&self,spec:TaskSpec)->Result<TaskResult,ExecutionError>;
    
}
#[derive(Debug,,thiserror::Error)]
pub enum ExecutionError {
    #[error("concurrency limit exceeded")]
    Throttled,
    #[error("execution failed : {0}")]
    ExecutionFailed(String),
    #[error("io error: {0}")]
    Io(#[from] std::io::Error),    
}