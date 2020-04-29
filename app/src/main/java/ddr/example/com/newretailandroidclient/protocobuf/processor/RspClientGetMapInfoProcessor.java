package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.download.DownLoadCallBack;
import ddr.example.com.newretailandroidclient.download.DownLoadImageService;
import ddr.example.com.newretailandroidclient.download.FileUtil;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.MapFileStatus;
import ddr.example.com.newretailandroidclient.entity.info.MapInfo;
import ddr.example.com.newretailandroidclient.helper.SortClass;
import ddr.example.com.newretailandroidclient.other.Logger;


/**
 * 接收地图信息
 */
public class RspClientGetMapInfoProcessor extends BaseProcessor implements DownLoadCallBack {

    private GlobalParameter globalParameter;
    private List<String> urlList;
    private List<String> mapNames;
    private List<byte[]> bitmapBytes;
    FileUtil fileUtil;

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        Logger.e("接收到文件http地址");
        globalParameter=GlobalParameter.getInstance();
        BaseCmd.rspClientGetMapInfo rspClientGetMapInfo= (BaseCmd.rspClientGetMapInfo) msg;
        if (globalParameter.isLan()){
            List<BaseCmd.rspClientGetMapInfo.MapInfoItem> mapInfoItemList;
            List<MapInfo> mapInfoList=new ArrayList<>();
            Logger.e("局域网地图数据"+commonHeader.getGuid());
            mapInfoItemList=rspClientGetMapInfo.getMapDataList();
            urlList=new ArrayList<>();
            mapNames=new ArrayList<>();
            bitmapBytes=new ArrayList<>();
            if (mapInfoItemList!=null&&mapInfoItemList.size()>0){
                mapInfoList=new ArrayList<>();
                for (int i=0;i<mapInfoItemList.size();i++){
                    MapInfo mapInfo=new MapInfo();
                    mapInfo.setMapName(mapInfoItemList.get(i).getName().toStringUtf8());
                    mapInfo.setWidth((int)mapInfoItemList.get(i).getWidth());
                    mapInfo.setHeight((int)mapInfoItemList.get(i).getHitght());
                    mapInfo.setAuthor(mapInfoItemList.get(i).getAuthor().toStringUtf8());
                    mapInfo.setTime(longToDate(mapInfoItemList.get(i).getTimeStamp()));
                    mapInfo.setTaskItemList(mapInfoItemList.get(i).getTaskSetList());
                    ByteString bytes=mapInfoItemList.get(i).getBkPicAddr();
                    String url=bytes.toStringUtf8();
                    byte [] bitmaps=null;
                    if (url.contains("http:")){
                        Logger.e("-----url:"+url);
                        urlList.add(url);
                        String files=url.split("//")[1];
                        String fileDir= files.split("/")[1];        //文件夹名
                        String fileName=files.split("/")[2];        //文件名
                        fileUtil=new FileUtil(fileDir,0);
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
                    }else {
                        bitmaps=bytes.toByteArray();
                        // bitmapBytes.add(bitmaps);
                    }
                    mapInfo.setBytes(bitmaps);
                    mapInfoList.add(mapInfo);
                }
                SortClass sortClass=new SortClass();
                Collections.sort(mapInfoList,sortClass);
                for(int i=0,size=mapInfoList.size();i<size;i++){
                    mapNames.add(mapInfoList.get(i).getMapName());
                    Logger.e("-------:"+mapInfoList.get(i).getTime());
                }
                MapFileStatus mapFileStatus=MapFileStatus.getInstance();
                mapFileStatus.setMapNames(mapNames);
                mapFileStatus.setPictureUrls(urlList);
                mapFileStatus.setMapInfos(mapInfoList);
                mapFileStatus.setBitmapBytes(bitmapBytes);
            }
        }else{
            if (!globalParameter.isLan() && globalParameter.getPassword().equals("admin") && commonHeader.getGuid().equals(globalParameter.robotID1)){
                List<BaseCmd.rspClientGetMapInfo.MapInfoItem> mapInfoItemList;
                List<MapInfo> mapInfoList=new ArrayList<>();
                Logger.e("广域网两台机器机器一地图数据"+commonHeader.getGuid());
                mapInfoItemList=rspClientGetMapInfo.getMapDataList();
                urlList=new ArrayList<>();
                mapNames=new ArrayList<>();
                bitmapBytes=new ArrayList<>();
                if (mapInfoItemList!=null&&mapInfoItemList.size()>0){
                    mapInfoList=new ArrayList<>();
                    for (int i=0;i<mapInfoItemList.size();i++){
                        MapInfo mapInfo=new MapInfo();
                        mapInfo.setMapName(mapInfoItemList.get(i).getName().toStringUtf8());
                        mapInfo.setWidth((int)mapInfoItemList.get(i).getWidth());
                        mapInfo.setHeight((int)mapInfoItemList.get(i).getHitght());
                        mapInfo.setAuthor(mapInfoItemList.get(i).getAuthor().toStringUtf8());
                        mapInfo.setTime(longToDate(mapInfoItemList.get(i).getTimeStamp()));
                        mapInfo.setTaskItemList(mapInfoItemList.get(i).getTaskSetList());
                        ByteString bytes=mapInfoItemList.get(i).getBkPicAddr();
                        String url=bytes.toStringUtf8();
                        byte [] bitmaps=null;
                        if (url.contains("http:")){
                            Logger.e("-----url:"+url);
                            urlList.add(url);
                            String files=url.split("//")[1];
                            String fileDir= files.split("/")[1];        //文件夹名
                            String fileName=files.split("/")[2];        //文件名
                            fileUtil=new FileUtil(fileDir,0);
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
                        }else {
                            bitmaps=bytes.toByteArray();
                            // bitmapBytes.add(bitmaps);
                        }
                        mapInfo.setBytes(bitmaps);
                        mapInfoList.add(mapInfo);
                    }
                    SortClass sortClass=new SortClass();
                    Collections.sort(mapInfoList,sortClass);
                    for(int i=0,size=mapInfoList.size();i<size;i++){
                        mapNames.add(mapInfoList.get(i).getMapName());
                        Logger.e("-------:"+mapInfoList.get(i).getTime());
                    }
                    MapFileStatus mapFileStatus=MapFileStatus.getInstance();
                    mapFileStatus.setMapNames(mapNames);
                    mapFileStatus.setPictureUrls(urlList);
                    mapFileStatus.setMapInfos(mapInfoList);
                    mapFileStatus.setBitmapBytes(bitmapBytes);
                }
            }else if (!globalParameter.isLan() && globalParameter.getPassword().equals("admin") && commonHeader.getGuid().equals(globalParameter.robotID2)){
                Logger.e("广域网两台机器不加载机器人二地图"+commonHeader.getGuid());
            } else if (!globalParameter.getPassword().equals("admin") && !globalParameter.getPassword().equals("null")){
                Logger.e("广域网单台机器-----"+commonHeader.getGuid());
                List<BaseCmd.rspClientGetMapInfo.MapInfoItem> mapInfoItemList;
                List<MapInfo> mapInfoList=new ArrayList<>();
                mapInfoItemList=rspClientGetMapInfo.getMapDataList();
                urlList=new ArrayList<>();
                mapNames=new ArrayList<>();
                bitmapBytes=new ArrayList<>();
                if (mapInfoItemList!=null&&mapInfoItemList.size()>0){
                    mapInfoList=new ArrayList<>();
                    for (int i=0;i<mapInfoItemList.size();i++){
                        MapInfo mapInfo=new MapInfo();
                        mapInfo.setMapName(mapInfoItemList.get(i).getName().toStringUtf8());
                        mapInfo.setWidth((int)mapInfoItemList.get(i).getWidth());
                        mapInfo.setHeight((int)mapInfoItemList.get(i).getHitght());
                        mapInfo.setAuthor(mapInfoItemList.get(i).getAuthor().toStringUtf8());
                        mapInfo.setTime(longToDate(mapInfoItemList.get(i).getTimeStamp()));
                        mapInfo.setTaskItemList(mapInfoItemList.get(i).getTaskSetList());
                        ByteString bytes=mapInfoItemList.get(i).getBkPicAddr();
                        String url=bytes.toStringUtf8();
                        byte [] bitmaps=null;
                        if (url.contains("http:")){
                            Logger.e("-----url:"+url);
                            urlList.add(url);
                            String files=url.split("//")[1];
                            String fileDir= files.split("/")[1];        //文件夹名
                            String fileName=files.split("/")[2];        //文件名
                            fileUtil=new FileUtil(fileDir,0);
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
                        }else {
                            bitmaps=bytes.toByteArray();
                            // bitmapBytes.add(bitmaps);
                        }
                        mapInfo.setBytes(bitmaps);
                        mapInfoList.add(mapInfo);
                    }
                    SortClass sortClass=new SortClass();
                    Collections.sort(mapInfoList,sortClass);
                    for(int i=0,size=mapInfoList.size();i<size;i++){
                        mapNames.add(mapInfoList.get(i).getMapName());
                        Logger.e("-------:"+mapInfoList.get(i).getTime());
                    }
                    MapFileStatus mapFileStatus=MapFileStatus.getInstance();
                    mapFileStatus.setMapNames(mapNames);
                    mapFileStatus.setPictureUrls(urlList);
                    mapFileStatus.setMapInfos(mapInfoList);
                    mapFileStatus.setBitmapBytes(bitmapBytes);
                }
            }
        }
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.updateMapList));
    }

    @Override
    public void onDownLoadSuccess(final File file, final File target) {
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
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        return simpleDateFormat.format(date);
    }

}
