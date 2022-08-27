package a.gleb.oauthsecurityserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OauthSecurityServerApplication

fun main(args: Array<String>) {
    runApplication<OauthSecurityServerApplication>(*args)
}
