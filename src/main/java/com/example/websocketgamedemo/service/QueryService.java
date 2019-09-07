package com.example.websocketgamedemo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.websocketgamedemo.util.HttpResult;
import com.example.websocketgamedemo.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QueryService {

    public String queryText(String name) {
        HttpResult httpResult;
        JSONObject bodyJson;
        JSONArray jsonArray = new JSONArray();
        StringBuilder result = new StringBuilder();

        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("a", "GET_KEYWORDS");
        params.put("kw", name);
        try {
            String queryTextUrl = "https://sffc.sh-service.com/wx_miniprogram/sites/feiguan/trashTypes_2/Handler/Handler.ashx";
            httpResult = httpUtil.doGet(queryTextUrl, params, null);
            String body = httpResult.getBody();
            bodyJson = JSON.parseObject(body);
            jsonArray = bodyJson.getJSONArray("kw_arr");
            result.append("你查询的是：").append(name).append("\n");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String garbageName = jsonObject.getString("Name");
                String garbageType = jsonObject.getString("TypeKey");
                result.append(garbageName).append("-->").append(garbageType).append("\n");
            }
        } catch (Exception e) {
            System.out.println("查询失败");
            return "无查询结果，请确认是否输入正确的垃圾名称。如：鸡蛋";
        }
        System.out.println(result.toString());
        return result.toString();
    }

    public static void main(String[] args) {
        QueryService query = new QueryService();
        query.queryText("鸡蛋");
    }
}
