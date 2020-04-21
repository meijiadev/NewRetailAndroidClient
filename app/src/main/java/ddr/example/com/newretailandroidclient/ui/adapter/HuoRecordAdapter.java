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
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.other.Logger;

public class HuoRecordAdapter extends BaseAdapter<HuoRecord>{
    private TextView tv_huo_num;
    private ImageView iv_huo_image;
    private TextView tv_huo_name;
    private OnItemClickListener mOnItemClickListener;
    private Handler handler;

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
                .setText(R.id.tv_huo_name,item.getHuoName());

        new Thread(new Runnable(){
            @Override
            public void run() {
                Logger.e("图片地址A---"+item.getHuoUrl()+item.getHuoNum());
                getHttpBitmap(item.getHuoUrl());
            }
        }).start();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap= (Bitmap) msg.obj;
                helper.setImageBitmap(R.id.iv_huo_image,bitmap);
            }
        };

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
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
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
     * 设置图片的路径
     *
     * @param
     */
    private Bitmap lookBitmap;
    public void transformHuoInfo(List<HuoRecord> huoRecords) {
        for (int i = 0; i < huoRecords.size(); i++) {
            String dirName = huoRecords.get(i).getHuoID();
            String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" +"SellImage"+ dirName + "/" + ".png";
            if (pngPath!=null){
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(pngPath);
                    lookBitmap = BitmapFactory.decodeStream(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
