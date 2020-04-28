package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Date;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusExTwo;
import ddr.example.com.newretailandroidclient.other.Logger;


public class NotifyBaseStatusExProcessor extends BaseProcessor {
    private GlobalParameter globalParameter;

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        globalParameter=GlobalParameter.getInstance();
        BaseCmd.notifyBaseStatusEx notifyBaseStatusEx= (BaseCmd.notifyBaseStatusEx) msg;
       // Logger.e("---------NotifyBaseStatusExProcessor");
        Logger.d("机器ID"+commonHeader.getGuid());
        if (globalParameter.isLan()){
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
            notifyBaseStatusEx1.setRobotid(notifyBaseStatusEx.getRobotid().toStringUtf8());
        }else {
            if (!globalParameter.getPassword().equals("admin")) {
                Logger.e("单台广域网机器");
                NotifyBaseStatusEx notifyBaseStatusEx1 = NotifyBaseStatusEx.getInstance();
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
                notifyBaseStatusEx1.setRobotid(notifyBaseStatusEx.getRobotid().toStringUtf8());
            }else if (globalParameter.getPassword().equals("admin") && commonHeader.getGuid().equals(globalParameter.robotID1)) {
                Logger.e("两台广域网，机器1的数据");
                NotifyBaseStatusEx notifyBaseStatusEx1 = NotifyBaseStatusEx.getInstance();
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
                notifyBaseStatusEx1.setRobotid(notifyBaseStatusEx.getRobotid().toStringUtf8());
            }else if (globalParameter.getPassword().equals("admin") && commonHeader.getGuid().equals(globalParameter.robotID3)){
                Logger.e("两台广域网，机器2的数据");
                NotifyBaseStatusExTwo notifyBaseStatusExTwo = NotifyBaseStatusExTwo.getInstance();
                notifyBaseStatusExTwo.setCurroute(notifyBaseStatusEx.getCurrroute().toStringUtf8());
                notifyBaseStatusExTwo.setCurrpath(notifyBaseStatusEx.getCurrpath().toStringUtf8());
                notifyBaseStatusExTwo.setMode(notifyBaseStatusEx.getModeValue());
                notifyBaseStatusExTwo.setSonMode(notifyBaseStatusEx.getSonmodeValue());
                notifyBaseStatusExTwo.seteDynamicOAStatus(notifyBaseStatusEx.getDynamicoaValue());
                notifyBaseStatusExTwo.setStopStat(notifyBaseStatusEx.getStopstat());
                notifyBaseStatusExTwo.setPosAngulauspeed(notifyBaseStatusEx.getPosangulauspeed());
                notifyBaseStatusExTwo.setPosDirection(notifyBaseStatusEx.getPosdirection());
                notifyBaseStatusExTwo.setPosLinespeed(notifyBaseStatusEx.getPoslinespeed());
                notifyBaseStatusExTwo.setPosX(notifyBaseStatusEx.getPosx());
                notifyBaseStatusExTwo.setPosY(notifyBaseStatusEx.getPosy());
                notifyBaseStatusExTwo.seteSelfCalibStatus(notifyBaseStatusEx.getSelfcalibstatusValue());
                notifyBaseStatusExTwo.setChargingStatus(notifyBaseStatusEx.getChargingStatus());
                notifyBaseStatusExTwo.setTaskCount(notifyBaseStatusEx.getTaskCount());
                notifyBaseStatusExTwo.setTaskDuration(notifyBaseStatusEx.getTaskDuration());
                notifyBaseStatusExTwo.setExceptionValue(notifyBaseStatusEx.getRobotexceptionValue());
                notifyBaseStatusExTwo.setLocationed(notifyBaseStatusEx.getBLocated());
                notifyBaseStatusExTwo.setChargingType(notifyBaseStatusEx.getChargingTypeValue());
                notifyBaseStatusExTwo.seteTaskMode(notifyBaseStatusEx.getTaskmodeValue());
                notifyBaseStatusExTwo.setTemopTaskNum(notifyBaseStatusEx.getTemporaryTaskCount());
                notifyBaseStatusExTwo.setRotbotid(notifyBaseStatusEx.getRobotid().toStringUtf8());
            }


        }

        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss:SSS");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
//        Logger.e("接受notifybase数据时间"+ ss);
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.updateBaseStatus));

    }
}
