package a.gleb.oauth2manager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Oauth2ManagerApplication

fun main(args: Array<String>) {
    runApplication<Oauth2ManagerApplication>(*args)
}
