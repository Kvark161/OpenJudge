package eskimo.backend;

import eskimo.backend.authorization.AuthenticationInterceptor;
import eskimo.backend.config.EskimoContextInitializer;
import eskimo.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;

@SpringBootApplication
@EnableConfigurationProperties
public class BackendApp extends WebMvcConfigurerAdapter {

    public static void main(String... args) {
        new SpringApplicationBuilder(BackendApp.class)
                .initializers(new EskimoContextInitializer(args))
                .run(args);
    }

    @Autowired
    private UserService userService;

    @Bean
    public AuthenticationInterceptor interceptor(UserService userService) {
        return new AuthenticationInterceptor(userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor(userService));
    }

    @Bean
    ErrorViewResolver supportPathBasedLocationStrategyWithoutHashes() {
        return (request, status, model) -> status == HttpStatus.NOT_FOUND
                ? new ModelAndView("index.html", Collections.emptyMap(), HttpStatus.OK)
                : null;
    }
}
