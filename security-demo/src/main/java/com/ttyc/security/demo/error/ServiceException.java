package com.ttyc.security.demo.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceException extends RuntimeException {

    ExceptionEntity error;

}
