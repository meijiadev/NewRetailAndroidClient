package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.other.Logger;



public class RspLoginProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspLogin rspLogin= (BaseCmd.rspLogin) msg;
        Logger.e("返回AI登陆信息"+rspLogin.getYourRoleValue());
        Logger.e("登陆成功");
        switch (rspLogin.getYourRoleValue()){
            case 2:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginSuccess));
                break;
            case 0:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginAiSuccess));
                break;
        }

    }
}
