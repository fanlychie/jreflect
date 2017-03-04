package org.fanlychie.reflection;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean 描述符, 提供全局的操作对象属性或类属性的方法
 * Created by fanlychie on 2017/3/4.
 */
public class BeanDescriptor {

    /**
     * 操作的目标
     */
    private Object target;

    /**
     * {@link FieldDescriptor}
     */
    private FieldDescriptor fieldDescriptor;

    /**
     * 内存缓存
     */
    private static final Map<Class<?>, FieldDescriptor> DECLARED_FIELD_CACHE = new HashMap<>();

    /**
     * 构建实例
     *
     * @param obj 操作的实例对象
     */
    public BeanDescriptor(Object obj) {
        this.target = obj;
        preHandle();
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
     * 获取字段描述符 {@link FieldDescriptor}
     *
     * @return 返回字段描述符
     */
    public FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    /**
     * 预处理, 检查类型是否已经缓存, 若没有, 则进行初始化并放到缓存
     */
    private void preHandle() {
        Class<?> targetClass = null;
        if (target instanceof Class) {
            targetClass = (Class<?>) target;
        } else {
            targetClass = target.getClass();
        }
        fieldDescriptor = DECLARED_FIELD_CACHE.get(targetClass);
        if (fieldDescriptor == null) {
            synchronized (DECLARED_FIELD_CACHE) {
                if (DECLARED_FIELD_CACHE.get(targetClass) == null) {
                    fieldDescriptor = new FieldDescriptor(targetClass)
                            .accessibleStatic(true).stopClass(Object.class).init();
                    DECLARED_FIELD_CACHE.put(targetClass, fieldDescriptor);
                }
            }
        }
    }

}