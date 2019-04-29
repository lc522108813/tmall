package com.lc.tmall.mapper;

import com.lc.tmall.domain.ESProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ESProductDao {
    
    List<ESProduct> getAllESProductList(@Param("id")Long id);
    
}
