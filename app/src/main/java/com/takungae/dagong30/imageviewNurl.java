package com.takungae.dagong30;

import android.widget.ImageView;

/**
 * Created by m on 6/12/16.
 * 不仅仅有imageview和url. 貌似还应该组合一个显示位置.
 */
public class ImageviewNurl {
    public final ImageView iv;
    public final String url;

    public ImageviewNurl(ImageView iv, String url) {
        this.iv = iv;
        this.url = url;
    }

}
