package com.lc.tmall.mapper;

import com.lc.tmall.model.UmsAdmin;
import com.lc.tmall.model.UmsAdminExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

    public int deleteByPrimaryKey(Long id){
        return umsAdminMapper.deleteByPrimaryKey(id);
    }
}
