package org.fanlychie.jreflect;

import org.fanlychie.jreflect.exception.ReflectionCastException;
import org.fanlychie.jreflect.util.MethodSignatureUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 构造器描述符, 提供全局的操作类的构造器的方法
 * Created by fanlychie on 2017/3/20.
 */
public class ConstructorDescriptor<T> {

    /**
     * 任意的 Class 类型
     */
    private Class<T> pojoClass;

    /**
     * 构造器签名对照表
     */
    private Map<String, Constructor<?>> constructorSignature;

    /**
     * 内存缓存
     */
    private static final Map<Class<?>, Map<String, Constructor<?>>> CLASS_CONSTRUCTOR_DESCRIPTOR_CACHE = new HashMap<>();

    /**
     * 构建实例
     *
     * @param pojoClass 任意的 Class 类型
     */
    public ConstructorDescriptor(Class<T> pojoClass) {
        this.pojoClass = pojoClass;
        preHandle();
    }

    /**
     * 创建实例
     *
     * @param argValues 构造器参数的值列表
     * @return 返回创建的实例对象
     */
    public T newInstance(Object... argValues) {
        String signature = MethodSignatureUtils.hashCodeString(null, argValues);
        Constructor<T> constructor = (Constructor<T>) constructorSignature.get(signature);
        if (constructor == null) {
            throw MethodSignatureUtils.methodOperationException(pojoClass.getSimpleName(), argValues);
        }
        try {
            return constructor.newInstance(argValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 获取类声明的构造器
     *
     * @return 返回类声明的构造器参数签名对照表
     */
    private Map<String, Constructor<?>> getDeclaredConstructors() {
        Map<String, Constructor<?>> constructorSignature = new HashMap<>();
        Constructor<?>[] constructors = pojoClass.getDeclaredConstructors();
        if (constructors != null) {
            for (Constructor<?> constructor : constructors) {
                String signature = MethodSignatureUtils.hashCodeString(null, constructor.getParameterTypes());
                if (!constructorSignature.containsKey(signature)) {
                    constructorSignature.put(signature, constructor);
                }
            }
        }
        return constructorSignature;
    }

    /**
     * 预处理, 检查是否已经缓存, 若没有, 则进行初始化并加载到内存缓存
     */
    private void preHandle() {
        synchronized (CLASS_CONSTRUCTOR_DESCRIPTOR_CACHE) {
            constructorSignature = CLASS_CONSTRUCTOR_DESCRIPTOR_CACHE.get(pojoClass);
            if (constructorSignature == null) {
                constructorSignature = getDeclaredConstructors();
                CLASS_CONSTRUCTOR_DESCRIPTOR_CACHE.put(pojoClass, constructorSignature);
            }
        }
    }

}