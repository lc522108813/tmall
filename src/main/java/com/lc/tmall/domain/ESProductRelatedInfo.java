package com.lc.tmall.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 商品相关的品牌名称，分类名称以及属性 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ESProductRelatedInfo {

    private List<String> brandNames;

    private List<String> productCategoryNames;

    private List<ProductAttr> productAttrs;

}
