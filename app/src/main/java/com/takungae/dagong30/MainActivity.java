package com.takungae.dagong30;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.UUID;

/**
 * 先手工实现界面切换, 以后会考虑弄一个界面切换的框架.
 */

public class MainActivity extends AppCompatActivity {
    private String mck = ":::::::::main activity:::::::::";
    private Uri lUri=null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    private   View layoutmain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_main);

        /**
         * 设置content的另一种方法.
         *
         */
        LayoutInflater inflater = getLayoutInflater();
        //LayoutInflater.from(this).inflate().
         layoutmain = inflater.inflate(R.layout.activity_main, null);
        setContentView(layoutmain);


        dgruning.r().init(this);



        Log.i(mck, "----uri---" + lUri);
        Log.v("v", "hello world!");
        Log.d("d", "hello world!");
        Log.i("i", "hello world!");
        Log.w("w", "hello world!");
        Log.e("e", "hello world!");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 这个是回退链, 有空再弄.
     */
    @Override
    public void onBackPressed() {
        Log.i(mck, "back press begin");

//        super.onBackPressed();
        Log.i(mck, "back press end");
        setContentView(layoutmain);
    }


    /**
     * 下面是点击区. 第一个: 点击有惊喜
     *
     * @param v
     */

    public void onbuttonhappy(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }

    /**
     * 下面是点击区. 第二个: 照片搜索, 返回码: 3
     *
     * @param v
     */

    public void onbuttonphotosearch(View v) {

        startActivityForResult(new Intent(Intent.ACTION_PICK, null)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"), 3);

    }

    /**
     * 下面是点击区. 第三个: 拍照搜索, 返回码: 2
     *
     * @param v
     */

    public void onbuttoncamerasearch(View v) {
        Intent cI = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {//改为存储到系统相册, Environment.DIRECTORY_DCIM
            lUri = Uri.parse("file://" + getExternalFilesDir(Environment.DIRECTORY_DCIM
                    //  Environment.DIRECTORY_PICTURES
            ) + "/" + UUID.randomUUID() + ".jpg");
            Log.d(mck, "sys file:11:" + lUri);

            /* Uri xxx=       Uri.fromFile(File.createTempFile(
                    UUID.randomUUID() + "",
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)));//// : 5/25/16 是否这样就好了?*/
            cI.putExtra(MediaStore.EXTRA_OUTPUT, lUri);
        } catch (Exception e) {
            Log.d(mck, e.getMessage());
        }
        startActivityForResult(cI, 2);
    }

    /**
     * 用户从相册3或者相机2返回之后, 继续做下一步. 下一步就是后面的选切函数code:4.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(mck, "onactivity+request:" + requestCode + "  :::resultcode:::" + resultCode + "  data::" + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Log.w(mck, "code2--luri:::" + lUri);
            startPhotoZoom(lUri);//奔着4去了.

        }
        if (requestCode == 3) {
            Log.i(mck, "code3---dataluri:::" + data.getData());


            lUri=data.getData();
            startPhotoZoom(lUri);//奔着4去了.
        }
        /**
         * 此处大量操作性代码.
         * 直接跳转下一页, 防止误操作.
         * 不用task, 改为thread, 是否会更简单?
         *
         * 启动线程去查询结果.
         * 跳转到下一个页面.
         *
         */
        if (requestCode == 4) {
            dgruning.makeNshow(getApplicationContext(), "搜索中..1.", Toast.LENGTH_SHORT);
            /**
             * 目前还有加载好结果数据, 也没有parse为json结果. isprepare要在那个独立的线程里面设置为true.
             */
            //dgruning.usedefaultunprepare = false;
            //dgruning.isprepare =false;

            Thread thread = new Thread(new search());
            thread.start();
            View v = findViewById(R.id.button_searchresult);

            dgruning.makeNshow(getApplicationContext(), "搜索中..2.", Toast.LENGTH_SHORT);

            v.performClick();
            dgruning.makeNshow(getApplicationContext(), "搜索中..3.", Toast.LENGTH_SHORT);

        }
    }

    class search implements Runnable {
        public void run() {
            try {

                String uploadUrl = dgruning.url;
                dgruning.urlpara p = new dgruning.urlpara("token",dgruning._token); //dgruning._token);
                dgruning.urlpara p2 = new dgruning.urlpara("type","0");
                Log.d(mck, "    uploadUrl:::::: " + uploadUrl+"    luri: "+lUri+"    p:"+dgruning._token+" :@p2@: "+p2);

                    String result=dgruning.r().posturlstring(lUri, uploadUrl, p, p2);
                    // 报错在这里, 没有返回结果.
                Log.d(mck, "result:::::::::"+result);

//                if(result.is)
                ////解析result.
                /**
                 * 如果返回是false, 那么就应该显示button, [没有搜索结果, 返回]
                 * todo.
                 */
                dgruning.r().prepareArts(result);

                /**
                 * 确定加载了正确的艺术品搜索结果数据.
                 */
            //dgruning.isprepare =true;

                ////建立更多的线程, 下载这些图片. 并且把图片保存在本机. 用之前的uuid建立一个目录. 这些图片都顺序放在目录里面.
                ///然后使用缓存机制. 建立对象, 然后, 显示对象.
                ////这个地方还有线程池的问题.

                ////  5/30/16 呼唤主线程, 刷界面, 貌似不该在这里.
//               runOnUiThread(new flash_ui_searchresult());

            } catch (Exception e) {
                Log.d(mck, e + "");
            }
            //在ui线程, 作动作, 更新瀑布流//// : 5/30/16
//            runOnUiThread(Runnable);
        }
    }

    /***
     * 这个地方用搜索结果, 然后, 互换搜索结果页面.
     *
     */
   /* class flash_ui_searchresult implements Runnable {
        public void run() {
            View v = findViewById(R.id.button_searchresult);
            dgruning.usedefaultunprepare = false;
            v.performClick();
        }
    }*/








    /**
     * 选切函数, 选好了图再切, 这个图有可能来自于相册, 也可能来自于相机.
     *
     * @param uri
     */

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        Log.w(mck, "startphotozoom:::::::" + uri);

        intent.setDataAndType(uri, "image/*");

        Log.w(mck, "setdata");

        //这句说我们要切图了.
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例, 0解除比例
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        Log.w(mck, "aspect");


        //这句话是是否拉伸变形
        intent.putExtra("scale", false);

        // outputX outputY 是初始值, 这个设置64有问题, 导致输出就是64, 设成0不行, 但是, 删除就可以了.
