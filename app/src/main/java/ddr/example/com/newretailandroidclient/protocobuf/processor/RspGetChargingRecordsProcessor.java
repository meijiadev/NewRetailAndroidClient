package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
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
    /**
     * GMT(格林威治标准时间)转换当前北京时间
     * 比如：1526217409 -->2018/5/13 21:16:49 与北京时间相差8个小时，调用下面的方法，是在1526217409加上8*3600秒
     * @param  lt
     * @return
     */
    public static String stampToDate(Long lt) {
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            res = simpleDateFormat.format(lt*1000);
        }catch (Exception e){
            e.printStackTrace();
        }
//        Logger.e("时间"+res);
        return res;
    }
}
