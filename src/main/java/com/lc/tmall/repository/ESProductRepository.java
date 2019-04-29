package com.lc.tmall.repository;

import com.lc.tmall.domain.ESProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/** 商品ES操作类 **/
public interface ESProductRepository  extends ElasticsearchRepository<ESProduct, Long> {

    Page<ESProduct> findByNameOrSubTitleOrKeywords(String name, String subTitle, String keywords, Pageable page);

}
