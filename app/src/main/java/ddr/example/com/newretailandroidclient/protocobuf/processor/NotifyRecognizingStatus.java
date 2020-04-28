package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.other.Logger;

public class NotifyRecognizingStatus extends BaseProcessor{
    private GlobalParameter globalParameter;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.notifyRecognizingStatus notifyRecognizingStatus= (DDRAIServiceCmd.notifyRecognizingStatus) msg;
        globalParameter=GlobalParameter.getInstance();
        switch (notifyRecognizingStatus.getTypeValue()){
            case 0:
                globalParameter.setNotiAutoResult("使能");
                break;
            case 1:
                globalParameter.setNotiAutoResult("失能");
                break;
        }
//        Logger.e("接受结果------");
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataNotifyAuto));
    }
}
