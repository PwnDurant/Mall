package com.zqq.authserver.controller;

import com.zqq.common.constant.AuthServerConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    /**
     * 发送一个请求直接跳转到页面
     *
     * SpringMVC viewController ：将请求和页面映射
     * @return
     */

//    @GetMapping("/login.html")
//    public String loginPage(){
//
//        return "login";
//    }
//
//    @GetMapping("/reg.html")
//    public String regPage(){
//
//        return "reg";
//    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute==null){
//            没登入
            return "login";
        }else{
            return "redirect:http://mall.com";
        }
    }




}
