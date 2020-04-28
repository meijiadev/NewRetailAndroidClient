package ddr.example.com.newretailandroidclient.common;

import android.os.Environment;

/**
 * time： 2019/11/11
 * desc： 全局参数
 */
public class GlobalParameter {
    //可变参数
    public static GlobalParameter globalParameter;
    private boolean isLan;//是否局域网登陆
    private String autoReatilResult;//自动售卖返回结果
    private String notiAutoResult;//是否自动售卖
    private String password="ceshi";

    //固定参数
    public static final int DEFAULT=0;
    public static final String ROBOT_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/"+"机器人"+"/";
    public String robotID1="retail_1";
    public String robotID2="retail_2";
    public String robotID3="retail_2";

    public static GlobalParameter getInstance(){
        if (globalParameter==null){
            synchronized (GlobalParameter.class){
                if (globalParameter==null){
                    globalParameter=new GlobalParameter();
                }
            }
        }
        return globalParameter;
    }

    public boolean isLan() {
        return isLan;
    }

    public void setLan(boolean lan) {
        isLan = lan;
    }

    public String getAutoReatilResult() {
        return autoReatilResult;
    }

    public void setAutoReatilResult(String autoReatilResult) {
        this.autoReatilResult = autoReatilResult;
    }

    public String getNotiAutoResult() {
        return notiAutoResult;
    }

    public void setNotiAutoResult(String notiAutoResult) {
        this.notiAutoResult = notiAutoResult;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
