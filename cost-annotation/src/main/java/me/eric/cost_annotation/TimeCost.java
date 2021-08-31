package me.eric.cost_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *     author : eric
 *     time   : 2021/08/14
 *     desc   : 统计方法耗时
 * </pre>
 */
// 作用于方法上面
@Target(ElementType.METHOD)
// 保留在字节码时期
@Retention(RetentionPolicy.CLASS)
public @interface TimeCost {
}
