package com.lxsx.gulimall.interceptor;

import com.lxsx.gulimall.constants.AuthServerContants;
import com.lxsx.gulimall.constants.CarConstants;
import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.vo.UserInfoVo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


/**
 * 购物车过滤器
 * 用来下发user-key cookie的
 */
public class CartInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<UserInfoVo> threadLocal = new ThreadLocal<>();

    //目标方法执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //检查是否登录
        UserInfoVo userInfoVo = new UserInfoVo();
        HttpSession session = request.getSession();
        MemberEntityTo memberEntityTo = (MemberEntityTo) session.getAttribute(AuthServerContants.LOGIN_USER);
        if (memberEntityTo!=null) {
            userInfoVo.setUserId(memberEntityTo.getId());
        }
        //检查是否已经携带了user_key cookie
        Cookie[] cookies = request.getCookies();
        if (cookies!=null&&cookies.length!=0) {
            for (Cookie cookie : cookies) {
                if (CarConstants.COOKIE_USER_KEY.equals(cookie.getName())) {
                    userInfoVo.setIfCookie(false);
                    userInfoVo.setUserKey(cookie.getValue());
                }
            }

        }
        //登没登录陆都设置票据 ,并且没有携带uer_key就设设置临时票据
        if (StringUtils.isEmpty(userInfoVo.getUserKey())) {
            String user_key = UUID.randomUUID().toString().replace("-", "");
            userInfoVo.setUserKey(user_key);
        }
        threadLocal.set(userInfoVo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoVo userInfoVo = threadLocal.get();
        if (userInfoVo.getIfCookie()) {
            //设置临时票据
            Cookie userCookie = new Cookie(CarConstants.COOKIE_USER_KEY, userInfoVo.getUserKey());
            //设置临时票据的domin 和 失效时间
            userCookie.setDomain(CarConstants.COOKIE_DOMAIN);
            userCookie.setMaxAge(CarConstants.COOKIE_DIE_TIME);
            response.addCookie(userCookie);
        }

    }
}
