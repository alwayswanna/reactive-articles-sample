package a.gleb.reactivearticlesapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author a.gleb
 */
@SpringBootApplication
@EnableScheduling
public class ReactiveArticlesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveArticlesAppApplication.class, args);
    }

}
