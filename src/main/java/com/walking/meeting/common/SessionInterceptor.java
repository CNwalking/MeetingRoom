package com.walking.meeting.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.walking.meeting.dataobject.dao.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }
    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle start");
        HandlerMethod handlerMethod = (HandlerMethod) o;
        // 带包名的名字
        String methodName = handlerMethod.getMethod().getName();
        // 只拿方法名
        String className = handlerMethod.getBean().getClass().getSimpleName();

        // 登录注册忘记密码放行
        if ("/user/login".equals(httpServletRequest.getRequestURI()) || "/user/register".equals(httpServletRequest.getRequestURI())
                ||"/reset/offline".equals(httpServletRequest.getRequestURI())) {
            return true;
        }
        log.info("拦截器拦截到请求，className:{},methodName:{}",className,methodName);
        UserDO userDO = (UserDO)httpServletRequest.getSession().getAttribute(Const.CURRENT_USER);
        if (ObjectUtils.isEmpty(userDO)) {
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out = httpServletResponse.getWriter();
            Map map = Maps.newHashMap();
            map.put("success", false);
            map.put("msg", "请先登录哦");
            out.println(JSONObject.parse(JSON.toJSONString(map)));
            out.flush();
            out.close();
            return false;
        }
//        // 重定向
//        Object object = httpServletRequest.getSession().getAttribute(Const.CURRENT_USER);
//        if (null == object) {
//            httpServletResponse.sendRedirect("/user/session/timeout");
//            return false;}
        return true;
    }
}