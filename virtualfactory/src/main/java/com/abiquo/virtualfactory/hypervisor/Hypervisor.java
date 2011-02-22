package com.abiquo.virtualfactory.hypervisor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.abiquo.server.core.enumerator.HypervisorType;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Hypervisor {
	HypervisorType type();
	boolean overrides() default false;	
}
