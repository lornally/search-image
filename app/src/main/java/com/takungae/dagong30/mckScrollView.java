package com.takungae.dagong30;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
     * 这个id有用的, 布局时, 需要制定blow, 以及leftalign.
     */
    //private int id = 100;//最后一个id.


    /**
     * 对图片进行管理的工具类
     */
    private waterfallimageload imageLoader;


    /**
     * ScrollView下的直接子布局,增删都在这里.
     */
    public RelativeLayout rlscroll;

    /**
     * MyScrollView布局的高度。
     */
    private int scrollViewHeight;
    private int scrollViewWidth;


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
//        Log.d(mck, " onAttachedToWindow   2: height: " + scrollViewHeight + "    width: " + columnWidth + "   rls:" + rlscroll);
        DisplayMetrics dm =getResources().getDisplayMetrics();



        scrollViewHeight = dm.heightPixels;
        scrollViewWidth = dm.widthPixels;
       // Log.d(mck, " onAttachedToWindow   3: height: " + scrollViewHeight + "    width: " + columnWidth + "   rls:" + rlscroll);


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
            Log.d(mck, "runable hasnotallshow:  3:  " + "    tasksize: " + LoadImageTask.taskCollection.size());
            Log.d(mck, "runable hasnotallshow:  4:  page:" + page*PAGE_SIZE+"    artsize: " + dgruning.r().sArtist.size());

            if(page * PAGE_SIZE >= dgruning.r().sArtist.size()){
                dgruning.makeNshow(getContext(), "全部结果已展示", Toast.LENGTH_SHORT);

                return;
            }
            else if (LoadImageTask.taskCollection.size() < 5) {
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
        LoadImageTask.firstColumnHeight = 0;
        LoadImageTask.secondColumnHeight = 0;
       // id=100;
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
         * 这个地方要检查, 如果还没有load好图片, 就不清理缓存. 因为清理缓存的方式太粗暴了.
         * 还是去checkvisibility改.
         */
//        if(page * PAGE_SIZE >= dgruning.r().sArtist.size())
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

                /**
                 * 这种情况仅仅发生在, load图片的线程中. 但是, 也挺魔幻的, 貌似不该这样写代码哈.
                 * 其实完全可以把这段挪出去, 所有的下载图片的线程, 都是弄好了图片再下载. 对的, 这样逻辑就通畅了.
                 * todo, 改成所有的图片下载都是有imageview的. 而且, imageview和url, 不该绑定.
                 *
                 */
                final ImageView imageView = new ImageView(getContext());
                final imageviewNurl inu=new imageviewNurl(imageView, dgruning.r().sArtist.get(i).getPicture_url());
//				imageView.setLayoutParams(layoutParams);
                imageViewList.add(imageView);
                LoadImageTask task = new LoadImageTask(this, inu, scrollViewWidth/2);//// TODO: 6/12/16
                LoadImageTask.taskCollection.add(task);
                task.execute();
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

              Log.d(mck, "                     checkvisibility 2: "+i+"   tag: "+iv.getTag(R.string.isshowok)+"      border-top:"+iv.getTag(R.string.border_top));

            if(iv.getTag(R.string.isshowok)==null)return;


            int borderTop = (Integer) iv.getTag(R.string.border_top);////  5/31/16 崩溃在这里.因为没有settag, 直接读就崩溃了.
            //6.14, 为啥. 又崩溃.
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
                final  imageviewNurl inu=new imageviewNurl(iv,""+iv.getTag(R.string.image_url) );
                //    Log.d(mck, "                     checkvisibility 4: "+i+"   tag: "+imageView.getTag(R.string.isshowok));
                imageviewshowurlpicture(inu);

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
     * @param inu
     * 此处参数依赖于imageview有一个url, tag,
     * 严重不合理. 如果有bug, 就恨隐蔽, 应该修改
     * 已经改为双参数了.
     */
    public void imageviewshowurlpicture(imageviewNurl inu){
        Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(inu.url);
        if (bitmap != null) {
            inu.iv.setImageBitmap(bitmap);
        } else {
            /***
             * 这里的加载不仅仅包括从网络加载, 还包括从磁盘加载.
             */
            //   Log.d(mck, "                     checkvisibility 5: "+i+"   tag: "+imageView.getTag(R.string.isshowok));

            LoadImageTask task = new LoadImageTask(this, inu, scrollViewWidth);
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

}