package a.gleb.oauthservice.configuration;

import a.gleb.oauthservice.configuration.properties.OauthServiceConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OauthServiceConfigurationProperties.class)
public class OauthServiceConfiguration {}
