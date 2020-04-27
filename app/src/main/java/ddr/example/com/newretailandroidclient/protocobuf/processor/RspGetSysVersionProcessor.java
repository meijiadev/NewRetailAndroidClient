package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.other.ComputerEdition;
import ddr.example.com.newretailandroidclient.entity.other.ComputerEditions;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.other.Logger;


public class RspGetSysVersionProcessor extends BaseProcessor {

    private ComputerEditions computerEditions;
    private List<ComputerEdition> computerEditionList;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspGetSysVersion rspGetSysVersion=(BaseCmd.rspGetSysVersion)msg;
        List<BaseCmd.rspGetSysVersion.ComponentVerItem> sysInfoList =rspGetSysVersion.getSysInfoList();
        computerEditions = ComputerEditions.getInstance();
        computerEditionList =new ArrayList<>();
        for (int i=0;i<sysInfoList.size();i++){
            Logger.e("日期"+sysInfoList.get(i).getDate().toStringUtf8());
            ComputerEdition computerEdition =new ComputerEdition();
            computerEdition.setData(sysInfoList.get(i).getDate().toStringUtf8());
            computerEdition.setVersion(sysInfoList.get(i).getVersion().toStringUtf8());
            computerEdition.setType(sysInfoList.get(i).getType().getNumber());
            computerEditionList.add(computerEdition);
        }
        Logger.e("数量"+ computerEditionList.size());
        computerEditions.setComputerEditionList(computerEditionList);
        Logger.e("实际数量"+ computerEditions.getComputerEditionList().size());
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("版本信息接受时间"+ ss);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateVersion));
    }
}
