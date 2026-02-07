package me.ultard.travo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TravoApplication

fun main(args: Array<String>) {
    runApplication<TravoApplication>(*args)
}
