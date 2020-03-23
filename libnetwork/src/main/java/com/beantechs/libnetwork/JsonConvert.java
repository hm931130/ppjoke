package com.beantechs.libnetwork;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class JsonConvert implements Convert {
    /**
     * 默认的Json转JaveBean的转换器
     *
     * @param response
     * @param type
     * @return
     */
    @Override
    public Object convert(String response, Type type) {

        JSONObject jsonObject = JSONObject.parseObject(response);
        Object data = jsonObject.get("data");
        return JSONObject.parseObject(data.toString(), type);
//        if (data != null) {
//            Object data1 = data.get("data");
//            return JSONObject.parseObject(data1.toString(), type);
//        }
//        return null;
    }
}
