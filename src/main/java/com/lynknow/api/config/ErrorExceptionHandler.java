package com.lynknow.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lynknow.api.exception.BaseException;
import com.lynknow.api.exception.InternalServerErrorException;
import com.lynknow.api.pojo.response.BaseResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ErrorExceptionHandler implements AuthenticationEntryPoint {

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(Exception ex, WebRequest request) {
        BaseResponse<Object> response = new BaseResponse<Object>();
        response.setStatus(false);
        response.setMessage("Interval Server Error");
        response.setCode(500);
        response.setMessage(ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof BaseException) {
            BaseException exc = (BaseException) ex;
            response.setCode(exc.getCode());
            response.setMessage(exc.getMessage());
            if (exc.getData() != null) {
                response.setData(exc.getData());
            }

            status = exc.getStatus();
        } else if (ex instanceof Exception) {
            ex.printStackTrace();
            return new ResponseEntity<>(response, status);
        }

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public @ResponseBody
    String handleNoMethodException(HttpServletRequest request, NoHandlerFoundException ex) {
        return ex.getMessage();
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException authException) throws IOException, ServletException {
        BaseResponse<Object> response = new BaseResponse<Object>();
        response.setStatus(false);
        response.setMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.setCode(401);

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");

        ServletOutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BaseResponse<Object> response = new BaseResponse<Object>();
        response.setStatus(false);
        response.setMessage("Bad Request");
        response.setCode(400);

        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> errors = new HashMap<>();
        List<String> messages = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
            messages.add(error.getDefaultMessage());
        });

        response.setMessage(messages.get(0));
        response.setData(errors);

        return new ResponseEntity<>(response, status);
    }

}
