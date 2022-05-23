package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 10:32
 * @description: 检查员工是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器，支持通配符
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 定义不需要拦截的路径
     */
    private static final String[] URLS = new String[]{
            //员工登录登出
            "/employee/login",
            "/employee/logout",
            //静态资源
            "/backend/**",
            "/front/**",
            //文件上传下载
            "/common/**",
            //发送短信验证码
            "/user/sendMsg",
            //用户登录
            "/user/login"
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取请求url
        String requestURI = request.getRequestURI();
        log.info("此次请求url为：{}", requestURI);

        //2.判断此次请求是否需要被处理
        boolean check = check(URLS, requestURI);

        //3.不需要被处理直接放行
        if (check) {
            log.info("本次请求{}不需要被处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4-1.判断员工是否已经登录,如果已经登录就放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("员工已经登录，登录id为：{}", request.getSession().getAttribute("employee"));
            //将员工id设置进当前线程的Threadlocal
            Long employeeId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(request, response);
            return;
        }

        //4-2.判断用户是否已经登录,如果已经登录就放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已经登录，登录id为：{}", request.getSession().getAttribute("user"));
            //将用户id设置进当前线程的Threadlocal
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //5.如果未登录则返回未登录结果,通过输出流向客户端响应数据
        log.info("员工未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {

        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
