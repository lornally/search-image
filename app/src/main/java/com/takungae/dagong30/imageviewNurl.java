package com.takungae.dagong30;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by m on 6/12/16.
 * 不仅仅有imageview和url. 貌似还应该组合一个显示位置.
 * 位置不应该在这里搞,
 * 但是, 初始化点击时间, 确实应该在这里.
 */
public class ImageviewNurl {
    public final ImageView iv;
    public final String url;
    final static String mck="--iv..N..rl--";
    final private LayoutImageV layoutImageview ;
    public ImageviewNurl(ImageView iv, String url, LayoutImageV layoutImageview) {
        this.iv = iv;
        this.url = url;
        this.layoutImageview = layoutImageview;
        this.iv.setOnClickListener(new imageviewonclicklistener());

    }


    /**
     * 瀑布流的onclick在这里,
     * 每个点击都会进入详情页,
     * 全屏显示点击内容的详细情况.
     * 重新显示的bug, 原来在这里.
     *
     */
    class imageviewonclicklistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//                ImageView iv = (ImageView) v;

            final String iurl = "" + v.getTag(R.string.image_url);
            /**
             * 如果artbigshow没有搞过, 那么就搞一下.
             */
            if (null == MainActivity._drn.layoutartbigshow){
                MainActivity._drn.layoutartbigshow = (FrameLayout)
                        LayoutInflater.from(MainActivity.cma).inflate(R.layout.art_bigshow, null);
            }
            final ImageView i=(ImageView) MainActivity._drn.layoutartbigshow.findViewById(R.id.imageview_art_detail);
            final ImageviewNurl inu=new ImageviewNurl(i, iurl, layoutImageview);
            // i.setImageBitmap(); //  6/12/16  应该加载各种关于art的东西, 不需要, url, 可以带走.
            // i.setTag(R.string.image_url, iurl);
//                i.setTag(R.string.Art, a);
            Imageloader.getInstance().imageviewshowurlpicture(inu, layoutImageview);
            Log.i(mck, "layoutwaterfall after: " + MainActivity._drn.layoutartbigshow);
            /**
             * 加载界面, 把简介写进去.
             * 把id, 也写进去.
             */
            final Art a=MainActivity._drn.stringartHashMap.get(iurl);

            final TextView textView=(TextView)MainActivity._drn.layoutartbigshow.findViewById(R.id.detail_text);
            textView.setText(a.getIllustrate());
            ((MainActivity)MainActivity.cma).a=a;
            /*
            final TextView weixin=(TextView)MainActivity._drn.layoutartbigshow.findViewById(R.id.share_friend);
            final TextView friendcircle=(TextView)MainActivity._drn.layoutartbigshow.findViewById(R.id.share_moment);
            weixin.setTag(a);
            friendcircle.setTag(a);*/
            /**
             * 微信api, 需要先注册, 再使用 妹的.
             */
            ((MainActivity)MainActivity.cma).reg2wx();//reg2wx

            // hide the window title.
//            ((Activity) MainActivity.cma).requestWindowFeature(Window.FEATURE_NO_TITLE);
            // hide the status bar and other OS-level chrome
            ((Activity) MainActivity.cma).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Log.d(mck, "before set content");
            ((Activity) MainActivity.cma).setContentView(MainActivity._drn.layoutartbigshow);
            Log.d(mck, "after set content");


        }
    }




}
