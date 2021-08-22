package com.zyj.gulimall.product.exception;


import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zyj
 * @create 2021-08-23 0:32
 *
 *         统一异常处理
 */
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "com.zyj.gulimall.product.controller")
@RestControllerAdvice(basePackages = "com.zyj.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = Throwable.class)
    public R handleException(){
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }

    /**
     * 数据校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException (MethodArgumentNotValidException e) {
        //log.info("数据校验出现问题{},异常类型{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        // 1、获取校验错误结果
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((item) -> {
            // 获取错误提示
            //String message = item.getDefaultMessage();
            // 获取错误属性名
            //String field = item.getField();
            errorMap.put(item.getField(), item.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(), BizCodeEnum.VAILD_EXCEPTION.getMsg()).put("data", errorMap);
    }
}
