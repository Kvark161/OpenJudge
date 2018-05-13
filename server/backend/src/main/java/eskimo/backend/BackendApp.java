package eskimo.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.config.AppSettingsProvider;
import eskimo.backend.rest.interceptor.AuthenticationInterceptor;
import eskimo.backend.rest.interceptor.NotFoundInterceptor;
import eskimo.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.Collections;

@SpringBootApplication
@EnableConfigurationProperties
public class BackendApp extends WebMvcConfigurerAdapter {

    public static void main(String... args) {
        SpringApplication.run(BackendApp.class, args);
    }

    @Autowired
    private UserService userService;

    @Bean
    public AuthenticationInterceptor authenticationInterceptor(UserService userService) {
        return new AuthenticationInterceptor(userService);
    }

    @Bean
    public NotFoundInterceptor notFoundInterceptor() {
        return new NotFoundInterceptor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor(userService));
        registry.addInterceptor(notFoundInterceptor());
    }

    @Bean
    ErrorViewResolver supportPathBasedLocationStrategyWithoutHashes() {
        return (request, status, model) -> status == HttpStatus.NOT_FOUND
                ? new ModelAndView("index.html", Collections.emptyMap(), HttpStatus.OK)
                : null;
    }

    @Bean
    public DataSource dataSource(AppSettingsProvider appSettingsProvider) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:" + appSettingsProvider.getDatabasePath() +
                ";MULTI_THREADED=TRUE;mode=MySQL;DATABASE_TO_UPPER=FALSE;");
        dataSource.setUsername("eskimo");
        dataSource.setPassword("eskimo");

        return dataSource;
    }
}
