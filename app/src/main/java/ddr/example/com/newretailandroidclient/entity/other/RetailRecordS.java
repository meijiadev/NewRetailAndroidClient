package ddr.example.com.newretailandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class RetailRecordS {
    public static RetailRecordS retailRecordS;
    private List<RetailRecord> retailRecordList=new ArrayList<>();
    private int countNum;
    private int postion;
    public static RetailRecordS getInstance(){
        if(retailRecordS==null){
            synchronized (RetailRecordS.class){
                if (retailRecordS==null){
                    retailRecordS=new RetailRecordS();
                }
            }
        }
        return retailRecordS;
    }

    public List<RetailRecord> getRetailRecordList() {
        return retailRecordList;
    }

    public void setRetailRecordList(List<RetailRecord> retailRecordList) {
        this.retailRecordList = retailRecordList;
    }

    public int getCountNum() {
        return countNum;
    }

    public void setCountNum(int countNum) {
        this.countNum = countNum;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }
}
