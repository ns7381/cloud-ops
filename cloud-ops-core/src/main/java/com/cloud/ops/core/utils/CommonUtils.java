/**
 * 
 */
package com.cloud.ops.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author cww<br>
 * @version 1.0
 * 2009-12-7 下午05:29:27<br>
 */
public class CommonUtils {
	private CommonUtils() {
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getEnum(Class<T> enumType, String name) {
		T e = null;
		try {
			//e = (T) 
			Class t = enumType;
			e = (T) Enum.valueOf(t, name);
		} catch(Exception ex) {
			//忽略所有异常
		}
		return e;
	}
	
	/**
	 * 判断是否是枚举类型（java5）
	 * @param enumClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEnum(Class enumClass) {
	    while ( enumClass.isAnonymousClass() ) {
	    	enumClass = enumClass.getSuperclass();
	    }
	    return enumClass.isEnum();
	}
	
	/**
	 * 判断是否为原始类型
	 */
    @SuppressWarnings("rawtypes")
	public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
	
	@SuppressWarnings("rawtypes")
	public static boolean isNumber(Class cls) {
		return cls.getSuperclass().equals(Number.class) || CommonUtils.isWrapClass(cls) || cls.isPrimitive();
	}
	
	/**
	 * 清理数组中值为空的元素
	 * @param arrayObject
	 * @return List/Object/
	 */
	@SuppressWarnings("rawtypes")
	public static List<Object> clearNulls(Object arrayObject) {
		List<Object> objs = new ArrayList<Object>();
		if(arrayObject != null) {
			if(arrayObject.getClass().isArray()) {
				int length = Array.getLength(arrayObject);
				for(int i=0; i<length; i++) {
					Object obj = Array.get(arrayObject, i);
					if(obj != null && ! obj.toString().trim().equals("")) {
						objs.add(obj);
					}
				}
			} else if(isIterator(arrayObject)) {
				Iterable it = (Iterable) arrayObject;
				Iterator itor = it.iterator();
				while(itor.hasNext()) {
					Object obj = itor.next();
					if(obj != null && ! obj.toString().trim().equals("")) {
						objs.add(obj);
					}
				}
			}
		}
		return objs;
	}
	
	public static boolean isIterator(Class clazz) {
		boolean isIterator = false;
		isIterator = Iterable.class.isAssignableFrom(clazz);
		return isIterator;
	}
	
	public static boolean isIterator(Object obj) {
		if(obj == null) {
			return false;
		}
		return isIterator(obj.getClass());
	}
	
	/**
	 * Convert a list to an array
	 * @param cls
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] buildArray(Class<T> cls, List<T> values) {
		T[] array = (T[]) Array.newInstance(cls, values.size());
		int i = 0;
		for(T t:values) {
			array[i] = t;
			i++;
		}
		return array;
	}
}
