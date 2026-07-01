use std::sync::Arc;
use tokio::sync::{oneshot,Semaphore};
use tracing_subscriber::fmt::format::FmtSpan;

mod config;
mod execution;
mod proto;
mod server;
mod services;

use config::Config;
use execution::ShellExecutor;
use server::run_server;

#[tokio::main]
async fn main()-> Result<(),Box<dyn std::error::Error>>{
    let config = Config::from_env()?;

    tracing_subscriber::fmt()
    .with_env_filter(tracing_subscriber::EnvFilter::from_default_env())
    .with_span_events(FmtSpan::CLOSE)
    .json()
    .init();

    std::panic::set_hook(Box::new(|info|{
        tracing::error!(panic = %info,"service panicked");
    }));
    let concurrency = Arc::new(Semaphore::new(config.max_concurrent_tasks));
    let executor: Arc<dyn execution::TaskExecutor> = Arc::new(ShellExecutor::new(concurrency));
    let (shutdown_tx,shutdown_rx) = oneshot::channel();
    let server_handle = tokio::spawn(run_server(config.bind_addr,executor,shutdown_rx));
    tokio::spawn(async move {
        tokio::signal::ctrl_c().await.expect("failed to install ctrl+c handler");
        tracing::info!("shutdown signal received");
        let _ = shutdown_tx.send(());
    });
    server_handle.await??;
    tracing::info!("server shutdown complete");
    Ok(())
}