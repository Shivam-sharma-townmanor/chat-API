package in.banking.aap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI chatSystemOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Chat System API")
                        .description("Real-time chat system for User and Client communication")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Shivam Sharma")
                                .email("shivam@example.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
    	return new OpenAPI()
    			.servers(List.of(
        				new Server().url("https://chat-api-production-c901.up.railway.app")));
    }
}