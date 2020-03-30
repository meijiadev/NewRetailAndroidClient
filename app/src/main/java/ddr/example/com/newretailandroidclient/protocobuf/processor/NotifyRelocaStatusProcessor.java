package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;

/**
 * 通知重定位状态
 */
public class NotifyRelocaStatusProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.notifyRelocStatus relocStatus= (BaseCmd.notifyRelocStatus) msg;
        int status=relocStatus.getStatus();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateRelocationStatus,status));
    }
}
