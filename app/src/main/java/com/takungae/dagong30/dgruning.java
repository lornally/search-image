package com.takungae.dagong30;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by m on 11/13/15.
 * 这个类的目标是保存运行期的各种状态, 顺便做token和device_id的持久化.
 * 使用这个类, 必须先做init, 这个方法会拿回持久化的参数.
 * 认真考虑是否把这个initSurfaceView改为构造函数.
 * todo 依赖注入貌似是解决方案. 目前是让mainactivty持有一个全局实例, 不是一个好的解决方案.
 * 然后才可以使用这个类.
 */
public class DgRuning {
    //这货和dgrunning的artlist完全等价, 因此, 应该注入进来. 目前是在dgruning的prepare声明了.
    public final ArrayList<Art> sArtist = new ArrayList<>();
    public final HashMap<String, Art> stringartHashMap = new LinkedHashMap<>();
    /**
     * 这个是瀑布流.
     *
     */
    MScrollView layoutwaterfall=null;

    /**
     * 艺术品详情.
     */
    FrameLayout layoutartbigshow = null;

    private final String mck = "--DgRuning--";
    public static final String uploadurl = "http://app.takungae.com/Api/Index/upload";
    public   String _token ;// 改回位nothing之后, 需要调试.
    public final String _device_id ;
    public final Context sContext ;
    public  String _weixinurl="";
    public  String _artstring="nothing";
    public final int screenHeight;
    public final  int screenWidth;

    /**
     * 准备结果: >0, 代表结果数, 并且代表结果正常.
     */
    public final static int mini=1;
    //public final static int NoEntriesFound=0;
    public final static int runing=-1;
    public final static int error=-2;

    //public Uri lUri ;//= null;


    public int finishprepare=DgRuning.runing;

    /**
     * 全局就用这一个toast, 提升toast的使用效率.
     */
    private static Toast toast;
//    public int height ;
//    public int width ;

