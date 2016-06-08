package com.takungae.dagong30;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by m on 11/13/15.
 * 这个类的目标是保存运行期的各种状态, 顺便做token和device_id的持久化.
 * 使用这个类, 必须先做init, 这个方法会拿回持久化的参数.
 * 认真考虑是否把这个initSurfaceView改为构造函数.
 * todo 依赖注入貌似是解决方案.
 * 然后才可以使用这个类.
 */
public class dgruning {
    //这货和dgrunning的artlist完全等价, 因此, 应该注入进来. 目前是在dgruning的prepare声明了.
    public ArrayList<art> sArtist = null;
    public HashMap<String, art> stringartHashMap = null;
    /**
     * 这个是瀑布流.
     *
     * @param v
     */
    mckScrollView layoutwaterfall;

    /**
     * 艺术品详情.
     */
    FrameLayout layoutartbigshow = null;

    private final String mck = "--dgruning--";

    public static final String url = "http://app.takungae.com/Api/Index/upload";

    public static String _token = "nothing";// 改回位nothing之后, 需要调试.
    public static String _device_id = "";
    public static Context sContext;

    /**
     * 全局就用这一个toast, 提升toast的使用效率.
     */
    private static Toast toast;


    /******
     * 准备好, 要显示的素材.
     * 这个东西要不要重复利用?
     * 这个地方对于集合的要求是:
     * 1, 要有顺序. 因为是搜索结果.
     * 2, 要能够从url映射过去. 因为到了后面, 我们要从url映射回art对象.
     * 貌似linkedhashmap是满足要求的. 这个思路不合适, 没有get(i)方法. 我还是另外建立一个set吧.
     */
    public boolean prepareArts(String result) {
        sArtist = null;
        ArrayList<art> lArts = new ArrayList<>();
        LinkedHashMap<String, art> lhmarts = new LinkedHashMap<>();
        try {
            JSONObject jb = new JSONObject(result);
            JSONArray ja = jb.getJSONArray("data");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject j = ja.getJSONObject(i);
                art a = new art();
                a.setArt_name(j.optString("art_name"));
                Log.d(mck, ":::::artname::::" + a.getArt_name());
                a.setAuthor(j.optString("author"));
                Log.d(mck, ":::::author::::" + a.getAuthor());
//                a.setAuthor_introduce(j.optString("author_introduce"));
//                Log.d(mck, ":::::author introduce::::" + a.getAuthor_introduce());
                a.setIllustrate(j.optString("illustrate"));
//                Log.d(mck, ":::::illustrate:::" + a.getIllustrate());
                a.setPicture_url(j.optString("picture_url"));
                Log.d(mck, ":::::picture_url:::" + a.getPicture_url());
                a.setPicture_id(j.optString("picture_id"));
                Log.d(mck, ":::::picture_id:::" + a.getPicture_id());

                a.setCollection_id(j.optInt("collection_id"));

                a.setThumb_url(j.optString("thumb_url"));
                Log.d(mck, ":::::thumb_url:::" + a.getThumb_url());
                //和picture的内容是一样的, 因此为了避免混淆, 我注释掉了.
                lhmarts.put(a.getPicture_url(), a);
                lArts.add(a);
            }
            //mArts=lArts;
            stringartHashMap = lhmarts;
            sArtist = lArts;
            //isprepare=true;
//            layoutwaterfall.hasnotresult=false;
            return true;
        } catch (Exception e) {
            sArtist = null;
            //isprepare=true;
            stringartHashMap = null;
//            layoutwaterfall.hasnotresult=false;
            Log.d(mck, "--------e------" + e.toString() + "   : " + sArtist);
            return false;
        }
    }

    /******
     * 准备好, 测试用的素材.
     */

    public void prepareDefaultArts() {
        String s = sContext.getString(R.string.prepareartlist);
        Log.d(mck, "::::::s:::::" + s);
        prepareArts(s);
        //usedefaultunprepare=false;

    }

    public static void makeNshow(final Context context, final String text, final int duration) {
        if (toast == null) {
            //如果還沒有用過makeText方法，才使用
            toast = android.widget.Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }


    /**
     * 使用post方法.
     *
     * @param fileuri   文件地址.
     * @param pUrlparas 上传需要拼接的参数.
     * @param uploadurl 上传url.
     * @return
     */
    public String posturlstring(Uri fileuri, String uploadurl, urlpara... pUrlparas) {
        return new String(posturlbytes(fileuri, uploadurl, pUrlparas));
    }

    public byte[] posturlbytes(Uri fileuri, String uploadUrl, urlpara... pUrlparas) {

        try {//准备参数....
            for (urlpara p : pUrlparas) {
                uploadUrl = Uri.parse(uploadUrl).buildUpon().appendQueryParameter(p.getName(), p.getValue()).build().toString();
            }

//            Log.d(mck, "final url::::::" + uploadUrl);
//            String macid=params[2];
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";

            URL url = new URL(uploadUrl);
            HttpURLConnection lHttpURLConnection = (HttpURLConnection) url
                    .openConnection();
            Log.d(mck, "url::::::" + lHttpURLConnection.getURL());
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
                            .getOutputStream());//// : 6/3/16 报错在这里.
            ) {


                Log.d(mck, "-----dos1::::" + dos);


                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"xxxxxxxx.jpg\"" + end);
                dos.writeBytes(end);

                if (null != fileuri) {
                    try (InputStream is = sContext.getContentResolver().openInputStream(fileuri)) {
                        int readby = 0;
                        byte[] bytes = new byte[100];
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

                int readby = 0;
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
                //                ArrayList<art> arts = new ArrayList<>();
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
     */
    private final static dgruning ourInstance = new dgruning();

    /**
     * 思考,怎样保证用户在使用r函数之前一定会使用init或者getinstance呢?  最好是借助编译器的力量
     * todo 依赖注入解决问题.
     *
     * @return
     */
    public static dgruning r() {
        //这就是一个单例, singleton
        return ourInstance;
    }


    private dgruning() {


    }


    /******
     * init是真正的初始化函数, 在fragment的oncreateview使用, 因为需要getactivity作为context参数.
     * 在主线程 调用新的线程, 拿到token.
     * 拿到application的context最靠谱了
     * <p/>
     * //
     */

    public void init(Context c) {
        init_token_deviceid(c);
    }

    /**
     * 会被直接调用的init函数, 系统相机使用的时候, 就需要直接调用这个初始化函数.
     *
     * @param c
     */
    public void init_token_deviceid(Context c) {
        sContext = c.getApplicationContext();
        //拿到application的context最靠谱了.

        _device_id = PreferenceManager.getDefaultSharedPreferences(c).getString("device_id", "nothing");
        _token = PreferenceManager.getDefaultSharedPreferences(c).getString("token", "nothing");
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
        //三个参数, url, mac地址, deviceid.
        //        改为使用uuid代替
    }


    /*****
     * 得到token 构造函数限制了, 只有第一次打开程序才执行一次.
     */

    private class tokentask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String deviceid = params[0];
            String uploadUrl = params[1];
            urlpara p = new urlpara("device_id", deviceid);
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

    public static class urlpara {
        private final String name;
        private final String value;

        public urlpara(String pName, String pValue) {
            name = pName;
            value = pValue;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public String geturlstring(String uploadurl, urlpara... pUrlparas) {
        return new String(geturlbytes(uploadurl, pUrlparas));
    }

    /**
     * 不能使用post方法, 必须是get方法拿图片.
     *
     * @param uploadUrl
     * @param pUrlparas
     * @return
     */

    public byte[] geturlbytes(String uploadUrl, urlpara... pUrlparas) {
        try {//准备参数....
            for (urlpara p : pUrlparas) {
                uploadUrl = Uri.parse(uploadUrl).buildUpon().appendQueryParameter(p.getName(), p.getValue()).build().toString();
            }
//            Log.d(mck, "final url::::::" + uploadUrl);
//            String macid=params[2];
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";

            URL url = new URL(uploadUrl);
            HttpURLConnection lHttpURLConnection = (HttpURLConnection) url
                    .openConnection();

            Log.d(mck, "url::::::" + lHttpURLConnection.getURL());

            int status = lHttpURLConnection.getResponseCode();

            Log.d(mck, "net status::::::::::" + status + "");
            InputStream error = lHttpURLConnection.getErrorStream();
            Log.d(mck, "net error:::::::::" + error);
            InputStream in = lHttpURLConnection.getInputStream();

            int i = 0;
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

}
