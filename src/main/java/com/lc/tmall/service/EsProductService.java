package com.lc.tmall.service;

import com.lc.tmall.mapper.ESProductDao;
import com.lc.tmall.repository.ESProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EsProductService {

    @Autowired
    private ESProductRepository esProductRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ESProductDao esProductDao;

    /**
     * 从数据库中导入所有商品到ES
     */


}
