package eskimo.backend;

import eskimo.backend.authorization.AuthenticationInterceptor;
import eskimo.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableConfigurationProperties
@EnableWebMvc
public class BackendApp extends WebMvcConfigurerAdapter {

    public static void main(String... args) {
        SpringApplication.run(BackendApp.class, args);
    }

    @Autowired UserService userService;

    @Bean
    public AuthenticationInterceptor interceptor(UserService userService) {
        return new AuthenticationInterceptor(userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor(userService));
    }
}
