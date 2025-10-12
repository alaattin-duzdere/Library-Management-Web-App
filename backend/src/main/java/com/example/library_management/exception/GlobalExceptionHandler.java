package com.example.library_management.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<ApiError<String>> handleBaseException(BaseException ex, WebRequest request){
        MessageType status = ex.getStatus();

        return ResponseEntity
                .status(status.getHttpStatus())
                .body(createApiError(status.getMessage(),request,status.getCode()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class )
    public ResponseEntity<ApiError<Map<String, List<String>>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){
        Map<String, List<String>> map = new HashMap<>();
        for(ObjectError objError : ex.getBindingResult().getAllErrors()){
            String fieldName = ((FieldError)objError).getField();
            if (map.containsKey(fieldName)){
                map.put(fieldName,addValue(map.get(fieldName), objError.getDefaultMessage()));
            }else {
                map.put(fieldName, addValue(new ArrayList<>(), objError.getDefaultMessage()));
            }
        }
        return ResponseEntity.badRequest().body(createApiError(map,request,HttpStatus.BAD_REQUEST.value()));
    }

    private List<String> addValue(List<String> list, String newValue){
        list.add(newValue);
        return list;
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }return "";
    }

    public <E> ApiError<E> createApiError(E message, WebRequest request,Integer status) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(status);

        Exception<E> exception = new Exception<>();
        exception.setPath(request.getDescription(false));
        exception.setCreateTime(new Date());
        exception.setMessage(message);
        exception.setHostname(getHostname());

        apiError.setException(exception);

        return apiError;
    }
}
