package com.takungae.dagong30;

import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;

/**
 * Created by m on 11/11/15.
 * 本来想用parcelable解决问题, 可是, 那个drawale有无法同时声明为parcelable, 因此, 还是用回serialble
 * 改为单个activity方案, 这个都不需要了.
 */
public class Art implements Serializable {



   /* @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable((Parcelable)mDrawable,0);
        dest.writeString(illustrate);

    }
    public static final Parcelable.Creator<Art> CREATOR
            = new Parcelable.Creator<Art>() {
        public Art createFromParcel(Parcel in) {
            Art lArt= new Art();
            lArt.mDrawable=(Drawable)(in.readParcelable(null));
            lArt.illustrate=in.readString();
            return lArt;
        }

        public Art[] newArray(int size) {
            return new Art[size];
        }
    };
*/

    /**用静态方法拿回来不可取, 我使用了dgruning的静态变量来临时保存.
     *
     *//*
    transient private BitmapDrawable mDrawable;
    private boolean hasResultDrawable =false;

    public boolean isHasResultDrawable() {
        return hasResultDrawable;
    }

    public void setHasResultDrawable(boolean pHasResultDrawable) {
        hasResultDrawable = pHasResultDrawable;
    }*/

    /**
     * 初始化默认值. 因此不改变hasResultdrawable属性
     * @param pDrawable
     * 无论如何, 都应该赋值成功, 不应该不赋值.
     * 我错了, 还真应该不赋值, 因为唯一的调用是搞定默认的图片: [图片加载中]
     * 这个调用也已经取消了. 此函数为废弃函数.
     */
//    public void initDrawable(BitmapDrawable pDrawable) {
//        if(hasResultDrawable)return;
//        mDrawable = pDrawable;
//    }
//    public BitmapDrawable getDrawable() {
//        return mDrawable;
//    }

    /**
     * 设置实际值因此改变hasResultdrawbale属性
     * @param pDrawable
     */
//    public void setDrawable(BitmapDrawable pDrawable) {
//        mDrawable = pDrawable;
//        hasResultDrawable =true;
//    }
    private String picture_id ="423642";
    private String art_id="439934";
    private String page_url="";
    private String picture_url="http://app.takungae.com:8080/group1/M00/30/0E/wKggP1YxQFKAc7e6AAn5hO_izKo714.jpg";
    private String local_picture_file="";
    private String local_picture_file_size="";
    private String thumb_picture_file="";
    private String download_time="2015-10-28 21:38:57";
    private String http_code="0";
    private String picture_index="19d81084-0c5a-45e5-9dd8-7776cf931de6";
    private String group="group1";
    private String filename="M00/30/0E/wKggP1YxQFKAc7e6AAn5hO_izKo714.jpg";
    private String audit_status="0";
    private String art_url="http://auction.artron.net/paimai-art0006520041/";
    private String art_name="山水 镜片 设色纸本 ";
    private String art_time="暂无";
    private String author="卢禹舜";
    private String author_url="http://artso.artron.net/auction/search_auction.php?keyword=%E5%8D%A2%E7%A6%B9%E8%88%9C";
    private String author_id="0";
    private String author_introduce="1962~  现任中国国家画院常务副院长、院务委员、中国艺术研究院博士生导师、哈尔滨师范大学副校长、全国政协委员、中国美术家协会理事、中国画艺委会副主任、中国画学会副会长，第二届“全国中青年德艺双馨文艺工作者”、中宣部四个一批人才，有突出贡献优秀专家，享受国务院政府特殊津贴。\r";
    private String art_size="69×66cm";
    private String gallery_name="";
    private String gallery_url="";
    private String art_picture="[\"http://img3.artron.net/auction/2011/art000652/d/art0006520041.jpg\"]";
    private String auction_time="2011-10-30";
    private String auction_address="辽宁建投拍卖公司";
    private String deal_price="流拍";
    private String art_classify="中国书画>绘画";
    private String estimate_price="RMB 　50;000-50;000";
    private String special_performance="中国书画大家专场";
    private String auction_company="辽宁建投拍卖公司";
    private String auction_meeting="2011秋季艺术品拍会";
    private String illustrate="作者：卢禹舜\n尺寸：69×66cm\n\n艺术品名称：山水 镜片 设色纸本 \n创作时间：暂无\n作品分类：中国书画>绘画\n估价：RMB 　50;000-50;000";
    private String storage_address="";
    private String release_date="2015-08-10 18:26:47";
    private String author_picture="http://img3.artron.net/artist/A0000172/2012052116095887724.jpg";
    private String author_birthday="1962~";
    private String thumb_url="http://app.takungae.com:8080/group1/M00/30/0E/wKggP1YxQFKAc7e6AAn5hO_izKo714.jpg";
    private int collection_id=0;



    public String getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(String pPicture_id) {
        picture_id = pPicture_id;
    }

    public String getArt_id() {
        return art_id;
    }

