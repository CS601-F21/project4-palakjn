package web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Responsible for setting configuration for spring boot application to run
 *
 * @author Palak Jain
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Expose directory "event-photos" so that all the stored photos inside will be visible when loaded.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("event-photos", registry);
    }

    /**
     * Add resource handler for directory "event-photos"
     * @param dirName
     * @param registry
     */
    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");

        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/"+ uploadPath + "/");
    }
}
