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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * 自定义的ScrollView，在其中动态地对图片进行添加。
 * 
 * @author guolin
 */
public class mckScrollView extends ScrollView implements OnTouchListener {
    /**
     * log标签.
     */
    private static final String mck="------mcksv-------";
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
	private int firstColumnHeight=0;

	/**
	 * 当前第二列的高度
	 */
	private int secondColumnHeight=0;

	private static int firstcolumn;//第一列的最后一个id.
	private static int secondcolumn;//的二列的最后一个id.
	private static int id=100;//最后一个id.




	/**
	 * 当前第三列的高度
	 */
	//private int thirdColumnHeight;

	/**
	 * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
	 */
	private boolean loadOnce;

	/**
	 * 对图片进行管理的工具类
	 */
	private testwaterfall imageLoader;

	/**
	 * 第一列的布局
	 */
	//private LinearLayout firstColumn;

	/**
	 * 第二列的布局
	 */
	//private LinearLayout secondColumn;

	/**
	 * 第三列的布局
	 */
	//private LinearLayout thirdColumn;

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private static Set<LoadImageTask> taskCollection;

	/**
	 * MyScrollView下的直接子布局。
	 */
	private static View scrollLayout;

	/**
	 * MyScrollView布局的高度。
	 */
	private static int scrollViewHeight;



    /**
	 * 记录上垂直方向的滚动距离。
	 */
	private static int lastScrollY = -1;

	/**
	 * 记录所有界面上的图片，用以可以随时控制对图片的释放。
	 */
	private List<ImageView> imageViewList = new ArrayList<>();

	/**
	 * 在Handler中进行图片可见性检查的判断，以及加载更多图片的操作。
     * handler就是一个死循环.
	 */
	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
            Log.d(mck, ":::::::handler");
            mckScrollView myScrollView = (mckScrollView) msg.obj;
//			int scrollY = myScrollView.getScrollY();
			// 如果当前的滚动位置和上次相同，表示已停止滚动
            //停止滚动, 再加载, 是为了加载更顺畅妈?
            //保持5个以上的下载任务.
            Message message = new Message();
            message.obj = myScrollView;

            if ( taskCollection.size()<5) {
                myScrollView.loadMoreImages();

            }
            /**
             * 本来的逻辑是, 不加载图片的情况下, 才检查元素可见性.
             * 改为, 不论如何都检查元素的可见性.
             */
                myScrollView.checkVisibility();

                // 5毫秒后再次对滚动位置进行判断
                handler.sendMessageDelayed(message, 5);

