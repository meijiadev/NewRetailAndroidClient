package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfoTwo;
import ddr.example.com.newretailandroidclient.other.Logger;


public class NotifyEnvInfoProcessor extends BaseProcessor {
    private GlobalParameter globalParameter;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        globalParameter=GlobalParameter.getInstance();
        BaseCmd.notifyEnvInfo notifyEnvInfo= (BaseCmd.notifyEnvInfo) msg;
        if (globalParameter.isLan()){
            NotifyEnvInfo notifyEnvInfo1=NotifyEnvInfo.getInstance();
            notifyEnvInfo1.setBatt(notifyEnvInfo.getBatt());
            notifyEnvInfo1.setTemp(notifyEnvInfo.getTemp());
            notifyEnvInfo1.setCh2o(notifyEnvInfo.getCh2O());
            notifyEnvInfo1.setCo2(notifyEnvInfo.getCo2());
            notifyEnvInfo1.setHum(notifyEnvInfo.getHum());
            notifyEnvInfo1.setPm25(notifyEnvInfo.getPm25());
        }else {
            if (!globalParameter.getPassword().equals("admin")) {
                NotifyEnvInfo notifyEnvInfo1 = NotifyEnvInfo.getInstance();
                notifyEnvInfo1.setBatt(notifyEnvInfo.getBatt());
                notifyEnvInfo1.setTemp(notifyEnvInfo.getTemp());
                notifyEnvInfo1.setCh2o(notifyEnvInfo.getCh2O());
                notifyEnvInfo1.setCo2(notifyEnvInfo.getCo2());
                notifyEnvInfo1.setHum(notifyEnvInfo.getHum());
                notifyEnvInfo1.setPm25(notifyEnvInfo.getPm25());
            }else{
                if (commonHeader.getGuid().equals(globalParameter.robotID1)){
                    Logger.e("广域网两台机器，机器1的状态显示");
                    NotifyEnvInfo notifyEnvInfo1 = NotifyEnvInfo.getInstance();
                    notifyEnvInfo1.setBatt(notifyEnvInfo.getBatt());
                    notifyEnvInfo1.setTemp(notifyEnvInfo.getTemp());
                    notifyEnvInfo1.setCh2o(notifyEnvInfo.getCh2O());
                    notifyEnvInfo1.setCo2(notifyEnvInfo.getCo2());
                    notifyEnvInfo1.setHum(notifyEnvInfo.getHum());
                    notifyEnvInfo1.setPm25(notifyEnvInfo.getPm25());
                }else if (commonHeader.getGuid().equals(globalParameter.robotID3)){
                    Logger.e("广域网两台机器，机器2的状态显示");
                    NotifyEnvInfoTwo notifyEnvInfoTwo = NotifyEnvInfoTwo.getInstance();
                    notifyEnvInfoTwo.setBatt(notifyEnvInfo.getBatt());
                }

//                Logger.e("电量" + notifyEnvInfoTwo.getBatt());
            }
        }

    }
}
