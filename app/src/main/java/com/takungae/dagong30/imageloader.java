package com.takungae.dagong30;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

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
 * Created by m on 5/31/16.
 * 这基本是个静态类. 提供了一系列方法.
 *
 * todo 应该把loadimagetask搬到这里面来, 并且私有化, 不允许从外边直接调用loadimagetask.
 */
public class Imageloader {


    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private final static LruCache<String, Bitmap> mMemoryCache;
    private final static String mck="Imageloader";

    static {
        // 获取应用程序最大可用内存
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 纯粹的工具类, 不需要构造函数.
     */
    private Imageloader() {
    }
    private final static Imageloader hh=new Imageloader();

     static Imageloader getInstance(){
        return hh;
    }

    /**
     * 将一张图片存储到LruCache中。
     * 为了线程安全, 这个地方不能用static
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width /  reqWidth);
            inSampleSize = widthRatio;
            Log.d(mck, "req 1:"+reqWidth+"   wid 1:"+widthRatio);

        }
        Log.d(mck, "req 2:"+reqWidth+"   ins 2:"+inSampleSize);

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth) {
        Log.d(mck, "path 3:"+pathName+"      wid 3:"+reqWidth);
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 没有指定宽度的情况下, 直接拿到bitmap
     * @param pathName
     * @return
     */
   /* public static Bitmap decodeSampledBitmapFromResource(String pathName
                                                         ) {
        Log.d(mck, "path 3:"+pathName+"      wid 3:");
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        return BitmapFactory.decodeFile(pathName, options);
    }
*/
    /**
     * 用来在一个imageview显示图片的函数.
     * @param inu
     * 此处参数依赖于imageview有一个url, tag,
     * 严重不合理. 如果有bug, 就恨隐蔽, 应该修改
     * 已经改为双参数了.
     * 需要移动走. 移动到imageloader里面.
     * 这里才是加载图片的入口.
     */
    public  void imageviewshowurlpicture(ImageviewNurl inu, LayoutImageV liv){
        Bitmap bitmap = getBitmapFromMemoryCache(inu.url);
        if (bitmap != null) {
            inu.iv.setImageBitmap(bitmap);
            Log.d(mck, "    isp:::"+inu.iv.getId());
        } else {
            /***
             * 这里的加载不仅仅包括从网络加载, 还包括从磁盘加载.
             */
            //   Log.d(mck, "                     checkvisibility 5: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

            LoadImageTask task = new LoadImageTask(inu, liv);
            task.execute();
            //      Log.d(mck, "                     checkvisibility 6: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

        }
    }
    /**
     * 异步下载图片的任务。
     * 加载图片应该子imageloader, 永远不应该直接调用loadimagetask.
     *
     * @author guolin
     */
    /**
     * 记录所有正在下载或等待下载的任务。
     */
    static int id=100;
    public static final Set<LoadImageTask> taskCollection = new HashSet<>();

    class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {




        /**
         * 可重复使用的ImageView
         */
        final private ImageviewNurl inu;
        //final static String mck="..l.i.t..";
        //    final private int columnWidth;

        final private LayoutImageV layoutImageview;

        /**
         * 将可重复使用的ImageView传入
         * 这个task不仅仅是从网络下载, 从内存载入, 从硬盘载入都在这里,
         * 因此, 这里就是初始化imageview的地方.
         * , 必须考察这个view参数是否必须的, 删除试试看
         * todo 分几列, 列的宽度, 都不应该在这里考虑.
         * 应该是一个interface考虑.
         *
         * @param iV
         * @param cW
         */
        public LoadImageTask(ImageviewNurl iV, LayoutImageV cW) {
            inu = iV;
            layoutImageview = cW;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {


            Bitmap imageBitmap = getBitmapFromMemoryCache(inu.url);
            Log.d(mck, "doinback: 1: "+imageBitmap);
            if (imageBitmap == null) {
                imageBitmap = url2bitmap(inu.url);
            }
            Log.d(mck, "doinback: 2:h:"+imageBitmap.getHeight()+"      w:"+imageBitmap.getWidth());

            return imageBitmap;
        }


        /**
         * 这个地方的问题在于, 计算了控件的宽度和高度.
         *
         * todo 这个函数里面应该调用正确的函数, 我们只是把bitmap传进去就好了.
         *
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(mck, "dopost: "+bitmap);

            if (bitmap == null) return;


            inu.iv.setImageBitmap(bitmap);

            //addImage(bitmap, columnWidth, scaledHeight);
            //这个报错, 崩溃. // : 6/3/16
//            Log.d(mck, "dopost sh:"+scaledHeight+ "    id:"+inu.iv.getId());

            /**
             * 这一段要移回到task里面. , 改正确牛牛的的牛的.哈哈哈, 宽这里高和要改一下, 兼容各种情况.
             * 简单 imagerNurl, 里面加上宽度参数, 哈哈哈, niude牛的.
             *
             * 下面这两段没用的, 这个tag可以不设置, 以后都基于inu.url也可以的. todo.
             */
            //if (null!=inu.iv.getTag(R.string.image_url))return;

            //inu.iv.setImageBitmap(bitmap);
//            inu.iv.setScaleType(ImageView.ScaleType.FIT_XY);
//            inu.iv.setPadding(5, 5, 5, 5);
           // inu.iv.setTag(R.string.image_url, inu.url);

            //inu.iv.setId(id++);//这句话绝对有问题.todo 解决id问题.
            //generateViewId

            /**
             * 每张图片一开始都是不在内存的, 因此在这里初始化onclick时间是合适的. 不会多做也不会少做.
             * 或许不合适. 因为这里其实没什么内容. 可以挪到inu的初始化地方.
             */
//            inu.iv.setOnClickListener(new imageviewonclicklistener());
            //findColumnToAdd(imageView, imageHeight).addView(imageView);注释掉得代码.

//            RelativeLayout.LayoutParams layoutParams =
//                    new RelativeLayout.LayoutParams(layoutImageview.getColumnWidth(), scaledHeight);

            layoutImageview.addimageatposition(inu);

            /**
             * 标记这个imageview是可以操作的, 比如checkvisibility.
             */
            inu.iv.setTag(R.string.isshowok, true);

            //这个报错, 崩溃. // : 6/3/16

            taskCollection.remove(this);
        }

        /**
         * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
         *
         * @param imageUrl 图片的URL地址
         * @return 加载到内存的图片。
         */
        public Bitmap url2bitmap(String imageUrl) {
//        Log.d(mck, "loadimages : "+ columnWidth);

            File imageFile = new File(getImagePath(imageUrl));
            if (!imageFile.exists()) {
                downloadImage(imageUrl);
            }
            if (imageUrl != null) {
                Bitmap bitmap = Imageloader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), layoutImageview.getColumnWidth());
                if (bitmap != null) {
                    addBitmapToMemoryCache(imageUrl, bitmap);
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
         *                 todo 代码太曲折了, 竟然是先存成file, 再从file里面读出来. 妈呀.
         */
        private void downloadImage(String imageUrl) {
            Log.d(mck, "downloadimage");

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Log.d("TAG", "monted sdcard");
            } else {
                Log.d("TAG", "has no sdcard");
            }

            try{
                URL url = new URL(imageUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setDoOutput(false);
                Log.d(mck, "uploadurl" + url + con);
                Log.d(mck, "resp:::" + con.getResponseCode());
                Log.d(mck, "errstr:::::" + con.getErrorStream());
                File imageFile = new File(getImagePath(imageUrl));
                try (
                        BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        BufferedOutputStream bos = new BufferedOutputStream(fos)
                ){

                    byte[] b = new byte[1024];
                    int length;
                    while ((length = bis.read(b)) != -1) {
                        bos.write(b, 0, length);
                        bos.flush();
                    }
                }

                if (imageFile != null) {
                    Bitmap bitmap = Imageloader.decodeSampledBitmapFromResource(
                            imageFile.getPath(), layoutImageview.getColumnWidth());
                    if (bitmap != null) {
                        addBitmapToMemoryCache(imageUrl, bitmap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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


}
