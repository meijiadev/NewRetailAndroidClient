package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.ErrorRecord;
import ddr.example.com.newretailandroidclient.entity.other.ErrorRecordS;

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
                    resonV="防碰撞急停";
                    break;
                case 3:
                    resonV="防碰撞急停结束";
                    break;
                case 4:
                    resonV="电机实效";
                    break;
                case 5:
                    resonV="货盘停止";
                    break;
                case 6:
                    resonV="货柜存货未取超时";
                    break;
            }
            errorRecord=new ErrorRecord();
            errorRecord.setId(""+((postion*8)+postion+i+1));
            errorRecord.setTime(String.valueOf(errorLogList.get(i).getTime()));
            errorRecord.setError_type(resonV);
            errorRecordList.add(errorRecord);
        }
        errorRecordS.setErrorRecordList(errorRecordList);
        errorRecordS.setCountNum(rspGetErrorRecords.getTotalnums());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSellErrorRecord));
    }

}
