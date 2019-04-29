package com.lc.tmall.controller;


import com.lc.tmall.consts.CommonConsts;
import com.lc.tmall.dto.CommonResult;
import com.lc.tmall.dto.UmsAdminLoginParam;
import com.lc.tmall.dto.UmsAdminParam;
import com.lc.tmall.model.UmsAdmin;
import com.lc.tmall.model.UmsPermission;
import com.lc.tmall.model.UmsRole;
import com.lc.tmall.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {

    @Autowired
    private UmsAdminService umsAdminService;


    @ApiOperation(value = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = umsAdminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return new CommonResult().failed();
        }
        return new CommonResult().success(umsAdmin);
    }


    // 登录，并返回token
    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        String token = umsAdminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (null == token) {
            return new CommonResult().validateFailed("账号或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", CommonConsts.TOKEN_HEAD);
        return new CommonResult().success(tokenMap);
    }

    // 登出
    @ApiOperation(value = "登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new CommonResult().success(null);
    }

    // 刷新token
    @RequestMapping(value = "/token/refresh", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(CommonConsts.TOKEN_HEADER);
        String refreshToken = umsAdminService.refreshToken(token);
        if (refreshToken == null) {
            return new CommonResult().failed();
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", CommonConsts.TOKEN_HEAD);
        return new CommonResult().success(tokenMap);
    }

    @ApiOperation(value = "获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getItem(@PathVariable Long id) {
        UmsAdmin umsAdmin = umsAdminService.getItem(id);
        return new CommonResult().success(umsAdmin);
    }


    @ApiOperation(value = "获取某个用户的角色列表")
    @RequestMapping(value = "/role/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getRoles(@PathVariable Long id) {
        List<UmsRole> roleList = umsAdminService.getRoles(id);
        if (roleList == null) {
            return new CommonResult().failed();
        }
        return new CommonResult().success(roleList);
    }


    @ApiOperation(value="获取当前登录用户信息")
    @RequestMapping(value="/info",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAdminInfo(Principal principal){
        String  username = principal.getName();
        UmsAdmin umsAdmin = umsAdminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("roles", new String[]{"TEST"});
        data.put("icon", umsAdmin.getIcon());
        return new CommonResult().success(data);
    }


    @ApiOperation(value="删除指定用户信息")
    @RequestMapping(value="/delete/{id}",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id){
        int count=umsAdminService.deleteAdmin(id);
        if(count>0){
            return new CommonResult().success(null);
        }
        return new CommonResult().failed();
    }

    @ApiOperation(value="修改用户的权限角色")
    @RequestMapping(value="/role/update",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRole(@RequestParam("adminId")Long adminId,
                                   @RequestParam("roleIds")List<Long> roleIds){
        int count=umsAdminService.updateRole(adminId,roleIds);
        if(count>0){
            return new CommonResult().success(null);
        }
        return new CommonResult().failed();

    }


    @ApiOperation(value="获取指定用户的角色")
    @RequestMapping(value="/role/{adminId}",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getRoleList(@PathVariable Long adminId){
        List<UmsRole> list=umsAdminService.getRoles(adminId);
        return new CommonResult().success(list);
    }

    @ApiOperation(value="给用户分配+-权限")
    @RequestMapping(value="/permission/update",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePermission(@RequestParam Long adminId,
                                         @RequestParam("permissionIds") List<Long> permissionIds){

        int count = umsAdminService.updatePermission(adminId,permissionIds);
        if(count>0){
            return new CommonResult().success(count);
        }
        return new CommonResult().failed();
    }


    @ApiOperation(value = "获取用户所有权限（包括+-权限）")
    @RequestMapping(value="/permission/{adminId}",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getPermissionList(@PathVariable Long adminId){
        List<UmsPermission> permissionList=umsAdminService.getPermissionList(adminId);
        return new CommonResult().success(permissionList);
    }
}

