package a.gleb.reactivearticlesapp.configuration;

import a.gleb.reactivearticlesapp.configuration.properties.ArticleApplicationProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(ArticleApplicationProperties.class)
@Configuration
public class ArticleApplicationConfig {
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
