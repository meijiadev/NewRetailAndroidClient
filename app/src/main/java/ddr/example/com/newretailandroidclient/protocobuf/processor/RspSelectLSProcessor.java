package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import DDRCommProto.RemoteCmd;

import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.other.Logger;

/**
 * time： 2020/1/16
 * desc： 连接机器人返回结果
 */
public class RspSelectLSProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        Logger.e("机器人连接成功！");
        RemoteCmd.rspSelectLS rspSelectLS= (RemoteCmd.rspSelectLS) msg;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.connectedToRobot));
    }
}
