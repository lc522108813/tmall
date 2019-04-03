package com.lc.tmall.controller;


import com.lc.tmall.dto.CommonResult;
import com.lc.tmall.dto.UmsAdminLoginParam;
import com.lc.tmall.dto.UmsAdminParam;
import com.lc.tmall.model.UmsAdmin;
import com.lc.tmall.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {

    @Autowired
    private UmsAdminService umsAdminService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @ApiOperation(value="注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestBody UmsAdminParam umsAdminParam ){
        UmsAdmin umsAdmin=umsAdminService.register(umsAdminParam);
        if(umsAdmin==null){
            return new CommonResult().failed();
        }
        return new CommonResult().success(umsAdmin);
    }


    // 登录，并返回token
    @ApiOperation(value="登录")
    @RequestMapping(value="/login",method=RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam){
        String token= umsAdminService.login(umsAdminLoginParam.getUsername(),umsAdminLoginParam.getPassword());
        if(null==token){
            return new CommonResult().validateFailed("账号或密码错误");
        }
        Map<String,String> tokenMap=new HashMap<>();
        tokenMap.put("token",token) ;
        tokenMap.put("tokenHead",tokenHead);
        return new CommonResult().success(tokenMap);
    }
}
