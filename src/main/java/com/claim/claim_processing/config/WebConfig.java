package com.claim.claim_processing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.resources.images.path}")
    private String imagePath;

    @Value("${app.resources.videos.path}")
    private String videoPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath + "/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + videoPath + "/")
                .setCachePeriod(3600);
    }
}
