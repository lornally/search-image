package com.takungae.dagong30;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
//import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 先手工实现界面切换, 以后会考虑弄一个界面切换的框架.
 *
 * @author m
 */

public class MainActivity extends AppCompatActivity implements LayoutImageV {
    private String mck = "::::main activity::::";
    private Uri lUri;/*;=Uri.parse("file://" + getExternalFilesDir(Environment.DIRECTORY_DCIM
                              //  Environment.DIRECTORY_PICTURES
    ) + "/" + UUID.randomUUID() + ".jpg"); ;//= null;
*/
    private View layoutmain;
    //    private final Context c=this;
    public static Context cma;//外部类使用.
    private final List<ImageView> imageViewList = new ArrayList<>();
//    private final List<TextView> textViewList = new ArrayList<>();

    public static DgRuning _drn;

    @Override
    public int getColumnWidth() {
        return _drn.screenWidth;
    }

    /**
     * 这个页面很简单, 本身是固定高度的. 因此不需要任何布局代码.
     *
     * @param v 仅仅实现接口, 没办法java8才有默认实现. 此时android只支持java7, -- 2016-6-29
     */
    @Override
    public void addimageatposition(ImageviewNurl v) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 设置content的另一种方法.
         * 查阅了文档, 设置content多次不是一个好主意. 貌似. 因为重新计算了整个界面.
         *
         */
        Log.d("d", "hello world!!!!!!!");

        layoutmain = View.inflate(this, R.layout.activity_main, null);
        setContentView(layoutmain);


//        Log.i(mck, "----uri---" + lUri);
//        Log.v("v", "hello world!";);
//        Log.i("i", "hello world!");
//        Log.w("w", "hello world!");
//        Log.e("e", "hello world!");
        cma = this;
        _drn = new DgRuning();//crash. // : 6/20/16

        Thread thread = new Thread(new layoutimageT()); //用线程解决问题.
        thread.start();
        Log.d("d", "hello world!");

