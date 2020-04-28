package ddr.example.com.newretailandroidclient.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;

public class HuoRecordAdapter extends BaseAdapter<HuoRecord>{
    private TextView tv_huo_num;
    private ImageView iv_huo_image;
    private TextView tv_huo_name;
    private OnItemClickListener mOnItemClickListener;
    private Handler handler;
    private GlobalParameter globalParameter=GlobalParameter.getInstance();

    public HuoRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    public HuoRecordAdapter(int layoutResId, @Nullable List<HuoRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<HuoRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull HuoRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull HuoRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HuoRecord item) {
        super.convert(helper, item);

        tv_huo_num=helper.getView(R.id.tv_huo_num);
        iv_huo_image=helper.getView(R.id.iv_huo_image);
        tv_huo_name=helper.getView(R.id.tv_huo_name);
        helper.setText(R.id.tv_huo_num,item.getHuoNum())
                .setText(R.id.tv_huo_name,cutName(item.getHuoName()));
        if (globalParameter.isLan()){
            try {
                String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人图片" + "/" +"SellImage"+"/"+ item.getHuoID() + ".png";
                Glide.with(mContext).load(pngPath).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) helper.getView(R.id.iv_huo_image));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                String files=item.getHuoWurl().split("//")[1];
                String fileDir= files.split("/")[2];        //文件夹名
                String fileName=files.split("/")[4];        //文件名
                String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人图片" + "/" +fileDir+"/"+ fileName;
                Logger.e("对比地址"+pngPath+"----"+fileDir+"-----"+fileName);
                Glide.with(mContext).load(pngPath).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) helper.getView(R.id.iv_huo_image));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                Logger.e("图片地址A---"+item.getHuoUrl()+item.getHuoNum());
//                getHttpBitmap(item.getHuoUrl());
//            }
//        }).start();
//        handler=new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                Bitmap bitmap= (Bitmap) msg.obj;
//                helper.setImageBitmap(R.id.iv_huo_image,bitmap);
//            }
//        };

    }
    public interface OnItemClickListener{ 
        void onClick(int position);
        void onLongClick(int position);
}
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }

    @Nullable
    @Override
    public HuoRecord getItem(int position) {
        return super.getItem(position);
    }
    
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }
    /**
     * 加载本地图片
     */
    public static Bitmap getLoacalBitmap(String id) {
        try {
            String dirName =id;
            String pngPath = GlobalParameter.ROBOT_FOLDER +"SellImage"+"/"+ dirName + ".png";
            Logger.e("对比的图片地址"+pngPath);
            FileInputStream fis = new FileInputStream(pngPath);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  void getHttpBitmap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        Message msg = new Message();
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.connect();
            int code = conn.getResponseCode();
            if (code==200){
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                msg.obj=bitmap;
                handler.sendMessage(msg);
                is.close();
            }else {
                Logger.e("请求失败"+code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 裁剪中英法名称
     * @param value
     * @return
     */
    private String cutName(String value){
        Logger.e("裁剪"+value);
        String v=null;
        if (value.length()>0 && value.contains("&")){
            v=value.substring(0,value.indexOf("&"));
        }else {
            v=value;
        }
        return v;
    }

}
