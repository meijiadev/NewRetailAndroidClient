package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.ChongRecord;
import ddr.example.com.newretailandroidclient.entity.other.ChongRecordS;
import ddr.example.com.newretailandroidclient.entity.other.PageNum;
import ddr.example.com.newretailandroidclient.other.ExcelUtil;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.adapter.ChongRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.PageAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;

public class SellChongRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_sell_chong)
    RecyclerView recycle_sell_chong;
    @BindView(R.id.recycle_pages_y_chong)
    RecyclerView recycle_pages_y_chong;
    @BindView(R.id.tv_d_c_excel)
    TextView tv_d_c_excel;
    private PageNum pageNum;
    private PageAdapter pageAdapter;
    private List<PageNum> pageNumList;
    private TcpAiClient tcpAiClient;

    private ChongRecord chongRecord;
    private ChongRecordAdapter chongRecordAdapter;
    private ChongRecordS chongRecordS;
    private List<ChongRecord> chongRecordList;
    private List<ChongRecord> nChongRecordList=new ArrayList<>();

    private int pageNumber;//页数
    private int allPageNumber;//总数量
    private int nowPagepostion=0;//当前页

    private boolean isExcel=false;

    private String filePath = "/sdcard/新零售机器列表";

    private GlobalParameter globalParameter;

    private TcpClient tcpClient;

    public static SellChongRecord newInstance(){
        return new SellChongRecord();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_chong;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataSellChongRecord:
                if (isExcel){
                    nChongRecordList=chongRecordS.getChongRecordList();
                    exportExcel(getContext());
                    isExcel=false;
                }else {
                    getData();
                    setData();
                    setPageData();
                    onClick();
                    isCheckPage();
                }
                break;
        }
    }

    @Override
    protected void initView() {
        chongRecordAdapter = new ChongRecordAdapter(R.layout.item_chong_record);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_sell_chong.setLayoutManager(layoutManager);
        recycle_sell_chong.setAdapter(chongRecordAdapter);

        pageAdapter=new PageAdapter(R.layout.item_page_num);
        LinearLayoutManager layoutManager1 =new LinearLayoutManager(getAttachActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_pages_y_chong.setLayoutManager(layoutManager1);
        recycle_pages_y_chong.setAdapter(pageAdapter);

    }

    @Override
    protected void initData() {
        tcpAiClient= TcpAiClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        globalParameter=GlobalParameter.getInstance();
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        postSellChong(0,9);
        chongRecordS=ChongRecordS.getInstance();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
    /**
     * 点击事件
     */
    @OnClick({R.id.iv_add_y,R.id.iv_trigger_y,R.id.tv_d_c_excel})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.iv_add_y:
                Logger.e("--"+nowPagepostion+"++"+pageNumber);
                if (nowPagepostion<pageNumber-1){
                    postSellChong((nowPagepostion+1)*8+(nowPagepostion+1),9);
                    nowPagepostion++;
                    isCheckPage();
                }else {
                    toast("已经是最后一页了");
                }
                break;
            case R.id.iv_trigger_y:
                if (nowPagepostion>1){
                    postSellChong((nowPagepostion-1)*8+(nowPagepostion-1),9);
                    nowPagepostion--;
                    isCheckPage();
                }else {
                    toast("已经是第一页了");
                }
                break;
            case R.id.tv_d_c_excel:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否输出日志到本地")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                isExcel=true;
                                postSellChong(0,100);
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消");
                            }
                        }).show();
                break;
        }
    }
    /**
     * 获取数据并拆分
     */
    private void getData(){
        chongRecordList=chongRecordS.getChongRecordList();
        Logger.e("数量"+chongRecordList.size());
        allPageNumber=chongRecordS.getCountNum();
        int ysnum=allPageNumber%9;//余数
        if(ysnum==0){
            pageNumber=allPageNumber/9;
        }else {
            pageNumber=(allPageNumber/9)+1;
        }
        Logger.e("页数"+pageNumber);
        Logger.e("余数"+ysnum);
        //偏移量
//        int offset = pageNumber-1;
//        for (int i = 0; i < pageNumber; i++) {
//            List<ChongRecord> value;
//            boolean is=i==offset;
//            Logger.e("是否----"+is+offset+"---"+i);
//            if (i==offset && ysnum>0){
//                value=chongRecordList.subList(i*8+i,(i*8+i)+ysnum);
//            }else {
//                value=chongRecordList.subList(i*8+i,(i+1)*8+i+1);
//            }
//            nChongRecordList.add(value);
//        }
//        Logger.e("数据------"+nChongRecordList.get(1).get(1).getId()+"大小"+nChongRecordList.size());
    }

    /**
     * 插入数据
     */
    private void setData(){
        Logger.e("数量"+chongRecordList.size());
        chongRecordAdapter.setNewData(chongRecordList);
    }
    /**
     * 插入页数
     */
    private void setPageData(){
        pageNumList=new ArrayList<>();
        Logger.e("数据"+pageNumber);
        for (int i=0;i<pageNumber;i++){
            pageNum=new PageNum();
            pageNum.setPagenum(i+1);
            pageNumList.add(pageNum);
        }
//        pageAdapter.getItem(0).setSelected(true);
        pageAdapter.setNewData(pageNumList);
    }

    /**
     * 判断页数是否选中
     */
    private void isCheckPage(){
        for (int i=0;i<pageNumList.size();i++){
            pageNumList.get(i).setSelected(false);
        }
        pageNumList.get(nowPagepostion).setSelected(true);
        pageAdapter.setNewData(pageNumList);
    }

    private TextView tv_page_num;
    /**
     * 列表下的点击事件
     */
    private void onClick(){
        pageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            nowPagepostion=position;
            switch (view.getId()){
                case R.id.tv_page_num:
                    tv_page_num=(TextView) view;
                    Logger.e("点击第"+position+"页");
                    isCheckPage();
                    chongRecordS.setPostion(position);
                    postSellChong((position*8+position),9);
                    break;
            }
        });
    }
    /**
     * 发送获取零售记录的请求
     */
    private void postSellChong(int start,int num){
        DDRAIServiceCmd.reqGetChargingRecords reqGetChargingRecords=DDRAIServiceCmd.reqGetChargingRecords.newBuilder()
                .setStartnum(start)
                .setNeednums(num)
                .build();
        if (globalParameter.isLan()){
            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGetChargingRecords);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGetChargingRecords);
        }
    }

    private void exportExcel(Context context) {

        File file = new File(filePath);
        if (!file.exists()) {
            Logger.e("不存在目录");
            file.mkdirs();
        }else {
            Logger.e("存在目录"+filePath);
        }

        String excelFileName = "/充电记录.xls";

        String[] title = {"ID","充电指令时间", "对接结果", "开始充电时间","开始充电电量","结束充电时间","结束充电电量","充电耗时"};

        String sheetName = "充电记录";

        filePath = filePath + excelFileName;

        ExcelUtil.initExcel(filePath, sheetName, title);

        ExcelUtil.writeObjListToExcel(nChongRecordList, filePath, context,2);

        Logger.e("excel已导出至：" + filePath);

        toast("Excel已导出至"+filePath);

        filePath = "/sdcard/新零售机器列表";
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        postSellChong((nowPagepostion*8+nowPagepostion),9);
        Logger.e("刷新数据");
    }

}
