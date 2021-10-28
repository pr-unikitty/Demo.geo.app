package demo.geo.app;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomContainer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    
    @Override
    public void customize(ConfigurableServletWebServerFactory factory){
        factory.setPort(8086);
    }
}