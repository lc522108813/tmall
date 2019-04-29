package com.lc.tmall.service;

import com.lc.tmall.consts.CommonConsts;
import com.lc.tmall.dto.UmsAdminParam;
import com.lc.tmall.mapper.*;
import com.lc.tmall.model.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UmsAdminService {

    @Autowired
    private UmsAdminRoleRelationDao umsAdminRoleRelationDao;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private UmsAdminDao umsAdminDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UmsAdminRoleRelationMapper umsAdminRoleRelationMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UmsAdminPermissionRelationMapper umsAdminPermissionRelationMapper;

    @Autowired
    private UmsAdminPermissionRelationDao umsAdminPermissionRelationDao;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<UmsPermission> getPermissionList(Long adminId) {
        List<UmsPermission> permissions = umsAdminRoleRelationDao.getPermissionList(adminId);
        return permissions;
    }

    public Integer deleteAdmin(Long id){
        return umsAdminDao.deleteByPrimaryKey(id);
    }

    public Integer updateRole(Long adminId,List<Long> roleIds){
        int count=CollectionUtils.isEmpty(roleIds)?0:roleIds.size();
        UmsAdminRoleRelationExample example=new UmsAdminRoleRelationExample();
        example.createCriteria().andAdminIdEqualTo(adminId);
        umsAdminRoleRelationMapper.deleteByExample(example);
        if(CollectionUtils.isEmpty(roleIds)){
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for(Long roleId:roleIds){
                UmsAdminRoleRelation umsAdminRoleRelation=new UmsAdminRoleRelation();
                umsAdminRoleRelation.setRoleId(roleId);
                umsAdminRoleRelation.setAdminId(adminId);
                list.add(umsAdminRoleRelation);
            }
            umsAdminRoleRelationDao.insertList(list);
        }
        return count;
    }

    @Transactional
    public Integer updatePermission(Long adminId,List<Long> permissionIds){
        // 真实权限
        UmsAdminPermissionRelationExample relationExample = new UmsAdminPermissionRelationExample();
        relationExample.createCriteria().andAdminIdEqualTo(adminId);
        umsAdminPermissionRelationMapper.deleteByExample(relationExample);

        //获取用户所有角色权限
        List<UmsPermission> permissionList = umsAdminRoleRelationDao.getRolePermissionList(adminId);
        List<Long> rolePermissionList = permissionList.stream().map(UmsPermission::getId).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(permissionIds)){
            List<UmsAdminPermissionRelation> relationList=new ArrayList<>();
            //筛选出+权限
            //即找出不属于角色权限的权限
            List<Long> addPermissionIdList = permissionIds.stream().filter(permissionId -> !rolePermissionList.contains(permissionId)).collect(Collectors.toList());
            //筛选出-权限
            //找出
            List<Long> subPermissionIdList = rolePermissionList.stream().filter(permissionId -> !permissionIds.contains(permissionId)).collect(Collectors.toList());
            //插入+-权限关系
            relationList.addAll(convert(adminId,1,addPermissionIdList));
            relationList.addAll(convert(adminId,-1,subPermissionIdList));
            return umsAdminPermissionRelationDao.insertList(relationList);
        }
        return 0;
    }

    /**
     * 将+-权限关系转化为对象
     */
    private List<UmsAdminPermissionRelation> convert(Long adminId,Integer type,List<Long> permissionIdList) {
        List<UmsAdminPermissionRelation> relationList = permissionIdList.stream().map(permissionId -> {
            UmsAdminPermissionRelation relation = new UmsAdminPermissionRelation();
            relation.setAdminId(adminId);
            relation.setType(type);
            relation.setPermissionId(permissionId);
            return relation;
        }).collect(Collectors.toList());
        return relationList;
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
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            // 使用authenticationManager 进行验证
            Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            // 将验证结果存放到 SecurityContextHolder当中
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 之后开始生成token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            log.info("login exception:{}", e.toString());
        }
        return token;
    }


    /**
     * 刷新token
     **/
    public String refreshToken(String bareToken) {
        // 首先校验老的token
        String token = bareToken.substring(CommonConsts.TOKEN_HEAD.length());
        return jwtTokenUtil.refreshToken(token);
    }


    /**
     * 单个用户信息查询
     **/
    public UmsAdmin getItem(Long id) {
        UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(id);
        return umsAdmin;
    }


    /** 获取adminId下的所有权限信息 **/
    public List<UmsRole> getRoles(Long id) {
        return umsAdminRoleRelationDao.getRoleList(id);
    }



}
