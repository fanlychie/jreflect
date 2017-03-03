package org.fanlychie.reflection.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 基本数据类型与包装类型工具类
 * Created by fanlychie on 2017/3/3.
 */
public final class PrimitiveWrapperTypeUtils {

    /**
     * 包装类型与基本数据类型映射表
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP;

    /**
     * 比较两种数据类型是否匹配, 非基本数据类型与包装类型调用时, 直接返回 '==' 比较的结果.
     * 就基本数据类型与包装类型而言:
     * 1. 若 srcType 为包装类型, destType 若同为此包装类型或 destType 为此包装类型对应的基本数据类型, 返回 true;
     * 2. 若 srcType 为基本数据类型, destType 也必须同为此基本数据类型, 才返回 true;
     *
     * @param srcType  源类型
     * @param destType 目标类型
     * @return 匹配返回 true, 否则返回 false
     */
    public static boolean matche(Class<?> srcType, Class<?> destType) {
        if (srcType.isPrimitive()) {
            if (!destType.isPrimitive()) {
                return false;
            }
        } else {
            if (destType.isPrimitive()) {
                destType = PRIMITIVE_WRAPPER_MAP.get(destType);
            }
        }
        return srcType == destType;
    }

    /**
     * 获取参数给定的类型的基本数据类型
     *
     * @param type 类型
     * @return 若 type 为非基本数据类型或包装类型, 则返回 null
     */
    public static Class<?> getPrimitiveType(Class<?> type) {
        if (type.isPrimitive()) {
            return type;
        }
        if (PRIMITIVE_WRAPPER_MAP.containsValue(type)) {
            for (Class<?> primitiveType : PRIMITIVE_WRAPPER_MAP.keySet()) {
                if (PRIMITIVE_WRAPPER_MAP.get(primitiveType) == type) {
                    return primitiveType;
                }
            }
        }
        return null;
    }

    /**
     * 初始化数据
     */
    static {
        PRIMITIVE_WRAPPER_MAP = new HashMap<>();
        PRIMITIVE_WRAPPER_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(double.class, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(char.class, Character.class);
    }

    // 私有化
    private PrimitiveWrapperTypeUtils() {

    }

}