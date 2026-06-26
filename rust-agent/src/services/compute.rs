use tonic::Code::Ok;
use tonic::{Request,Response,Status};
use std::result;
use std::sync::Arc;
use crate::execution::{TaskExecutor,ExecutorError};
use crate::proto::compute::{TaskSpec,TaskResult};
use crate::proto::compute::compute_engine_server::ComputeEngine;

pub struct ComputeService{
    executor:Arc<dyn TaskExecutor>,
}
impl ComputeService {
    pub fn new(executor: Arc<dyn TaskExecutor>) -> Self{
        Self{executor}
    }
}
#[tonic::async_trait]
impl ComputeEngine for ComputeService{
    async fn execute_task(&self,request:Request<TaskSpec>)-> Result<Response<TaskResult>,Status>{
        let result = Self.executo.execute(request.into_inner()).await.map_err(|e| match e {
            ExecutionEror::Throttled =>
            Status::resource_exhausted("conecurrency limit reached"),
            ExecutionError::ExectionFailed(msg) => Status::internal(msg),
            ExecutionError::Io(io_err) => Status::internal(format!("io {}", io_err)),
        })?;
        Ok(Response::new(result))
    }
    
}