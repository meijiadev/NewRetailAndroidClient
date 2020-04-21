package ddr.example.com.newretailandroidclient.entity.other;

import java.util.List;

public class MapRecordClickS {
    public static MapRecordClickS mapRecordClickS;
    private List<MapRecordClick> mapRecordClickList;
    public static MapRecordClickS getInstance(){
        if (mapRecordClickS==null){
            synchronized (MapRecordClickS.class){
                if (mapRecordClickS==null){
                    mapRecordClickS=new MapRecordClickS();
                }
            }
        }
        return mapRecordClickS;
    }

    public List<MapRecordClick> getMapRecordClickList() {
        return mapRecordClickList;
    }

    public void setMapRecordClickList(List<MapRecordClick> mapRecordClickList) {
        this.mapRecordClickList = mapRecordClickList;
    }
}