//        intent.putExtra("outputX", 100);
//        intent.putExtra("outputY", 100);


        //不要用值直接返回bitmap.
        intent.putExtra("return-data", false);

        //返回一个文件到这个luri里面
        intent.putExtra(MediaStore.EXTRA_OUTPUT, lUri);
        Log.d(mck, "luri startphotozoom: "+lUri);

        //用jpeg格式.
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        //不要人脸识别.
        intent.putExtra("noFaceDetection", true);
        Log.w(mck, "before start");

        startActivityForResult(intent, 4);
        Log.w(mck, "after start");

    }


    //==========================================================================================

    /**
     * 下面是tab区: 首页, 这个就是backto首页
     *
     * @param v
     */

    public void onbuttonbackhome(View v) {

    }

    public void onbuttongallery(View v) {

        //替换布局为waterfall.

        Log.i(mck, "layoutwaterfall before: " + dgruning.r().layoutwaterfall);

        /**
         * 如果waterfall没有搞过, 那么就搞一下.
         */

        if (null == dgruning.r().layoutwaterfall)
            dgruning.r().layoutwaterfall = //new mckScrollView(this);
                    (mckScrollView) getLayoutInflater().inflate(R.layout.waterfall, null);

        Log.i(mck, "layoutwaterfall after: " + dgruning.r().layoutwaterfall);

        /**
         * 如果还没有准备好, 那么就来准备好默认的显示素材.
         * 这个主要是作为测试函数存在的.
         * 这个是最基础的从string中初始化.
         * 收藏列表是从一个接口初始化. 比较简单.
         * 搜索结果最复杂, 要上传图片. 因此最后写.
         * 这个地方要判断是否要自己搞.
         */
        //dgruning.r().initlist();

        if(null==lUri){
            dgruning.r().prepareDefaultArts();
        }




        /**
         * 自动执行mckscrollview
         * 这个地方要判断一下, 如何正确的姿势setcontentview.
         * 解决办法, 在ondestroy里面setcontent一个空的xml.
         */
        setContentView(dgruning.r().layoutwaterfall);


/**

        //向waterfall 添加button.

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlwaterfall);






        //考虑把这些button组织为一个数据结构. 这样就可以展示了.
        for (int i = 100; i < 310; i++) {
            Button b = new Button(this);
            //设置按钮本身.
            b.setId(i);
            b.setText("b" + i);

            b.setPadding(50, 50, 50, 50);
            b.setBackgroundColor(Color.parseColor("#8899aa") + i * 100);
//            b.setBackground(R.drawable.a1080);
//            b.setForeground(getResources().getDrawable(R.drawable.house, getTheme())); 这个是api23的, 不是api1. 所以不能用.
//            b.setBackgroundResource(R.drawable.heart);
            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, R.drawable.heart, R.drawable.note, R.drawable.camera);
//            b.setCompoundDrawablesRelative();
//            b.setCompoundDrawables();

            //计算dp转化的数据.
            float t = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());

            //设置按钮的布局.
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) t, (int) t);
            //Log.i(mck, "tttt"+t);


            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.alignWithParent = true;

            if (i < 102) {
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                layoutParams.addRule(RelativeLayout.BELOW, i - 2);
            }

            int n = i / 2;
            Log.i(mck, "n" + n + "i" + i + "!!!" + (i != n * 2));
            if (i != n * 2) layoutParams.addRule(RelativeLayout.RIGHT_OF, 100);


            //向relativelayout里面添加按钮. 如果移除某个控件: removeview


            //这句话应该是无效的, 因为下面一句话加了.
            b.setLayoutParams(layoutParams);
           // relativeLayout.addView(b, layoutParams);
            //注意, 上面两句话都添加了layoutparams, 所以有一句话是没有用的.

        }*/



    }

    /**
     * 废弃
     * 在这里更新界面流.
     * 计算每个button的高度,
     * 计算每个button的顶部高度.
     * 把顶部最高的一个扑上去.
     */
    /*class refresh_waterfall implements Runnable{
        public void  run(){
            ;
        }





    }
*/





    /**
     * 下面是tab区:收藏
     *
     * @param v
     */

    public void onbuttonbookmark(View v) {


        /**
         * 和点击相册几乎一模一样. 考虑如何合并为一个.
         */


        //替换布局为waterfall.

        Log.i(mck, "layoutwaterfall before: " + dgruning.r().layoutwaterfall);

        /**
         * 如果waterfall没有搞过, 那么就搞一下.
         */

        if (null == dgruning.r().layoutwaterfall)
            dgruning.r().layoutwaterfall = (mckScrollView)getLayoutInflater().inflate(R.layout.waterfall, null);

        Log.i(mck, "layoutwaterfall after: " + dgruning.r().layoutwaterfall);
//        dgruning.r().initlist();

        Thread thread = new Thread(new bookmarklist());
        thread.start();





        /**
         * 自动执行mckscrollview
         */

        setContentView(dgruning.r().layoutwaterfall);


    }

    class bookmarklist implements Runnable {
        public void run() {
            try {

                String uploadUrl = "http://app.takungae.com:80/Api/Collection/my_collection";
                dgruning.urlpara p = new dgruning.urlpara("token",dgruning._token); //dgruning._token);
//                Log.d(mck, "    uploadUrl:::::: " + uploadUrl+"    luri: "+lUri+"    p:"+dgruning._token+" :@p2@: "+p2);

                String result=dgruning.r().posturlstring(lUri, uploadUrl, p);
                // 报错在这里, 没有返回结果.
                Log.d(mck, "result:::::::::"+result);

//                if(result.is)
                ////解析result.
                /**
                 * 这个地方如果返回false, 那么应该放一个button在界面上, [返回].
                 * todo.
                 */
                dgruning.r().prepareArts(result);

                /**
                 * 确定加载了正确的艺术品搜索结果数据.
                 */
//                dgruning.isprepare =true;

                ////建立更多的线程, 下载这些图片. 并且把图片保存在本机. 用之前的uuid建立一个目录. 这些图片都顺序放在目录里面.
                ///然后使用缓存机制. 建立对象, 然后, 显示对象.
                ////这个地方还有线程池的问题.

                //// : 5/30/16 呼唤主线程, 刷界面, 貌似不该在这里.
//               runOnUiThread(new flash_ui_searchresult());

            } catch (Exception e) {
                Log.d(mck, e + "");
            }
            //在ui线程, 作动作, 更新瀑布流//// : 5/30/16
//            runOnUiThread(Runnable);
        }
    }


//==========================================================================================================================

    /**
     * 下面是点击区.
     * 备用.
     *
     * @param v
     */

    public void onbuttonhappy1111(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://member.takungae.com/zh/register_e_channel.htm"));
        startActivity(browserIntent);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.none);
    }
}
