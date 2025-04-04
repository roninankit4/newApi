package com.Assignment.Config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1)
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        String userId = req.getParameter("userId");
        
        logger.info("Request: {} {} from user {}", 
                req.getMethod(), 
                req.getRequestURI(),
                userId != null ? userId : "unknown");
        
        chain.doFilter(request, response);
    }
}
