package com.takungae.dagong30;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * 自定义的ScrollView，在其中动态地对图片进行添加。
 * *目前结构
 #runable根据情况, 调用loadimage.
 #onscrool, 根据情况, 调用checkvisible.

 *
 * @author machangkun
 */
public class mckScrollView extends ScrollView {
    /**
     * log标签.
     */
    private static final String mck = "------mcksv-------";
    /**
     * context
     */
    final Context context;

    /**
     * 每页要加载的图片数量
     */
    public static final int PAGE_SIZE = 15;

    /**
     * 记录当前已加载到第几页
     */
    private int page;

    /**
     * 每一列的宽度
     */
    private int columnWidth;

    /**
     * 当前第一列的高度
     */
    private int firstColumnHeight = 0;

    /**
     * 当前第二列的高度
     */
    private int secondColumnHeight = 0;

    private int firstcolumn;//第一列的最后一个id.
    private int secondcolumn;//的二列的最后一个id.

    /**
     * 这个id有用的, 布局时, 需要制定blow, 以及leftalign.
     */
    private int id = 100;//最后一个id.


    /**
     * 对图片进行管理的工具类
     */
    private waterfallimageload imageLoader;


    /**
     * 记录所有正在下载或等待下载的任务。
     */
    private final Set<LoadImageTask> taskCollection = new HashSet<>();

    /**
     * ScrollView下的直接子布局,增删都在这里.
     */
    public RelativeLayout rlscroll;

    /**
     * MyScrollView布局的高度。
     */
    private int scrollViewHeight;


    /**
     * 记录上垂直方向的滚动距离。
     */
    //private int lastScrollY = -1;

    /**
     * 记录所有界面上的图片，用以可以随时控制对图片的释放。
     */
    private List<ImageView> imageViewList = new ArrayList<>();


    private static int toasttime = 0; //toast计数器


    /**
     * MyScrollView的构造函数, 从xml构造要用这个.
     * 注释掉吧, 留着比较烦.
     * 不能注释, 明白了, 必须有这个才行.
     * 不需要这里吊起轮询, 因为有onattachwindows呢.
     * 同样原因, 不需要在这里remove all.
     *
     * @param c
     * @param attrs
     */
    public mckScrollView(Context c, AttributeSet attrs) {

        super(c, attrs);
        Log.d(mck, ":::::::mckstrlllview constractor 2con");



        imageLoader = waterfallimageload.getInstance();

        /**
         * 据说这个方法能解决ondraw重复呼入的问题, 明天测试一下.
         * 太神奇了, 真的有用.
         */
        setWillNotDraw(true);
        context = c;
        /**
         * 这两个应该在构造函数搞定, 这样就只需要搞一次了.
         */
        Log.d(mck, " onAttachedToWindow   2: height: " + scrollViewHeight + "    width: " + columnWidth + "   rls:" + rlscroll);

        DisplayMetrics dm =getResources().getDisplayMetrics();



        scrollViewHeight = dm.heightPixels;
        columnWidth = dm.widthPixels/2;
        Log.d(mck, " onAttachedToWindow   3: height: " + scrollViewHeight + "    width: " + columnWidth + "   rls:" + rlscroll);


    }



