package com.ares.controlplane

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ControlPlaneApplication

fun main(args: Array<String>) {
    runApplication<ControlPlaneApplication>(*args)
}
