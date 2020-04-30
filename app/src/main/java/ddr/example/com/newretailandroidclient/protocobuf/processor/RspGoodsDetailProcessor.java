package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.HuoProduct;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspGoodsDetailProcessor extends BaseProcessor{
    private HuoProduct huoProduct;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspGoodsDetail rspGoodsDetail= (DDRAIServiceCmd.rspGoodsDetail) msg;
        huoProduct=HuoProduct.getInstance();
        huoProduct.setId(rspGoodsDetail.getGoodsID().toStringUtf8());
        huoProduct.setName(rspGoodsDetail.getGoodsName().toStringUtf8());
        huoProduct.setImageLan(rspGoodsDetail.getImageLanUri().toStringUtf8());
        huoProduct.setDescription(rspGoodsDetail.getDescription().toStringUtf8());
        huoProduct.setImageWan(rspGoodsDetail.getImageWanUri().toStringUtf8());
        huoProduct.setPrice(rspGoodsDetail.getUnitprice().toStringUtf8());
        huoProduct.setStartdata(rspGoodsDetail.getProductdate().toStringUtf8());
        huoProduct.setLifedata(rspGoodsDetail.getShelflife().toStringUtf8());
        Logger.e("返回信息"+rspGoodsDetail.getImageWanUri().toStringUtf8());
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("接受货物详情时间"+ ss);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateGoodProduct,huoProduct));
    }
}
