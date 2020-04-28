package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.protobuf.ByteString;
import com.yhao.floatwindow.FloatWindow;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import butterknife.BindView;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.HuoProduct;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.adapter.ChongRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.HuoRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.HuoProductDialog;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;

public class SellHuoRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_sell_huo)
    RecyclerView recycle_sell_huo;

    private HuoRecord huoRecord;
    private HuoRecordAdapter huoRecordAdapter;
    private List<HuoRecord> huoRecordList;
    private HuoRecordS huoRecordS;
    private TcpAiClient tcpAiClient;
    private TcpClient tcpClient;
    private HuoProduct huoProduct;
    private GlobalParameter globalParameter;

    public static SellHuoRecord newInstance(){
        return new SellHuoRecord();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_huo;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataSellHuoRecord:
                getData();
                break;
            case updateGoodProduct:
                onClickHuoProduct();
//                Logger.e("---详情"+huoProduct.getDescription());
//                showProductlPopupWindow();
                break;
        }
    }

    @Override
    protected void initView() {
        huoRecordAdapter = new HuoRecordAdapter(R.layout.item_sell_huo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        recycle_sell_huo.setLayoutManager(gridLayoutManager);
        recycle_sell_huo.setAdapter(huoRecordAdapter);

    }

    @Override
    protected void initData() {
        tcpAiClient= TcpAiClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        tcpClient= tcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        huoProduct= HuoProduct.getInstance();
        globalParameter=GlobalParameter.getInstance();
        huoRecordS=HuoRecordS.getInstance();
        postSellHuo("product");
        onItemClick();

    }
    /**
     * 获取数据
     */
    private void getData(){

        huoRecordList=huoRecordS.getHuoRecordList();
        Logger.e("获取商品列表的长度"+huoRecordList.size());
        huoRecordAdapter.setNewData(huoRecordList);
    }
    /**
     * 列表点击事件
     */
    private void onItemClick(){
       huoRecordAdapter.setOnItemClickListener(new HuoRecordAdapter.OnItemClickListener() {
           @Override
           public void onClick(int position) {
               Logger.e("点击"+position+"-----"+huoRecordList.get(position).getHuoID());
//               toast("点击"+position);
               postGoodProduct(huoRecordList.get(position).getHuoID());
           }

           @Override
           public void onLongClick(int position) {

           }
       });
    }
    /**
     * 发送获取商品记录的请求
     */
    private void postSellHuo(String value){
        Logger.e("发送获取商品记录-------");
        DDRAIServiceCmd.reqStockInfo reqStockInfo=DDRAIServiceCmd.reqStockInfo.newBuilder()
                .setContent(ByteString.copyFromUtf8(value))
                .build();
        if (globalParameter.isLan()){
            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqStockInfo);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqStockInfo);
        }
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("发送货物时间"+ ss);

    }
    /**
     * 发送获取商品详情的请求
     */
    private void postGoodProduct(String goodID){
        DDRAIServiceCmd.reqGoodsDetail reqGoodsDetail=DDRAIServiceCmd.reqGoodsDetail.newBuilder()
                .setGoodsID(ByteString.copyFromUtf8(goodID))
                .build();
        if (globalParameter.isLan()){
            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGoodsDetail);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGoodsDetail);
        }
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("发送货物详情时间"+ ss);
    }

    private CustomPopuWindow customPopuWindow;
    private TextView tv_name;
    private ImageView iv_imageLan;
    private ImageView iv_imageWan;
    private TextView tv_description;
    private TextView tv_price;
    private TextView tv_startdata;
    private TextView tv_lifedata;
    private ImageView iv_quit;
    private Handler handler;
    /**
     * 点击详情弹窗
     */
    private void showProductlPopupWindow() {
        View contentView = null;
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_good_product, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getContext())
                .setView(contentView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAsDropDown(findViewById(R.id.recycle_sell_huo),50, 50);
        iv_imageLan=contentView.findViewById(R.id.iv_product);
        tv_name=contentView.findViewById(R.id.tv_p_name);
        tv_description=contentView.findViewById(R.id.tv_description);
        tv_price=contentView.findViewById(R.id.tv_p_price);
        tv_startdata=contentView.findViewById(R.id.tv_p_data);
        tv_lifedata=contentView.findViewById(R.id.tv_live_data);
        iv_quit=contentView.findViewById(R.id.iv_product_quit);
        if (huoProduct!=null){
            tv_name.setText(cutName(huoProduct.getName()));
            tv_description.setText(cutName(huoProduct.getDescription()));
            tv_price.setText("￥ "+huoProduct.getPrice());
            tv_startdata.setText("生产日期：   "+huoProduct.getStartdata());
            tv_lifedata.setText("保质期：   "+huoProduct.getLifedata());
        }
        if (globalParameter.isLan()){
            try {
                String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人图片" + "/" +"SellImage"+"/"+ huoProduct.getId() + ".png";
                iv_imageLan.setImageBitmap(getLoacalBitmap(pngPath));
//                Glide.with(mContext).load(pngPath).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) iv_imageLan);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                Logger.e(huoProduct.getImageWan());
                String files=huoProduct.getImageWan().split("//")[1];
                String fileDir= files.split("/")[2];        //文件夹名
                String fileName=files.split("/")[4];        //文件名
                String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人图片" + "/" +fileDir+"/"+ fileName;
//                iv_imageLan.setImageBitmap(getLoacalBitmap(pngPath));
                Logger.e("对比地址"+pngPath+"----"+fileDir+"-----"+fileName+"++++++"+files);
                Glide.with(getContext()).load(pngPath).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_imageLan);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        iv_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("点击关闭");
                customPopuWindow.dissmiss();
                try {
                    FloatWindow.get().show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 加载本地图片
     */
    public static Bitmap getLoacalBitmap(String pngPath) {
        try {
//            String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" +"SellImage"+"/"+ dirName + ".png";
            Logger.e("对比的图片地址"+pngPath);
            FileInputStream fis = new FileInputStream(pngPath);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
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

    /**
     * 点击商品详情弹窗
     */
    private void onClickHuoProduct(){
        String pngPath=null;
        if (globalParameter.isLan()){
            try {
                  pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人图片" + "/" +"SellImage"+"/"+ huoProduct.getId() + ".png";
//                iv_imageLan.setImageBitmap(getLoacalBitmap(pngPath));
//                Glide.with(mContext).load(pngPath).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) iv_imageLan);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                Logger.e(huoProduct.getImageWan());
                String files=huoProduct.getImageWan().split("//")[1];
                String fileDir= files.split("/")[2];        //文件夹名
                String fileName=files.split("/")[4];        //文件名
                pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人图片" + "/" +fileDir+"/"+ fileName;
//                iv_imageLan.setImageBitmap(getLoacalBitmap(pngPath));
                Logger.e("对比地址"+pngPath+"----"+fileDir+"-----"+fileName+"++++++"+files);
//                Glide.with(getContext()).load(pngPath).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_imageLan);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        new HuoProductDialog.Builder(getAttachActivity())
                .setName(cutName(huoProduct.getName()))
                .setPrice("￥ "+huoProduct.getPrice())
                .setStartData("生产日期：   "+huoProduct.getStartdata())
                .setDescription(cutName(huoProduct.getDescription()))
                .setLiveDat("保质期：   "+huoProduct.getLifedata())
                .setImage(getLoacalBitmap(pngPath))
                .setListener(new HuoProductDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("关闭");
                    }
                }).show();


    }
    @Override
    protected void onRestart() {
        super.onRestart();
        postSellHuo("product");
        Logger.e("刷新数据");
    }
}
