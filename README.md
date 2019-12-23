# DynamicDataSourcePool
动态数据库连接池

### 介绍
基于spring boot + drui + mybatis 实现动态化的数据源切换和加载，数据源切换是拦截器中通过header请求处理，暂未增加加密解密措施，现仅支持presto连接
