package com.takungae.dagong30;

import android.content.Context;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by m on 6/1/16.
 * 这个类从dgruning拆了出来. 基本被废弃了, 以后也不大会写camera了.
 *
 */
public class mckcamera {
    static double cut = 1;//相机预览和相机拍照的比率. 需要考虑屏幕因素.
    public static Context sContext;



    public int preview_height = 0;
    public int preview_width = 0;
    private final String mck="mckcamera";
    private static String _token="", _device_id="";





    public mckcamera(Context c) {
        index.upload.device_height = PreferenceManager.getDefaultSharedPreferences(c).getInt("device_height", 0);
        index.upload.device_width = PreferenceManager.getDefaultSharedPreferences(c).getInt("device_width", 0);
        preview_height = PreferenceManager.getDefaultSharedPreferences(c).getInt("preview_height", 0);
        preview_width = PreferenceManager.getDefaultSharedPreferences(c).getInt("preview_width", 0);
    }





    /****
     * 针对后台接口实现的类.
     */
    public static class index {
        public static class upload {
            public static int device_width = 0;
            public static int device_height = 0;
            public static final String type = "0";//固定为裁剪好上传. "0"
            public static String token = _token;


        }
    }

    public static class Device {
        public static class getToken {
            public static String device_id = _device_id;
        }
    }

    /*****
     * 对surface的初始化参数进行初始化.
     * 必须在init之后才能执行.
     */
    public double initSurfaceView(Camera.Parameters p) {
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //这个是设置自动聚焦.

        /**
         * 如果预览尺寸存在, 那么程序就该返回了.
         */
        if (preview_width > 0) {
            Log.d(mck, "尺寸存在尺寸存在尺寸存在尺寸存在尺寸存在尺寸存在尺寸存在尺寸存在尺寸存在");
            p.setPreviewSize(preview_width, preview_height);
            p.setPictureSize(index.upload.device_width, index.upload.device_height);
            Log.d(mck, ":width::" + preview_width + ":::preview设置:::height:" + preview_height);
            Log.d(mck, "::height:" + index.upload.device_height + ":::pitture设置:::width:" + index.upload.device_width);

            /**
             * 比例必须取比较小的那个, 否则乘回来, 会发生尺寸过大的问题.
             */
            cut= (double) index.upload.device_width/preview_width;
            double cut2= (double) index.upload.device_height/preview_height;
            cut= cut>cut2 ? cut2 : cut;
            return cut;
        }
        ;
        /**
         * preview size 最佳预览尺寸和最佳拍照尺寸必须在一起计算.
         * 1, 得到两个有序集合(貌似树最合理).
         * 2, 从preview中拿到最大的, 和picture中逐个比较.
         */
        Log.d(mck, "preview size");

        /**
         * 得到preview和picture的树.
         */
        SortedSet<cameraSize> lPreviewSizeSortedSet =new TreeSet<>();
        for (Camera.Size s : p.getSupportedPreviewSizes()) {
            lPreviewSizeSortedSet.add(new cameraSize(s.width, s.height));
            Log.d(mck, "preview::s:width:" + s.width + "::s:height:" + s.height);
        }
        SortedSet<cameraSize> lPictureSizeSortedSet =new TreeSet<>();
        for (Camera.Size s : p.getSupportedPictureSizes()) {
            lPictureSizeSortedSet.add(new cameraSize(s.width, s.height));
            Log.d(mck, "picture::s:width:" + s.width + "::s:height:" + s.height);
        }
        Log.d(mck, "bigpreview size"+ "preview::s:width:" + lPreviewSizeSortedSet.first().width + "::s:height:" + lPreviewSizeSortedSet.first().height);
        Log.d(mck, "bigpicture size"+ "picture::s:width:" + lPictureSizeSortedSet.first().width + "::s:height:" + lPictureSizeSortedSet.first().height);
        cameraSize             lpre=lPreviewSizeSortedSet.first();
        cameraSize             lpic=lPictureSizeSortedSet.first();
        Log.d(mck, "开始循环判断比例相似开始循环判断比例相似开始循环判断比例相似开始循环判断比例相似开始循环判断比例相似");
        setpictureNpreview:
        for(cameraSize pre:lPreviewSizeSortedSet){
            for(cameraSize pic:lPictureSizeSortedSet){
                Log.d(mck, "pre.width"+pre.width+" pre.height"+pre.height+"  pic.width"+pic.width+" pic.height"+pic.height);
                if(pre.enough(pic)){
                    lpre=pre;
                    lpic=pic;
                    break setpictureNpreview;
                }
            }
        }
        Log.d(mck,"结束判断比例结束判断比例结束判断比例结束判断比例结束判断比例");

        index.upload.device_width = lpic.width;
        index.upload.device_height = lpic.height;
        preview_width = lpre.width;
        preview_height = lpre.height;

        /**
         * 把预览尺寸和拍照尺寸存入本地存贮, 下次就不必再计算了.
         */
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putInt("preview_width", preview_width).apply();
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putInt("preview_height", preview_height).apply();

        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putInt("device_width", index.upload.device_width).apply();
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putInt("device_height", index.upload.device_height).apply();

        /**
         *  很多机型不支持预览尺寸=拍照尺寸,因此必须一起计算出结果.
         *  因此必须分别计算比例, 然后比较比例的差距.
         *  设置previewsize和pictusize.
         */
        p.setPreviewSize(preview_width, preview_height);
        p.setPictureSize(index.upload.device_width, index.upload.device_height);
        Log.d(mck, ":width::" + preview_width + ":::preview设置:::height:" + preview_height);
        Log.d(mck, "::height:" + index.upload.device_height + ":::pitture设置:::width:" + index.upload.device_width);
        cut= (double) index.upload.device_width/preview_width;
        return cut;
    }





    private class cameraSize implements Comparable <cameraSize>{
        public final int width;
        public final int height;
        public final int area;
        public final double tan;
        private final double en = 0.01;

        public cameraSize(int pWidth, int pHeight) {
            width = pWidth;
            height = pHeight;
            area = width * height;
            tan = (double) width / height;
        }

        public boolean enough(cameraSize c) {

            Log.d(mck, "---en---:"+en+" tan:"+((c.tan - tan) / tan)+" 判断结果:"+ (((c.tan - tan) / tan) < en));
            Log.d(mck, "0.2<0.01"+(0.2<0.01));
            return (Math.abs((c.tan - tan) / tan) < en);

            //如果比例差距足够小, 那么就找到了足够接近的两个size.
        }

        @Override
        public int compareTo(cameraSize another) {
            int i = another.area-area;
            if (i != 0) return i;
            return width-another.width ;
        }

        /**
         * 可以用反射判断下, 是否是cameraSize, 如果不是直接false, 如果是, 那么再判断高度和宽度.
         * @param o
         * @return
         */
        public boolean equals(cameraSize o) {
            return (height == o.height) && (width == o.width);
        }
    }




}
