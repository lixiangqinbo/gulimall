package com.lxsx.gulimall.ware.interceptor;

import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.ware.constants.AuthServerContants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
@Component
public class WareInterceptor implements HandlerInterceptor {

        public static final ThreadLocal<MemberEntityTo> threadLocal = new ThreadLocal<>();
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            /**
             * 判断是都登录
             */
            // TODO拦截器 执行了两次：报错
            /**
             * 第一次拦截是：/getSkuWare 获取了session
             * 第二次：/fare 获取为null 报错
             * 原因：异步调用导致的session不能共享
             */
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


