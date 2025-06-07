package boot.spring.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import boot.spring.tools.ResponseResult;

/**
 * 登录校验的切面，若未登录则重定向到登录页面
 * @author shenzhanwang
 *
 */
@Component
@Aspect
public class LoginAspect {
	
	@Pointcut("execution(* boot.spring.controller.*.*(..)) && "
			+ "!execution(* boot.spring.controller.Login.*(..))") // 排除登录相关控制器
    public void login() {
    }
	
	
	@Around("login()")
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取session中的用户信息
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUser = (String) request.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            // 判断是否为API请求
            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/api/")) {
                // API请求返回JSON错误
                return ResponseResult.error("请先登录");
            } else {
                // 页面请求重定向到登录页面
                return "redirect:/login";
            }
        }
        return joinPoint.proceed();
    }
}
