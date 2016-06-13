package com.takungae.dagong30;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 异步下载图片的任务。
 *
 * @author guolin
 */
class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {


    /**
     * 记录所有正在下载或等待下载的任务。
     */
    public static final Set<LoadImageTask> taskCollection = new HashSet<>();
    /**
     * 当前第一列的高度
     */
    public static int firstColumnHeight = 0;
    /**
     * 当前第二列的高度
     */
    public static int secondColumnHeight = 0;
    /**
     * 每一列的宽度
     * 这个没有用, 只在调用的时候有用
     */
    //private int columnWidth;

    public static int firstcolumn;//第一列的最后一个id.
    public static int secondcolumn;//的二列的最后一个id.
    private com.takungae.dagong30.mckScrollView mckScrollView;
    /**
     * 可重复使用的ImageView
     */
    final private imageviewNurl inu;
    final static waterfallimageload imageLoader = waterfallimageload.getInstance();
    final static String mck="..l.i.t..";
    final private int columnWidth;
    static int id=100;

    /**
     * 将可重复使用的ImageView传入
     * 这个task不仅仅是从网络下载, 从内存载入, 从硬盘载入都在这里,
     * 因此, 这里就是初始化imageview的地方.
     *
     * @param imageView
     * @param columnWidth
     */
    public LoadImageTask(mckScrollView mckScrollView, imageviewNurl imageView, int columnWidth) {
        this.mckScrollView = mckScrollView;
        inu = imageView;
        this.columnWidth = columnWidth;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {


        Bitmap imageBitmap = imageLoader
                .getBitmapFromMemoryCache(inu.url);
        Log.d(mck, "doinback: 1: "+imageBitmap);
        if (imageBitmap == null) {
            imageBitmap = url2bitmap(inu.url);
        }
        Log.d(mck, "doinback: 2:h:"+imageBitmap.getHeight()+"      w:"+imageBitmap.getWidth());

        return imageBitmap;
    }


    /**
     * 这个地方的问题在于, 计算了控件的宽度和高度.
     * @param bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d(mck, "dopost: "+bitmap);
        if (bitmap != null) {
            double ratio = bitmap.getWidth() / (columnWidth * 1.0);
            Log.d(mck, "dopost: ratio: "+ratio);
            int scaledHeight = (int) (bitmap.getHeight() / ratio);


            inu.iv.setImageBitmap(bitmap);

            //addImage(bitmap, columnWidth, scaledHeight);
            //这个报错, 崩溃. // : 6/3/16
            Log.d(mck, "dopost sh:"+scaledHeight);

            /**
             * 这一段要移回到task里面. todo, 改正确牛牛的的牛的.哈哈哈, 宽这里高和要改一下, 兼容各种情况.
             * 简单 imagerNurl, 里面加上宽度参数, 哈哈哈, niude牛的.
             *
             */
        if (null!=inu.iv.getTag(R.string.image_url))return;

            inu.iv.setImageBitmap(bitmap);
            inu.iv.setScaleType(ImageView.ScaleType.FIT_XY);
            inu.iv.setPadding(5, 5, 5, 5);
            inu.iv.setTag(R.string.image_url, inu.url);
            inu.iv.setTag(R.string.isshowok, true);
            inu.iv.setId(id++);
            inu.iv.setOnClickListener(new imageviewonclicklistener());
            //findColumnToAdd(imageView, imageHeight).addView(imageView);注释掉得代码.

            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(columnWidth, scaledHeight);

            addimageatposition(inu.iv, layoutParams);
            //这个报错, 崩溃. // : 6/3/16



        }
        taskCollection.remove(this);
    }

    /**
     * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
     *
     * @param imageUrl 图片的URL地址
     * @return 加载到内存的图片。
     */
    public Bitmap url2bitmap(String imageUrl) {
        Log.d(mck, "loadimages : "+ columnWidth);

        File imageFile = new File(getImagePath(imageUrl));
        if (!imageFile.exists()) {
            downloadImage(imageUrl);
        }
        if (imageUrl != null) {
            Bitmap bitmap = waterfallimageload.decodeSampledBitmapFromResource(
                    imageFile.getPath(), columnWidth);
            if (bitmap != null) {
                imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 向ImageView中添加一张图片
     * 不仅仅, 这里是初始化imageview的地方, 我们需要存储的东西都放到里面去.
     * 这个就是不合理之处. 我全都移走了.
     *
     * @param bitmap      待添加的图片
     * @param imageWidth  图片的宽度
     * @param imageHeight 图片的高度
     */
  /*  private void addImage(Bitmap bitmap) {


        inu.iv.setImageBitmap(bitmap);


    }*/


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
            if (null == dgruning.r().layoutartbigshow){
                dgruning.r().layoutartbigshow = (FrameLayout)
                        LayoutInflater.from(mckScrollView.context).inflate(R.layout.art_bigshow, null);
            }
            final ImageView i=(ImageView) dgruning.r().layoutartbigshow.findViewById(R.id.imageview_art_detail);
            final imageviewNurl inu=new imageviewNurl(i, iurl);
           // i.setImageBitmap(); //  6/12/16  应该加载各种关于art的东西, 不需要, url, 可以带走.
           // i.setTag(R.string.image_url, iurl);
//                i.setTag(R.string.art, a);
            mckScrollView.imageviewshowurlpicture(inu);
            Log.i(mck, "layoutwaterfall after: " + dgruning.r().layoutartbigshow);
            /**
             * 加载界面, 把简介写进去.
             * 把id, 也写进去.
             */
            final art a=dgruning.r().stringartHashMap.get(iurl);

            final TextView textView=(TextView)dgruning.r().layoutartbigshow.findViewById(R.id.detail_text);
            textView.setText(a.getIllustrate());
            ((MainActivity)mckScrollView.context).a=a;
            /*
            final TextView weixin=(TextView)dgruning.r().layoutartbigshow.findViewById(R.id.share_friend);
            final TextView friendcircle=(TextView)dgruning.r().layoutartbigshow.findViewById(R.id.share_moment);
            weixin.setTag(a);
            friendcircle.setTag(a);*/
            /**
             * 微信api, 需要先注册, 再使用 妹的.
             */
            ((MainActivity)mckScrollView.context).reg2wx();//reg2wx

            // hide the window title.
//            ((Activity) mckScrollView.context).requestWindowFeature(Window.FEATURE_NO_TITLE);
            // hide the status bar and other OS-level chrome
            ((Activity) mckScrollView.context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mckScrollView.context).setContentView(dgruning.r().layoutartbigshow);

        }
    }


    /**
     * 添加图片, todo 丑陋的要命, 这些代码.
     */
    private void addimageatposition(ImageView v, RelativeLayout.LayoutParams rl) {
        Log.d(mck, "addimageatposition");

        rl.setMargins(0, 0, 0, 0);
        rl.alignWithParent = true;
        /**
         * 这地方真心需要指针, 有了指针, 代码就不必如此丑陋.
         * 或者函数式也行.
         */
        Log.d(mck, " addimageatposition fistco 1:"+ firstcolumn+"      h:"+ firstColumnHeight);
        Log.d(mck, " addimageatposition sectco 1:"+ secondcolumn+"      h:"+ secondColumnHeight);
        Log.d(mck,"addimageatposition id:"+v.getId());
        if (firstColumnHeight == 0) {
            rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            firstcolumn = v.getId();
            v.setTag(R.string.border_top, firstColumnHeight);
            firstColumnHeight += rl.height;
            v.setTag(R.string.border_bottom, firstColumnHeight);
        } else if (secondColumnHeight == 0) {
            rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rl.addRule(RelativeLayout.RIGHT_OF, firstcolumn);
            secondcolumn = v.getId();
            v.setTag(R.string.border_top, secondColumnHeight);
            secondColumnHeight += rl.height;
            v.setTag(R.string.border_bottom, secondColumnHeight);
        } else if (firstColumnHeight <= secondColumnHeight) {
            rl.addRule(RelativeLayout.BELOW, firstcolumn);
            firstcolumn = v.getId();
            v.setTag(R.string.border_top, firstColumnHeight);
            firstColumnHeight += rl.height;
            v.setTag(R.string.border_bottom, firstColumnHeight);
        } else {
            rl.addRule(RelativeLayout.BELOW, secondcolumn);
            rl.addRule(RelativeLayout.RIGHT_OF, firstcolumn);
            secondcolumn = v.getId();
            v.setTag(R.string.border_top, secondColumnHeight);
            secondColumnHeight += rl.height;
            v.setTag(R.string.border_bottom, secondColumnHeight);
        }
        Log.d(mck, " addimageatposition fistco 2:"+ firstcolumn+"h:"+ firstColumnHeight);
        Log.d(mck, " addimageatposition sectco 2:"+ secondcolumn+"h:"+ secondColumnHeight);

        //向relativelayout里面添加按钮. 如果移除某个控件: removeview


        //这句话应该是无效的, 因为下面一句话加了.
        //b.setLayoutParams(layoutParams);
        Log.d(mck, "addimageatposition r: " + mckScrollView.rlscroll.getId() + "   v: " + v.getTag(R.string.image_url) + "   rl: " + rl);
        mckScrollView.rlscroll.addView(v, rl);
        //这个报错, 崩溃. // : 6/3/16

    }

    /**
     * 找到此时应该添加图片的一列。原则就是对三列的高度进行判断，当前高度最小的一列就是应该添加的一列。
     *
     * @param imageView
     * @param imageHeight
     * @return 应该添加图片的一列
     */
    /*
    private LinearLayout findColumnToAdd(ImageView imageView,
            int imageHeight) {
        if (firstColumnHeight <= secondColumnHeight) {
            if (firstColumnHeight <= thirdColumnHeight) {
                imageView.setTag(R.string.border_top, firstColumnHeight);
                firstColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, firstColumnHeight);
                return firstColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        } else {
            if (secondColumnHeight <= thirdColumnHeight) {
                imageView.setTag(R.string.border_top, secondColumnHeight);
                secondColumnHeight += imageHeight;
                imageView
                        .setTag(R.string.border_bottom, secondColumnHeight);
                return secondColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        }
    }
*/

    /**
     * 将图片下载到SD卡缓存起来。
     *
     * @param imageUrl 图片的URL地址。
     */
    private void downloadImage(String imageUrl) {
        Log.d(mck, "downloadimage");

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d("TAG", "monted sdcard");
        } else {
            Log.d("TAG", "has no sdcard");
        }
        HttpURLConnection con = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        File imageFile = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setDoInput(true);
            con.setDoOutput(false);
            Log.d(mck, "uploadurl" + url + con);
            Log.d(mck, "resp:::" + con.getResponseCode());
            Log.d(mck, "errstr:::::" + con.getErrorStream());
            bis = new BufferedInputStream(con.getInputStream());

            imageFile = new File(getImagePath(imageUrl));
            fos = new FileOutputStream(imageFile);
            bos = new BufferedOutputStream(fos);
            byte[] b = new byte[1024];
            int length;
            while ((length = bis.read(b)) != -1) {
                bos.write(b, 0, length);
                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (imageFile != null) {
            Bitmap bitmap = waterfallimageload.decodeSampledBitmapFromResource(
                    imageFile.getPath(), columnWidth);
            if (bitmap != null) {
                imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
            }
        }
    }

    /**
     * 获取图片的本地存储路径。
     *
     * @param imageUrl 图片的URL地址。
     * @return 图片的本地存储路径。
     */
    private String getImagePath(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        String imageName = imageUrl.substring(lastSlashIndex + 1);
        String imageDir = Environment.getExternalStorageDirectory()
                .getPath() + "/PhotoWallFalls/";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;
        return imagePath;
    }
}
