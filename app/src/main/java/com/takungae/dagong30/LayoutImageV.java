package com.takungae.dagong30;


/**
 * Created by m on 6/21/16.
 *比较关键的接口. 线程下载好图片之后, 用来回调view的插入
 */
public interface LayoutImageV {

    /**
     * 用来设置有几列.
     * @return 列数. 实现接口的view可以控制自己有几列.
     */
    int getColumnWidth();

    /**
     * 把图片查到合适的位置, 实现接口的view, 可以把得到了bitmap的imageview插入到合适的位置.
     * @param v 得到的imageview和url对.
     */
    void addimageatposition(ImageviewNurl v);

}
