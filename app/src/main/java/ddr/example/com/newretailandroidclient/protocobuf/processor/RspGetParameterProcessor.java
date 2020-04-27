package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.Parameter;
import ddr.example.com.newretailandroidclient.entity.other.Parameters;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspGetParameterProcessor extends BaseProcessor{
    private Parameters parameters;
    private List<Parameter> parameterList;

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspConfigOperational rspConfigOperational= (BaseCmd.rspConfigOperational) msg;
        List<BaseCmd.configData> configDataList=rspConfigOperational.getDataList();
        parameters= Parameters.getInstance();
        parameterList=new ArrayList<>();
        Logger.e("接受数量"+configDataList.size());
        for (int i=0;i<configDataList.size();i++){
            Logger.e("key"+configDataList.get(1).getData().getKey());
            Parameter parameter=new Parameter();
            parameter.setKey(configDataList.get(i).getData().getKey().toStringUtf8());
            parameter.setValue(configDataList.get(i).getData().getValue().toStringUtf8());
            parameter.setdValue(configDataList.get(i).getData().getDefauleValue().toStringUtf8());
            parameter.setWritable(configDataList.get(i).getData().getWritable().toStringUtf8());
            parameter.setAlias(configDataList.get(i).getData().getAlias().toStringUtf8());
            parameterList.add(parameter);
        }
        Logger.e("数量"+parameterList.size());
        parameters.setParameterList(parameterList);
        Logger.e("实际数量"+parameters.getParameterList().size());
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("版本信息导航信息接受时间"+ ss);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataParameter));
    }
}
