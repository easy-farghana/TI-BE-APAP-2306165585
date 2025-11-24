package apap.ti._5.accommodation_2306165585_be.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import apap.ti._5.accommodation_2306165585_be.security.jwt.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    // ===================== JWT API SECURITY =====================
    @Bean
    @Order(1)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(requests -> requests
                // Property endpoints
                .requestMatchers(HttpMethod.GET, "/api/property")
                    .hasAnyAuthority(RoleGroup.PROPERTY_ROLES)
                .requestMatchers(HttpMethod.GET, "/api/property/{propertyId}")
                    .hasAnyAuthority(RoleGroup.PROPERTY_ROLES)
                .requestMatchers(HttpMethod.POST, "/api/property/create")
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)
                .requestMatchers(HttpMethod.PUT, "/api/property/update")
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)
                .requestMatchers(HttpMethod.DELETE, "/api/property/delete/{propertyId}")
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)

                // Room Type endpoints
                .requestMatchers(HttpMethod.GET, "/api/room-type/property/{propertyId}")
                    .hasAnyAuthority(RoleGroup.PROPERTY_ROLES)
                .requestMatchers(HttpMethod.GET, "/api/room-type/{roomTypesId}")
                    .hasAnyAuthority(RoleGroup.PROPERTY_ROLES)

                // Room endpoints


                // Booking endpoints


                // Bill endpoints
                .requestMatchers(HttpMethod.GET, "/api/bill")
                    .hasAnyAuthority(RoleGroup.SUPERADMIN)
                .requestMatchers(HttpMethod.GET, "/api/bill/details/{billId}")
                    .hasAnyAuthority(RoleGroup.ALL_ROLES)
                .requestMatchers(HttpMethod.POST, "/api/bill/create")
                    .hasAnyAuthority(RoleGroup.OWNER_ROLES)
                .requestMatchers(HttpMethod.GET, "/api/bill/customer")
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.GET, "/api/bill/{serviceName}")
                    .hasAnyAuthority(RoleGroup.SERVICE_ROLES)
                .requestMatchers(HttpMethod.POST, "/api/bill/{billId}/pay")
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    String jsonResponse = String.format(
                        "{\"status\": 403, \"message\": \"You are not authorized to access this resource.\", \"timestamp\": \"%s\", \"data\": null}",
                        java.time.LocalDateTime.now().toString()
                    );

                    response.getWriter().write(jsonResponse);
                })
            );

        return http.build();
    }
}
