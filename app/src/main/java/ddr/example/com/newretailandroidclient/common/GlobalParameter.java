package ddr.example.com.newretailandroidclient.common;

/**
 * time： 2019/11/11
 * desc： 全局参数
 */
public class GlobalParameter {
    public static final int DEFAULT=0;
    public static GlobalParameter globalParameter;
    private boolean isLan;
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
}
