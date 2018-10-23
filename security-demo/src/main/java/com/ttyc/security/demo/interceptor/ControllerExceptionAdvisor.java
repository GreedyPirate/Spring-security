package com.ttyc.security.demo.interceptor;

import com.ttyc.security.demo.error.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Created by Jaynnay on 2018/4/16
 **/
@RestControllerAdvice(basePackages="com..controller",annotations={RestController.class})
@Slf4j
public class ControllerExceptionAdvisor{

    @ExceptionHandler({ServiceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel handleServiceException(ServiceException ex){
        Integer code = ex.getError().getCode();
        String msg = ex.getError().getMsg();
        log.error(msg);

        ResponseModel model = new ResponseModel();
        model.setCode(code);
        model.setMsg(msg);

        return model;
    }

    /**
     * 其他错误
     * @param ex
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseModel exception(Exception ex) {
        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String msg = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        log.error(msg);

        ResponseModel model = new ResponseModel();
        model.setCode(code);
        model.setMsg(msg);

        return model;
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseModel exception(MethodArgumentNotValidException ex) {
        ResponseModel model = new ResponseModel();
        model.setData(null);
        model.setCode(HttpStatus.BAD_REQUEST.value());
        model.setMsg(buildErrorMessage(ex));
        return model;
    }


    /**
     * 构建错误信息
     * @param ex
     * @return
     */
    private String buildErrorMessage(MethodArgumentNotValidException ex){
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        StringBuilder messageBuilder = new StringBuilder();
        objectErrors.stream().forEach(error -> {
            if(error instanceof FieldError){
                FieldError fieldError = (FieldError) error;
                messageBuilder.append(fieldError.getDefaultMessage()).append(",");
            }
        });
        String message  = messageBuilder.deleteCharAt(messageBuilder.length() - 1).toString();
        log.error(message);
        return message;
    }
}
