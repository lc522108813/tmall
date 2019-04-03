package com.lc.tmall.service;

import com.lc.tmall.dto.UmsAdminParam;
import com.lc.tmall.mapper.UmsAdminDao;
import com.lc.tmall.mapper.UmsAdminMapper;
import com.lc.tmall.mapper.UmsAdminRoleRelationDao;
import com.lc.tmall.model.UmsAdmin;
import com.lc.tmall.model.UmsAdminExample;
import com.lc.tmall.model.UmsPermission;
import com.lc.tmall.utils.JwtTokenUtil;
import com.lc.tmall.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UmsAdminService {

    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private UmsAdminDao umsAdminDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<UmsPermission> getPermissionList(Long adminId) {
        List<UmsPermission> permissions=adminRoleRelationDao.getPermissionList(adminId);
        return permissions;
    }

    public UmsAdmin getAdminByUsername(String username) {
        return umsAdminDao.getAdminByUsername(username);
    }

    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        // 校验用户名是否已经被注册过
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdminParam.getUsername());
        List<UmsAdmin> list = umsAdminMapper.selectByExample(example);
        if (list.size() != 0) {
            return null;
        }
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        // 密码加密
        String md5Password = MD5Util.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(md5Password);
        umsAdminMapper.insertSelective(umsAdmin);
        return umsAdmin;
    }

    public String login(String username, String password) {
        String token = null;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, MD5Util.encode(password));
        try {
            // 使用authenticationManager 进行验证
            Authentication auth = authenticationManager.authenticate(authenticationToken);
            // 将验证结果存放到 SecurityContextHolder当中
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 之后开始生成token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            log.info("login exception:{}",e.toString());
        }
        return token;
    }

}
