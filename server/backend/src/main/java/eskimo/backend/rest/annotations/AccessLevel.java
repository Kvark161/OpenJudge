package eskimo.backend.rest.annotations;

import eskimo.backend.entity.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface AccessLevel {
    /**
     * Highest role that have access to controller method
     */
    Role role();
}
