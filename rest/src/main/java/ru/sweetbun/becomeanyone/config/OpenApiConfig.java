package ru.sweetbun.becomeanyone.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("BecomeAnyone REST API").version("1.0"));

        Paths paths = openAPI.getPaths();
        if (paths != null) {
            paths.forEach(this::addPathParameters);
        }
        return openAPI;
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenApiCustomizer customisePathParameters() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths != null) {
                paths.forEach((path, pathItem) -> addPathParameters(path, pathItem));
            }
        };
    }

    private void addPathParameters(String path, PathItem pathItem) {
        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(path);

        List<Parameter> parameters = new ArrayList<>();
        while (matcher.find()) {
            String paramName = matcher.group(1);

            Parameter parameter = new Parameter()
                    .name(paramName)
                    .in("path")
                    .required(true)
                    .schema(new Schema<>().type("string"))
                    .description("Path parameter: " + paramName);
            parameters.add(parameter);
        }

        for (Operation operation : pathItem.readOperations()) {
            List<Parameter> operationParameters = operation.getParameters();
            if (operationParameters == null) {
                operation.setParameters(new ArrayList<>(parameters));
            } else {
                for (Parameter parameter : parameters) {
                    if (operationParameters.stream().noneMatch(p -> p.getName().equals(parameter.getName()))) {
                        operationParameters.add(parameter);
                    }
                }
            }
        }
    }

    /*@Bean
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
    }*/
}
