package org.fanlychie.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段(属性)反射
 * <p>
 * Created by fanlychie on 2017/3/2.
 */
public class FieldReflection {

    /**
     * 任意的类对象
     */
    private Class<?> beanClass;

    /**
     * 类反射得到的字段集合
     */
    private List<Field> fields;

    /**
     * 是否访问静态的字段
     */
    private boolean accessibleStatic;

    /**
     * 是否递归反射父类的字段
     */
    private boolean accessibleSuperClass;

    /**
     * 创建实例
     *
     * @param beanClass 任意的类对象
     */
    public FieldReflection(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public List<Field> getReflectedFields() {

        return null;
    }

    /**
     * 设置是否要访问静态的字段, 默认反射过程中遇到静态字段时自动丢掉, 若要操作静态字段, 此项设 true
     *
     * @param accessibleStatic 是否要访问静态的字段
     * @return 返回当前对象
     */
    public FieldReflection setAccessibleStatic(boolean accessibleStatic) {
        this.accessibleStatic = accessibleStatic;
        return this;
    }

    /**
     * 设置是否要递归访问父类的字段, 默认只反射参数给定类的字段
     *
     * @param accessibleSuperClass 是否递归反射父类的字段
     * @return 返回当前对象
     */
    public FieldReflection setAccessibleSuperClass(boolean accessibleSuperClass) {
        this.accessibleSuperClass = accessibleSuperClass;
        return this;
    }

    private List<Field> getClassDeclaredFields(Class<?> pojoClass) {
        List<Field> list = new ArrayList<>();
        Field[] fields = pojoClass.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                // 若 accessibleStatic = false, 遇到静态字段时, 直接跳过
                if (!accessibleStatic && (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                    continue;
                }
                field.setAccessible(true);
                list.add(field);
            }
        }
        return list;
    }

}