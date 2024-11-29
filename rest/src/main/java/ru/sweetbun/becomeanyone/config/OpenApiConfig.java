package ru.sweetbun.becomeanyone.config;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
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
}
