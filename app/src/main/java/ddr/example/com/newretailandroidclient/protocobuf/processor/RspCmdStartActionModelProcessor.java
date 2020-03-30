package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.other.Logger;


public class RspCmdStartActionModelProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader,msg);
        BaseCmd.rspCmdStartActionMode rspCmdStartActionMode= (BaseCmd.rspCmdStartActionMode) msg;
        Logger.e("--------》》》"+rspCmdStartActionMode.getMode()+"----->>"+rspCmdStartActionMode.getType());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.switchTaskSuccess));
        switch (rspCmdStartActionMode.getType()){
            case eSuccess:

                break;
        }
    }
}
