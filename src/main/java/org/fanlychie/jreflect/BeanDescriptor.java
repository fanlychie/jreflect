package org.fanlychie.jreflect;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean 描述符, 提供全局的操作对象或类的方法
 * Created by fanlychie on 2017/3/4.
 */
public class BeanDescriptor {

    /**
     * 操作的目标
     */
    private Object target;

    /**
     * 操作的目标类
     */
    private Class<?> targetClass;

    /**
     * {@link FieldDescriptor}
     */
    private FieldDescriptor fieldDescriptor;

    /**
     * {@link MethodDescriptor}
     */
    private MethodDescriptor methodDescriptor;

    /**
     * {@link ConstructorDescriptor}
     */
    private ConstructorDescriptor<?> constructorDescriptor;

    /**
     * 内存缓存
     */
    private static final Map<Class<?>, FieldDescriptor> FIELD_DESCRIPTOR_CACHE = new HashMap<>();

    /**
     * 构建实例
     *
     * @param obj 操作类属性(静态字段)可传 Class 实例, 操作对象属性(非静态字段)传具体的实例对象
     */
    public BeanDescriptor(Object obj) {
        this.target = obj;
        if (obj instanceof Class) {
            targetClass = (Class<?>) obj;
        } else {
            targetClass = obj.getClass();
        }
        preHandle();
    }

    /**
     * 调用构造方法创建实例
     *
     * @param argValues 构造方法的参数列表
     * @param <T>       期望返回的类型
     * @return 返回新的实例对象
     */
    public <T> T newInstance(Object... argValues) {
        target = getConstructorDescriptor().newInstance(argValues);
        return (T) target;
    }

    /**
     * 调用方法
     *
     * @param methodName 方法名称
     * @param argValues  方法的参数列表
     * @param <T>        期望返回的数据类型
     * @return 返回方法调用的结果
     */
    public <T> T invokeMethod(String methodName, Object... argValues) {
        return getMethodDescriptor().invokeMethod(methodName, argValues);
    }

    /**
     * 根据属性名称获取对象的属性的值
     *
     * @param name 属性名称
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValueByName(String name) {
        return fieldDescriptor.getValueByName(target, name);
    }

    /**
     * 根据属性类型获取对象的属性的值, 若没有找到参数给定类型的属性或找到多于1个以上将抛出异常
     *
     * @param type 属性类型, 严格匹配类型, Object.class 无效
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValueByType(Class<?> type) {
        return fieldDescriptor.getValueByType(target, type);
    }

    /**
     * 根据属性名称设置对象属性的值
     *
     * @param name  属性名称
     * @param value 值
     */
    public void setValueByName(String name, Object value) {
        fieldDescriptor.setValueByName(target, name, value);
    }

    /**
     * 根据属性值的类型设置对象属性的值, 若没有找到属性值参数的类型的属性或找到多于1个以上将抛出异常
     *
     * @param value 值
     */
    public void setValueByType(Object value) {
        fieldDescriptor.setValueByType(target, value);
    }

    /**
     * 获取目标对象
     *
     * @return 返回创建此实例对象时传递的构造器参数对象
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 获取字段描述符 {@link FieldDescriptor}
     *
     * @return 返回字段描述符
     */
    public FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    /**
     * 获取方法描述符
     *
     * @return {@link MethodDescriptor}
     */
    private MethodDescriptor getMethodDescriptor() {
        if (methodDescriptor == null) {
            methodDescriptor = new MethodDescriptor(target)
                    .accessibleSuperclass(true)
                    .init();
        }
        return methodDescriptor;
    }

    /**
     * 获取构造器描述符
     *
     * @return {@link ConstructorDescriptor}
     */
    private ConstructorDescriptor getConstructorDescriptor() {
        if (constructorDescriptor == null) {
            constructorDescriptor = new ConstructorDescriptor(targetClass);
        }
        return constructorDescriptor;
    }

    /**
     * 预处理, 检查是否已经缓存, 若没有, 则进行初始化并加载到内存缓存
     */
    private void preHandle() {
        synchronized (FIELD_DESCRIPTOR_CACHE) {
            fieldDescriptor = FIELD_DESCRIPTOR_CACHE.get(targetClass);
            if (fieldDescriptor == null) {
                fieldDescriptor = new FieldDescriptor(targetClass)
                        .accessibleSuperclass(false)
                        .accessibleStatic(true)
                        .stopClass(Object.class)
                        .init();
                FIELD_DESCRIPTOR_CACHE.put(targetClass, fieldDescriptor);
            }
        }
    }

}