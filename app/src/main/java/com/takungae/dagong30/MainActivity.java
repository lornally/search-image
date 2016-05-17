package com.takungae.dagong30;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 先手工实现界面切换, 以后会考虑弄一个界面切换的框架.
 */

public class MainActivity extends AppCompatActivity {
    private String mck="main activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();
        //LayoutInflater.from(this).inflate().
        View layout1 = inflater.inflate(R.layout.activity_main, null);
        setContentView(layout1);

    }

    /**
     * 这个是回退链, 有空再弄.
     */
    @Override
    public void onBackPressed() {
        Log.d(mck, "back press begin");

        super.onBackPressed();
        Log.d(mck, "back press end");
    }

    /**
     * 下面是点击区. 第一个: 点击有惊喜
     * @param v
     */

    public void onbuttonhappy(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }

    /**
     * 下面是点击区. 第二个: 照片搜索
     * @param v
     */

    public void onbuttonphotosearch(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }
    /**
     * 下面是点击区. 第三个: 拍照搜索
     * @param v
     */

    public void onbuttoncamerasearch(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }



    //==========================================================================================
    /**
     * 下面是tab区: 首页, 这个就是backto首页
     * @param v
     */

    public void onbuttonbackhome(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }
    /**
     * 下面是tab区: 相册
     * @param v
     */
    View layoutwaterfall;

    public void onbuttongallery(View v){
        Log.d(mck, "layoutwaterfall before"+layoutwaterfall);

        if(null==layoutwaterfall) layoutwaterfall = getLayoutInflater().inflate(R.layout.waterfall, null);

        Log.d(mck, "layoutwaterfall after"+layoutwaterfall);

        setContentView(layoutwaterfall);


    }
    /**
     * 下面是tab区:收藏
     * @param v
     */

    public void onbuttonbookmark(View v){

    }


//==========================================================================================================================

    /**
     * 下面是点击区.
     * 备用.
     *
     *
     * @param v
     */

    public void onbuttonhappy1111(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }




}
