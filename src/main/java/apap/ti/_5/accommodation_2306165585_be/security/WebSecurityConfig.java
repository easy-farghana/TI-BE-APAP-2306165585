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

import apap.ti._5.accommodation_2306165585_be.restcontroller.*;
import apap.ti._5.accommodation_2306165585_be.security.api.ApiKeyFilter;
import apap.ti._5.accommodation_2306165585_be.security.jwt.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;

 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private ApiKeyFilter apiKeyFilter;

    // ===================== JWT API SECURITY =====================
    @Bean
    @Order(1)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> requests
                // Property endpoints
                .requestMatchers(HttpMethod.GET, "/api" + PropertyController.BASE_URL)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                .requestMatchers(HttpMethod.GET, "/api" + PropertyController.VIEW_PROPERTY)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                .requestMatchers(HttpMethod.POST, "/api" + PropertyController.CREATE_PROPERTY)
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)
                .requestMatchers(HttpMethod.PUT, "/api" + PropertyController.UPDATE_PROPERTY)
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)
                .requestMatchers(HttpMethod.DELETE, "/api" + PropertyController.DELETE_PROPERTY)
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)
                .requestMatchers(HttpMethod.POST, "/api" + PropertyController.ADD_MAINTENANCE)
                    .hasAnyAuthority(RoleGroup.PROPERTY_OWNER_ONLY)

                // Room Type endpoints
                .requestMatchers(HttpMethod.GET, "/api" + RoomGlobalController.BASE_URL_TYPE)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                .requestMatchers(HttpMethod.POST, "/api" + RoomGlobalController.CREATE_TYPE)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_OWNER)
                .requestMatchers(HttpMethod.GET, "/api" + RoomGlobalController.VIEW_TYPES_BY_PROPERTY)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                .requestMatchers(HttpMethod.GET, "/api" + RoomGlobalController.VIEW_TYPE)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)

                // Room endpoints
                .requestMatchers(HttpMethod.GET, "/api" + RoomGlobalController.BASE_URL_ROOM)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)                

                // Booking endpoints
                .requestMatchers(HttpMethod.GET, "/api" + AccommodationBookingController.BASE_URL)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                .requestMatchers(HttpMethod.GET, "/api" + AccommodationBookingController.VIEW_BOOKING)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
               .requestMatchers(HttpMethod.POST, "/api" + AccommodationBookingController.CREATE_BOOKING)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.GET, "/api" + AccommodationBookingController.UPDATE_BOOKING)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.GET, "/api" + AccommodationBookingController.UPDATE_BOOKING_STATUS)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)

                // Review endpoints
                .requestMatchers(HttpMethod.GET, "/api" + ReviewController.BASE_URL)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                .requestMatchers(HttpMethod.POST, "/api" + ReviewController.CREATE_REVIEW)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.GET, "/api" + ReviewController.VIEW_REVIEW_BY_CUSTOMER)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.GET, "/api" + ReviewController.VIEW_REVIEW_BY_PROPERTY)
                    .hasAnyAuthority(RoleGroup.ACCOMMODATION_ROLES)
                    
                // Bill endpoints
                .requestMatchers(HttpMethod.GET, "/api" + BillController.BASE_URL)
                    .hasAnyAuthority(RoleGroup.SUPERADMIN)
                .requestMatchers(HttpMethod.GET, "/api" + BillController.VIEW_BILL_DETAILS)
                    .hasAnyAuthority(RoleGroup.ALL_ROLES)
                .requestMatchers(HttpMethod.POST, "/api" + BillController.CREATE_BILL)
                    .permitAll()
                .requestMatchers(HttpMethod.GET, "/api" + BillController.VIEW_CUSTOMER_BILL)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.GET, "/api" + BillController.VIEW_SERVICE_BILL)
                    .hasAnyAuthority(RoleGroup.SERVICE_ROLES)
                .requestMatchers(HttpMethod.POST, "/api" + BillController.PAY_BILL)
                    .hasAnyAuthority(RoleGroup.CUSTOMER)
                .requestMatchers(HttpMethod.PUT, "/api" + BillController.UPDATE_BILL)
                    .permitAll()
                
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
