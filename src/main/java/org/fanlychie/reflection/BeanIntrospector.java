package org.fanlychie.reflection;

import org.fanlychie.reflection.exception.ReflectionCastException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean 内省, 提供全局的操作bean对象的 getter/setter 方法
 * Created by fanlychie on 2017/3/4.
 */
public class BeanIntrospector {

    /**
     * 操作的目标
     */
    private Object target;

    /**
     * {@link NamePropertyDescriptor}
     */
    private NamePropertyDescriptor namePropertyDescriptor;

    /**
     * 缓存
     */
    private static final Map<Class<?>, NamePropertyDescriptor> NAME_PROPERTY_DESCRIPTOR_CACHE = new HashMap<>();

    /**
     * 构建实例
     *
     * @param obj 操作的实例对象
     */
    public BeanIntrospector(Object obj) {
        this.target = obj;
        preHandle();
    }

    /**
     * 调用 Getter 方法
     *
     * @param name 对应的属性名称
     * @param args 方法参数, 没有可不传
     * @param <T>  期待的返回值类型
     * @return 返回方法调用的结果
     */
    public <T> T invokeGetterMethod(String name, Object... args) {
        try {
            return (T) namePropertyDescriptor.getPropertyDescriptor(name).getReadMethod().invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 调用 Setter 方法
     *
     * @param name 对应的属性名称
     * @param args 方法参数, 没有可不传
     */
    public void invokeSetterMethod(String name, Object... args) {
        try {
            namePropertyDescriptor.getPropertyDescriptor(name).getWriteMethod().invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 将 Map 转换为 POJO 对象
     *
     * @param map         参数
     * @param targetClass 转换为的对象类型
     * @param <T>         期望返回值的类型
     * @return 返回转换后的对象, 若 map 参数为 null, 则返回 null
     */
    public static <T> T convertMapToObject(Map<String, Object> map, Class<T> targetClass) {
        if (map != null && !map.isEmpty()) {
            T target = null;
            try {
                target = targetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ReflectionCastException(e);
            }
            BeanIntrospector beanIntrospector = new BeanIntrospector(target);
            Collection<PropertyDescriptor> properties = beanIntrospector.namePropertyDescriptor.getPropertyDescriptors();
            for (PropertyDescriptor property : properties) {
                Method setter = property.getWriteMethod();
                if (setter != null) {
                    try {
                        setter.invoke(target, map.get(property.getName()));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ReflectionCastException(e);
                    }
                }
            }
            return target;
        }
        return null;
    }

    /**
     * 将 POJO 对象转换为 Map 表示
     *
     * @param obj 对象
     * @return 若参数对象为 null, 则返回 null
     */
    public static Map<String, Object> convertObjectToMap(Object obj) {
        if (obj != null) {
            Map<String, Object> map = new HashMap<>();
            BeanIntrospector beanIntrospector = new BeanIntrospector(obj);
            Collection<PropertyDescriptor> properties = beanIntrospector.namePropertyDescriptor.getPropertyDescriptors();
            for (PropertyDescriptor property : properties) {
                Method getter = property.getReadMethod();
                if (getter != null && !property.getName().equals("class")) {
                    try {
                        map.put(property.getName(), getter.invoke(obj));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ReflectionCastException(e);
                    }
                }
            }
            return map;
        }
        return null;
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
        namePropertyDescriptor = NAME_PROPERTY_DESCRIPTOR_CACHE.get(targetClass);
        if (namePropertyDescriptor == null) {
            putCache(targetClass);
        }
    }

    /**
     * 放入缓存
     *
     * @param targetClass 目标类
     */
    private void putCache(Class<?> targetClass) {
        synchronized (NAME_PROPERTY_DESCRIPTOR_CACHE) {
            if (NAME_PROPERTY_DESCRIPTOR_CACHE.get(targetClass) == null) {
                try {
                    namePropertyDescriptor = new NamePropertyDescriptor(
                            Introspector.getBeanInfo(targetClass).getPropertyDescriptors());
                } catch (IntrospectionException e) {
                    throw new ReflectionCastException(e);
                }
                NAME_PROPERTY_DESCRIPTOR_CACHE.put(targetClass, namePropertyDescriptor);
            }
        }
    }

    /**
     * 名称-属性对象 描述符
     */
    private static class NamePropertyDescriptor {

        /**
         * 名称-属性对象 Map
         */
        private Map<String, PropertyDescriptor> namePropertyDescriptorMap;

        /**
         * 构建实例
         *
         * @param propertyDescriptors 属性描述符数组
         */
        public NamePropertyDescriptor(PropertyDescriptor[] propertyDescriptors) {
            init(propertyDescriptors);
        }

        /**
         * 获取属性描述符对象
         *
         * @param name 名称
         * @return 返回 PropertyDescriptor
         */
        public PropertyDescriptor getPropertyDescriptor(String name) {
            return namePropertyDescriptorMap.get(name);
        }

        /**
         * 获取属性描述符列表
         *
         * @return 返回 Collection
         */
        public Collection<PropertyDescriptor> getPropertyDescriptors() {
            return namePropertyDescriptorMap.values();
        }

        /**
         * 初始化
         *
         * @param propertyDescriptors
         */
        private void init(PropertyDescriptor[] propertyDescriptors) {
            namePropertyDescriptorMap = new HashMap<>();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                namePropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
        }

    }

}