package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.download.DownLoadCallBack;
import ddr.example.com.newretailandroidclient.download.DownLoadImageService;
import ddr.example.com.newretailandroidclient.download.FileUtil;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class RspStockInfoHuoProcessor extends BaseProcessor implements DownLoadCallBack {
    private HuoRecord huoRecord;
    private List<HuoRecord> huoRecordList;
    private HuoRecordS huoRecordS;
    private List<String> huoImageList=new ArrayList<>();
    FileUtil fileUtil;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspStockInfo rspStockInfo= (DDRAIServiceCmd.rspStockInfo) msg;
        List<DDRAIServiceCmd.rspStockInfo.StockInfo> stockInfoList=rspStockInfo.getStockInfoList();
        Logger.e("AI货物情况数量"+stockInfoList.size());
        huoRecordList=new ArrayList<>();
        huoRecordS=HuoRecordS.getInstance();
        for (int i=0;i<stockInfoList.size();i++){
            huoRecord=new HuoRecord();
            String huoDao="货道"+stockInfoList.get(i).getPathID();
            String huoRemain=String.valueOf(stockInfoList.get(i).getRemainNum());
            String huoMax=String.valueOf(stockInfoList.get(i).getMaxNum());
            huoRecord.setHuoUrl(stockInfoList.get(i).getImageLanUri().toStringUtf8());
            huoRecord.setHuoName(stockInfoList.get(i).getGoodsName().toStringUtf8());
            huoRecord.setHuoNum(huoDao+"（"+huoRemain+"/"+huoMax+"）");
            huoRecord.setHuoID(stockInfoList.get(i).getGoodsID().toStringUtf8());
            String url=stockInfoList.get(i).getImageLanUri().toStringUtf8();
            huoImageList.add(url);
            Logger.e(url+"-----------");
            String files=url.split("//")[1];
            String fileDir= files.split("/")[1];        //文件夹名
            String fileName=files.split("/")[2];        //文件名
            Logger.e("f"+files+"文件夹名"+fileDir+"文件名"+fileName);
            fileUtil=new FileUtil(fileDir);
            File target = null;
            if (url.contains(".png")||url.contains(".jpg")){
                Logger.e("----使用图片专用下载工具-----");
                try {
                    target=fileUtil.createSdFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Thread(new DownLoadImageService(context,url,this,target)).start();
            }else {
                try {
                    fileUtil.writeToSdFromInput(url,fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Logger.e("图片地址"+stockInfoList.get(i).getImageLanUri().toStringUtf8()+i);
            huoRecordList.add(huoRecord);
        }
        huoRecordS.setHuoImage(huoImageList);
        huoRecordS.setHuoRecordList(huoRecordList);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataSellHuoRecord));
    }

    @Override
    public void onDownLoadSuccess(File file, File target) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileUtil.copy(file,target);
            }
        }).start();
    }

    @Override
    public void onDownLoadFailed() {
        Logger.e("----图片下载失败");
    }
    /**
     * 将long型时间戳转成年月日的格式
     * @param time
     * @return
     */
    private String longToDate(long time){
        Date date=new Date(time);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:MM ");
        return simpleDateFormat.format(date);
    }
}
