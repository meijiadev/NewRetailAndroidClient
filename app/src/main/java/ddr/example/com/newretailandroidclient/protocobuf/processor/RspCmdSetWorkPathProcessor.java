package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.other.Logger;


public class RspCmdSetWorkPathProcessor extends BaseProcessor {

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context,commonHeader, msg);
        BaseCmd.rspCmdSetWorkPath rspCmdSetWorkPath= (BaseCmd.rspCmdSetWorkPath) msg;
        Logger.e("----"+rspCmdSetWorkPath.getType());
        switch (rspCmdSetWorkPath.getTypeValue()){
            case 0:
                Logger.e("------路径修改成功");
                break;
            case 1:
                Logger.e("------路径修改失败");
                break;
        }
    }

}
