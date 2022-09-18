package com.lxsx.gulimall.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;
/**
 * ConstraintValidator <ListValid,Integer>
 *
 *    Integer 决定检验的类型 ，可以是String 就校验String
 *    所以一个注解可以校验多个类型，一个类型写一个校验器
 */
public class ListValidConstraintValidator implements ConstraintValidator <ListValid,Integer>{

    private Set<Integer> set = new HashSet<>();
    //初始化方法 会将把我们注解的值获取给我们
    @Override
    public void initialize(ListValid valid) {
        int [] nums = valid.value();
        if (nums.length==0 || nums==null){
            return;
        }
        for (int num : nums) {
            set.add(num);
        }
    }

    /**
     *
     * @param value 提交的值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(value);
    }
}
