package com.anshul.hotel.filters;

import com.anshul.hotel.utilities.JWTUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtility jwtUtility;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
   final  String authHeader = request.getHeader("Authorization");

   if (authHeader!=null && authHeader.startsWith("Bearer ")){
       String token = authHeader.substring(7);

       try{
           String userName = jwtUtility.extractUsername(token);
           String role = jwtUtility.extractRole(token);
           if (userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
               SecurityContextHolder.getContext().setAuthentication(authToken);


           }
       }catch (Exception e){

           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.getWriter().write("Invalid or expired JWT token");
           return;
       }
   }
   filterChain.doFilter(request,response);
    }
}
