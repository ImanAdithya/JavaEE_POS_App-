package lk.ijse.jsp.filter;


import lk.ijse.jsp.util.ResponseUtil;

import javax.json.JsonObject;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*",filterName = "B")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse res= (HttpServletResponse) servletResponse;
        HttpServletRequest req= (HttpServletRequest) servletRequest;

        String auth=req.getHeader ("Auth");

        if(auth !=null && auth.equals ("user=admin,pass=admins")){
            filterChain.doFilter (servletRequest,servletResponse);
        }else {
            res.addHeader ("Content-Type","Application/json");
            res.setStatus (HttpServletResponse.SC_UNAUTHORIZED);

            JsonObject jsonObject = ResponseUtil.getJson ("Auth-Error", "You are not Authenticated to use this Service.!");

            res.getWriter ().print (jsonObject);
        }

    }

    @Override
    public void destroy() {

    }
}
