package com.lc.tmall.consts;

import org.springframework.beans.factory.annotation.Value;

public class CommonConsts {

    @Value("${jwt.tokenHeader}")
    public static  String TOKEN_HEADER;
    @Value("${jwt.tokenHead}")
    public static  String TOKEN_HEAD;
}
