package com.samedtek.conferenceboard.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Conference Board",
                description = "N11 Interview",
                contact = @Contact(
                        name = "Samed TEK",
                        email = "samedtek92@gmail.com"
                )))
public class OpenAPIConfig {

}
