package com.datatom.dspool.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.datatom.dspool.model.MonitorModel;
import com.datatom.dspool.utils.Common;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@RequestMapping("/monitor")
@RestController
public class MonitorController {
    @RequestMapping("/data")
    public Object monitorData() {
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            List<MonitorModel> resultList = new ArrayList<>();

            DruidStatManagerFacade druidStatManagerFacade = DruidStatManagerFacade.getInstance();
            // 获取连接池监控列表
            List<Map<String, Object>> dataSourceStatDataList = druidStatManagerFacade.getDataSourceStatDataList();
            // 遍历拼接监控数据

            for (Map<String, Object> map : dataSourceStatDataList) {
                MonitorModel monitorModel = new MonitorModel();

                // 获取连接池id，根据id获取sql监控相关信息
                int id = (int) map.get("Identity");

                List<Map<String, Object>> list = druidStatManagerFacade.getSqlStatDataList(id);

                monitorModel.setId(id);
                monitorModel.setUrl(String.valueOf(map.get("URL")));
                monitorModel.setUsername(String.valueOf(map.get("UserName")));
                monitorModel.setNotEmptyWaitCount((Long) map.get("NotEmptyWaitCount"));
                monitorModel.setNotEmptyWaitMillis((Long) map.get("NotEmptyWaitMillis"));
                monitorModel.setPoolingCount((Integer) map.get("PoolingCount"));
                monitorModel.setPoolingPeak((Integer) map.get("PoolingPeak"));
                monitorModel.setSqlList(list);

                resultList.add(monitorModel);
            }

            resultMap.put("code", Common.SUCCESS);
            resultMap.put("data", resultList);
            resultMap.put("msg", "查询成功");
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", Common.ERROR);
            resultMap.put("data", null);
            resultMap.put("msg", "查询失败，" + e.getMessage());
            return resultMap;
        }
    }
}