            /*
			if (scrollY == lastScrollY) {
				// 当滚动的最底部，并且当前没有正在下载的任务时，开始加载下一页的图片
                //删除了滚到最底部: scrollViewHeight + scrollY >= scrollLayout.getHeight()                &&
				if ( taskCollection.isEmpty()) {
					myScrollView.loadMoreImages();
				}
				myScrollView.checkVisibility();
			} else {
				lastScrollY = scrollY;
				Message message = new Message();
				message.obj = myScrollView;
				// 5毫秒后再次对滚动位置进行判断
				handler.sendMessageDelayed(message, 5);
			}*/
		};

	};

	/**
	 * MyScrollView的构造函数。
	 * 
	 * @param context
	 * @param attrs
	 */
	public mckScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);
        Log.d(mck, ":::::::mckstrlllview constractor");

        imageLoader = testwaterfall.getInstance();
		taskCollection = new HashSet<LoadImageTask>();
		setOnTouchListener(this);

	}

	/**
	 * 进行一些关键性的初始化操作，获取MyScrollView的高度，以及得到第一列的宽度值。并在这里开始加载第一页的图片。
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);

        Log.d(mck, "::::onlayout");
//        if(null==dgruning.sArtist){

//        }

		if (changed && !loadOnce) {
			scrollViewHeight = getHeight();
			scrollLayout = getChildAt(0);
			/**
			 * 改成relative layout
			 */
			/*firstColumn = (LinearLayout) findViewById(R.id.first_column);
			secondColumn = (LinearLayout) findViewById(R.id.second_column);
			thirdColumn = (LinearLayout) findViewById(R.id.third_column);*/

			columnWidth = findViewById(R.id.rlwaterfall).getWidth()/2;
			loadOnce = true;
			loadMoreImages();
		}
	}

	/**
	 * 监听用户的触屏事件，如果用户手指离开屏幕则开始进行滚动检测。
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
        Log.d(mck, "::::ontouch");
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Message message = new Message();
			message.obj = this;
			handler.sendMessageDelayed(message, 5);
		}
		return false;
	}

    static boolean allshow=true;


	/**
	 * 开始加载下一页的图片，每张图片都会开启一个异步线程去下载。
	 */
	public void loadMoreImages() {
        Log.d(mck, "loadmoreimages"+dgruning.sArtist);
		/**
		 * 设置点击事件. 传递一个art给后面.
		 * 貌似在这里是合适的.//todo 重新理一下art数据.
         * 这里其实是下载图片用的, 不是现实图片用的.
		 */
		if (!hasSDCard()) {
            Toast.makeText(getContext(), "未发现SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
//                if(null==dgruning.sArtist)return;
//        Toast.makeText(getContext(), "搜索中", Toast.LENGTH_SHORT)
//                .show();
//        Log.d(mck, "搜索中");



        int startIndex = page * PAGE_SIZE;
			int endIndex = (page * PAGE_SIZE + PAGE_SIZE) > dgruning.sArtist.size() ? dgruning.sArtist.size() :  (page * PAGE_SIZE + PAGE_SIZE);
        int morestartindex=((page-5) * PAGE_SIZE>0) ? page * PAGE_SIZE : 0;
            int morendindex=((page+6) * PAGE_SIZE<dgruning.sArtist.size()) ? page * PAGE_SIZE : dgruning.sArtist.size();



        if(startIndex>=dgruning.sArtist.size()){
            if(allshow)	Toast.makeText(getContext(), "全部结果已展示", Toast.LENGTH_SHORT)
                    .show();
            allshow=false;
            return;
        }



        if (startIndex < dgruning.sArtist.size()) {
				Toast.makeText(getContext(), "正在加载...", Toast.LENGTH_SHORT)
						.show();

				for (int i = startIndex; i < endIndex; i++) {
					LoadImageTask task = new LoadImageTask();
					taskCollection.add(task);
					task.execute(dgruning.sArtist.get(i).getPicture_url());
				}
				page++;
			}
	}

	/**
	 * 遍历imageViewList中的每张图片，对图片的可见性进行检查，如果图片已经离开屏幕可见范围，则将图片替换成一张空图。
     * 改为保留3倍屏幕尺寸.
	 */
	public void checkVisibility() {
        Log.d(mck, "checkvisibility");

        for (int i = 0; i < imageViewList.size(); i++) {
			ImageView imageView = imageViewList.get(i);
			int borderTop = (Integer) imageView.getTag(R.string.border_top);////  5/31/16 崩溃在这里.因为没有settag, 直接读就崩溃了.
			int borderBottom = (Integer) imageView
					.getTag(R.string.border_bottom);

            /**
             * 关键代码还是在这里, 反复判断是否在屏幕范围内, 然后决定加载图片, 还是把图片替换掉.
             */
            if (borderBottom > (getScrollY()-scrollViewHeight*4)
					&& borderTop < getScrollY() + scrollViewHeight*5) {
				String imageUrl = (String) imageView.getTag(R.string.image_url);
				Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
                    /***
                     * 这里的加载不仅仅包括从网络加载, 还包括从磁盘加载.
                     */
					LoadImageTask task = new LoadImageTask(imageView);
					task.execute(imageUrl);
				}
			} else {
				imageView.setImageResource(R.drawable.empty_photo);
				//// TODO: 6/1/16 图片和空间回收利用机制, 再考虑.  ruhelru缓存.
			}
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
		private ImageView mImageView;

		public LoadImageTask() {
		}

		/**
		 * 将可重复使用的ImageView传入
		 * 
		 * @param imageView
		 */
		public LoadImageTask(ImageView imageView) {
			mImageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
            Log.d(mck, "doinback");

            mImageUrl = params[0];
			Bitmap imageBitmap = imageLoader
					.getBitmapFromMemoryCache(mImageUrl);
			if (imageBitmap == null) {
				imageBitmap = loadImage(mImageUrl);
			}
			return imageBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
            Log.d(mck, "dopost");

            if (bitmap != null) {
				double ratio = bitmap.getWidth() / (columnWidth * 1.0);
				int scaledHeight = (int) (bitmap.getHeight() / ratio);
				addImage(bitmap, columnWidth, scaledHeight);
			}
			taskCollection.remove(this);
		}

		/**
		 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 加载到内存的图片。
		 */
		private Bitmap loadImage(String imageUrl) {
            Log.d(mck, "loadimages");

            File imageFile = new File(getImagePath(imageUrl));
			if (!imageFile.exists()) {
				downloadImage(imageUrl);
			}
			if (imageUrl != null) {
				Bitmap bitmap = testwaterfall.decodeSampledBitmapFromResource(
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
		 * 
		 * @param bitmap
		 *            待添加的图片
		 * @param imageWidth
		 *            图片的宽度
		 * @param imageHeight
		 *            图片的高度
		 */
		private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
            Log.d(mck, "addimage");

            RelativeLayout.LayoutParams layoutParams =
					new RelativeLayout.LayoutParams( imageWidth,  imageHeight);


			if (mImageView != null) {
				mImageView.setImageBitmap(bitmap);
			} else {
				ImageView imageView = new ImageView(getContext());
//				imageView.setLayoutParams(layoutParams);
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setPadding(5, 5, 5, 5);
				imageView.setTag(R.string.image_url, mImageUrl);
				imageView.setId(id++);
				//findColumnToAdd(imageView, imageHeight).addView(imageView);注释掉得代码.
				addimageatposition(imageView, layoutParams);
				imageViewList.add(imageView);
			}
		}

		/**
		 * 添加图片, 丑陋的要命, 这些代码.
		 */
		private void addimageatposition(ImageView v, RelativeLayout.LayoutParams rl){
            Log.d(mck, "addimageatposition");

            RelativeLayout r=(RelativeLayout) findViewById(R.id.rlwaterfall);
			rl.setMargins(0, 0, 0, 0);
			rl.alignWithParent = true;
			/**
			 * 这地方真心需要指针, 有了指针, 代码就不必如此丑陋.
			 * 或者函数式也行.
			 */

			if (firstColumnHeight==0){
				rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				firstcolumn=v.getId();
				v.setTag(R.string.border_top, firstColumnHeight);
				firstColumnHeight+=rl.height;
				v.setTag(R.string.border_bottom, firstColumnHeight);
			}else if(secondColumnHeight==0){
				rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				rl.addRule(RelativeLayout.RIGHT_OF, firstcolumn);
				secondcolumn=v.getId();
				v.setTag(R.string.border_top, secondColumnHeight);
				secondColumnHeight+=rl.height;
				v.setTag(R.string.border_bottom, secondColumnHeight);
			}else if(firstColumnHeight<=secondColumnHeight){
				rl.addRule(RelativeLayout.BELOW, firstcolumn);
				firstcolumn=v.getId();
				v.setTag(R.string.border_top, firstColumnHeight);
				firstColumnHeight+=rl.height;
				v.setTag(R.string.border_bottom, firstColumnHeight);
			}else {
				rl.addRule(RelativeLayout.BELOW, secondcolumn);
				rl.addRule(RelativeLayout.RIGHT_OF, firstcolumn);
				secondcolumn=v.getId();
				v.setTag(R.string.border_top, secondColumnHeight);
				secondColumnHeight+=rl.height;
				v.setTag(R.string.border_bottom, secondColumnHeight);
			}


			//向relativelayout里面添加按钮. 如果移除某个控件: removeview


			//这句话应该是无效的, 因为下面一句话加了.
			//b.setLayoutParams(layoutParams);
			r.addView(v, rl);

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
		 * @param imageUrl
		 *            图片的URL地址。
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
                Log.d(mck, "url"+url+con);
                Log.d(mck, "resp:::"+con.getResponseCode());
                Log.d(mck, "errstr:::::"+con.getErrorStream());
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
				Bitmap bitmap = testwaterfall.decodeSampledBitmapFromResource(
						imageFile.getPath(), columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
				}
			}
		}

		/**
		 * 获取图片的本地存储路径。
		 * 
		 * @param imageUrl
		 *            图片的URL地址。
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