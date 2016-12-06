package com.rest.service.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by qianjia on 2016/1/15.
 */
public class PropertiesUtil {
    public static final String DEFAULT_CHARSET_NAME = "GB18030";

    public static Properties getConfig(String configFile) {
        return getConfig(configFile, DEFAULT_CHARSET_NAME);
    }

    public static Properties getConfig(String configFile, String charsetName) {
        if (charsetName == null) {
            throw new IllegalArgumentException(
                    "Argu charsetName cannot be null");
        }
        if (configFile == null) {
            throw new IllegalArgumentException("Argu configFile cannot be null");
        }
        Properties prop =  new  Properties();
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(configFile );
        try  {
            prop.load(in);
        }  catch  (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
