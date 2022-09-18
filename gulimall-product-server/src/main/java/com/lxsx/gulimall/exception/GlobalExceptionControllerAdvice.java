package com.lxsx.gulimall.exception;

import com.lxsx.gulimall.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
/**
 * 全局的异常类
 */

/**
 * @Controller
 * @ResponseBody
 * @ControllerAdvice 等于@RestControllerAdvice
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionControllerAdvice {

    //规约异常类型处理范畴：：MethodArgumentNotValidException
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R produtExceptionHandler(MethodArgumentNotValidException e){
        log.info("数据校验出问题！"+e.getMessage()+e.getClass());
        Map<String,Object> errors = new HashMap<>();
        //获取校验对象集
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(item -> {
                String field = item.getField();
                String defaultMessage = item.getDefaultMessage();
                errors.put(field, defaultMessage);
            });
           return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),
                   BizCodeEnume.VALID_EXCEPTION.getMsg())
                   .put("error", errors);
        }
        return R.error(BizCodeEnume.UNKONW_EXCETION.getCode(),
                BizCodeEnume.UNKONW_EXCETION.getMsg());
    }

    //全局的校验处理方法
    @ExceptionHandler(value = Exception.class)
    public R globalExceptionHandler(Exception e){
        log.info(e.getMessage()+"::"+e.getClass());
        return R.error(BizCodeEnume.UNKONW_EXCETION.getCode(),
                BizCodeEnume.UNKONW_EXCETION.getMsg());
    }
}
