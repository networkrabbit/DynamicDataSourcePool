package com.datatom.dspool.model;

import java.util.List;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public class MonitorModel {
    private int id;
    /**
     * JDBCUrl
     */
    private String url;
    /**
     * 数据库连接用户名
     */
    private String username;
    /**
     * 等待时长，单位毫秒
     */
    private Long notEmptyWaitMillis;
    /**
     * 等待次数
     */
    private Long notEmptyWaitCount;
    /**
     * 池中连接数
     */
    private Integer poolingCount;
    /**
     * 最大连接数
     */
    private Integer poolingPeak;

    private List<Map<String, Object>> sqlList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getNotEmptyWaitMillis() {
        return notEmptyWaitMillis;
    }

    public void setNotEmptyWaitMillis(Long notEmptyWaitMillis) {
        this.notEmptyWaitMillis = notEmptyWaitMillis;
    }

    public Long getNotEmptyWaitCount() {
        return notEmptyWaitCount;
    }

    public void setNotEmptyWaitCount(Long notEmptyWaitCount) {
        this.notEmptyWaitCount = notEmptyWaitCount;
    }

    public Integer getPoolingCount() {
        return poolingCount;
    }

    public void setPoolingCount(Integer poolingCount) {
        this.poolingCount = poolingCount;
    }

    public Integer getPoolingPeak() {
        return poolingPeak;
    }

    public void setPoolingPeak(Integer poolingPeak) {
        this.poolingPeak = poolingPeak;
    }

    public List<Map<String, Object>> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<Map<String, Object>> sqlList) {
        this.sqlList = sqlList;
    }
}
