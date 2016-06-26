package com.takungae.dagong30;

/**
 * Created by m on 6/21/16.
 * 这是一个url的键值对, 为了设置url链接参数的.
 */
public class urlpara {
    private final String name;
    private final String value;

    public urlpara(String pName, String pValue) {
        name = pName;
        value = pValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
