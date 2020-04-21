package ddr.example.com.newretailandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class HuoProductS {
    public static HuoProductS huoProductS;
    private List<HuoProduct> huoProductList=new ArrayList<>();
    public static HuoProductS getInstance(){
        if (huoProductS==null){
            synchronized (HuoProductS.class){
                if (huoProductS==null){
                    huoProductS=new HuoProductS();
                }
            }
        }
        return huoProductS;
    }

    public List<HuoProduct> getHuoProductList() {
        return huoProductList;
    }

    public void setHuoProductList(List<HuoProduct> huoProductList) {
        this.huoProductList = huoProductList;
    }
}
