package com.lc.tmall.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttr {

    private Long attrId;

    private String attrName;

    private List<String> attrValues;



}
