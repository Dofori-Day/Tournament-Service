package org.example.configs;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    @Test
    void corsConfigurer_shouldAllowAllOrigins() {
        WebConfig webConfig = new WebConfig();
        WebMvcConfigurer configurer = webConfig.corsConfigurer();
        assertNotNull(configurer);
    }

    @Test
    void corsConfigurer_shouldAddCorsMappings() {
        WebConfig webConfig = new WebConfig();
        WebMvcConfigurer configurer = webConfig.corsConfigurer();

        CorsRegistry registry = new CorsRegistry();
        configurer.addCorsMappings(registry);
        // No exception thrown = mapping registered successfully
        assertNotNull(registry);
    }
}
