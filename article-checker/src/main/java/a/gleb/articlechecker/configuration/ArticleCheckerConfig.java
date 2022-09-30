package a.gleb.articlechecker.configuration;

import a.gleb.articlechecker.configuration.properties.ArticleCheckerConfigurationProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ArticleCheckerConfigurationProperties.class)
public class ArticleCheckerConfig {

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
