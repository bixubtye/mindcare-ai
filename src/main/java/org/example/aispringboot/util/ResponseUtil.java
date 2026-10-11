package org.example.aispringboot.util;


import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.common.ResultCode;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtil {
    //处理过滤器中的异常响应
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        //根据不同的状态码返回不同的相应
        int statusCode = switch (resultCode){
            case UNAUTHORIZED, ACCESS_UNAUTHORIZED,TOKEN_INVALID, TOKEN_EXPIRED,TOKEN_BLOCKED-> HttpStatus.UNAUTHORIZED.value();
            case TOKEN_ACCESS_FORBIDDEN -> HttpStatus.FORBIDDEN.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try(PrintWriter writer = response.getWriter()){
            String jsonResponse= JSONUtil.toJsonStr(Result.error(resultCode.getCode(),resultCode.getMsg(),null));
            writer.print(jsonResponse);
            writer.flush();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
