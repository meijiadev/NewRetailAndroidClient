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
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
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
    private GlobalParameter globalParameter;
    FileUtil fileUtil;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspStockInfo rspStockInfo= (DDRAIServiceCmd.rspStockInfo) msg;
        globalParameter=GlobalParameter.getInstance();
        List<DDRAIServiceCmd.rspStockInfo.StockInfo> stockInfoList=rspStockInfo.getStockInfoList();
        Logger.e("AI货物情况数量"+stockInfoList.size()+"-------"+stockInfoList.get(1).getImageWanUri().toStringUtf8()+"----"+stockInfoList.get(1).getImageLanUri().toStringUtf8());
        Logger.e("AI货物情况数量"+stockInfoList.size()+"-------"+stockInfoList.get(1).getImageWanUri()+"----"+stockInfoList.get(1).getImageLanUri());
//        SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
//        Date date=new Date(System.currentTimeMillis());//系统小时数
//        String ss=formatter.format(date);//获取当前时间
//        Logger.e("接受货物时间"+ ss);
        huoRecordList=new ArrayList<>();
        huoRecordS=HuoRecordS.getInstance();
        for (int i=0;i<stockInfoList.size();i++){
            huoRecord=new HuoRecord();
            String huoDao="货道"+stockInfoList.get(i).getPathID();
            Logger.e("-----"+huoDao);
            String huoRemain=String.valueOf(stockInfoList.get(i).getRemainNum());
            String huoMax=String.valueOf(stockInfoList.get(i).getMaxNum());
            huoRecord.setHuoUrl(stockInfoList.get(i).getImageLanUri().toStringUtf8());
            huoRecord.setHuoWurl(stockInfoList.get(i).getImageWanUri().toStringUtf8());
            huoRecord.setHuoName(stockInfoList.get(i).getGoodsName().toStringUtf8());
            huoRecord.setHuoNum(huoDao+"（"+huoRemain+"/"+huoMax+"）");
            huoRecord.setHuoID(stockInfoList.get(i).getGoodsID().toStringUtf8());
            String url=null;//地址
            String files=null;//去除http://
            String fileDir=null;//文件夹名
            String fileName=null;//文件名
            if (globalParameter.isLan()){
                url=stockInfoList.get(i).getImageLanUri().toStringUtf8();
                Logger.e(url+"-----------");
                files=url.split("//")[1];
                fileDir= files.split("/")[1];        //文件夹名
                fileName=files.split("/")[2];        //文件名
                Logger.e("f"+files+"文件夹名"+fileDir+"文件名"+fileName);
            }else {
                url=stockInfoList.get(i).getImageWanUri().toStringUtf8();
                Logger.e(url+"1111-----------"+url.length());
                if (url.length()>1){
                    files=url.split("//")[1];
                    fileDir= files.split("/")[2];        //文件夹名
                    fileName=files.split("/")[4];        //文件名
                    Logger.e("f"+files+"文件夹名"+fileDir+"文件名"+fileName);
                }
            }
            huoImageList.add(url);
            Logger.e(url+"-----------");
            //http://js1.073c.com/upload/images/2020-04-09/f268386bf57547678e532c587363d5d7.jpg
            //http://192.168.1.220:8000/SellImage/57382333826860317.png

            fileUtil=new FileUtil(fileDir,1);
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
            Logger.e("图片地址"+stockInfoList.get(i).getImageWanUri().toStringUtf8()+i);
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
