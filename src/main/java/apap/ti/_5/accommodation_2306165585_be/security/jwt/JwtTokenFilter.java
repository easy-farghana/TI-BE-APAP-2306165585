package apap.ti._5.accommodation_2306165585_be.security.jwt;
 
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import apap.ti._5.accommodation_2306165585_be.restdto.external.response.VerifyTokenResponseDTO;
import apap.ti._5.accommodation_2306165585_be.security.AuthClient;
import apap.ti._5.accommodation_2306165585_be.security.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@Component
public class JwtTokenFilter extends OncePerRequestFilter{
    @Autowired
    private AuthClient authClient;

    @Autowired
    private UserContext userContext;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Missing Authorization header\"}");
            return;
        }

        String token = header.substring(7);
        VerifyTokenResponseDTO verified = authClient.verifyToken(token);

        if (verified == null || !verified.isValid()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"Invalid or expired token\"}");
            return;
        }

        // Set ke userContext
        userContext.setUserID(verified.getUserId());
        userContext.setRole(verified.getRole());
        userContext.setEmail(verified.getEmail());
        userContext.setName(verified.getName());
        
        var authorities = List.of(
            new SimpleGrantedAuthority(verified.getRole())
        );

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                    verified.getEmail(),
                    null,
                    authorities
            );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

}
