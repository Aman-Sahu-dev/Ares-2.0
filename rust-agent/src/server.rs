use std::net::SocketAddr;
use std::sync::Arc;
use tonic::transport::Server;
use crate::execution::TaskExecutor;
use crate::services::compute::ComputeService;
use crate::proto::compute::compute_engine_server::ComputeEngineServer;

pub async fn run_server(
    addr: SocketAddr,
    executor:Arc<dyn TaskExecutor>,
    shutdown_rx:tokio::sync::oneshot::Receiver<()>,
)-> Result<(),tonic::transport::Error>{
    let svc = ComputeEngineServer::new(ComputeService::new(executor));
    Server::builder()
        .add_service(svc)
        .serve_with_shutdown(addr, async {shutdown_rx.await.ok(); })
        .await
}