package com.lxsx.gulimall.order.web.interceptor;

import com.lxsx.gulimall.order.constants.AuthServerContants;
import com.lxsx.gulimall.to.MemberEntityTo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

@Component
public class OrderInterceptor implements HandlerInterceptor {
    public static final ThreadLocal<MemberEntityTo> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/order/**", request.getRequestURI());
        boolean alipayed = antPathMatcher.match("/order/pay/alipay/success", request.getRequestURI());
        if (match||alipayed) {
            return true;
        }
        /**
         * 判断是都登录
         */
        // TODO拦截器 执行了两次：报错
        //Caused by: java.lang.IllegalStateException: Cannot create a session after the response has been committed
        //原因：页面的提交了一些新的错误信息，导致丢失seesion
        HttpSession session = request.getSession();
        MemberEntityTo memberEntityTo = (MemberEntityTo) session.getAttribute(AuthServerContants.LOGIN_USER);
        if (memberEntityTo==null) {
            //未登录，返回登录页面
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
            return false;
        }else{
            threadLocal.set(memberEntityTo);
            return true;
        }

    }
}
