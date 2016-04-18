package com.inverce.utils.events.annotations;


import com.inverce.utils.events.core.ThreadPolicy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE})
public @interface StateMachineMeta {
    ThreadPolicy threadPolicy() default ThreadPolicy.CallingThread;
}
