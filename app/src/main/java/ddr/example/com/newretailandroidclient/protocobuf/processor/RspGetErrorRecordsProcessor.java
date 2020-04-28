package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;
import android.os.Looper;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.ErrorRecord;
import ddr.example.com.newretailandroidclient.entity.other.ErrorRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspGetErrorRecordsProcessor extends BaseProcessor{
    private ErrorRecord errorRecord;
    private ErrorRecordS errorRecordS;
    List<ErrorRecord> errorRecordList;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspGetErrorRecords rspGetErrorRecords= (DDRAIServiceCmd.rspGetErrorRecords) msg;
        List<DDRAIServiceCmd.ErrorLog> errorLogList=rspGetErrorRecords.getErrorlogList();
        errorRecordS=ErrorRecordS.getInstance();
        errorRecordList=new ArrayList<>();
        for (int i=0;i<errorLogList.size();i++){
            int postion=errorRecordS.getPostion();
            String resonV=null;
            switch (errorLogList.get(i).getReasonValue()){
                case 0:
                    resonV="急停";
                    break;
                case 1:
                    resonV="急停结束";
                    break;
                case 2:
                    resonV="按下复位恢复行走";
                    break;
                case 3:
                    resonV="防碰撞急停";
                    break;
                case 4:
                    resonV="防碰撞急停结束";
                    break;
                case 5:
                    resonV="电机实效";
                    break;
                case 6:
                    resonV="货盘停止";
                    break;
                case 7:
                    resonV="货柜存货未取超时";
                    break;
            }
            errorRecord=new ErrorRecord();
            errorRecord.setId(""+((postion*8)+postion+i+1));
            errorRecord.setTime(String.valueOf(stampToDate(errorLogList.get(i).getTime())));
            errorRecord.setError_type(resonV);
            errorRecordList.add(errorRecord);
        }
        errorRecordS.setErrorRecordList(errorRecordList);
        errorRecordS.setCountNum(rspGetErrorRecords.getTotalnums());
        Logger.e("数量----"+rspGetErrorRecords.getTotalnums());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSellErrorRecord));
    }
    /**
     * GMT(格林威治标准时间)转换当前北京时间
     * 比如：1526217409 -->2018/5/13 21:16:49 与北京时间相差8个小时，调用下面的方法，是在1526217409加上8*3600秒
     * @param
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
        Logger.e("时间"+res);
        return res;
    }

}
