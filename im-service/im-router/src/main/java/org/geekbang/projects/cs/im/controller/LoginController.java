package org.geekbang.projects.cs.im.controller;

import com.alibaba.fastjson.JSON;
import org.geekbang.projects.cs.im.dto.IMLoginRequest;
import org.geekbang.projects.cs.im.dto.IMLoginResponse;
import org.geekbang.projects.cs.im.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @PostMapping(value = "/")
    public IMLoginResponse login(@RequestBody IMLoginRequest request){

        IMLoginResponse response = new IMLoginResponse();
        if(loginService.isLogin(request.getUserid())) {
            response.setCode("2001");
            response.setMsg("重复登录");
            return response;
        }

        loginService.login(request);
        logger.info("登录成功:{}", JSON.toJSONString(request));

        return response;
    }
}
