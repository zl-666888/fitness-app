package com.fitness.interceptor;

import com.fitness.common.Result;
import com.fitness.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Integer> ADMIN_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> ADMIN_ROLE_HOLDER = new ThreadLocal<>();

    private final JwtUtil jwtUtil;

    public AdminAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeResponse(response, 401, "缺少管理端认证令牌");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            writeResponse(response, 401, "管理端令牌无效或已过期");
            return false;
        }

        Integer adminId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        if (!"admin".equals(role) && !"super".equals(role)) {
            writeResponse(response, 403, "无管理端访问权限");
            return false;
        }

        ADMIN_ID_HOLDER.set(adminId);
        ADMIN_ROLE_HOLDER.set(role);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ADMIN_ID_HOLDER.remove();
        ADMIN_ROLE_HOLDER.remove();
    }

    public static Integer getCurrentAdminId() {
        return ADMIN_ID_HOLDER.get();
    }

    public static String getCurrentAdminRole() {
        return ADMIN_ROLE_HOLDER.get();
    }

    private void writeResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        PrintWriter writer = response.getWriter();
        writer.write(new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(Result.error(status, message)));
        writer.flush();
    }
}
