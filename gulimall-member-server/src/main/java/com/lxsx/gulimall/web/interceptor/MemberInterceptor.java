package com.lxsx.gulimall.web.interceptor;

import com.lxsx.gulimall.member.constant.AuthServerContants;
import com.lxsx.gulimall.to.MemberEntityTo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

/**
 * 访问会员服务的所有方法必须是登录状态的
 * 所以要拦截非登录的
 * 但是要考虑远程调用的
 */
@Component
public class MemberInterceptor implements HandlerInterceptor {
    /**
     * 当前线程共享常量
     */
    public static final ThreadLocal<MemberEntityTo> thredLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 放行一些RPC调用
         */
        //路径匹配
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        //放行查询订单的RPC调用
        boolean match = antPathMatcher.match("/member/**", request.getRequestURI());
        if (match) {
            return true;
        }
        HttpSession session = request.getSession();
        MemberEntityTo memberEntityTo = (MemberEntityTo) session.getAttribute(AuthServerContants.LOGIN_USER);
        if (memberEntityTo!=null) {
            thredLocal.set(memberEntityTo);
            return true;
        }else{
            //没登陆就去登录页面登录
            //未登录，返回登录页面
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
            return false;
        }
    }
}
