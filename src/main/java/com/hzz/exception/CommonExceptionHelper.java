package com.hzz.exception;

/**
 * Created by hongshuiqiao on 2017/10/11.
 */
public class CommonExceptionHelper {
    public static CommonException commonException(String errorMessage, Throwable cause){
        return CommonException.customException("COMMON", 10000, errorMessage, cause);
    }

    public static CommonRuntimeException commonRuntimeException(String errorMessage, Throwable cause) {
        return CommonRuntimeException.customException("COMMON", 20000, errorMessage, cause);
    }

    public static CommonException daoException(String errorMessage, Throwable cause) {
        return CommonException.customException("DAO", 11000, errorMessage, cause);
    }

    public static CommonRuntimeException daoRuntimeException(String errorMessage, Throwable cause) {
        return CommonRuntimeException.customException("DAO", 21000, errorMessage, cause);
    }

    public static CommonException modelException(String errorMessage, Throwable cause){
        return CommonException.customException("MODEL", 12000, errorMessage, cause);
    }

    public static CommonRuntimeException modelRuntimeException(String errorMessage, Throwable cause){
        return CommonRuntimeException.customException("MODEL", 22000, errorMessage, cause);
    }

    public static CommonException resourceException(String errorMessage, Throwable cause){
        return CommonException.customException("RESOURCE", 13000, errorMessage, cause);
    }

    public static CommonRuntimeException resourceRuntimeException(String errorMessage, Throwable cause){
        return CommonRuntimeException.customException("RESOURCE", 23000, errorMessage, cause);
    }

    public static CommonException cacheException(String errorMessage, Throwable cause){
        return CommonException.customException("CACHE", 17000, errorMessage, cause);
    }

    public static CommonRuntimeException cacheRuntimeException(String errorMessage, Throwable cause){
        return CommonRuntimeException.customException("CACHE", 27000, errorMessage, cause);
    }
}
