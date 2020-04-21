package ddr.example.com.newretailandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class HuoRecordS {
    public static HuoRecordS huoRecordS;
    private List<HuoRecord> huoRecordList=new ArrayList<>();
    private List<String> huoImage;
    public static HuoRecordS getInstance(){
        if (huoRecordS==null){
            synchronized (HuoRecordS.class){
                if (huoRecordS==null){
                    huoRecordS=new HuoRecordS();
                }
            }
        }
        return huoRecordS;
    }

    public List<HuoRecord> getHuoRecordList() {
        return huoRecordList;
    }

    public void setHuoRecordList(List<HuoRecord> huoRecordList) {
        this.huoRecordList = huoRecordList;
    }

    public List<String> getHuoImage() {
        return huoImage;
    }

    public void setHuoImage(List<String> huoImage) {
        this.huoImage = huoImage;
    }
}
