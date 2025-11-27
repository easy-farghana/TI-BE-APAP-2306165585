package apap.ti._5.accommodation_2306165585_be.security.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import apap.ti._5.accommodation_2306165585_be.restcontroller.AccommodationBookingController;
import apap.ti._5.accommodation_2306165585_be.restcontroller.BillController;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${accommodation-be.app.apiKey}")
    private String apiKey;

    private final List<String> protectedPaths = List.of(
        BillController.CREATE_BILL,
        AccommodationBookingController.PAY_BOOKING
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean needApiKey = protectedPaths.stream()
                .map(p -> "/api" + p)
                .anyMatch(path::startsWith);

        if (needApiKey) {
            String requestApiKey = request.getHeader("API-KEY");
            log.info("API-KEY from request: {}", requestApiKey);
            if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid API Key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}