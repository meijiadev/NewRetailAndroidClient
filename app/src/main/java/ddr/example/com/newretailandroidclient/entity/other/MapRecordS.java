package ddr.example.com.newretailandroidclient.entity.other;

import java.util.List;

public class MapRecordS {
    public static MapRecordS mapRecordS;
    private List<MapRecord> mapRecordList;
    private int countNum;//总数
    private int postion;//当前页数
    private int itemPostion;//当前点击item坐标
    public static MapRecordS getInstance(){
        if (mapRecordS==null){
            synchronized (MapRecordS.class){
                if (mapRecordS==null){
                    mapRecordS=new MapRecordS();
                }
            }
        }
        return mapRecordS;
    }

    public List<MapRecord> getMapRecordList() {
        return mapRecordList;
    }

    public void setMapRecordList(List<MapRecord> mapRecordList) {
        this.mapRecordList = mapRecordList;
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

    public int getItemPostion() {
        return itemPostion;
    }

    public void setItemPostion(int itemPostion) {
        this.itemPostion = itemPostion;
    }
}
