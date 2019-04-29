package com.lc.tmall.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "pms", type = "product", shards = 1, replicas = 0)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ESProduct implements Serializable {
    private static final long serialVersionUID = -7039166900574013914L;

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String productSn;

    private Long brandId;

    @Field(type = FieldType.Text)
    private String brandName;

    private Long productCategoryId;

    @Field(type = FieldType.Text)
    private String productCategoryName;

    private String pic;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String name;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String subTitle;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String keywords;

    private BigDecimal price;

    private Integer sale;
    private Integer newStatus;
    private Integer recommandStatus;
    private Integer stock;
    private Integer promotionType;
    private Integer sort;

    @Field(type = FieldType.Nested)
    private List<ESProductAttributeValue> attrValueList;
}