    public void setArt_id(String pArt_id) {
        art_id = pArt_id;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String pPage_url) {
        page_url = pPage_url;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String pPicture_url) {
        picture_url = pPicture_url;
    }

    public String getLocal_picture_file() {
        return local_picture_file;
    }

    public void setLocal_picture_file(String pLocal_picture_file) {
        local_picture_file = pLocal_picture_file;
    }

    public String getLocal_picture_file_size() {
        return local_picture_file_size;
    }

    public void setLocal_picture_file_size(String pLocal_picture_file_size) {
        local_picture_file_size = pLocal_picture_file_size;
    }

    public String getThumb_picture_file() {
        return thumb_picture_file;
    }

    public void setThumb_picture_file(String pThumb_picture_file) {
        thumb_picture_file = pThumb_picture_file;
    }

    public String getDownload_time() {
        return download_time;
    }

    public void setDownload_time(String pDownload_time) {
        download_time = pDownload_time;
    }

    public String getHttp_code() {
        return http_code;
    }

    public void setHttp_code(String pHttp_code) {
        http_code = pHttp_code;
    }

    public String getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(String pPicture_index) {
        picture_index = pPicture_index;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String pGroup) {
        group = pGroup;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String pFilename) {
        filename = pFilename;
    }

    public String getAudit_status() {
        return audit_status;
    }

    public void setAudit_status(String pAudit_status) {
        audit_status = pAudit_status;
    }

    public String getArt_url() {
        return art_url;
    }

    public void setArt_url(String pArt_url) {
        art_url = pArt_url;
    }

    public String getArt_name() {
        return art_name;
    }

    public void setArt_name(String pArt_name) {
        art_name = pArt_name;
    }

    public String getArt_time() {
        return art_time;
    }

    public void setArt_time(String pArt_time) {
        art_time = pArt_time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String pAuthor) {
        author = pAuthor;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String pAuthor_url) {
        author_url = pAuthor_url;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String pAuthor_id) {
        author_id = pAuthor_id;
    }

    public String getAuthor_introduce() {
        return author_introduce;
    }

    public void setAuthor_introduce(String pAuthor_introduce) {
        author_introduce = pAuthor_introduce;
    }

    public String getArt_size() {
        return art_size;
    }

    public void setArt_size(String pArt_size) {
        art_size = pArt_size;
    }

    public String getGallery_name() {
        return gallery_name;
    }

    public void setGallery_name(String pGallery_name) {
        gallery_name = pGallery_name;
    }

    public String getGallery_url() {
        return gallery_url;
    }

    public void setGallery_url(String pGallery_url) {
        gallery_url = pGallery_url;
    }

    public String getArt_picture() {
        return art_picture;
    }

    public void setArt_picture(String pArt_picture) {
        art_picture = pArt_picture;
    }

    public String getAuction_time() {
        return auction_time;
    }

    public void setAuction_time(String pAuction_time) {
        auction_time = pAuction_time;
    }

    public String getAuction_address() {
        return auction_address;
    }

    public void setAuction_address(String pAuction_address) {
        auction_address = pAuction_address;
    }

    public String getDeal_price() {
        return deal_price;
    }

    public void setDeal_price(String pDeal_price) {
        deal_price = pDeal_price;
    }

    public String getArt_classify() {
        return art_classify;
    }

    public void setArt_classify(String pArt_classify) {
        art_classify = pArt_classify;
    }

    public String getEstimate_price() {
        return estimate_price;
    }

    public void setEstimate_price(String pEstimate_price) {
        estimate_price = pEstimate_price;
    }

    public String getSpecial_performance() {
        return special_performance;
    }

    public void setSpecial_performance(String pSpecial_performance) {
        special_performance = pSpecial_performance;
    }

    public String getAuction_company() {
        return auction_company;
    }

    public void setAuction_company(String pAuction_company) {
        auction_company = pAuction_company;
    }

    public String getAuction_meeting() {
        return auction_meeting;
    }

    public void setAuction_meeting(String pAuction_meeting) {
        auction_meeting = pAuction_meeting;
    }

    public String getIllustrate() {
        return illustrate;
    }

    public void setIllustrate(String pIllustrate) {
        illustrate = pIllustrate;
    }

    public String getStorage_address() {
        return storage_address;
    }

    public void setStorage_address(String pStorage_address) {
        storage_address = pStorage_address;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String pRelease_date) {
        release_date = pRelease_date;
    }

    public String getAuthor_picture() {
        return author_picture;
    }

    public void setAuthor_picture(String pAuthor_picture) {
        author_picture = pAuthor_picture;
    }

    public String getAuthor_birthday() {
        return author_birthday;
    }

    public void setAuthor_birthday(String pAuthor_birthday) {
        author_birthday = pAuthor_birthday;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String pThumb_url) {
        thumb_url = pThumb_url;
    }

    public int getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(int pCollection_id) {
        collection_id = pCollection_id;
    }



}
