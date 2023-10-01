package com.example.employee.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = DateFormatValidator.class)
@Documented
public @interface ValidDateFormat {

  String message() default "{DateFormat.invalid}";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

}
