package cn.xdf.libnavannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * author:fumm
 * Date : 2021/ 03/ 18 4:43 PM
 * Dec : 自定义注解
 **/
@Target(ElementType.TYPE)
public @interface ActivityDestination {

    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
