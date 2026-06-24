use std::net::SocketAddr;

use tonic::Code::Ok;

#[derive(Debug)]
pub struct Config {
    pub bind_addr:SocketAddr,
    pub max_concurrent_tasks:usize,
}
impl Config {
    pub fn from_env()->Result<Self,Box<dyn std::error::Error>>{
        let addr = std::env::var("ARES_BIND_ADDR")
            .unwrap_or_else(|_| "127.0.0.1:0051".to_string())
            .parse()?;
        let max = std::env::var("ARES_MAX_CONCURRENT")
            .unwrap_or_else(|_| "10".to_string())
            .parse()?;
        Ok(Self {bind_addr: addr,max_concurrent_tasks:max})
    }
    
}