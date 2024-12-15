package ru.sweetbun.becomeanyone.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTests {

    @InjectMocks
    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
    }

    @Test
    void openAPI_WithValidConfiguration_ShouldReturnConfiguredOpenAPI() {
        // Act
        OpenAPI openAPI = openApiConfig.openAPI();

        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getInfo());

        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("JWT", securityScheme.getBearerFormat());
        assertEquals("bearer", securityScheme.getScheme());

        List<SecurityRequirement> securityRequirements = openAPI.getSecurity();
        assertNotNull(securityRequirements);
        assertFalse(securityRequirements.isEmpty());
        assertTrue(securityRequirements.get(0).get("Bearer Authentication") != null);
    }

    @Test
    void customisePathParameters_WithPaths_ShouldAddPathParameters() {
        // Arrange
        OpenAPI openAPI = new OpenAPI();
        Paths paths = new Paths();

        PathItem pathItem = new PathItem();
        paths.addPathItem("/tests/{id}", pathItem);
        openAPI.setPaths(paths);

        OpenApiCustomizer customizer = openApiConfig.customisePathParameters();

        // Act
        customizer.customise(openAPI);

        // Assert
        assertNotNull(openAPI.getPaths());
        PathItem updatedPathItem = openAPI.getPaths().get("/tests/{id}");
        assertNotNull(updatedPathItem);

        updatedPathItem.readOperations().forEach(operation -> {
            List<Parameter> parameters = operation.getParameters();
            assertNotNull(parameters);
            assertTrue(parameters.stream().anyMatch(param -> "id".equals(param.getName())));
        });
    }
}
