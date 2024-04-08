package uk.gov.cca.api.web.config.security.camunda_admin;

import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Collections;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

//https://github.com/camunda-community-hub/camunda-platform-7-keycloak/tree/master/examples/sso-kubernetes
//https://github.com/camunda-community-hub/camunda-platform-7-keycloak
@Configuration
public class CamundaAdminSecurityConfig {
    private final KeycloakLogoutHandler keycloakLogoutHandler;

    public CamundaAdminSecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
        this.keycloakLogoutHandler = keycloakLogoutHandler;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain camundaAdminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatchers(authorize -> authorize.requestMatchers(
                        antMatcher("/admin/**"),
                        antMatcher("/**/oauth2/**")))
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(
                                antMatcher("/admin/camunda-api/**"))
                        .permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(antMatcher("/**"))
                        .authenticated())
                .oauth2Login(Customizer.withDefaults())
                .logout()
                .logoutRequestMatcher(antMatcher("/**/logout"))
                .logoutSuccessHandler(keycloakLogoutHandler);
        return http.build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean containerBasedAuthenticationFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new ContainerBasedAuthenticationFilter());
        filterRegistrationBean.setInitParameters(Collections.singletonMap("authentication-provider", "uk.gov.cca.api.web.config.security.camunda_admin.KeycloakAuthenticationProvider"));
        filterRegistrationBean.setOrder(101); // make sure the filter is registered after the Spring Security Filter Chain
        filterRegistrationBean.addUrlPatterns("/admin/*");
        return filterRegistrationBean;
    }

    // The ForwardedHeaderFilter is required to correctly assemble the redirect URL for OAUth2 login.
    // Without the filter, Spring generates an HTTP URL even though the container route is accessed through HTTPS.
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.addUrlPatterns("/admin/*", "/oauth2/*");
        return filterRegistrationBean;
    }
}
