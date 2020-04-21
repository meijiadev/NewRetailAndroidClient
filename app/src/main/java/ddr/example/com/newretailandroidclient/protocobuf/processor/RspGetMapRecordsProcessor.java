package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.MapRecord;
import ddr.example.com.newretailandroidclient.entity.other.MapRecordClick;
import ddr.example.com.newretailandroidclient.entity.other.MapRecordClickS;
import ddr.example.com.newretailandroidclient.entity.other.MapRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspGetMapRecordsProcessor extends BaseProcessor{
    private MapRecord mapRecord;
    private MapRecordS mapRecordS;
    private List<MapRecord> mapRecordList;
    private MapRecordClick mapRecordClick;
    private MapRecordClickS mapRecordClickS;
    private List<MapRecordClick> mapRecordClickList;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspGetMapRecords rspGetMapRecords= (DDRAIServiceCmd.rspGetMapRecords) msg;
        List<DDRAIServiceCmd.MapEvenLog> mapEvenLogList=rspGetMapRecords.getMaplogList();
        mapRecordS=MapRecordS.getInstance();
        mapRecordList=new ArrayList<>();
        Logger.e("地图运行记录列表长度"+mapEvenLogList.size()+"---");
        for (int i=0;i<mapEvenLogList.size();i++){
            mapRecord=new MapRecord();
            int postion=mapRecordS.getPostion();
            String result=null;
            mapRecord.setId(""+((postion*8)+postion+i+1));
            mapRecord.setStart_time(String.valueOf(mapEvenLogList.get(i).getStarttime()));
            mapRecord.setEnd_time(String.valueOf(mapEvenLogList.get(i).getEndtime()));
            mapRecord.setRun_time(String.valueOf(mapEvenLogList.get(i).getDuration()));
            mapRecord.setRetail_map(mapEvenLogList.get(i).getMapname().toStringUtf8());
            mapRecord.setRetail_num(String.valueOf(mapEvenLogList.get(i).getSalesnum()));
            Logger.e("地图详情记录列表长度"+mapEvenLogList.get(i).getRecordList().size()+"---"+i);
            mapRecordClickList=new ArrayList<>();
            for (int j=0;j<mapEvenLogList.get(i).getRecordList().size();j++){
                mapRecordClick=new MapRecordClick();
                mapRecordClick.setStart_time(String.valueOf(mapEvenLogList.get(i).getRecordList().get(j).getStarttime()));
                mapRecordClick.setEnd_time(String.valueOf(mapEvenLogList.get(i).getRecordList().get(j).getEndtime()));
                mapRecordClick.setName("售卖点"+j);
                mapRecordClick.setRetail_map(mapEvenLogList.get(i).getMapname().toStringUtf8());
                mapRecordClick.setPosX(mapEvenLogList.get(i).getRecordList().get(j).getPosX());
                mapRecordClick.setPosX(mapEvenLogList.get(i).getRecordList().get(j).getPosX());
                mapRecordClick.setRun_time(String.valueOf(mapEvenLogList.get(i).getRecordList().get(j).getDuration()));
                switch (mapEvenLogList.get(i).getRecordList().get(j).getResultValue()){
                    case 0:
                        result="售货成功";
                        break;
                    case 1:
                        result="售货超时";
                        break;
                    case 2:
                        result="出货失败";
                        break;
                    case 3:
                        result="售卖故障";
                        break;
                }
                mapRecordClick.setResult(result);
                mapRecordClickList.add(mapRecordClick);
            }
            mapRecord.setMapRecordClickList(mapRecordClickList);
            mapRecordList.add(mapRecord);
        }
        mapRecordS.setMapRecordList(mapRecordList);
        mapRecordS.setCountNum(rspGetMapRecords.getTotalnums());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSellMapRecord));
    }
}
