package org.yalli.wah.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

public class CsrfCookieFilter extends OncePerRequestFilter {
    private final List<String> securedEndpoints = List.of(
//            "/v1/admins",
//            "/v1/admins/{adminId}",
//            "/v1/admins/events/**",
//            "/v1/admins/add-notification",
//            "/v1/admins/create-group",
//            "/v1/admins/delete-group",
//            "/v1/admins/update-group/",
//            "/v1/users/delete/{id}",
//            "/v1/events/saveEvent",
//            "/v1/events/unsaveEvent",
//            "/v1/files/upload",
//            "/v1/groups",
//            "/v1/groups/users/{userId}",
//            "/v1/groups",
//            "/v1/mentors",
//            "/v1/mentors/{id}"
            "/v1/users/login"
    );

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println( "pattern matching" + antPathMatcher.match("/v1/users/login", "/login"));
        System.out.println(request.getRequestURI());
        boolean isSecuredApi = securedEndpoints.stream().anyMatch(api -> antPathMatcher.match(api, request.getRequestURI())) && !request.getMethod().equals("GET");
        if (isSecuredApi && authentication!=null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken();
                response.setHeader("X-CSRF-TOKEN", csrfToken.getToken());
            }

        }
        filterChain.doFilter(request, response);


    }
}
