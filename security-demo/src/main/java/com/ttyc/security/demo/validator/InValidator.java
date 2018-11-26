package com.ttyc.security.demo.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class InValidator implements ConstraintValidator<In, Number> {// 校验Number类型 

    private Set<Integer> inValues;

    @Override
    public void initialize(In in) {
        inValues = new HashSet<>();
        int[] arr = in.values();
        for (int a : arr) {
            inValues.add(a);
        }
    }

    @Override
    public boolean isValid(Number propertyValue, ConstraintValidatorContext cxt) {
        if (propertyValue == null) {
            return false;
        }
        return inValues.contains(propertyValue.intValue());
    }
}