package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ddr.example.com.newretailandroidclient.entity.other.PageNum;
import ddr.example.com.newretailandroidclient.entity.other.RetailRecord;
import ddr.example.com.newretailandroidclient.entity.other.RetailRecordS;
import ddr.example.com.newretailandroidclient.other.ExcelUtil;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.adapter.PageAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.RetailRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;

/**
 * time：2020/04/01
 * desc 零售记录界面
 */
public class SellRetaileRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_sell_retail)
    RecyclerView recycle_sell_retail;
    @BindView(R.id.recycle_pages_y)
    RecyclerView recycle_pages_y;
    @BindView(R.id.tv_d_excel)
    TextView tv_d_excel;

    private RetailRecord retailRecord;
    private RetailRecordAdapter retailRecordAdapter;
    private RetailRecordS retailRecordS;
    private List<RetailRecord> retailRecordList;
    private List<RetailRecord> nRetaillist = new ArrayList<>();

    private PageNum pageNum;
    private PageAdapter pageAdapter;
    private List<PageNum> pageNumList;

    private int pageNumber;//页数
    private int allPageNumber;//总数量
    private int nowPagepostion=0;
    private TcpAiClient tcpAiClient;
    private TcpClient tcpClient;

    private AlertDialog alertDialog;
    private AlertDialog mDialog;


    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private int REQUEST_PERMISSION_CODE = 1000;


    private String filePath = "/sdcard/新零售机器列表";

    private boolean isExcel=false;

    private GlobalParameter globalParameter;
    public static SellRetaileRecord newInstance(){
        return new SellRetaileRecord();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_retailr;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataSellsRecord:
                if (isExcel){
                    nRetaillist=retailRecordS.getRetailRecordList();
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
        retailRecordAdapter = new RetailRecordAdapter(R.layout.item_retail_record);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_sell_retail.setLayoutManager(layoutManager);
        recycle_sell_retail.setAdapter(retailRecordAdapter);

        pageAdapter=new PageAdapter(R.layout.item_page_num);
        LinearLayoutManager layoutManager1 =new LinearLayoutManager(getAttachActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_pages_y.setLayoutManager(layoutManager1);
        recycle_pages_y.setAdapter(pageAdapter);


    }

    @Override
    protected void initData() {
        tcpAiClient= TcpAiClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        globalParameter=GlobalParameter.getInstance();
        postSellData(0,9);
        retailRecordS=RetailRecordS.getInstance();
//        requestPermission();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
    /**
     * 点击事件
     */
    @OnClick({R.id.iv_add_y,R.id.iv_trigger_y,R.id.tv_d_excel})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.iv_add_y:
                Logger.e("--"+nowPagepostion+"++"+pageNumber);
                if (nowPagepostion<pageNumber-1){
                    postSellData((nowPagepostion+1)*8+(nowPagepostion+1),9);
                    nowPagepostion++;
                    isCheckPage();
                }else {
                    toast("已经是最后一页了");
                }
                break;
            case R.id.iv_trigger_y:
                if (nowPagepostion>1){
                    postSellData((nowPagepostion-1)*8+(nowPagepostion-1),9);
                    nowPagepostion--;
                    isCheckPage();
                }else {
                    toast("已经是第一页了");
                }
                break;
            case R.id.tv_d_excel:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否输出日志到本地")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                isExcel=true;
                                postSellData(0,100);
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
        retailRecordList= retailRecordS.getRetailRecordList();
        Logger.e(retailRecordList.size()+"原来lisi"+retailRecordS.getRetailRecordList().size());
        allPageNumber=retailRecordS.getCountNum();
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
//            List<RetailRecord> value;
//            boolean is=i==offset;
//            Logger.e("是否----"+is+offset+"---"+i);
//            if (i==offset && ysnum>0){
//                value=retailRecordList.subList(i*8+i,(i*8+i)+ysnum);
//            }else {
//                value=retailRecordList.subList(i*8+i,(i+1)*8+i+1);
//            }
//            nRetaillist.add(value);
//        }
//        Logger.e("数据------"+nRetaillist.get(0).get(0).getId()+"大小"+nRetaillist.size());
    }

    /**
     * 插入数据
     */
    private void setData(){
        SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
//        toast("设置时间"+ss);
        retailRecordAdapter.setNewData(retailRecordList);
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
                    retailRecordS.setPostion(position);
                    postSellData((position*8+position),9);
                    break;
            }
        });
    }

    /**
     * 发送获取零售记录的请求
     */
    private void postSellData(int start,int end){
        DDRAIServiceCmd.reqAllSellsRecord reqAllSellsRecord=DDRAIServiceCmd.reqAllSellsRecord.newBuilder()
                .setStartnum(start)
                .setNeednums(end)
                .build();
        Logger.e("是否局域网"+globalParameter.isLan());
        if (globalParameter.isLan()){
            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqAllSellsRecord);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqAllSellsRecord);
            SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
            Date date=new Date(System.currentTimeMillis());//系统小时数
            String ss=formatter.format(date);//获取当前时间
            Logger.e("发送时间"+ ss);
//            toast("发送时间"+ ss);
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

        String excelFileName = "/零售记录.xls";

        String[] title = {"ID","商品名称", "结算方式", "结算编号","单价","数量","总价"};

        String sheetName = "零售记录";

        filePath = filePath + excelFileName;

        ExcelUtil.initExcel(filePath, sheetName, title);

        ExcelUtil.writeObjListToExcel(nRetaillist, filePath, context,0);

        Logger.e("excel已导出至：" + filePath);

        filePath = "/sdcard/新零售机器列表";
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        postSellData((nowPagepostion*8+nowPagepostion),9);
        Logger.e("刷新数据");
    }
}
