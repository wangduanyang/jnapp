package com.dc.jnapp_v1;

import java.io.Serializable;

public class Info implements Serializable  
{  
    private static final long serialVersionUID = -758459502806858414L;  
    /** 
     * 纬度 
     */  
    private double latitude;  
    /** 
     * 经度 
     */  
    private double longitude;  
    /** 
     * 图片ID，真实项目中可能是图片路径 
     */  
    private int imgId;  
    /** 
     * 商家名称 
     */  
    private String name;
    //msg
    private String msg;
    /** 
     * 距离 
     */  
    private String distance;  
    /** 
     * 赞数量 
     */  
    private int zan;  
    /**
     * 是否显示标志
     */
    public boolean fshow=true;
  
/*    public static List<Info> infos = new ArrayList<Info>();  
  
    static  
    {  
        infos.add(new Info(34.242652, 108.971171, R.drawable.ic_launcher, "英伦贵族小旅馆",  
                "距离209米", 1456));  
        infos.add(new Info(34.242952, 108.972171, R.drawable.ic_launcher, "沙井国际洗浴会所",  
                "距离897米", 456));  
        infos.add(new Info(34.242852, 108.973171, R.drawable.ic_launcher, "五环服装城",  
                "距离249米", 1456));  
        infos.add(new Info(34.242152, 108.971971, R.drawable.ic_launcher, "老米家泡馍小炒",  
                "距离679米", 1456));  
    }  */
  
    public Info()  
    {  
    }  
  
    public Info(double latitude, double longitude, int imgId, String name, String msg, 
            String distance, int zan)  
    {  
        super();  
        this.latitude = latitude;  
        this.longitude = longitude;  
        this.imgId = imgId;  
        this.name = name;  
        this.msg = msg;
        this.distance = distance;  
        this.zan = zan;  
        //this.fshow=true;//默认为true
    }  
  
    public double getLatitude()  
    {  
        return latitude;  
    }  
  
    public void setLatitude(double latitude)  
    {  
        this.latitude = latitude;  
    }  
  
    public double getLongitude()  
    {  
        return longitude;  
    }  
  
    public void setLongitude(double longitude)  
    {  
        this.longitude = longitude;  
    }  
  
    public String getName()  
    {  
        return name;  
    }  
  
    public int getImgId()  
    {  
        return imgId;  
    }  
  
    public void setImgId(int imgId)  
    {  
        this.imgId = imgId;  
    }  
  
    public void setName(String name)  
    {  
        this.name = name;  
    }  
  
    public String getDistance()  
    {  
        return distance;  
    }  
  
    public void setDistance(String distance)  
    {  
        this.distance = distance;  
    }  
  
    public int getZan()  
    {  
        return zan;  
    }  
  
    public void setZan(int zan)  
    {  
        this.zan = zan;  
    }

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}  
  
} 