package com.lc.tmall.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Auther: leichuang
 * @Date: 2019/3/11
 */
@Slf4j
@Configuration
@MapperScan(value = "com.lc.tmall.mapper",sqlSessionFactoryRef="sessionFactory")
public class MybatisConfig {

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DruidDataSource dataSource= DruidDataSourceBuilder.create().build();
        return dataSource;
    }


    //sqlSession
    @Bean("sessionFactory")
    public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception {
        PageInterceptor pageHelper = new PageInterceptor();
        Properties properties = new Properties();
        pageHelper.setProperties(properties);

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);//驼峰


        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath:/dao/*Mapper.xml"));
        bean.setPlugins(new Interceptor[]{pageHelper});
        bean.setConfiguration(configuration);
        return bean.getObject();
    }

    //transactionManager
    @Bean("transactionManager")
    public PlatformTransactionManager msgTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


}
