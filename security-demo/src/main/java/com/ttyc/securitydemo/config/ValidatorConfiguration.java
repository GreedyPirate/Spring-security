package com.ttyc.securitydemo.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class ValidatorConfiguration {
    @Bean
    public Validator validator(){
        Validator validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(false)   //快速返回
                .buildValidatorFactory()
                .getValidator();

        return validator;
    }
}