       // layoutimage();  //这个地方有大问题, 这个变成了线程中加载, 导致很可能oncreate加载失败.
    }


    /**
     * 在这里搞定runable
     */
    class layoutimageT implements Runnable {
        public void run() {
            ((MainActivity)MainActivity.cma).layoutimage();
        }
    }

    /**
     * 某些地方需要申明可以为空, 或者不可以为空.
     * 加载首页的图片.
     */


    protected void layoutimage() {
//        super.onResume();
        Log.d(mck, " layoutimage:1");
        final RelativeLayout reLyou = (RelativeLayout) findViewById(R.id.amrelayout);

        //reLyou.removeAllViews();

        /**
         * 检查界面上的images的可见情况. 不可见就加载.
         * 是否能拿到所有的imageview?
         */
        /**
         * 这个地方直接new一个macscrollview?
         */
        ////
        _drn.makeNshow(this.getString(R.string.Loading), Toast.LENGTH_SHORT);
        _drn._artstring=getString(R.string.prepareartlist);

        _drn.prepareDefaultArts();
//        Log.d(mck, " resume:2: "+_drn.sArtist.get(0).getArt_name());
        ImageView iv = (ImageView) findViewById(R.id.image0);
        imageViewList.add(iv);
//        Log.d(mck, "     resume:3: "+iv.getId()+ "       image0: "+R.id.image0);

        ImageView iv1 = (ImageView) findViewById(R.id.image1);
//        Log.d(mck, "     resume:4: "+iv1.getId()+ "       image0: "+R.id.image1);

        imageViewList.add(iv1);
        ImageView iv2 = (ImageView) findViewById(R.id.image2);
        imageViewList.add(iv2);
//        Log.d(mck, "     resume:5: "+iv2.getId()+ "       image0: "+R.id.image2);
        assert iv2 != null;
        final RelativeLayout.LayoutParams themerl = (RelativeLayout.LayoutParams) iv2.getLayoutParams();
        Log.d(mck, "  drn:" + _drn);
        Log.d(mck, "drn.artist" + _drn.sArtist);
        //sartlist is null // : 6/26/16
        final int as = _drn.sArtist.size();
        for (int i = 3; i < as; i++) {
            Log.d(mck, "    begin i:" + i);
            final ImageView imageView = new ImageView(getApplicationContext());
            imageView.setId(View.generateViewId());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(themerl.width, themerl.height);
            rl.setMargins(0, themerl.topMargin, 0, 0);
            imageViewList.add(imageView);
            Log.d(mck, "     i:::::" + imageViewList.get(i - 1).getId() + "    width:" + themerl.width + "    height:" + themerl.height);
            rl.addRule(RelativeLayout.BELOW, imageViewList.get(i - 1).getId());
            assert reLyou != null;
            reLyou.addView(imageView, rl);
        }
        Log.d(mck, "     as:::::" + as + "    width:" + themerl.width + "    height:" + themerl.height);
        for (int i = 0; i < as; i++) {
            Log.d(mck, "     i:::::" + i);
            final ImageView imageView = imageViewList.get(i);
            final ImageviewNurl inu = new ImageviewNurl(imageView, _drn.sArtist.get(i).getPicture_url());
            ImageLoader.getInstance().imageviewshowurlpicture(inu, this);
            /**
             * 增加作者
             */
            final TextView tv= new TextView(MainActivity.cma);
//            textViewList.add(tv);
            final Art a=MainActivity._drn.stringartHashMap.get(inu.url);
            tv.setText(a.getArt_name());
            tv.setTextColor(Color.DKGRAY);
            tv.setPadding(15,5,0,0);
            assert reLyou != null;
            reLyou.addView(tv, imageView.getLayoutParams());
        }

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
        imageViewList.clear();
//        textViewList.clear();
        layoutimage();
    }


    /**
     * 下面是点击区. 第一个: 点击有惊喜
     *
     * @param v 点击有惊喜是一个按钮.
     */

    public void onbuttonhappy(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://wap.takungae.com/cn/reg.php"));
        startActivity(browserIntent);
    }

    /**
     * 下面是点击区. 第二个: 照片搜索, 返回码: 3
     *
     * @param v 按钮
     */

    public void onbuttonphotosearch(View v) {

        startActivityForResult(new Intent(Intent.ACTION_PICK, null)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"), 3);

    }

    /**
     * 下面是点击区. 第三个: 拍照搜索, 返回码: 2
     *
     * @param v 按钮
     */

    public void onbuttoncamerasearch(View v) {
        Intent cI = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {//改为存储到系统相册, Environment.DIRECTORY_DCIM
            this.lUri = Uri.parse("file://" + getExternalFilesDir(Environment.DIRECTORY_DCIM
                    //  Environment.DIRECTORY_PICTURES
            ) + "/" + UUID.randomUUID() + ".jpg");
            Log.d(mck, "sys file:11:" + lUri);

            /* Uri xxx=       Uri.fromFile(File.createTempFile(
                    UUID.randomUUID() + "",
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)));//// : 5/25/16 是否这样就好了?*/
            cI.putExtra(MediaStore.EXTRA_OUTPUT, lUri);
        } catch (Exception e) {
            Log.d(mck, "onbuttoncamerasearch: " + e.getMessage());
        }
        startActivityForResult(cI, 2);
    }

    /**
     * 用户从相册3或者相机2返回之后, 继续做下一步. 下一步就是后面的选切函数code:4.
     * 但是, 用户有可能没有带回数据, 比如用户返回来了.
     *
     * @param requestCode 2, 3, 4, 三中
     * @param resultCode  一般没用.
     * @param data        结果数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(mck, "onactivity+request:" + requestCode + "  :::resultcode:::" + resultCode + "  data::" + data+"  url:"+this.lUri);


        /**
         * 但是, 用户有可能没有带回数据, 比如用户返回来了.
         * 用这个resultcode解决这个问题.
         */
        if(resultCode!= Activity.RESULT_OK)return;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Log.w(mck, "code2--luri:::" + lUri); //这个luri竟然是空? 不应该, 应该是调用前设置好的.
            if(lUri==null)return;
            startPhotoZoom(lUri);//奔着4去了.

        }
        if (requestCode == 3) {
            Log.i(mck, "code3---dataluri:::" + data.getData());


            lUri = data.getData();
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
            _drn.makeNshow("up..1.", Toast.LENGTH_SHORT);
            /**
             * 目前还有加载好结果数据, 也没有parse为json结果. isprepare要在那个独立的线程里面设置为true.
             */
            //_drn.usedefaultunprepare = false;
            //_drn.isprepare =false;
            _drn.finishprepare=DgRuning.runing;
            Thread thread = new Thread(new search());
            thread.start();
            View v = findViewById(R.id.button_searchresult);

            _drn.makeNshow("up..2.", Toast.LENGTH_SHORT);
            assert v != null;
            v.performClick();
            _drn.makeNshow("up..3.", Toast.LENGTH_SHORT);

        }
    }

    class search implements Runnable {
        public void run() {
            try {

                String uploadUrl = DgRuning.uploadurl;
                UrlPara p = new UrlPara("token", _drn._token); //DgRuning._token);
                UrlPara p2 = new UrlPara("type", "0");
                Log.d(mck, "    uploadUrl:::::: " + uploadUrl + "    luri: " + lUri + "    p:" + _drn._token + " :@p2@: " + p2);

                String result = _drn.posturlstring(lUri, uploadUrl, p, p2);
                // 报错在这里, 没有返回结果.
                Log.d(mck, "result:::::::::" + result);

//                if(result.is)
                ////解析result.
                /**
                 * 如果返回是false, 那么就应该显示button, [没有搜索结果, 返回]
                 *
                 */
                Log.d(mck, "before preparearts");
                final boolean bl = _drn.prepareArts(result);
                Log.d(mck, "search bl:" + bl);
                if (!bl) runOnUiThread(new nosearchresult());
                /**
                 * 呼唤主线程, 显示一个toast.
                 */



                /**
                 * 确定加载了正确的艺术品搜索结果数据.
                 */
            } catch (Exception e) {
                Log.d(mck, " search: " + e + "");
            }

        }
    }


    /**
     * 选切函数, 选好了图再切, 这个图有可能来自于相册, 也可能来自于相机.
     *
     * @param uri 图片地址.
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
        Log.d(mck, "luri startphotozoom: " + lUri);

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
     * 其实这个函数整个是没用的.
     *
     * @param v 没用
     */

    public void onbuttonbackhome(View v) {

    }

    public void onbuttongallery(View v) {

        //替换布局为waterfall.

        Log.i(mck, "onbuttongallery layoutwaterfall before: " + _drn.layoutwaterfall);

        /**
         * 如果waterfall没有搞过, 那么就搞一下.
         */

        if (null == _drn.layoutwaterfall)
            _drn.layoutwaterfall = (MScrollView)View.inflate(MainActivity.cma, R.layout.waterfall, null);
                    //getLayoutInflater().inflate(R.layout.waterfall, null);

        Log.i(mck, "onbuttongallery layoutwaterfall after: " + _drn.layoutwaterfall);

        /**
         * 如果还没有准备好, 那么就来准备好默认的显示素材.
         * 这个主要是作为测试函数存在的.
         * 这个是最基础的从string中初始化.
         * 收藏列表是从一个接口初始化. 比较简单.
         * 搜索结果最复杂, 要上传图片. 因此最后写.
         * 这个地方要判断是否要自己搞.
         */
        //_drn.initlist();
        Log.d(mck, "onbuttongallery luri: " + lUri);

        if (null == lUri) {
            _drn.prepareDefaultArts();
        } else {
            //_drn.clearart();
            _drn.sArtist.clear();
            lUri = null;// // : 6/22/16 不知道是否能解决二次进入的bug, 还是带来更严重的bug.
        }

        /**
         * 自动执行mckscrollview
         * 这个地方要判断一下, 如何正确的姿势setcontentview.
         * 解决办法, 在ondestroy里面setcontent一个空的xml.
         */
        setContentView(_drn.layoutwaterfall);


    }


    /**
     * 下面是tab区:收藏
     *
     * @param v 按钮
     */

    public void onbuttonbookmark(View v) {

        /**
         * 和点击相册几乎一模一样. 考虑如何合并为一个.
         * 就是把结果刷掉, 就对了.
         */
//        _drn.clearart();
        _drn.sArtist.clear();


        //替换布局为waterfall.

        Log.i(mck, "layoutwaterfall before: " + _drn.layoutwaterfall);

        /**
         * 如果waterfall没有搞过, 那么就搞一下.
         */

        if (null == _drn.layoutwaterfall)
            _drn.layoutwaterfall = (MScrollView) View.inflate(MainActivity.cma, R.layout.waterfall, null);
                    //getLayoutInflater().inflate(R.layout.waterfall, null);

        Log.i(mck, "layoutwaterfall after: " + _drn.layoutwaterfall);
//        _drn.initlist();

        Thread thread = new Thread(new bookmarklist());
        thread.start();
        /**
         * 自动执行mckscrollview
         */
        _drn.finishprepare=DgRuning.runing; // 这个把标志位设置为正确的, 否则, 标志位会保持之前的状态, 这就不对了.
        setContentView(_drn.layoutwaterfall);
    }

    /**
     *
     */
    class bookmarklist implements Runnable {
        public void run() {
            try {
                String uploadUrl = "http://app.takungae.com:80/Api/Collection/my_collection";
                UrlPara p = new UrlPara("token", _drn._token); //DgRuning._token);
                String result = _drn.posturlstring(lUri, uploadUrl, p);
                Log.d(mck, "result:::::::::" + result);
                /**
                 * 解析result.
                 * 如果返回是false, 那么就应该显示button, [没有搜索结果, 返回]
                 *
                 */
                final boolean bl = _drn.prepareArts(result);
                Log.d(mck, "bookmarklist bl1:" + bl);
                if (!bl) runOnUiThread(new nosearchresult());
                Log.d(mck, "bookmarklist bl2:" + bl);
                /**
                 *
                 * 呼唤主线程, 显示一个toast.
                 * 这个toast不能这么现实, 会报一个exception. 线程错误的exception.
                 */
                //DgRuning.makeNshow(c, "全部结果已展示", Toast.LENGTH_SHORT);


                /**
                 * 确定加载了正确的艺术品搜索结果数据.
                 */
            } catch (Exception e) {
                Log.d(mck, "bookmarklist: " + e + "");
            }
            //在ui线程, 作动作, 更新瀑布流//// : 5/30/16
//            runOnUiThread(Runnable);
        }


    }

    /**
     * 没有搜索结果的时候, 加载这个runble. 主要是显示, 让客人按返回键.
     */

    class nosearchresult implements Runnable {

        @Override
        public void run() {
            Log.d(mck, "nosearchresult 1: ");
            /**
             * 下面这句话虽然没有错, 但是, 实际上也没有用.
             */
            //_drn.layoutwaterfall.rlscroll.removeAllViews();
            Log.d(mck, "nosearchresult 2: ");

            RelativeLayout.LayoutParams rl =
                    new RelativeLayout.LayoutParams(600, 200);
            Log.d(mck, "nosearchresult 3: ");

            Button v = new Button(getApplicationContext());
            v.setId(R.id.noreasch);
            Log.d(mck, "nosearchresult 4: ");

            v.setText("no result, please back");
            rl.addRule(RelativeLayout.CENTER_IN_PARENT);
            Log.d(mck, "nosearchresult 5: ");

            _drn.layoutwaterfall.rlscroll.addView(v, rl);
            Log.d(mck, "nosearchresult 6: ");

        }
    }


