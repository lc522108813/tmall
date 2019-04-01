package com.lc.tmall.service;

import com.lc.tmall.dao.UmsAdminDao;
import com.lc.tmall.dao.UmsAdminRoleRelationDao;
import com.lc.tmall.mapper.UmsAdminMapper;
import com.lc.tmall.mapper.UmsAdminRoleRelationMapper;
import com.lc.tmall.model.UmsAdmin;
import com.lc.tmall.model.UmsAdminExample;
import com.lc.tmall.model.UmsPermission;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UmsAdminService {

    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;

    @Autowired
    private UmsAdminDao umsAdminDao;

    public List<UmsPermission> getPermissionList(Long adminId) {
        return adminRoleRelationDao.getPermissionList(adminId);
    }

    public UmsAdmin getAdminByUsername(String username){
        return umsAdminDao.getAdminByUsername(username);
    }


}
