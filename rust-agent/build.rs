fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::configure()
        .compile_protos(
            &["src/proto/compute.proto"],
            &["src/proto"],
        )?;
    Ok(())
}