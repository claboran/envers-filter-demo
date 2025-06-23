package de.laboranowitsch.poc.enversfilterdemo

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<EnversFilterDemoApplication>().with(TestcontainersConfiguration::class).run(*args)
}
