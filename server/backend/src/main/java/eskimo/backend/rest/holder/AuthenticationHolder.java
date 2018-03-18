package eskimo.backend.rest.holder;

import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.rest.interceptor.AuthenticationInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Holds information about current user and current user session (depends on request).
 * Filled in {@link AuthenticationInterceptor}, can be used, for example, in controller
 * to implement user depending logic.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter @Setter
public class AuthenticationHolder {
    private User user;
    private UserSession userSession;
}