    /**
     *
     */
    /*public void clearart(){
        sArtist.clear();
        //stringartHashMap=null;
    }*/
    /******
     * 准备好, 要显示的素材.
     * 这个东西要不要重复利用?
     * 这个地方对于集合的要求是:
     * 1, 要有顺序. 因为是搜索结果.
     * 2, 要能够从url映射过去. 因为到了后面, 我们要从url映射回art对象.
     * 貌似linkedhashmap是满足要求的. 这个思路不合适, 没有get(i)方法. 我还是另外建立一个set吧.
     * 只需要7个属性
     * art_name
     * author
     * illustrate
     * picture_url
     * picture_id
     * collection_id
     * thumb_url
     */
    public boolean prepareArts(String result) {
        //sArtist = null;
        //stringartHashMap=null;
        //ArrayList<Art> lArts = new ArrayList<>();
//        LinkedHashMap<String, Art> lhmarts = new LinkedHashMap<>();
        sArtist.clear();
        try {
            JSONObject jb = new JSONObject(result);
            JSONArray ja = jb.getJSONArray("data");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject j = ja.getJSONObject(i);
                Art a = new Art();
                a.setArt_name(j.optString("art_name"));
                Log.d(mck, ":::::artname::::" + a.getArt_name());
                a.setAuthor(j.optString("author"));
                Log.d(mck, ":::::author::::" + a.getAuthor());
//                a.setAuthor_introduce(j.optString("author_introduce"));
//                Log.d(mck, ":::::author introduce::::" + a.getAuthor_introduce());
                a.setIllustrate(j.optString("illustrate"));
//                Log.d(mck, ":::::illustrate:::" + a.getIllustrate());


                /**
                 * 图片模糊了, 因此我改了回来. 本来没问题, 是英文版那个版本引入的问题.
                 */

                a.setThumb_url(j.optString("thumb_url"));
                Log.d(mck, ":::::thumb_url:::" + a.getThumb_url()+a.getThumb_url().length());

                a.setPicture_url(j.optString("picture_url"));
                Log.d(mck, ":::::picture_url:::" + a.getPicture_url());


                a.setPicture_id(j.optString("picture_id"));
                Log.d(mck, ":::::picture_id:::" + a.getPicture_id());

                a.setCollection_id(j.optInt("collection_id"));

                stringartHashMap.put(a.getPicture_url(), a);
                sArtist.add(a);
            }
            //mArts=lArts;
            //stringartHashMap = lhmarts;

            //sArtist = lArts;
            //isprepare=true;
//            layoutwaterfall.hasnotresult=false;
//            sContext.getString(R.string.prepareartlist)=result;
            _artstring=result;
            PreferenceManager.getDefaultSharedPreferences(sContext).edit().putString("artstring", _artstring).apply();
            finishprepare=sArtist.size();
            Log.d(mck, " preparearts8  finishp: "+finishprepare);
            return true;
        } catch (Exception e) {
            //sArtist = null;
            //isprepare=true;
            //stringartHashMap = null;
//            layoutwaterfall.hasnotresult=false;
            Log.d(mck, "--------e------" + e.toString() + "   : " + sArtist);
            finishprepare=DgRuning.error;
            return false;
        }
    }

    /******
     * 准备好, 测试用的素材.
     * 优先从本地拿, 本地拿不到, 才从系统拿.
     */

    public void prepareDefaultArts() {
        //String s = sContext.getString(R.string.prepareartlist);
        //prepareArts(s);
        Log.d(mck, "::::::s:::::" );
        prepareArts(_artstring);
        //usedefaultunprepare=false;

    }

    public void makeNshow( final String text, final int duration) {
        ((Activity)MainActivity.cma).runOnUiThread(
                new Runnable() {
                    public void run() {
                        if (toast == null) {
                            //如果還沒有用過makeText方法，才使用


                            toast = android.widget.Toast.makeText(sContext, text, duration);


                        } else {
                            toast.setText(text);
                            toast.setDuration(duration);
                        }
                        toast.show();                    }
                }
        );


    }


    /**
     * 使用post方法.
     *
     * @param fileuri   文件地址.
     * @param pUrlParas 上传需要拼接的参数.
     * @param uploadurl 上传url.
     * @return string result to string.
     */
    public String posturlstring(Uri fileuri, String uploadurl, UrlPara... pUrlParas) {
        return new String(posturlbytes(fileuri, uploadurl, pUrlParas));
    }

    public byte[] posturlbytes(Uri fileuri, String uploadUrl, UrlPara... pUrlParas) {

        try {//准备参数....
            for (UrlPara p : pUrlParas) {
                uploadUrl = Uri.parse(uploadUrl).buildUpon().appendQueryParameter(p.getName(), p.getValue()).build().toString();
            }

//            Log.d(mck, "final uploadurl::::::" + uploadUrl);
//            String macid=params[2];
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";

            URL url = new URL(uploadUrl);
            HttpURLConnection lHttpURLConnection = (HttpURLConnection) url
                    .openConnection();
            Log.d(mck, "uploadurl::::::" + lHttpURLConnection.getURL());
            lHttpURLConnection.setDoInput(true);
            lHttpURLConnection.setDoOutput(true);
            lHttpURLConnection.setUseCaches(false);
            lHttpURLConnection.setRequestMethod("POST");
            lHttpURLConnection.setConnectTimeout(6 * 1000);
            lHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            lHttpURLConnection.setRequestProperty("Charset", "UTF-8");
            lHttpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            Log.d(mck, "-----dos1before::::" + fileuri);

            try (
                    DataOutputStream dos = new DataOutputStream(lHttpURLConnection
                            .getOutputStream())//// : 6/3/16 报错在这里.
            ) {


                Log.d(mck, "-----dos1::::" + dos);


                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"xxxxxxxx.jpg\"" + end);
                dos.writeBytes(end);

                if (null != fileuri) {
                    try (InputStream is = sContext.getContentResolver().openInputStream(fileuri)) {
                        int readby ;
                        byte[] bytes = new byte[100];
                        assert is!=null;

                        while ((readby = is.read(bytes)) > 0) {
                            dos.write(bytes, 0, readby);
                        }
                    }
                }


                dos.writeBytes(end);
                Log.d(mck, "writeend");

                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                Log.d(mck, "flush::::::" + dos.toString());

                dos.flush();
                Log.d(mck, "flushok:::::::" + dos.toString());


                int status = lHttpURLConnection.getResponseCode();


                Log.d(mck, "net status::::::::::" + status + "");
            }

            /******
             * 读取服务器的返回, 相似艺术作品的列表.
             */
            try (
                    InputStream error = lHttpURLConnection.getErrorStream();
                    InputStream is2 = lHttpURLConnection.getInputStream();
                    ByteArrayOutputStream bout = new ByteArrayOutputStream()

            ) {
                Log.d(mck, "error:::::::::" + error);
                //                Log.d(mck, "toto:::::"+lHttpURLConnection.getContentLength());

                Log.d(mck, "resmessage::::::::" + lHttpURLConnection.getResponseMessage());


                Log.d(mck, "-----is");//从这里到close要30多秒, 啥情况?

                int readby ;
                byte[] buffer = new byte[1024];

                while ((readby = is2.read(buffer)) > 0) {
                    bout.write(buffer, 0, readby);
                }
                Log.d(mck, "-----isclose----");
                lHttpURLConnection.disconnect();

                return bout.toByteArray();
                /******
                 * 这个很重要, 要在bout的其他操作之前, close或者flash他, 否则尾巴的数据就还在缓存中, 没有被写入.
                 */

                //                Log.d(mck,   "count:::::total" + total);

                /*****
                 * 解析服务器返回的json
                 */
                //                ArrayList<Art> arts = new ArrayList<>();
                //            return artlist.getInstance();//// :  这里返回artlist.
            }


        } catch (Exception e) {
            e.printStackTrace();

            Log.d(mck, "通用post连接程序失败" + e);
            return "网络连接失败".getBytes();

        }
    }


    /****
     * 单例
     * 貌似只能放弃单例.
     * 让activity持有一个final public的dgruning的变量了.
     */
   // private  static DgRuning ourInstance;// = new DgRuning(sContext);

    /**
     * 思考,怎样保证用户在使用r函数之前一定会使用init或者getinstance呢?  最好是借助编译器的力量
     *  依赖注入解决问题.
     * 如果没有init, 则抛出异常, 也是不错的解决方案.
     */
    /*public static DgRuning r() {
        //这就是一个单例, singleton

        return ourInstance;
    }
*/

    public DgRuning() {
        this.sContext = MainActivity.cma;

        /**
         * 这两个应该在构造函数搞定, 这样就只需要搞一次了.
         */
//        Log.d(mck, " onAttachedToWindow   2: height: " + screenHeight + "    width: " + columnWidth + "   rls:" + rlscroll);
        DisplayMetrics dm = this.sContext.getResources().getDisplayMetrics(); //就是崩溃在这里.
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;



        String temp_device_id =    PreferenceManager.getDefaultSharedPreferences(sContext).getString("device_id", "nothing");
        _token =        PreferenceManager.getDefaultSharedPreferences(sContext).getString("token", "nothing");
        _artstring=     PreferenceManager.getDefaultSharedPreferences(sContext).getString("artstring","nothing");

        if(_artstring.equals("nothing")){
            _artstring=sContext.getString(R.string.prepareartlist);
            PreferenceManager.getDefaultSharedPreferences(sContext).edit().putString("artstring", _artstring).apply();

        }
        //这段是拿到token:
        Log.d(mck, "ltoken before return::::" + _token);
        if (!(_token.equals("nothing"))){
            _device_id=temp_device_id;
            return;//保证拿token这件事只执行一次. 第二次就不会被执行了.
        }
        if (temp_device_id.equals("nothing")) {
            temp_device_id = "" + UUID.randomUUID();
            PreferenceManager.getDefaultSharedPreferences(sContext).edit().putString("device_id", temp_device_id).apply();
        }
        _device_id=temp_device_id;

        Log.d(mck, "+++++++++++should not get here, if not first run++++++++++");
        tokentask lTokentask = new tokentask();
        lTokentask.execute(_device_id + "", "http://app.takungae.com/Api/Device/getToken");
    }




    /******
     * init是真正的初始化函数, 在fragment的oncreateview使用, 因为需要getactivity作为context参数.
     * 在主线程 调用新的线程, 拿到token.
     * 拿到application的context最靠谱了
     * 一次程序启动, 只调用一次这个.
     * <p/>
     * //
     */

   /* public void init(Context c){//}, int w, int h) {
//        width=w;
//        height=h;

        init_token_deviceid(c);


    }
*/
    /**
     * 会被直接调用的init函数, 系统相机使用的时候, 就需要直接调用这个初始化函数.
     *
     * @param c
     */
    /*public void init_token_deviceid(Context c) {
        sContext = c.getApplicationContext();

        //拿到application的context最靠谱了.

        _device_id =    PreferenceManager.getDefaultSharedPreferences(c).getString("device_id", "nothing");
        _token =        PreferenceManager.getDefaultSharedPreferences(c).getString("token", "nothing");
        _artstring=     PreferenceManager.getDefaultSharedPreferences(c).getString("artstring","nothing");

        if(_artstring.equals("nothing")){
            _artstring=sContext.getString(R.string.prepareartlist);
            PreferenceManager.getDefaultSharedPreferences(c).edit().putString("artstring", _artstring).apply();

        }
        //这段是拿到token:
        Log.d(mck, "ltoken before return::::" + _token);
        if (!(_token.equals("nothing"))) return;//保证拿token这件事只执行一次. 第二次就不会被执行了.
        if (_device_id.equals("nothing")) {
            _device_id = "" + UUID.randomUUID();
            PreferenceManager.getDefaultSharedPreferences(c).edit().putString("device_id", _device_id).apply();
        }

        Log.d(mck, "+++++++++++should not get here, if not first run++++++++++");
        tokentask lTokentask = new tokentask();
        lTokentask.execute(_device_id + "", "http://app.takungae.com/Api/Device/getToken");
        //三个参数, uploadurl, mac地址, deviceid.
        //        改为使用uuid代替
    }*/


    /*****
     * 得到token 构造函数限制了, 只有第一次打开程序才执行一次.
     */

    private class tokentask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String deviceid = params[0];
            String uploadUrl = params[1];
            UrlPara p = new UrlPara("device_id", deviceid);
            Log.d(mck, "deviceid:::::" + deviceid + ":::::::tokenurl::::::" + uploadUrl);
            try {
                JSONObject lJSONObject = new JSONObject(geturlstring(uploadUrl, p));
                Log.d(mck, "-jasontostring-" + lJSONObject.toString() + "-data-" + lJSONObject.optString("data") + "-token-" + lJSONObject.optJSONObject("data").optString("token"));
                _token = lJSONObject.optJSONObject("data").optString("token");

                Log.d(mck, "token::::::::" + _token);
                if (null == _token || _token.length() == 0)
                    return "token 失败";//// 之前token为空, 是小董的bug, 返回的json串格式错误.
                PreferenceManager.getDefaultSharedPreferences(sContext).edit().putString("token", _token).apply();
                Log.d(mck, "token success");
                return "token成功";
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(mck, "token fail" + e);
                return "token失败";
            }
        }
    }

    public String geturlstring(String uploadurl, UrlPara... pUrlParas) {
        return new String(geturlbytes(uploadurl, pUrlParas));
    }

    /**
     * 不能使用post方法, 必须是get方法拿图片.
     *
     * @param uploadUrl address
     * @param pUrlParas parameter pair.
     * @return byte[] 或许改成arraylist更好.
     */

    public byte[] geturlbytes(String uploadUrl, UrlPara... pUrlParas) {
        try {//准备参数....
            for (UrlPara p : pUrlParas) {
                uploadUrl = Uri.parse(uploadUrl).buildUpon().appendQueryParameter(p.getName(), p.getValue()).build().toString();
            }
//            Log.d(mck, "final uploadurl::::::" + uploadUrl);
//            String macid=params[2];
//            String end = "\r\n";
//            String twoHyphens = "--";
//            String boundary = "******";

            URL url = new URL(uploadUrl);
            HttpURLConnection lHttpURLConnection = (HttpURLConnection) url
                    .openConnection();

            Log.d(mck, "uploadurl::::::" + lHttpURLConnection.getURL());

            int status = lHttpURLConnection.getResponseCode();

            Log.d(mck, "net status::::::::::" + status + "");
            InputStream error = lHttpURLConnection.getErrorStream();
            Log.d(mck, "net error:::::::::" + error);
            InputStream in = lHttpURLConnection.getInputStream();

            int i ;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
//                Log.d(mck, "iii:::" + mViewById);
            }
            out.close();
//            Log.d(mck, "out:::::::" + out.toString());
            lHttpURLConnection.disconnect();
//            Log.d(mck, "---------::::::" + (new String(out.toByteArray())) + "--------");
            //上面的代码拿到了json格式的token. 下面我们存储到shared preference
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(mck, "通用连接程序失败" + e);
            return "网络连接失败".getBytes();

        }
    }

    /**
     * 把log保留在文件上传回来的方法.
     *
     * @return 两句话的写法:
     * File file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()));
     * Runtime.getRuntime().exec("logcat -d -v time -f " + file.getAbsolutePath());}catch (IOException e){}
     */
/*
    public File extractLogToFileAndWeb() {
        //set a file
        Date datum = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        String fullName = df.format(datum) + "appLog.log";
        File file = new File(Environment.getExternalStorageDirectory(), fullName);

        //clears a file
        if (file.exists()) {
            file.delete();
        }


        //write log to file
        int pid = android.os.Process.myPid();
        try {
            String command = String.format("logcat -d -v threadtime *:*");
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String currentLine = null;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine != null && currentLine.contains(String.valueOf(pid))) {
                    result.append(currentLine);
                    result.append("\n");
                }
            }

            FileWriter out = new FileWriter(file);
            out.write(result.toString());
            out.close();

            //Runtime.getRuntime().exec("logcat -d -v time -f "+file.getAbsolutePath());
        } catch (IOException e) {
            // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Log.d(mck, "" + e);
        }

        //clear the log
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            Log.d(mck, "" + e);
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        return file;
    }
*/

}
