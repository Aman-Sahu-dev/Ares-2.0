use tonic::{transport::server,Request,Response,Status};
use compute::execution_engine_server::{ExecutionEngine,ExecutionEngineServer};
use compute::{TaskRequest,TaskResponse,NodeStatus,Empty};

pub mod compute{
    tonic::include_proto!("compute");
}
pub struct AresWorker{}

impl ExecutionEngine for AresWorker {
    
}