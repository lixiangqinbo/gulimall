package com.lxsx.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.lxsx.gulimall.auth.constant.RequestContants;
import com.lxsx.gulimall.auth.constant.SMSConstant;
import com.lxsx.gulimall.auth.service.AliyunSasFeiginService;
import com.lxsx.gulimall.auth.service.RegisterFeignService;


import com.lxsx.gulimall.auth.vo.LoginUserVo;
import com.lxsx.gulimall.auth.vo.UserRegisterVo;
import com.lxsx.gulimall.exception.BizCodeEnume;
import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.utils.R;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lxsx.gulimall.auth.constant.AuthServerContants.LOGIN_USER;

@Controller
public class LoginController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private AliyunSasFeiginService aliyunSasFeiginService;
    @Resource
    private RegisterFeignService registerFeignService;

    @PostMapping("/login")
    public String longin(LoginUserVo loginUserVo, RedirectAttributes redirectAttributes, HttpSession session){
        R res = registerFeignService.login(loginUserVo);
        if (res.getCode()== RequestContants.REQUEST_SUCESS) {
            MemberEntityTo data = res.getData(new TypeReference<MemberEntityTo>() {});
            session.setAttribute(LOGIN_USER, data);
            return "redirect:http://gulimall.com/index.html";
        }
        Map<String,String > error = new HashMap<>();
        error.put("msg", res.getData(new TypeReference<String >(){}));
        redirectAttributes.addFlashAttribute("errors", error);
        return "redirect:http://auth.gulimall.com/login.html";
    }


    @PostMapping(value = "/register")
    public String reg(@Valid UserRegisterVo userRegisterVo, BindingResult result, RedirectAttributes redirectAttributes){
        /**
         * 数据验证
         */
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String, String> error = fieldErrors.stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",error);

            return "redirect:http://auth.gulimall.com/reg.html";
        }
        /**
         * 验证验证码是否过期
         */
        String record = stringRedisTemplate.opsForValue().get(SMSConstant.SMS_PRE + userRegisterVo.getPhone());
        if (StringUtils.isEmpty(record)) {
            Map<String, String> error = new HashMap<>();
            error.put("errors","验证码已过期！");
            redirectAttributes.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        /**
         * 验证验证码是否不一致
         */
        String[] split = record.split(":");
        if (!split[0].equals(userRegisterVo.getCode())) {
            Map<String, String> error = new HashMap<>();
            error.put("errors","验证码不匹配！");
            redirectAttributes.addFlashAttribute("errors",error);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        /**
         * 保存数据，删除redis
         */
        stringRedisTemplate.delete(SMSConstant.SMS_PRE + userRegisterVo.getPhone());
        R register = registerFeignService.register(userRegisterVo);
        if (register.getCode() != RequestContants.REQUEST_SUCESS) {
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        /**
         * 将数据携带到login 页面
         */
        redirectAttributes.addFlashAttribute("data", userRegisterVo);
        /**
         * qq/weibo/ auth2.0 登录
         * 自己注册
         */
        return "redirect:http://auth.gulimall.com/login.html";
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        String record = stringRedisTemplate.opsForValue().get(SMSConstant.SMS_PRE + phone);
        if (!StringUtils.isEmpty(record)) {
            String[] split = record.split(":");
            Long start = Long.valueOf(split[split.length - 1]);
            Long end = System.currentTimeMillis();
            if (end-start<60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),
                        BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }

        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        aliyunSasFeiginService.sendCode(phone,code);
        /**
         * 设置一个过期位
         */
        stringRedisTemplate.opsForValue().set(SMSConstant.SMS_PRE+phone, code+":"+System.currentTimeMillis(),15, TimeUnit.MINUTES);

        return R.ok();
    }
}
