package com.datatom.dspool.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.datatom.dspool.datasource.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description 数据源配置文件
 */
@Configuration
@MapperScan(basePackages = "com.datatom.dspool.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceConfig {

    @Autowired
    private Environment env;

    // 获取application.yml中默认数据源的配置信息

    @Value("${spring.datasource.druid.url}")
    private String defaultUrl;
    @Value("${spring.datasource.druid.username}")
    private String defaultUserName;
    @Value("${spring.datasource.druid.password}")
    private String defaultPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String defaultDriver;


    /**
     * 定义key，使用该key获取定义的数据源名
     */
    @Value("${datasource-list}")
    private String datasourceList;

    /**
     * 获取默认数据源
     * @return 返回根据默认数据源配置生成的DruidDataSource对象
     */
    protected  DataSource getDefaultDataSource(){
        DruidDataSource defaultDataSource = new DruidDataSource();
        defaultDataSource.setUrl(defaultUrl);
        defaultDataSource.setUsername(defaultUserName);
        defaultDataSource.setPassword(defaultPassword);
        defaultDataSource.setDriverClassName(defaultDriver);

        return defaultDataSource;
    }

    /**
     * 获取所有配置文件中的数据源
     * @return 返回所有数据源
     */
    protected  Map<Object, Object> getDataSources(){
        Map<Object,Object> map = new HashMap<>(16);
        if (datasourceList != null && datasourceList.length() > 0) {
            String[] names = datasourceList.split(",");
            for (String name : names) {
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setUrl(env.getProperty("spring.datasource." + name + ".url"));
                dataSource.setUsername(env.getProperty("spring.datasource." + name + ".username"));
                dataSource.setPassword(env.getProperty("spring.datasource." + name + ".password"));
                dataSource.setDriverClassName(env.getProperty("spring.datasource." + name + ".driver-class-name"));

                map.put(name, dataSource);
            }
        }
        return map;
    }


    /**
     * 定义mapper文件位置
     * @return mapper文件字符串
     */
    protected  String getMapperLocation(){
        return "classpath*:mapper/*.xml";
    }

    /**
     * 动态数据源，主要作用是执行 setTargetDataSources ， setDefaultTargetDataSource 以配置默认数据源和数据源列表
     * @return DynamicDataSource 对象
     */
    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();

        Map<Object, Object> dataSources = getDataSources();
        if (dataSources.size() > 0) {
            dynamicDataSource.setTargetDataSources( dataSources );
        }
        DataSource ds = getDefaultDataSource();
        if (ds != null) {
            dynamicDataSource.setDefaultTargetDataSource( ds );
        }
        return dynamicDataSource;
    }

    // 注入bean

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("dynamicDataSource") DataSource dynamicDataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 设置字段为空时依旧返回
        configuration.setCallSettersOnNulls(true);
        bean.setConfiguration(configuration);

        bean.setDataSource(dynamicDataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources( getMapperLocation() ));
        return bean.getObject();
    }


}
