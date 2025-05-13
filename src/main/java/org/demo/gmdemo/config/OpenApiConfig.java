package org.demo.gmdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gmOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GM Digital Product Subscription API")
                        .version("1.0.0")
                        .description("API for managing SaaS-style digital product subscriptions and vehicle assignments.")
                );
    }
}
