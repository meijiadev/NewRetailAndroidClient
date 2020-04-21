package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.ChongRecord;
import ddr.example.com.newretailandroidclient.entity.other.ChongRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspGetChargingRecordsProcessor extends BaseProcessor{
    private ChongRecord chongRecord;
    private ChongRecordS chongRecordS;
    private List<ChongRecord> chongRecordList;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspGetChargingRecords rspGetChargingRecords = (DDRAIServiceCmd.rspGetChargingRecords) msg;
        List<DDRAIServiceCmd.ChargingLog> chargingLogList=rspGetChargingRecords.getCharginglogList();
        chongRecordS=ChongRecordS.getInstance();
        String result=null;
        chongRecordList=new ArrayList<>();
        Logger.e("接受数量"+chargingLogList.size());
        for (int i=0;i<chargingLogList.size();i++){
            int postion=chongRecordS.getPostion();
            chongRecord=new ChongRecord();
            switch (chargingLogList.get(i).getResultValue()){
                case 0:
                    result="成功";
                    break;
                case 1:
                    result="失败";
                    break;
            }
            chongRecord.setId(""+((postion*8)+postion+i+1));
            chongRecord.setDj_result(result);
            chongRecord.setCd_order(postion+i+"时间");
            chongRecord.setStart_cd_time(String.valueOf(chargingLogList.get(i).getStarttime()));
            chongRecord.setStart_cd_num(String.valueOf(chargingLogList.get(i).getBeforeC()));
            chongRecord.setEnd_cd_time(String.valueOf(chargingLogList.get(i).getEndtime()));
            chongRecord.setEns_cd_num(String.valueOf(chargingLogList.get(i).getAfterC()));
            chongRecord.setCd_h_time(String.valueOf(chargingLogList.get(i).getDuration()));
            chongRecordList.add(chongRecord);
        }
        chongRecordS.setChongRecordList(chongRecordList);
        Logger.e("接受后数据"+chongRecordList.size());
        chongRecordS.setCountNum(rspGetChargingRecords.getTotalnums());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSellChongRecord));
    }
}
