package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    //捕获自定义异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException c){
        ResultCode code = c.getResultCode();
        LOGGER.error("catch exception : {}",c.getMessage());
        return new ResponseResult(code);
    }

    //捕获不可预知异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        //记录日志
        LOGGER.error("catch exception:{}",exception.getMessage());
        if (EXCEPTIONS==null){
            EXCEPTIONS = builder.build();
        }
        final ResultCode resultCode = EXCEPTIONS.get(exception.getClass());

        if (resultCode != null) {
            return new ResponseResult(resultCode);
        }
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }
    static{
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }
}
