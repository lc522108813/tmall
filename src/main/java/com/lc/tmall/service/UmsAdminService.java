package com.lc.tmall.service;

import com.lc.tmall.mapper.UmsAdminRoleRelationMapper;
import com.lc.tmall.model.UmsPermission;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UmsAdminService {

    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationDao;

    public List<UmsPermission> getPermissionList(Long adminId) {
        return adminRoleRelationDao.getPermissionList(adminId);
    }


}
