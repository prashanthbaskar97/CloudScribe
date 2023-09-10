package com.prashanth.cloud.app.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hari.cloud.app.dao.User;
import com.hari.cloud.app.service.UserService;
import com.hari.cloud.app.util.Utility;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Response;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.PrintWriter;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.util.NoSuchElementException;

@Order(value = 2)
public class AuthenticationFilter extends OncePerRequestFilter {
    UserService userService;

    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("authorization");
        if(authHeader==null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid auth token");
            return;
        }
        String decodedAuthHeader="";
        try {
            authHeader = authHeader.substring("Basic".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(authHeader);
            decodedAuthHeader = new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }


        String[] email_pass = decodedAuthHeader.split(":");
        if(email_pass.length < 2 || email_pass[0].isBlank() || email_pass[1].isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        String email = email_pass[0];
        String password = email_pass[1];

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user;
            try {
                user = userService.getUserBy(email);
            } catch (PSQLException e) {
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.getWriter().write(convertObjectToJson(response));
                return;
            } catch (CannotCreateTransactionException e) {
                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                response.getWriter().write(convertObjectToJson(response));
                return;
            }

            // Perform user password validation
            Boolean isAuthenticated = isUserAuthenticated(password, user);

            if(isAuthenticated) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    private Boolean isUserAuthenticated(String password, User user) {
        System.out.println(Utility.passwordEncoder.matches(password, user.getPassword())+" Encoded pwd");
        return Utility.passwordEncoder.matches(password, user.getPassword());
    }
}