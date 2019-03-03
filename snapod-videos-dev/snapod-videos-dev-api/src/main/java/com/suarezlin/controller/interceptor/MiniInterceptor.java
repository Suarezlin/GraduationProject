package com.suarezlin.controller.interceptor;

import com.suarezlin.utils.CommonReturnType;
import com.suarezlin.utils.JsonUtils;
import com.suarezlin.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    /**
     *  拦截请求，在 controller 之前拦截
     * @param request
     * @param response
     * @param handler
     * @return fals:e 请求被拦截，不放行; true: 放行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String method = request.getMethod();
        String url = request.getRequestURI();
        url = url.split("/")[1];
        if (method.equals("GET") && url.equals("video")) {
            return true;
        }

        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
                setResponseError(response, CommonReturnType.errorTokenMsg("登录过期，请重新登录"));
                return false;
            } else {
                if (!uniqueToken.equals(userToken)) {
                    setResponseError(response, CommonReturnType.errorTokenMsg("您已下线，请重新登录"));
                    return false;
                }
            }
        } else {
            setResponseError(response, CommonReturnType.errorTokenMsg("用户 token 不能为空，请重新登录"));
            return false;
        }

        return true;
    }

    /**
     * controller 之后，视图查询之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 视图渲染之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public void setResponseError(HttpServletResponse response, CommonReturnType result) throws IOException, UnsupportedEncodingException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