    /**
     * 在Handler中进行图片可见性检查的判断，以及加载更多图片的操作。
     * handler就是一个死循环.
     * 检查图片可见性, 应该在ontouch, 不应该在这里!!!
     * 改一下, 改成post. 另外, 检查可见性就应该在这里. ontouch已经不再捕获了.
     * 其实就干两件事:
     * 1, 看看有文件没下载, 唤起一个task去下载文件.
     * 2, 看看下载到头了, 就不去下载了.
     */
    Runnable scrollrun = new Runnable() {


        @Override
        public void run() {
            Log.d(mck, ":::::::runable   0:");

            Log.d(mck, "runable isprepare before  1:  " + dgruning.r().sArtist);
            /**
             * 这个地方判断是否显示了, 无搜索结果之后应该显示的内容. 如果那样, 就返回, 并结束轮询.
             */
            if (null == dgruning.r().sArtist) {

                View v=findViewById(R.id.noreasch);
                Log.d(mck, "runable 1.5: "+v);
                if (null!=v){
                    dgruning.makeNshow(getContext(), "没有结果", Toast.LENGTH_SHORT);
                    return;
                }

                dgruning.makeNshow(getContext(), "匹配中:" + (++toasttime), Toast.LENGTH_SHORT);
                postDelayed(scrollrun, 1500);
                Log.d(mck, "runable isprepare::::  2:  " + dgruning.r().sArtist);
                return;
            }
            Log.d(mck, "runable hasnotallshow:  3:  " + "    tasksize: " + taskCollection.size());
            Log.d(mck, "runable hasnotallshow:  4:  page:" + page*PAGE_SIZE+"    artsize: " + dgruning.r().sArtist.size());

            if(page * PAGE_SIZE >= dgruning.r().sArtist.size()){
                dgruning.makeNshow(getContext(), "全部结果已展示", Toast.LENGTH_SHORT);

                return;
            }
            else if (taskCollection.size() < 5) {
                dgruning.makeNshow(getContext(), "正在加载......", Toast.LENGTH_SHORT);

                Log.d(mck, "runable load more    5:   ");
                loadMoreImages();
            }

            /**
             * 本来的逻辑是, 不加载图片的情况下, 才检查元素可见性.
             * 1, 改为, 不论如何都检查元素的可见性.
             * 2, 应该再改一下, 改为, 在页面不滚动的情况下, 检测图片的可见性.
             * 因为已经不滚了, 因此应该结束这个死循环.
             * 3, 改为每次都检查可见性.
             * 滚动到底, 就不再轮询了.
             *
             */
            //checkVisibility();

            // 5毫秒后再次对滚动位置进行判断
            postDelayed(scrollrun, 50);
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(mck, "onfinishinflate");
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d(mck,"onwindowvisibilitychanged");
    }

    /**
     * 清空界面挪到这挺好的, 貌似.
     * 不好, 第一次进入, 无法拿到清空的layout, 因为这个layout还没有初始化成功.
     * 还是放到这里因为我们把这个函数的调用改在了atachwindows里面.
     * 不能放到单参数的构造器里面, 因为inflate会调用双参数构造器. 神奇了.
     * 只能放到attachwindows里面.
     * 明白了, 不能在这里removeall, 需要到ondraw之后才行.
     *  貌似没用, 一会儿改回来试试.
     *  高度和宽度不能在这里搞, 因为还没有onmessure呢, 所以高宽都是零.
     */


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        /**
         *         非常适合作初始化

         */
        page = 0;
        toasttime = 0;
        //lastScrollY = -1;
        firstColumnHeight = 0;
        secondColumnHeight = 0;
        id=100;
        Log.d(mck, "   init ok:   : ");


        /**
         * 清掉之前的内容.
         */
        Log.d(mck, "onattachedtowindow 1:   rlscroll 1: " + rlscroll);
        rlscroll = (RelativeLayout) findViewById(R.id.rlwaterfall);
        Log.d(mck, "onattachedtowindow 2:    rlscroll 2:" + rlscroll);
        rlscroll.removeAllViews();



        if (null == dgruning.r().sArtist) {
            Log.d(mck, "onAttachedToWindow 3:");
            postDelayed(scrollrun, 1500);
            return;
        }
        Log.d(mck, "onAttachedToWindow 4:artlist 4:" + dgruning.r().sArtist.size());

        postDelayed(scrollrun, 5);
        Log.d(mck, "onAttachedToWindow 5:  after   handle 5: ");


    }



