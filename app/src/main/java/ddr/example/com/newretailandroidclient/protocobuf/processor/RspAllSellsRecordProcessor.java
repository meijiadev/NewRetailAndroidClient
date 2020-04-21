package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.RetailRecord;
import ddr.example.com.newretailandroidclient.entity.other.RetailRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspAllSellsRecordProcessor extends BaseProcessor{
    private RetailRecord retailRecord;
    private List<RetailRecord> retailRecordList;
    private RetailRecordS retailRecordS;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspAllSellsRecord rspAllSellsRecord= (DDRAIServiceCmd.rspAllSellsRecord) msg;
        List<DDRAIServiceCmd.rspAllSellsRecord.SellRecord> sellRecordList=rspAllSellsRecord.getAllSellsRecordList();
        Logger.e("接受AI大小"+sellRecordList.size());
        retailRecordS=RetailRecordS.getInstance();
        retailRecordList=new ArrayList<>();
        for (int i=0;i<sellRecordList.size();i++){
            int postion=retailRecordS.getPostion();
            retailRecord=new RetailRecord();
            retailRecord.setId(""+((postion*8)+postion+i+1));
            retailRecord.setName(sellRecordList.get(i).getProductname().toStringUtf8());
            retailRecord.setSettlement(String.valueOf(sellRecordList.get(i).getPaytype()));
            retailRecord.setB_num(sellRecordList.get(i).getOrdercode().toStringUtf8());
            retailRecord.setPrice(String.valueOf(sellRecordList.get(i).getUnitprice()));
            retailRecord.setNumber(String.valueOf(sellRecordList.get(i).getSellnum()));
            retailRecord.setTotal(String.valueOf(sellRecordList.get(i).getTotalprice()));
            retailRecord.setCount_num(rspAllSellsRecord.getTotalnums());
            retailRecordList.add(retailRecord);
        }
        retailRecordS.setRetailRecordList(retailRecordList);
        retailRecordS.setCountNum(rspAllSellsRecord.getTotalnums());
        Logger.e("数量"+rspAllSellsRecord.getTotalnums());
        SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("接受时间"+ ss);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSellsRecord));

    }
}
