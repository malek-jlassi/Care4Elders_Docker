package tn.health.userservice.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/identites/**")
                .addResourceLocations("file:uploads/identites/");


        // Exposer le dossier uploads/diplomes/
        registry.addResourceHandler("/uploads/diplomes/**")
                .addResourceLocations("file:uploads/diplomes/");

        // Exposer le dossier uploads/diplomes/
        registry.addResourceHandler("/uploads/dossiers/**")
                .addResourceLocations("file:uploads/dossiers/");


    }
}