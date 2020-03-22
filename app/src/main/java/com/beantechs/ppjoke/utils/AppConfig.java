package com.beantechs.ppjoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.beantechs.libcommon.AppGlobals;
import com.beantechs.ppjoke.model.BottomBar;
import com.beantechs.ppjoke.model.Destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {

    private static HashMap<String, Destination> mDestConfig = null;

    private static BottomBar mBottomBar = null;

    public static BottomBar getBottomBar() {
        if (mBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            mBottomBar = JSONObject.parseObject(content, BottomBar.class);
        }
        return mBottomBar;
    }

    public static HashMap<String, Destination> getDestConfig() {
        if (mDestConfig == null) {
            String content = parseFile("destnation.json");
            mDestConfig = JSONObject.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            });
        }
        return mDestConfig;
    }


    public static String parseFile(String fileName) {
        AssetManager assetManager = AppGlobals.getApplication().getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = assetManager.open(fileName);
            String line = null;
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
