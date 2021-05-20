package com.github.ong.util;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 16:57
 * @Description 类操作工具包
 */
public class ClassUtil {

    public static final String CLASS_NAME_VOID = "void";

    /**
     * 如果类 有泛型，获取真实类属性
     * @param type
     * @return
     */
    public static Type getRawType(Type type){
        if (Objects.isNull(type)){
            return null;
        }

        if (type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getRawType();
        }
        return type;
    }

    /**
     * 获取泛型
     * @param type
     * @return
     */
    public static Type[] getActualTypes(Type type){
        if (type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }
        return new Type[]{Object.class};
    }

    public static Type[] getMethodParamTypes(Method method, Class paramClass){

        Type[] types = method.getGenericParameterTypes();
        if (ArrayUtil.isEmpty(types)){
            return null;
        }

        if (!checkMethodHasParamType(method, paramClass)){
            return null;
        }

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if (parameterizedType.getRawType().getTypeName().equals(paramClass.getName())) {
                    return parameterizedType.getActualTypeArguments();
                }
            }
        }

        return new Type[]{Object.class};
    }

    public static Type[] getReturnTypes(Method method){
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        return new Type[]{Object.class};
    }

    public static boolean checkMethodHasParamType(Method method, Class paramClass){
        Type[] types = method.getGenericParameterTypes();
        if (ArrayUtil.isEmpty(types)){
            return false;
        }

        for (Type type : types) {
            if (checkIfSupplierClass(type, paramClass)){
                return true;
            }
        }

        return false;
    }

    public static boolean checkIfSupplierClass(Type type, Class<?> supplierClass){
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType().getTypeName().equals(supplierClass.getName())) {
                return true;
            }
        } else {
            if (type.getTypeName().equals(supplierClass.getName())) {
                return true;
            }
        }
        return false;
    }
}
