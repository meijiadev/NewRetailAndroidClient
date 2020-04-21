package ddr.example.com.newretailandroidclient.entity.other;

import java.util.List;

public class ChongRecordS {
    public static ChongRecordS chongRecordS;
    private List<ChongRecord> chongRecordList;
    private int countNum;
    private int postion;
    public static ChongRecordS getInstance(){
        if (chongRecordS==null){
            synchronized (ChongRecordS.class){
                if (chongRecordS==null){
                    chongRecordS=new ChongRecordS();
                }
            }
        }
        return chongRecordS;
    }

    public List<ChongRecord> getChongRecordList() {
        return chongRecordList;
    }

    public void setChongRecordList(List<ChongRecord> chongRecordList) {
        this.chongRecordList = chongRecordList;
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
