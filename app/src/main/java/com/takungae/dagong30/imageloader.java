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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by m on 5/31/16.
 * 这基本是个静态类. 提供了一系列方法.
 *
 *  应该把loadimagetask搬到这里面来, 并且私有化, 不允许从外边直接调用loadimagetask, 做了.
 */
public class ImageLoader {


    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private final static LruCache<String, Bitmap> mMemoryCache;
    private final static String mck="ImageLoader";

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
    private ImageLoader() {
    }
    private final static ImageLoader hh=new ImageLoader();

     static ImageLoader getInstance(){
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
     * todo 此处貌似没有范型的必要.
     *
     */
    public  void imageviewshowurlpicture(final ImageviewNurl inu,final LayoutImageV liv){
        Bitmap bitmap = getBitmapFromMemoryCache(inu.url);
        if (bitmap != null) {
            //inu.iv.setImageBitmap(bitmap);
            inu.layoutimage(liv, bitmap);
            Log.d(mck, "    isp:::"+inu.iv.getId());
        } else {
            /***
             * 这里的加载不仅仅包括从网络加载, 还包括从磁盘加载.
             */

            LoadImageTask task = new LoadImageTask(inu, liv);
            task.execute();

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
//    static int id=100;
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
         *  分几列, 列的宽度, 都不应该在这里考虑.
         * 应该是一个interface考虑.
         *
         * @param iV 需要下载的inu
         * @param cW 回调要用的实例, 通常调用方(activity或者view)把自己放进来.
         */
        private LoadImageTask(ImageviewNurl iV, LayoutImageV cW) {
            inu = iV;
            layoutImageview = cW;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {


            Bitmap imageBitmap = getBitmapFromMemoryCache(inu.url);
            Log.d(mck, "doinback: 1: "+imageBitmap);
            if (imageBitmap == null) {
                imageBitmap = url2bitmap(inu.url);//去拿图片.
            }
            Log.d(mck, "doinback: 1.5: "+imageBitmap);
            //总是崩溃在这里. 奇怪了.
            Log.d(mck, "doinback: 2:h:"+imageBitmap.getHeight()+"      w:"+imageBitmap.getWidth());

            return imageBitmap;
        }


        /**
         * 这个地方的问题在于, 计算了控件的宽度和高度.
         *
         *  这个函数里面应该调用正确的函数, 我们只是把bitmap传进去就好了.
         *  这两个问题都改了, 目前已经很简洁了.
         *
         * @param bitmap 线程中下载的图片.
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
           // Log.d(mck, "dopost: "+bitmap);
            if (bitmap == null) return;
            inu.layoutimage( layoutImageview, bitmap);
            taskCollection.remove(this);
        }


        /**
         * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
         *
         * @param imageUrl 图片的URL地址
         * @return 加载到内存的图片。
         */
        public Bitmap url2bitmap(final String imageUrl) {
//        Log.d(mck, "loadimages : "+ columnWidth);

            File imageFile = new File(getImagePath(imageUrl));
            Log.d(mck, "imageURL 1:"+imageUrl);

            if (!imageFile.exists()) {
                downloadImage(imageUrl); //下载图片.
                //imageFile = new File(getImagePath(imageUrl));
            }
            Log.d(mck, "imageURL 2:"+imageUrl);
            if (imageUrl != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), layoutImageview.getColumnWidth());
                if (bitmap != null) {
                    addBitmapToMemoryCache(imageUrl, bitmap);
                    return bitmap;
                }
            }

            Log.d(mck, "url2bitmap error");
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
        private void downloadImage(final String imageUrl) {
            Log.d(mck, "downloadimage:"+ imageUrl);

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
                imageFile.createNewFile();//既然是没有下载过的, 那么久应该新建文件, 不能信任系统.


                Log.d(mck, "imagf:"+imageFile.exists()+"       imgurl: "+imageUrl);

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
                Log.d(mck, "imagefile.exist: "+imageFile.exists());
                Log.d(mck, "imagefile.paht"+imageFile.getPath());

                    Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                            imageFile.getPath(), layoutImageview.getColumnWidth());
                    if (bitmap != null) {
                        addBitmapToMemoryCache(imageUrl, bitmap);
                    }else Log.d(mck, "no image load");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * 获取图片的本地存储路径。
         * 这个地方new file是没用的. 不需要. 貌似错了, 不在这里处理, 后面就会出错.
         *
         * @param imageUrl 图片的URL地址。
         * @return 图片的本地存储路径。
         */
        private String getImagePath(final String imageUrl) {
            int lastSlashIndex = imageUrl.lastIndexOf("/");
            String imageName = imageUrl.substring(lastSlashIndex + 1);
            String imageDir = Environment.getExternalStorageDirectory()
                    .getPath() + "/PhotoWallFalls/";

            File file = new File(imageDir);
            if (!file.exists()) {
                file.mkdirs();
            }

            //更准确的方法
            /*File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File file = new File(path, filname +".jpg");
            try {
                // Make sure the Pictures directory exists.
                path.mkdirs();

                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }*/




            return imageDir + imageName;
        }
    }


}
