package org.fanlychie.reflection;

import org.fanlychie.reflection.exception.FieldOperationException;
import org.fanlychie.reflection.exception.ReflectionCastException;
import org.fanlychie.reflection.util.PrimitiveWrapperTypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于操作对象属性或类属性, 同一 Class 可重复使用以提升效率
 * Created by fanlychie on 2017/3/2.
 */
public class FieldDescriptor {

    /**
     * 任意的 Class 类型
     */
    private Class<?> pojoClass;

    /**
     * 是否允许访问静态的属性
     */
    private boolean accessibleStatic;

    /**
     * 是否递归查找父类的属性
     */
    private boolean accessibleSuperclass;

    /**
     * 查找到的 <字段名称, 字段对象> Map
     */
    private Map<String, Field> nameFieldMap;

    /**
     * 构建实例
     *
     * @param pojoClass 任意的 Class 类型
     */
    public FieldDescriptor(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
    }

    /**
     * 根据属性名称获取对象的属性的值
     *
     * @param obj  具体对象, 若操作类属性(静态属性), 此项传 null
     * @param name 属性名称
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValue(Object obj, String name) {
        try {
            return (T) getFieldByName(name).get(obj);
        } catch (Exception e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 根据属性类型获取对象的属性的值, 若没有找到参数给定类型的属性或找到多于1个以上将抛出异常
     *
     * @param obj  具体对象, 若操作类属性(静态属性), 此项传 null
     * @param type 属性类型, 严格匹配类型, Object.class 无效
     * @param <T>  期望的类型
     * @return 返回对象属性的值
     */
    public <T> T getValue(Object obj, Class<?> type) {
        try {
            return (T) getFieldByType(type).get(obj);
        } catch (Exception e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 根据属性名称设置对象属性的值
     *
     * @param obj   具体对象, 若操作类属性(静态属性), 此项传 null
     * @param name  属性名称
     * @param value 值
     */
    public void setValue(Object obj, String name, Object value) {
        try {
            getFieldByName(name).set(obj, value);
        } catch (Exception e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 根据属性值的类型设置对象属性的值, 若没有找到属性值参数的类型的属性或找到多于1个以上将抛出异常
     *
     * @param obj   具体对象, 若操作类属性(静态属性), 此项传 null
     * @param value 值
     */
    public void setValue(Object obj, Object value) {
        Field field = null;
        FieldOperationException foe = null;
        Class<?> valueType = value.getClass();
        try {
            field = getFieldByType(valueType);
        } catch (FieldOperationException e) {
            foe = e;
            try {
                Class<?> primitiveType = PrimitiveWrapperTypeUtils.getPrimitiveType(valueType);
                if (primitiveType != null) {
                    field = getFieldByType(primitiveType);
                }
            } catch (Exception ex) {
            }
        }
        if (field == null) {
            throw foe;
        }
        try {
            field.set(obj, value);
        } catch (Exception e) {
            throw new ReflectionCastException(e);
        }
    }

    /**
     * 获取查找到的 <字段名称, 字段对象> Map
     *
     * @return 返回查找到的 <字段名称, 字段对象> Map
     */
    public Map<String, Field> getNameFieldMap() {
        if (nameFieldMap == null) {
            nameFieldMap = lookupClassNameFieldMap(pojoClass);
        }
        return nameFieldMap;
    }

    /**
     * 获取查找到的字段对象集合
     *
     * @return 返回查找到的字段对象集合
     */
    public List<Field> getFields() {
        return new ArrayList<>(getNameFieldMap().values());
    }

    /**
     * 获取查找到的字段名称集合
     *
     * @return 返回查找到的字段名称集合
     */
    public List<String> getFieldNames() {
        return new ArrayList<>(getNameFieldMap().keySet());
    }

    /**
     * 设置是否允许访问静态的字段, 默认查找的过程中遇到静态字段时自动丢掉, 若要操作静态字段, 此项需设为 true
     *
     * @param accessibleStatic 是否允许访问静态的字段
     * @return 返回当前对象
     */
    public FieldDescriptor accessibleStatic(boolean accessibleStatic) {
        this.accessibleStatic = accessibleStatic;
        return this;
    }

    /**
     * 设置是否要递归查找父类的字段, 默认只查找参数给定的类的字段
     *
     * @param accessibleSuperclass 否要递归查找父类的字段
     * @return 返回当前对象
     */
    public FieldDescriptor accessibleSuperclass(boolean accessibleSuperclass) {
        this.accessibleSuperclass = accessibleSuperclass;
        return this;
    }

    /**
     * 判断字段是否为静态的
     *
     * @param field 字段对象
     * @return 若为静态字段则返回 true, 否则返回 false
     */
    private boolean isStaticField(Field field) {
        return (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
    }

    /**
     * 根据名称获取字段对象
     *
     * @param name 字段名称
     * @return 返回得到的字段对象
     */
    private Field getFieldByName(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        Field field = getNameFieldMap().get(name);
        if (field == null) {
            throw new FieldOperationException(pojoClass.getName() + " 类中找不到名称为 " + name + " 的字段");
        }
        return field;
    }

    /**
     * 根据类型获取字段对象
     *
     * @param type 字段类型
     * @return 返回得到的字段对象
     */
    private Field getFieldByType(Class<?> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        List<Field> fields = getFields();
        List<Field> matches = new ArrayList<>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (fieldType != Object.class && (PrimitiveWrapperTypeUtils.matche(fieldType, type) || fieldType.isAssignableFrom(type))) {
                matches.add(field);
            }
        }
        if (matches.isEmpty()) {
            throw new FieldOperationException(pojoClass.getName() + " 类中找不到类型为 " + type.getName() + " 的字段");
        }
        if (matches.size() > 1) {
            StringBuilder names = new StringBuilder("[ ");
            for (Field match : matches) {
                names.append(match.getName()).append(", ");
            }
            names.replace(names.length() - 2, names.length(), " ]");
            throw new FieldOperationException(pojoClass.getName() + " 类中找到多个属性类型为 " + type.getName() + " 的字段: " + names);
        }
        return matches.get(0);
    }

    /**
     * 获取参数给定的类声明的属性字段集合
     *
     * @param pojoClass POJO 类
     * @return 返回参数给定的类声明的属性字段集合
     */
    private List<Field> getClassDeclaredFields(Class<?> pojoClass) {
        List<Field> list = new ArrayList<>();
        Field[] fields = pojoClass.getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                // 若 accessibleStatic=false, 遇到静态字段直接跳过
                if (!accessibleStatic && isStaticField(field)) {
                    continue;
                }
                field.setAccessible(true);
                list.add(field);
            }
        }
        return list;
    }

    /**
     * 查找参数给定的类的 <字段名称, 字段对象> Map
     *
     * @param pojoClass POJO 类
     * @return 返回参数给定的类的 <字段名称, 字段对象> Map
     */
    private Map<String, Field> lookupClassNameFieldMap(Class<?> pojoClass) {
        Map<String, Field> nameFieldMap = new HashMap<>();
        do {
            List<Field> fields = getClassDeclaredFields(pojoClass);
            for (Field field : fields) {
                String name = field.getName();
                if (!nameFieldMap.containsKey(name)) {
                    nameFieldMap.put(name, field);
                }
            }
        } while (accessibleSuperclass && (pojoClass = pojoClass.getSuperclass()) != null);
        return nameFieldMap;
    }

}