//==========================================================================================================================


    /**
     * 销毁activity时, 给一个没有内容的layout.
     */


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.none);
    }


//==========================================================================================================================

    /**
     * 下面是bigshow页面的点击区, 主要就是各种分享啥的.
     *
     */

    public Art a;
    private IWXAPI wxapi;

    public void reg2wx() {
        final String APP_ID = "wx434f0a989ac6a564";
        //// TODO: 这个地方有配置管理, 或者编译配置的需求. 3/7/16 正式打包的时候, 需要替换这个app_id为正式的app_id.

        wxapi = WXAPIFactory.createWXAPI(this, APP_ID);
        wxapi.registerApp(APP_ID);
    }


    /**
     * 不应该在这里错误的初始化, 初始化, 还是应该去另一个地方.
     * lTextView.setText(DgRuning.sArt4detailactivity.getIllustrate());
     * ok了, 改好了.
     */
    public void ondetailclick(View v) {
        Log.d(mck, "\\\\\\\\show detail, =======/////////");


        TextView lTextView = (TextView) findViewById(R.id.detail_text);
        assert lTextView !=null;
        lTextView.setMovementMethod(ScrollingMovementMethod.getInstance());


        View lView = findViewById(R.id.detail_text_layout);
        assert lView!=null;
        lView.setVisibility(View.VISIBLE);
        lView = findViewById(R.id.imageview_detail_button);
        assert lView!=null;

        lView.setVisibility(View.INVISIBLE);
        lView = findViewById(R.id.imageview_share_button);
        assert lView!=null;

        lView.setVisibility(View.INVISIBLE);
    }

    public void ondetail_cancel_click(View v) {
        Log.d(mck, "\\\\\\\\share/////////");
        View nLViewnv = findViewById(R.id.layout_share);
        assert nLViewnv!=null;

        nLViewnv.setVisibility(View.INVISIBLE);
        View lView = findViewById(R.id.detail_text_layout);
        assert lView!=null;

        lView.setVisibility(View.INVISIBLE);
        lView = findViewById(R.id.imageview_detail_button);
        assert lView!=null;

        lView.setVisibility(View.VISIBLE);
        lView = findViewById(R.id.imageview_share_button);
        assert lView!=null;

        lView.setVisibility(View.VISIBLE);
    }

    public void onshareclick(View v) {
        Log.d(mck, "\\\\\\\\share/////////");
        View nLViewnv = findViewById(R.id.layout_share);
        assert nLViewnv!=null;

        nLViewnv.setVisibility(View.VISIBLE);
        Log.d(mck, "a:" + a);

        Log.d(mck, "a.id:" + a.getArt_name());

        if (a.getCollection_id() == 1) return;
        /**
         * 分享=收藏, 要加入收藏夹.
         * */
        collecttask collecttask = new collecttask();
        collecttask.execute(a.getPicture_id() + "", "http://app.takungae.com:80/Api/Collection/add_collection");
        //三个参数, url, mac地址, deviceid.
        //        改为使用uuid代替
    }


    /*****
     * 添加到收藏夹, 只有不被收藏的内容才需要收藏
     */

    private class collecttask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String pictureid = params[0];
            String uploadUrl = params[1];
            UrlPara p = new UrlPara("picture_id", pictureid);
            UrlPara tp = new UrlPara("token", _drn._token);


            Log.d(mck, "pic id:::::" + pictureid);
            Log.d(mck, "up nurl::::::" + uploadUrl);
            try {
                _drn.geturlstring(uploadUrl, tp, p);

                Log.d(mck, "collecttask success");
                return "collecttask成功";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(mck, "token fail" + e);
                return "collecttask失败";
            }


        }
    }

    public void onshare_cancel_click(View v) {
        Log.d(mck, "\\\\\\\\share/////////");
        View nLViewnv = findViewById(R.id.layout_share);
//        assert nLViewnv != null;
        assert nLViewnv!=null;

        nLViewnv.setVisibility(View.INVISIBLE);

    }

    /*****
     * 得到wexinurl
     */

    private class weixinurltask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String pictureid = params[0];
            String uploadUrl = params[1];
            UrlPara p = new UrlPara("picture_id", pictureid);
            UrlPara tp = new UrlPara("token", _drn._token);

            Log.d(mck, "weixinurltask::::::" + uploadUrl);
            try {
                JSONObject lJSONObject = new JSONObject(_drn.geturlstring(uploadUrl, p, tp));
                Log.d(mck, "-jasontostring-" + lJSONObject.toString());

                _drn._weixinurl = lJSONObject.getJSONObject("data").getString("web_url");
                Log.d(mck, "weixinurl::::::::" + _drn._weixinurl);
                if (null == _drn._weixinurl || _drn._weixinurl.length() == 0)
                    return "weixinurl 失败";


                Log.d(mck, "weixinurl success");
                return "weixinurl 成功";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(mck, "token fail" + e);
                return "token失败";
            }


        }
    }

    /**
     * 1, 拿到分享的url
     * 2, 把这个url给微信.
     *
     * @param v 系统
     */
    public void onclick_weixin(View v) {

        SendMessageToWX.Req re = prepareweixin();
        wxapi.sendReq(re);


    }

    public void onclick_friendcircle(View v) {


        SendMessageToWX.Req re = prepareweixin();


        re.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxapi.sendReq(re);

    }

    private SendMessageToWX.Req prepareweixin() {

        /**
         * 分享=收藏, 要加入收藏夹.
         * */
        weixinurltask ltask = new weixinurltask();
        ltask.execute(a.getPicture_id() + "", "http://app.takungae.com:80/Api/Index/share_art");
        //
        Log.d(mck, "sendme re: 1");


//        Toast.makeText(ActivityArtDetail.this, "微信", Toast.LENGTH_SHORT).show();

        /**
         * text分享, 只有文字.
         */
        WXTextObject to = new WXTextObject();
        to.text = a.getArt_name() + "-" + a.getAuthor() + "\r\n" + a.getPicture_url();
        Log.d(mck, "sendme re: 2:" + to.text);

        /**
         * 图片分享, 竟然是only pic, 木有文字.
         */
//        final ImageLoader imageLoader = ImageLoader.getInstance();

        Bitmap imageBitmap = ImageLoader
                .getBitmapFromMemoryCache(a.getPicture_url());
        Log.d(mck, "sendme re: 2.5:: " + imageBitmap);
        if (imageBitmap == null) {

            File imageFile = new File(a.getPicture_url());
            imageBitmap = ImageLoader.decodeSampledBitmapFromResource(
                    imageFile.getPath(), getColumnWidth());
        }

        // BitmapFactory.decodeResource(getResources(), a.getDrawable().);
//            WXImageObject lWXImageObject = new WXImageObject(imageBitmap);

        Bitmap thumbmap = Bitmap.createScaledBitmap(imageBitmap, 32, 32, true);
        Log.d(mck, "sendme re: 3:" + thumbmap.getHeight());


        /**
         * 网页分享
         */
        WXWebpageObject WebpageObject = new WXWebpageObject();
        WebpageObject.webpageUrl = _drn._weixinurl;

        Log.d(mck, "sendme re: 4:" + WebpageObject.webpageUrl);


        WXMediaMessage ms = new WXMediaMessage(WebpageObject);
//        ms.mediaObject=lWXImageObject;
        ms.thumbData = Util.bmpToByteArray(thumbmap, true);


        ms.title = to.text;
        ms.description = a.getArt_name() + a.getAuthor();
        Log.d(mck, "sendme re: 5:" + ms.description);

        SendMessageToWX.Req re = new SendMessageToWX.Req();
        re.transaction = String.valueOf(System.currentTimeMillis());
        re.message = ms;
        Log.d(mck, "sendme re: 6:" + re.transaction);

        return re;
    }


    public void onclick_weibo(View v) {
        //敬请期待
        Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
    }


}
