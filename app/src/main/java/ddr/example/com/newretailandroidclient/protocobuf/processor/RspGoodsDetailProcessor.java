package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.HuoProduct;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspGoodsDetailProcessor extends BaseProcessor{
    private HuoProduct huoProduct;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspGoodsDetail rspGoodsDetail= (DDRAIServiceCmd.rspGoodsDetail) msg;
        huoProduct=new HuoProduct();
        huoProduct.setId(rspGoodsDetail.getGoodsID().toStringUtf8());
        huoProduct.setName(rspGoodsDetail.getGoodsName().toStringUtf8());
        huoProduct.setImageLan(rspGoodsDetail.getImageLanUri().toStringUtf8());
        huoProduct.setDescription(rspGoodsDetail.getDescription().toStringUtf8());
        huoProduct.setImageWan(rspGoodsDetail.getImageWanUri().toStringUtf8());
        huoProduct.setPrice(String.valueOf(rspGoodsDetail.getUnitprice()));
        huoProduct.setStartdata(rspGoodsDetail.getProductdate().toStringUtf8());
        huoProduct.setLifedata(rspGoodsDetail.getShelflife().toStringUtf8());
        Logger.e("返回信息"+rspGoodsDetail.getGoodsID().toStringUtf8());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateGoodProduct,huoProduct));
    }
}
