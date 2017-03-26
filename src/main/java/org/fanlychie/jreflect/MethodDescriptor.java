package org.fanlychie.jreflect;

import org.fanlychie.jreflect.exception.ReflectionCastException;
import org.fanlychie.jreflect.util.MethodSignatureUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方法描述符, 提供全局的操作类或对象函数的方法
 * Created by fanlychie on 2017/3/20.
 */
public class MethodDescriptor {

    /**
     * 目标对象
     */
    private Object target;

    /**
     * 目标类
     */
    private Class<?> targetClass;

    /**
     * 是否递归查找父类的属性
     */
    private boolean accessibleSuperclass;

    /**
     * 在递归查找时, 遇到此类则终止
     */
    private Class<?> stopClass;

    /**
     * 方法签名对照表
     */
    private Map<String, Method> methodSignature;

    /**
     * 内存缓存
     */
    private static final Map<Class<?>, Map<String, Method>> CLASS_METHOD_DESCRIPTOR_CACHE = new HashMap<>();

    /**
     * 构建实例
     *
     * @param obj 操作类属性(静态字段)可传 Class 实例, 操作对象属性(非静态字段)传具体的实例对象
     */
    public MethodDescriptor(Object obj) {
        if (obj instanceof Class) {
            this.targetClass = (Class<?>) obj;
        } else {
            this.target = obj;
            this.targetClass = obj.getClass();
        }
    }

    /**
     * 调用方法
     *
     * @param methodName 方法名称
     * @param argValues  方法参数的值列表
     * @param <T>        期望返回的数据类型
     * @return 返回方法调用的结果
     */
    public <T> T invokeMethod(String methodName, Object... argValues) {
        String signature = MethodSignatureUtils.hashCodeString(methodName, argValues);
        Method method = getMethodSignature().get(signature);
        if (method == null) {
            throw MethodSignatureUtils.methodOperationException(methodName, argValues);
        }
        try {
            return (T) method.invoke(target, argValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 设置是否要递归查找父类的方法, 默认只查找参数给定的类的方法
     *
     * @param accessibleSuperclass 否要递归查找父类的方法
     * @return 返回当前对象
     */
    public MethodDescriptor accessibleSuperclass(boolean accessibleSuperclass) {
        this.accessibleSuperclass = accessibleSuperclass;
        return this;
    }

    /**
     * 在递归查找时, 遇到此类则终止
     *
     * @param stopClass 终止递归的类
     * @return 返回当前对象
     */
    public MethodDescriptor stopClass(Class<?> stopClass) {
        this.stopClass = stopClass;
        return this;
    }

    /**
     * 初始化
     *
     * @return 返回当前对象
     */
    MethodDescriptor init() {
        synchronized (CLASS_METHOD_DESCRIPTOR_CACHE) {
            methodSignature = CLASS_METHOD_DESCRIPTOR_CACHE.get(targetClass);
            if (methodSignature == null) {
                methodSignature = lookupClassMethodSignature(targetClass);
                CLASS_METHOD_DESCRIPTOR_CACHE.put(targetClass, methodSignature);
            }
        }
        return this;
    }

    /**
     * 获取方法签名对照表
     *
     * @return 返回方法签名对照表
     */
    private Map<String, Method> getMethodSignature() {
        if (methodSignature == null) {
            init();
        }
        return methodSignature;
    }

    /**
     * 获取类声明的方法列表
     *
     * @param pojoClass 任意的 Class 类型
     * @return 返回类声明的方法列表
     */
    private List<Method> getDeclaredMethods(Class<?> pojoClass) {
        List<Method> list = new ArrayList<>();
        Method[] methods = pojoClass.getDeclaredMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                method.setAccessible(true);
                list.add(method);
            }
        }
        return list;
    }

    /**
     * 查找类声明的方法签名表
     *
     * @param pojoClass 任意的 Class 类型
     * @return 返回类声明的方法签名表
     */
    private Map<String, Method> lookupClassMethodSignature(Class<?> pojoClass) {
        Map<String, Method> methodSignature = new HashMap<>();
        do {
            List<Method> methods = getDeclaredMethods(pojoClass);
            for (Method method : methods) {
                String signature = MethodSignatureUtils.hashCodeString(method.getName(), method.getParameterTypes());
                if (!methodSignature.containsKey(signature)) {
                    methodSignature.put(signature, method);
                }
            }
        } while (accessibleSuperclass && (pojoClass = pojoClass.getSuperclass()) != stopClass);
        return methodSignature;
    }

}