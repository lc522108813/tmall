package com.lc.tmall.dao;

import com.lc.tmall.mapper.UmsAdminMapper;
import com.lc.tmall.model.UmsAdmin;
import com.lc.tmall.model.UmsAdminExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UmsAdminDao {

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    public UmsAdmin getAdminByUsername(String username){
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = umsAdminMapper.selectByExample(example);
        if (adminList != null && adminList.size() > 0) {
            return adminList.get(0);
        }
        return null;
    }
}
