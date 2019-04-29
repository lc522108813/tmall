package com.lc.tmall.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ESProductAttributeValue implements Serializable {
    private static final long serialVersionUID = -8518675306817688927L;

    private Long id;

    private Long productAttributeId;

    @Field(type=FieldType.Text)
    private String value;

    /** 0 规格 1 参数 **/
    private Integer type;

    @Field(type=FieldType.Text)
    private String name;

}