    /**
     * @param l
     * @param t
     * @param oldl
     * @param oldt 这个函数checkvisibility就好了, 完全没必要让轮询去判断
     *             这个函数需要log跟踪一下. .
     *             这个函数就没有唤起轮询的必要性.
     *             检查可见性, 还是放到轮询里面, 这个函数唤醒轮询.
     *             因为在一开始加载的时候, 就需要判断可见性????.
     *             不对, 一开始可以直接加载, 可见性就放到这里, 从轮询中删除可见性判断
     *             从这里删除轮询.
     */


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(mck, "::::onScrollChanged   1:");
        if (null == dgruning.r().sArtist) {
            //   dgruning.makeNshow(getContext(), "搜索中", Toast.LENGTH_SHORT).imageviewshowurlpicture();
            return;
        }
        /**
         *
         * 这个是否需要, notouch, 就是检查可见性是否就好了.
         * 注释掉, 试试.
         * 注释之后, 发现不行, 因为滑动到位之后, 就不调用这个函数了.
         * 再次注释试试.
         */
        checkVisibility();
//        Log.d(mck, "::::ontouch   2:");
     //   postDelayed(scrollrun, 5);

    }


    /**
     * 开始加载下一页的图片，每张图片都会开启一个异步线程去下载.
     * prepareart返回的值可以作判断用, .
     */
    public void loadMoreImages() {

        Log.d(mck, "loadmoreimages   0 size: " + dgruning.r().sArtist.size());

        /**
         * 设置点击事件. 传递一个art给后面.
         * 貌似在这里是合适的.// 重新理一下art数据.
         * 这里其实是下载图片用的, 不是现实图片用的.
         */
        if (!hasSDCard()) {
            dgruning.makeNshow(getContext(), "未发现SD卡", Toast.LENGTH_SHORT);
            return;
        }


        int startIndex = page * PAGE_SIZE;
        int endIndex = (page * PAGE_SIZE + PAGE_SIZE) > dgruning.r().sArtist.size() ? dgruning.r().sArtist.size() : (page * PAGE_SIZE + PAGE_SIZE);
        Log.d(mck, "loadmoreimages   1 star: " + startIndex + "   end: " + endIndex+ "    size: "+dgruning.r().sArtist.size());


        /**
         * 因为在runable里面已经判断了, 因此永远不会执行到这里.
         */
       /* if (startIndex >= dgruning.r().sArtist.size()) {
            dgruning.makeNshow(getContext(), "全部结果已展示", Toast.LENGTH_SHORT);
            return;
        }*/


        if (startIndex < dgruning.r().sArtist.size()) {
            dgruning.makeNshow(getContext(), "正在加载...", Toast.LENGTH_SHORT);

            for (int i = startIndex; i < endIndex; i++) {
                LoadImageTask task = new LoadImageTask();
                taskCollection.add(task);
                task.execute(dgruning.r().sArtist.get(i).getPicture_url());
            }
            page++;
        }
    }

    /**
     * 遍历imageViewList中的每张图片，对图片的可见性进行检查，如果图片已经离开屏幕可见范围，则将图片替换成一张空图。
     * 改为保留3倍屏幕尺寸.
     * 这个函数, 导致多次ondraw.
     */
    public void checkVisibility() {
        //这个也必须注释掉, 否则喷的内容太多.
        // Log.d(mck, "                     checkvisibility 1: ");

        for (int i = 0; i < imageViewList.size(); i++) {

            final ImageView iv = imageViewList.get(i);

            //  Log.d(mck, "                     checkvisibility 2: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

            int borderTop = (Integer) iv.getTag(R.string.border_top);////  5/31/16 崩溃在这里.因为没有settag, 直接读就崩溃了.
            int borderBottom = (Integer) iv
                    .getTag(R.string.border_bottom);

            /**
             * 关键代码还是在这里, 反复判断是否在屏幕范围内, 然后决定加载图片, 还是把图片替换掉.
             *  是否能加一个判断, 判断这个imageview是否改动过了.  正在弄.
             */
            if (borderBottom > (getScrollY() - scrollViewHeight * 4)
                    && borderTop < getScrollY() + scrollViewHeight * 5) {
                //  Log.d(mck, "                     checkvisibility 3: "+i+"   tag: "+imageView.getTag(R.string.isshowok));


                if ((boolean) iv.getTag(R.string.isshowok)) continue;
                //    Log.d(mck, "                     checkvisibility 4: "+i+"   tag: "+imageView.getTag(R.string.isshowok));
                imageviewshowurlpicture(iv, ""+iv.getTag(R.string.image_url));

                //   Log.d(mck, "                     checkvisibility 7: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

                iv.setTag(R.string.isshowok, true);
                //    Log.d(mck, "                     checkvisibility 8: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

            } else {
                //    Log.d(mck, "                     checkvisibility 9: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

                iv.setImageResource(R.drawable.empty_photo);
                iv.setTag(R.string.isshowok, false);
                //    Log.d(mck, "                     checkvisibility 10: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

                //// : 6/1/16 图片和空间回收利用机制, 再考虑.  ruhelru缓存.
            }
        }
    }

    /**
     * 用来在一个imageview显示图片的函数.
     * @param iv
     * 此处参数依赖于imageview有一个url, tag,
     * 严重不合理. 如果有bug, 就恨隐蔽, 应该修改
     * 已经改为双参数了.
     */
    private void imageviewshowurlpicture(imageviewNurl inu){
        Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(inu.url);
        if (bitmap != null) {
            inu.iv.setImageBitmap(bitmap);
        } else {
            /***
             * 这里的加载不仅仅包括从网络加载, 还包括从磁盘加载.
             */
            //   Log.d(mck, "                     checkvisibility 5: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

            LoadImageTask task = new LoadImageTask(inu);
            task.execute();
            //      Log.d(mck, "                     checkvisibility 6: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

        }


    }

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    private boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String mImageUrl;

        /**
         * 可重复使用的ImageView
         */
        private ImageView mImageView=null;

        public LoadImageTask() {
        }

        /**
         * 将可重复使用的ImageView传入
         * 这个task不仅仅是从网络下载, 从内存载入, 从硬盘载入都在这里,
         * 因此, 这里就是初始化imageview的地方.
         *
         * @param imageView
         */
        public LoadImageTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d(mck, "doinback:"+params[0]);

            mImageUrl = params[0];
            Bitmap imageBitmap = imageLoader
                    .getBitmapFromMemoryCache(mImageUrl);
            Log.d(mck, "doinback: 1: "+imageBitmap);
            if (imageBitmap == null) {
                imageBitmap = loadImage(mImageUrl);
            }
            Log.d(mck, "doinback: 2:h:"+imageBitmap.getHeight()+"      w:"+imageBitmap.getWidth());

            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(mck, "dopost: "+bitmap);
            if (bitmap != null) {
                double ratio = bitmap.getWidth() / (columnWidth * 1.0);
                Log.d(mck, "dopost: ratio: "+ratio);
                int scaledHeight = (int) (bitmap.getHeight() / ratio);
                addImage(bitmap, columnWidth, scaledHeight);
                //这个报错, 崩溃. // : 6/3/16
                Log.d(mck, "dopost sh:"+scaledHeight);
            }
            taskCollection.remove(this);
        }

        /**
         * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
         *
         * @param imageUrl 图片的URL地址
         * @return 加载到内存的图片。
         */
        private Bitmap loadImage(String imageUrl) {
            Log.d(mck, "loadimages : "+columnWidth);

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
         *
         * @param bitmap      待添加的图片
         * @param imageWidth  图片的宽度
         * @param imageHeight 图片的高度
         */
        private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
            Log.d(mck, "addimage h:"+imageHeight+"     w:"+imageWidth);

            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(imageWidth, imageHeight);


            if (mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                /**
                 * 这种情况仅仅发生在, load图片的线程中. 但是, 也挺魔幻的, 貌似不该这样写代码哈.
                 * 其实完全可以把这段挪出去, 所有的下载图片的线程, 都是弄好了图片再下载. 对的, 这样逻辑就通畅了.
                 * todo, 改成所有的图片下载都是有imageview的. 而且, imageview和url, 不该绑定.
                 *
                 */
                ImageView imageView = new ImageView(getContext());
//				imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ScaleType.FIT_XY);
                imageView.setPadding(5, 5, 5, 5);
                imageView.setTag(R.string.image_url, mImageUrl);
                imageView.setTag(R.string.isshowok, true);
                imageView.setId(id++);
                imageView.setOnClickListener(new imageviewonclicklistener());
                //findColumnToAdd(imageView, imageHeight).addView(imageView);注释掉得代码.
                addimageatposition(imageView, layoutParams);
                //这个报错, 崩溃. // : 6/3/16


                imageViewList.add(imageView);
            }
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
                if (null == dgruning.r().layoutartbigshow){
                    dgruning.r().layoutartbigshow = (FrameLayout)
                            LayoutInflater.from(context).inflate(R.layout.art_bigshow, null);
                }
                final ImageView i=(ImageView) dgruning.r().layoutartbigshow.findViewById(R.id.imageview_art_detail);
               // i.setImageBitmap(); //  6/12/16  应该加载各种关于art的东西, 不需要, url, 可以带走.
               // i.setTag(R.string.image_url, iurl);
//                i.setTag(R.string.art, a);
                imageviewshowurlpicture(i, iurl);
                Log.i(com.takungae.dagong30.mckScrollView.mck, "layoutwaterfall after: " + dgruning.r().layoutartbigshow);
                /**
                 * 加载界面.  .
                 */
                // hide the window title.
                ((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE);
                // hide the status bar and other OS-level chrome
                ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) context).setContentView(dgruning.r().layoutartbigshow);

            }
        }


        /**
         * 添加图片, 丑陋的要命, 这些代码.
         */
        private void addimageatposition(ImageView v, RelativeLayout.LayoutParams rl) {
            Log.d(mck, "addimageatposition");

            rl.setMargins(0, 0, 0, 0);
            rl.alignWithParent = true;
            /**
             * 这地方真心需要指针, 有了指针, 代码就不必如此丑陋.
             * 或者函数式也行.
             */
            Log.d(mck, " addimageatposition fistco 1:"+firstcolumn+"      h:"+firstColumnHeight);
            Log.d(mck, " addimageatposition sectco 1:"+secondcolumn+"      h:"+secondColumnHeight);
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
            Log.d(mck, " addimageatposition fistco 2:"+firstcolumn+"h:"+firstColumnHeight);
            Log.d(mck, " addimageatposition sectco 2:"+secondcolumn+"h:"+secondColumnHeight);

            //向relativelayout里面添加按钮. 如果移除某个控件: removeview


            //这句话应该是无效的, 因为下面一句话加了.
            //b.setLayoutParams(layoutParams);
            Log.d(mck, "addimageatposition r: " + rlscroll.getId() + "   v: " + v.getTag(R.string.image_url) + "   rl: " + rl);
            rlscroll.addView(v, rl);
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

}