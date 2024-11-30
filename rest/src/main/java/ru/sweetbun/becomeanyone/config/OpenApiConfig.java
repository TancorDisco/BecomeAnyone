package ru.sweetbun.becomeanyone.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OperationCustomizer customizeGlobalResponses() {
        return (operation, handlerMethod) -> {
            operation.getResponses().addApiResponse("400", createApiResponse("Некорректный запрос"));
            operation.getResponses().addApiResponse("404", createApiResponse("Ресурс не найден"));
            operation.getResponses().addApiResponse("500", createApiResponse("Внутренняя ошибка сервера"));
            return operation;
        };
    }

    private ApiResponse createApiResponse(String description) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription(description);
        apiResponse.setContent(new Content().addMediaType("application/json",
                new MediaType().schema(new Schema<>().type("string"))));
        return apiResponse;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("BecomeAnyone REST API")
                        .version("1.0"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
