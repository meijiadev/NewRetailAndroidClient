package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.other.Logger;


public class NotifyBaseStatusExProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
       // Logger.e("---------NotifyBaseStatusExProcessor");
        BaseCmd.notifyBaseStatusEx notifyBaseStatusEx= (BaseCmd.notifyBaseStatusEx) msg;
        NotifyBaseStatusEx notifyBaseStatusEx1=NotifyBaseStatusEx.getInstance();
        notifyBaseStatusEx1.setCurroute(notifyBaseStatusEx.getCurrroute().toStringUtf8());
        notifyBaseStatusEx1.setCurrpath(notifyBaseStatusEx.getCurrpath().toStringUtf8());
        notifyBaseStatusEx1.setMode(notifyBaseStatusEx.getModeValue());
        notifyBaseStatusEx1.setSonMode(notifyBaseStatusEx.getSonmodeValue());
        notifyBaseStatusEx1.seteDynamicOAStatus(notifyBaseStatusEx.getDynamicoaValue());
        notifyBaseStatusEx1.setStopStat(notifyBaseStatusEx.getStopstat());
        notifyBaseStatusEx1.setPosAngulauspeed(notifyBaseStatusEx.getPosangulauspeed());
        notifyBaseStatusEx1.setPosDirection(notifyBaseStatusEx.getPosdirection());
        notifyBaseStatusEx1.setPosLinespeed(notifyBaseStatusEx.getPoslinespeed());
        notifyBaseStatusEx1.setPosX(notifyBaseStatusEx.getPosx());
        notifyBaseStatusEx1.setPosY(notifyBaseStatusEx.getPosy());
        notifyBaseStatusEx1.seteSelfCalibStatus(notifyBaseStatusEx.getSelfcalibstatusValue());
        notifyBaseStatusEx1.setChargingStatus(notifyBaseStatusEx.getChargingStatus());
        notifyBaseStatusEx1.setTaskCount(notifyBaseStatusEx.getTaskCount());
        notifyBaseStatusEx1.setTaskDuration(notifyBaseStatusEx.getTaskDuration());
        notifyBaseStatusEx1.setExceptionValue(notifyBaseStatusEx.getRobotexceptionValue());
        notifyBaseStatusEx1.setLocationed(notifyBaseStatusEx.getBLocated());
        notifyBaseStatusEx1.setChargingType(notifyBaseStatusEx.getChargingTypeValue());
        notifyBaseStatusEx1.seteTaskMode(notifyBaseStatusEx.getTaskmodeValue());
        notifyBaseStatusEx1.setTemopTaskNum(notifyBaseStatusEx.getTemporaryTaskCount());
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("接受notifybase数据时间"+ ss);
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.updateBaseStatus));

    }
}
