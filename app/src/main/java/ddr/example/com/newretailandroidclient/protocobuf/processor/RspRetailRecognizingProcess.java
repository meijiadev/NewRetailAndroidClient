package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;

public class RspRetailRecognizingProcess extends BaseProcessor{
    private GlobalParameter globalParameter;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspRetailRecognizing rspRetailRecognizing= (DDRAIServiceCmd.rspRetailRecognizing) msg;
        globalParameter=GlobalParameter.getInstance();
        switch (rspRetailRecognizing.getRetValue()){
            case 0:
                globalParameter.setAutoReatilResult("成功");
                break;
            case 1:
                globalParameter.setAutoReatilResult("连接失败");
                break;
            case 2:
                globalParameter.setAutoReatilResult("网络错误");
                break;
            case 3:
                globalParameter.setAutoReatilResult("错误状态");
                break;
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataRetailAuto));
    }
}
