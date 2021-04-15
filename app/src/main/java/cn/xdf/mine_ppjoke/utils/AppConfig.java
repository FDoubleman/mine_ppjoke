package cn.xdf.mine_ppjoke.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import cn.xdf.libcommon.AppGlobals;
import cn.xdf.mine_ppjoke.model.BottomBar;
import cn.xdf.mine_ppjoke.model.Destination;

/**
 * author:fumm
 * Date : 2021/ 03/ 19 11:22 AM
 * Dec : app 配置项
 **/
public class AppConfig {


    public static HashMap<String, Destination> sDestConfig;
    public static BottomBar sBottomBar;

    public static HashMap<String, Destination> getDestConfig() {
        if (sDestConfig != null) {
            return sDestConfig;
        }
        String content = parseFile("destination.json");
        sDestConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
        });

        return sDestConfig;
    }

    public static BottomBar getBottomBar() {
        if (sBottomBar == null) {
            String content = parseFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return sBottomBar;
    }


    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();

        try {
            is = assets.open(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
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
        return builder.toString();
    }

}
