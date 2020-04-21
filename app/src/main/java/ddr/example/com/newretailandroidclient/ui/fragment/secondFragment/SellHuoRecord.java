package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.protobuf.ByteString;
import com.yhao.floatwindow.FloatWindow;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import butterknife.BindView;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.HuoProduct;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecordS;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.ui.adapter.ChongRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.HuoRecordAdapter;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;

public class SellHuoRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_sell_huo)
    RecyclerView recycle_sell_huo;

    private HuoRecord huoRecord;
    private HuoRecordAdapter huoRecordAdapter;
    private List<HuoRecord> huoRecordList;
    private HuoRecordS huoRecordS;
    private TcpAiClient tcpAiClient;
    private HuoProduct huoProduct;

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
                huoProduct= (HuoProduct) messageEvent.getData();
                Logger.e("---详情"+huoProduct.getDescription());
                showProductlPopupWindow();
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
               toast("点击"+position);
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
        tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqStockInfo);

    }
    /**
     * 发送获取商品详情的请求
     */
    private void postGoodProduct(String goodID){
        DDRAIServiceCmd.reqGoodsDetail reqGoodsDetail=DDRAIServiceCmd.reqGoodsDetail.newBuilder()
                .setGoodsID(ByteString.copyFromUtf8(goodID))
                .build();
        tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGoodsDetail);
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
        new Thread(new Runnable(){
            @Override
            public void run() {
                Logger.e("图片地址A---"+huoProduct.getImageLan());
                getHttpBitmap(huoProduct.getImageLan());
            }
        }).start();
        View contentView = null;
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_good_product, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getContext())
                .setView(contentView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAtLocation(findViewById(R.id.recycle_sell_huo), Gravity.CENTER,0, 0);
        iv_imageLan=contentView.findViewById(R.id.iv_product);
        tv_name=contentView.findViewById(R.id.tv_p_name);
        tv_description=contentView.findViewById(R.id.tv_description);
        tv_price=contentView.findViewById(R.id.tv_p_price);
        tv_startdata=contentView.findViewById(R.id.tv_p_data);
        tv_lifedata=contentView.findViewById(R.id.tv_live_data);
        iv_quit=contentView.findViewById(R.id.iv_product_quit);
        if (huoProduct!=null){
            tv_name.setText(huoProduct.getName());
            tv_description.setText(huoProduct.getDescription());
            tv_price.setText("￥ "+huoProduct.getPrice());
            tv_startdata.setText("生产日期：   "+huoProduct.getStartdata());
            tv_lifedata.setText("保质期：   "+huoProduct.getLifedata()+" 天");
        }
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap= (Bitmap) msg.obj;
                iv_imageLan.setImageBitmap(bitmap);
            }
        };
        iv_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
     * 根据地址获取图片
     * @param url
     */
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
//            conn.connect();
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
    @Override
    protected void onRestart() {
        super.onRestart();
        postSellHuo("product");
        Logger.e("刷新数据");
    }
}
