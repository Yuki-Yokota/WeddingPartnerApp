package com.example.weddingpartnerapp.common;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import com.example.weddingpartnerapp.model.SessionUser;

public class SessionUserInterceptor implements HandlerInterceptor{
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws IOException {
		HttpSession session = request.getSession();
		SessionUser user = (SessionUser)session.getAttribute("user");
		if(user==null) {
			response.sendRedirect("login");
			return false;
		}
        return true;
    }
}
