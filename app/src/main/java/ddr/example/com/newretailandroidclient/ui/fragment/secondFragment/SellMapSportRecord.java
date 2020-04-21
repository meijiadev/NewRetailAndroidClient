package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.MapFileStatus;
import ddr.example.com.newretailandroidclient.entity.info.MapInfo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.other.MapRecord;
import ddr.example.com.newretailandroidclient.entity.other.MapRecordClick;
import ddr.example.com.newretailandroidclient.entity.other.MapRecordS;
import ddr.example.com.newretailandroidclient.entity.other.PageNum;
import ddr.example.com.newretailandroidclient.entity.point.TargetPoint;
import ddr.example.com.newretailandroidclient.other.ExcelUtil;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.adapter.MapRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.PageAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.newretailandroidclient.widget.textview.BasrTextView;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.newretailandroidclient.widget.view.MapImageView;
import ddr.example.com.newretailandroidclient.widget.view.PointView;
import ddr.example.com.newretailandroidclient.widget.view.ZoomImageView;

public class SellMapSportRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_pages_y_map)
    RecyclerView recycle_pages_y_map;
    @BindView(R.id.recycle_sell_map)
    RecyclerView recycle_sell_map;
    @BindView(R.id.iv_sell_m_quit)
    ImageView iv_sell_m_quit;
    @BindView(R.id.tv_sell_map_all)
    BasrTextView tv_sell_map_all;
    @BindView(R.id.tv_sell_map_success)
    BasrTextView tv_sell_map_success;
    @BindView(R.id.tv_sell_map_fail)
    BasrTextView tv_sell_map_fail;
    @BindView(R.id.tv_sell_map_time)
    BasrTextView tv_sell_map_time;
    @BindView(R.id.tv_sell_map_fault)
    BasrTextView tv_sell_map_fault;
    @BindView(R.id.tv_sell_m_s_time)
    TextView tv_sell_m_s_time;
    @BindView(R.id.tv_sell_m_e_time)
    TextView tv_sell_m_e_time;
    @BindView(R.id.tv_sell_m_r_time)
    TextView tv_sell_m_r_time;
    @BindView(R.id.tv_sell_m_result)
    TextView tv_sell_m_result;
    @BindView(R.id.tv_sell_m_order)
    TextView tv_sell_m_order;
    @BindView(R.id.relative_sell_map)
    RelativeLayout relative_sell_map;
    @BindView(R.id.mv_sell_map)
    ZoomImageView mv_sell_map;

    private PageNum pageNum;
    private PageAdapter pageAdapter;
    private List<PageNum> pageNumList;

    private MapRecord mapRecord;
    private MapRecordAdapter mapRecordAdapter;
    private MapRecordS mapRecordS;
    private List<MapRecord> mapRecordList;
    private List<MapRecord> nMapRecordList=new ArrayList<>();

    private int pageNumber;//页数
    private int allPageNumber;//总数量
    private int nowPagepostion=0;//当前页
    private TcpAiClient tcpAiClient;

    private MapRecordClick mapRecordClick;
    private List<MapRecordClick> mapRecordClickList;

    private TargetPoint targetPoint;
    private List<TargetPoint> targetPointList;
    private TargetPointAdapter targetPointAdapter;

    private MapFileStatus mapFileStatus;
    private List<MapInfo> mapInfos = new ArrayList<>(); //地图列表
    private String bitmapPath;          // 点击的图片存储地址
    private Bitmap lookBitmap;          //点击的图片

    private boolean isExcel=false;

    private String filePath = "/sdcard/新零售机器列表";

    private GlobalParameter globalParameter;

    private TcpClient tcpClient;

    public static SellMapSportRecord newInstance(){
        return new SellMapSportRecord();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_map;
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataSellMapRecord:
                if (isExcel){
                    nMapRecordList=mapRecordS.getMapRecordList();
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
        mapRecordAdapter = new MapRecordAdapter(R.layout.item_map_record);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_sell_map.setLayoutManager(layoutManager);
        recycle_sell_map.setAdapter(mapRecordAdapter);

        pageAdapter=new PageAdapter(R.layout.item_page_num);
        LinearLayoutManager layoutManager1 =new LinearLayoutManager(getAttachActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_pages_y_map.setLayoutManager(layoutManager1);
        recycle_pages_y_map.setAdapter(pageAdapter);

    }

    @Override
    protected void initData() {
        tcpAiClient= TcpAiClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        globalParameter=GlobalParameter.getInstance();
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        postSellMap(0,9);
        mapRecordS= MapRecordS.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        onItemClick();
        isChecked();
        tv_sell_map_all.isChecked(true);
        targetPointAdapter = new TargetPointAdapter(R.layout.item_show_recycler);
        onShowItemClick();
        transformMapInfo(mapFileStatus.getMapInfos());
    }
    /**
     * 点击事件
     */
    @OnClick({R.id.iv_add_y,R.id.iv_trigger_y,R.id.tv_sell_map_all,R.id.tv_sell_map_fault,R.id.tv_sell_map_fail,R.id.tv_sell_map_success,R.id.tv_sell_map_time
    ,R.id.iv_sell_m_quit,R.id.tv_d_m_excel})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.iv_add_y:
                Logger.e("--"+nowPagepostion+"++"+pageNumber);
                if (nowPagepostion<pageNumber-1){
                    postSellMap((nowPagepostion+1)*8+(nowPagepostion+1),9);
                    nowPagepostion++;
                    isCheckPage();
                }else {
                    toast("已经是最后一页了");
                }
                break;
            case R.id.iv_trigger_y:
                if (nowPagepostion>1){
                    postSellMap((nowPagepostion-1)*8+(nowPagepostion-1),9);
                    nowPagepostion--;
                    isCheckPage();
                }else {
                    toast("已经是第一页了");
                }
                break;
            case R.id.tv_d_m_excel:
                isExcel=true;
                postSellMap(0,100);
                break;
            case R.id.tv_sell_map_all:
                Logger.e("点击全部");
                tv_sell_map_all.isChecked(true);
                tv_sell_map_success.isChecked(false);
                tv_sell_map_time.isChecked(false);
                tv_sell_map_fail.isChecked(false);
                tv_sell_map_fault.isChecked(false);
                showPopupWindow(tv_sell_map_all,0);
                break;
            case R.id.tv_sell_map_success:
                tv_sell_map_all.isChecked(false);
                tv_sell_map_success.isChecked(true);
                tv_sell_map_time.isChecked(false);
                tv_sell_map_fail.isChecked(false);
                tv_sell_map_fault.isChecked(false);
                setResultClick(0);
                break;
            case R.id.tv_sell_map_time:
                tv_sell_map_all.isChecked(false);
                tv_sell_map_success.isChecked(false);
                tv_sell_map_time.isChecked(true);
                tv_sell_map_fail.isChecked(false);
                tv_sell_map_fault.isChecked(false);
                setResultClick(1);
                break;
            case R.id.tv_sell_map_fail:
                tv_sell_map_all.isChecked(false);
                tv_sell_map_success.isChecked(false);
                tv_sell_map_time.isChecked(false);
                tv_sell_map_fail.isChecked(true);
                tv_sell_map_fault.isChecked(false);
                setResultClick(2);
                break;
            case R.id.tv_sell_map_fault:
                tv_sell_map_all.isChecked(false);
                tv_sell_map_success.isChecked(false);
                tv_sell_map_time.isChecked(false);
                tv_sell_map_fail.isChecked(false);
                tv_sell_map_fault.isChecked(true);
                setResultClick(3);
                break;
            case R.id.iv_sell_m_quit:
                relative_sell_map.setVisibility(View.GONE);
                break;
        }
    }
    /**
     * 获取数据并拆分
     */
    private void getData(){
        mapRecordList=mapRecordS.getMapRecordList();
        allPageNumber=mapRecordS.getCountNum();
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
//            List<MapRecord> value;
//            boolean is=i==offset;
//            Logger.e("是否----"+is+offset+"---"+i);
//            if (i==offset && ysnum>0){
//                value=mapRecordList.subList(i*8+i,(i*8+i)+ysnum);
//            }else {
//                value=mapRecordList.subList(i*8+i,(i+1)*8+i+1);
//            }
//            nMapRecordList.add(value);
//        }
//        Logger.e("数据------"+nMapRecordList.get(1).get(1).getId()+"大小"+nMapRecordList.size());
    }

    /**
     * 插入数据
     */
    private void setData(){
        mapRecordAdapter.setNewData(mapRecordList);
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
     * 页数列表下的点击事件
     */
    private void onClick(){
        pageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            nowPagepostion=position;
            switch (view.getId()){
                case R.id.tv_page_num:
                    tv_page_num=(TextView) view;
                    Logger.e("点击第"+position+"页");
                    isCheckPage();
                    mapRecordS.setPostion(position);
                    postSellMap((position*8+position),9);
                    break;
            }
        });
    }

    /**
     * 地图列表下的点击事件
     */
    private void onItemClick(){
        mapRecordAdapter.setOnItemChildClickListener((adapter, view, position) ->{
            switch (view.getId()){
                case R.id.tv_map_handle:
                    Logger.e("点击地图----");
                    mapRecordS.setItemPostion(position);
                    relative_sell_map.setVisibility(View.VISIBLE);
                    mapRecordClickList=mapRecordList.get(position).getMapRecordClickList();
                    Logger.e("查看地图坐标长度"+mapRecordClickList.size());
                    if (mapInfos.size()>0){
                        for (int i=0;i<mapInfos.size();i++){
                            if (mapInfos.get(i).getMapName().equals(mapRecordList.get(position).getRetail_map())){
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(mapInfos.get(i).getBitmap());
                                    Logger.e("地址"+fis);
                                    lookBitmap= BitmapFactory.decodeStream(fis);
                                    mv_sell_map.setImageBitmap(lookBitmap);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (mapRecordClickList.size()>0){
                        getRetailPoint();
                    }else {
                        toast("当前页面没有售卖点");
                    }
                    break;
            }
        }
        );

    }
    SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
    Date date=new Date(System.currentTimeMillis());//系统小时数
    String ss=formatter.format(date);//获取当前时间
    /**
     * 发送获取地图运行记录的请求
     */
    private void postSellMap(int start,int num){
        DDRAIServiceCmd.reqGetMapRecords reqGetMapRecords=DDRAIServiceCmd.reqGetMapRecords.newBuilder()
                .setStartnum(start)
                .setNeednums(num)
                .build();
        if (globalParameter.isLan()){
            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGetMapRecords);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGetMapRecords);
        }
        toast("发送时间"+ss);


    }

    /**
     * 判断哪个页面是否被选中
     */
    protected void isChecked() {
        tv_sell_map_all.setType(1);
        tv_sell_map_success.setType(1);
        tv_sell_map_time.setType(1);
        tv_sell_map_fail.setType(1);
        tv_sell_map_fault.setType(1);
    }
    /**
     * 获取售卖点信息并解析
     */
    private void getRetailPoint(){
        targetPointList=new ArrayList<>();
        for (int i=0;i<mapRecordClickList.size();i++){
            targetPoint=new TargetPoint();
            targetPoint.setName(mapRecordClickList.get(i).getName());
            targetPoint.setX(mapRecordClickList.get(i).getPosX());
            targetPoint.setY(mapRecordClickList.get(i).getPosY());
            targetPointList.add(targetPoint);
        }
        Logger.e("售卖点SIZE"+targetPointList.size());
        targetPointAdapter.setNewData(targetPointList);

    }

    /**
     * 点击显示弹窗信息
     */
    private CustomPopuWindow customPopuWindow;
    private RecyclerView showRecycler;
    private TextView tv_all_selected;
    private boolean allShowPoint;

    private void showPopupWindow(View view, int type) {
        try {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.window_point, null);
            customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getContext())
                    .setView(contentView)
                    .create()
                    .showAsDropDown(view, 0, 0);
            showRecycler = contentView.findViewById(R.id.show_Recycler);
            tv_all_selected = contentView.findViewById(R.id.tv_all_select);
            if (type != 0 && type != 1) {
                tv_all_selected.setVisibility(View.GONE);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            showRecycler.setLayoutManager(layoutManager);
            switch (type) {
                case 0:
                    showRecycler.setAdapter(targetPointAdapter);
                    targetPointAdapter.setNewData(targetPointList);
                    if (allShowPoint) {
                        tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                        tv_all_selected.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
                        tv_all_selected.setTextColor(getResources().getColor(R.color.text_gray));
                    }
                    break;
            }
            tv_all_selected.setOnClickListener((v)-> {
                switch (type) {
                    case 0:
                        if (allShowPoint) {
                            allShowPoint = false;
                            tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
                            tv_all_selected.setTextColor(getResources().getColor(R.color.text_gray));
                            for (TargetPoint targetPoint : targetPointList) {
                                targetPoint.setMultiple(false);
                            }
                        } else {
                            allShowPoint = true;
                            tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                            tv_all_selected.setTextColor(getResources().getColor(R.color.white));
                            for (TargetPoint targetPoint : targetPointList) {
                                targetPoint.setMultiple(true);
                            }
                        }
                        PointView.getInstance(getContext()).setTargetPoints(targetPointList);
                        mv_sell_map.invalidate();
                        targetPointAdapter.setNewData(targetPointList);
                        tv_sell_m_s_time.setText("");
                        tv_sell_m_e_time.setText("");
                        tv_sell_m_r_time.setText("");
                        tv_sell_m_result.setText("");
                        tv_sell_m_order.setText("");
                        break;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 弹窗事件点击事件
     */
    private void onShowItemClick() {
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (targetPointList.get(position).isMultiple()) {
                Logger.e("点击单个----");
                targetPointList.get(position).setMultiple(false);
                PointView.getInstance(getContext()).setTargetPoints(targetPointList);
                mv_sell_map.invalidate();
                targetPointAdapter.setNewData(targetPointList);
            } else {
                Logger.e("点击单个++++");
                targetPointList.get(position).setMultiple(true);
                PointView.getInstance(getContext()).setTargetPoints(targetPointList);
                mv_sell_map.invalidate();
                tv_sell_m_s_time.setText(mapRecordClickList.get(position).getStart_time());
                tv_sell_m_e_time.setText(mapRecordClickList.get(position).getEnd_time());
                tv_sell_m_r_time.setText(mapRecordClickList.get(position).getRun_time());
                tv_sell_m_result.setText(mapRecordClickList.get(position).getResult());
                tv_sell_m_order.setText("DDR"+position+"_order");
                targetPointAdapter.setNewData(targetPointList);
            }
        });
    }
    /**
     * 点击选中结果
     */
    private void setResultClick(int result){
        Logger.e("结果"+result);
        try {
            switch (result) {
                case 0:
                    for (int i=0;i<mapRecordClickList.size();i++) {
                        Logger.e("结果---"+mapRecordClickList.get(i).getResult());
                        if (mapRecordClickList.get(i).getResult().equals("售货成功")) {
                            targetPointList.get(i).setMultiple(true);
                        } else {
                            targetPointList.get(i).setMultiple(false);
                        }
                    }
                    break;
                case 1:
                    for (int i=0;i<mapRecordClickList.size();i++) {
                        if (mapRecordClickList.get(i).getResult().equals("售货超时")) {
                            targetPointList.get(i).setMultiple(true);
                        } else {
                            targetPointList.get(i).setMultiple(false);
                        }
                    }
                    break;
                case 2:
                    for (int i=0;i<mapRecordClickList.size();i++) {
                        if (mapRecordClickList.get(i).getResult().equals("出货失败")) {
                            targetPointList.get(i).setMultiple(true);
                        } else {
                            targetPointList.get(i).setMultiple(false);
                        }
                    }
                    break;
                case 3:
                    for (int i=0;i<mapRecordClickList.size();i++) {
                        if (mapRecordClickList.get(i).getResult().equals("售卖故障")) {
                            targetPointList.get(i).setMultiple(true);
                        } else {
                            targetPointList.get(i).setMultiple(false);
                        }
                    }
                    break;
            }
            Logger.e("----"+targetPointList.size());
            PointView.getInstance(getContext()).setTargetPoints(targetPointList);
            mv_sell_map.invalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置图片的路径
     *
     * @param infoList
     */
    public void transformMapInfo(List<MapInfo> infoList) {
        for (int i = 0; i < infoList.size(); i++) {
            String dirName = infoList.get(i).getMapName();
            String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" + dirName + "/" + "bkPic.png";
            if (pngPath != null) {
                infoList.get(i).setBitmap(pngPath);
            } else {
                infoList.remove(i);
            }
            if (dirName.equals(NotifyBaseStatusEx.getInstance().getCurroute())) {
                infoList.get(i).setUsing(true);
            } else {
                infoList.get(i).setUsing(false);
            }
        }
        mapInfos = infoList;

    }

    private void exportExcel(Context context) {

        File file = new File(filePath);
        if (!file.exists()) {
            Logger.e("不存在目录");
            file.mkdirs();
        }else {
            Logger.e("存在目录"+filePath);
        }

        String excelFileName = "/地图运行记录.xls";

        String[] title = {"ID","启动时间", "结束结果", "运行时长","售卖地图","售卖次数"};

        String sheetName = "地图运行记录";

        filePath = filePath + excelFileName;

        ExcelUtil.initExcel(filePath, sheetName, title);

        ExcelUtil.writeObjListToExcel(nMapRecordList, filePath, context,3);

        Logger.e("excel已导出至：" + filePath);

        toast("Excel已导出至"+filePath);

        filePath = "/sdcard/AndroidExcelDemo";
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        postSellMap(0,9);
        Logger.e("刷新数据");
    }
}
