package com.lc.tmall.config;

import com.lc.tmall.consts.CommonConsts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2API文档的配置
 * Created by macro on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi(){
        // 在Header中添加Authorization
        List<Parameter> pars=new ArrayList<>();
        ParameterBuilder authPar=new ParameterBuilder();
        authPar.name("Authorization").description("Authorization for jwtAuth").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(authPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lc.tmall.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("tmall")
                .description("mall后台模块")
                .contact("leichuang")
                .version("1.0")
                .build();
    }

    private List<ApiKey> securitySchemes(){
        ApiKey apiKey=new ApiKey(CommonConsts.TOKEN_HEADER, CommonConsts.TOKEN_HEADER, "header");
        List<ApiKey> list=new ArrayList<ApiKey>();
        list.add(apiKey);
        return list;
    }

    private List<SecurityContext> securityContexts(){
        return new ArrayList<>();
    }

}
