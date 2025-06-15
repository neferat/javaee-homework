package boot.spring.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import boot.spring.po.User;
import boot.spring.tools.ResponseResult;

/**
 * Admin权限校验切面
 * @author admin
 */
@Component
@Aspect
public class AdminPermissionAspect {
    
    /**
     * 检查admin权限的方法
     */
    @Around("@annotation(boot.spring.annotation.RequireAdminPermission)")
    public Object checkAdminPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取session中的用户信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUsername = (String) request.getSession().getAttribute("currentUser");
        User userInfo = (User) request.getSession().getAttribute("userInfo");
        
        // 检查是否登录
        if (currentUsername == null) {
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/")) {
                return ResponseResult.error("请先登录");
            } else {
                return "redirect:/login";
            }
        }
        
        // 检查是否为admin用户
        boolean isAdmin = false;
        
        // 方式1：通过用户名判断
        if ("admin".equals(currentUsername)) {
            isAdmin = true;
        }
        
        // 方式2：通过用户等级判断（如果有用户信息）
        if (userInfo != null && userInfo.getUserLevel() != null && userInfo.getUserLevel() >= 5) {
            isAdmin = true;
        }
        
        // 方式3：通过用户ID判断（admin通常是第一个用户）
        if (userInfo != null && userInfo.getUserId() != null && userInfo.getUserId().equals(1L)) {
            isAdmin = true;
        }
        
        if (!isAdmin) {
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/")) {
                return ResponseResult.error("权限不足，需要管理员权限");
            } else {
                return "error/403"; // 返回403错误页面
            }
        }
        
        return joinPoint.proceed();
    }
} 