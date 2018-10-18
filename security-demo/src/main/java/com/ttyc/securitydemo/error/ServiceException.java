package com.ttyc.securitydemo.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceException extends RuntimeException{

    ExceptionEntity error;

}
