package ddr.example.com.newretailandroidclient.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRActivity;
import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.MapFileStatus;
import ddr.example.com.newretailandroidclient.entity.info.MapInfo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusExTwo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfoTwo;
import ddr.example.com.newretailandroidclient.entity.point.PathLine;
import ddr.example.com.newretailandroidclient.entity.point.TargetPoint;
import ddr.example.com.newretailandroidclient.entity.point.TaskMode;
import ddr.example.com.newretailandroidclient.helper.ListTool;
import ddr.example.com.newretailandroidclient.other.DpOrPxUtils;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;
import ddr.example.com.newretailandroidclient.widget.textview.BasrTextView;
import ddr.example.com.newretailandroidclient.widget.textview.GridTextView;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.newretailandroidclient.widget.view.GridLayerView;
import ddr.example.com.newretailandroidclient.widget.view.LineView;
import ddr.example.com.newretailandroidclient.widget.view.PointView;
import ddr.example.com.newretailandroidclient.widget.view.ZoomImageView;

public class AllRetailActivity extends DDRActivity {
    @BindView(R.id.tv_all_quit)
    TextView tvAllQuit;
    @BindView(R.id.tv_all_reference)
    TextView tvAllReference;
    @BindView(R.id.iv_all_jt)
    ImageView ivAllJt;
    @BindView(R.id.iv_all_cd)
    ImageView ivAllCd;
    @BindView(R.id.tv_all_name1)
    TextView tvAllName1;
    @BindView(R.id.tv_all_num1)
    TextView tvAllNum1;
    @BindView(R.id.tv_all_status)
    TextView tvAllStatus;
    @BindView(R.id.iv_all_jt2)
    ImageView ivAllJt2;
    @BindView(R.id.iv_all_cd2)
    ImageView ivAllCd2;
    @BindView(R.id.tv_all_name2)
    TextView tvAllName2;
    @BindView(R.id.tv_all_num2)
    TextView tvAllNum2;
    @BindView(R.id.tv_all_status2)
    TextView tvAllStatus2;
    @BindView(R.id.tv_all_start)
    BasrTextView tvAllStart;
    @BindView(R.id.tv_all_stop)
    BasrTextView tvAllStop;
    @BindView(R.id.tv_all_end)
    BasrTextView tvAllEnd;
//    @BindView(R.id.iv_all_mapImage)
//    MapImageView0 iv_all_mapImage;
//    @BindView(R.id.iv_all_mapImage1)
//    MapImageView1 iv_all_mapImage1;
    @BindView(R.id.tv_now_task)
    TextView tv_now_task;
    @BindView(R.id.mv_sell_auto)
    ZoomImageView mv_sell_auto;

    private GlobalParameter globalParameter;
    private NotifyEnvInfo notifyEnvInfo;
    private NotifyEnvInfoTwo notifyEnvInfoTwo;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyBaseStatusExTwo notifyBaseStatusExTwo;
    private int batteryNum=0;
    private int batteryNum2=0;
    private TcpClient tcpClient;
    private String mapName;//地图名
    private String taskName;//任务名
    private int lsNum=1; //临时任务次数

    private MapFileStatus mapFileStatus;
    private List<MapInfo> mapInfoList=new ArrayList<>();

    private LineView lineView;
    private List<PathLine> pathLines = new ArrayList<>();                         //解析后的路径列表
    private List<PathLine> selectPaths;

    private RecyclerView recycler_task_check;
    private CustomPopuWindow customPopWindow;
    private StringAdapter taskCheckAdapter;
    private List<String> groupList=new ArrayList<>();
    private Bitmap lookBitmap;          //点击的图片
    private float x1=0;
    private float y1=0;
    private float x2=0;
    private float y2=0;

    private List<TargetPoint> targetPointList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateDDRVLNMap:
                try {
                    Logger.e("------地图名："+mapFileStatus.getMapName()+"当前"+mapName+"地图列表长度"+mapFileStatus.getMapInfos().size());
                    mapInfoList=mapFileStatus.getMapInfos();
                    Logger.e("目标点大小"+mapInfoList.size());
//                    transformMapInfo(mapFileStatus.getMapInfos());
                    pathLines = ListTool.deepCopy(mapFileStatus.getPathLines());
                    targetPoints = ListTool.deepCopy(mapFileStatus.getTargetPoints());
                    Logger.e("目标点大小"+targetPoints.size()+mapInfoList.size());
                    if (mapInfoList.size()>0){
                        for (int i=0;i<mapInfoList.size();i++){
                            if (mapInfoList.get(i).getMapName().equals(mapName)){
                                byte[]bytes=mapInfoList.get(i).getBytes();
                                if (bytes!=null){
                                    Logger.e("地图名图片字节流");
                                    lookBitmap=getBitmapFromByte(bytes);
//                                    iv_all_mapImage.setBitamp(getBitmapFromByte(bytes));
                                }else {
                                    Logger.e("地图名图片字节流");
                                    FileInputStream fis = null;
                                    try {
                                        fis = new FileInputStream(mapInfoList.get(i).getBitmap());
                                        Logger.e("地址"+fis);
                                        lookBitmap= BitmapFactory.decodeStream(fis);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
//                                    iv_all_mapImage.setMapBitmap(mapName);
                                }
                                mv_sell_auto.setImageBitmap(lookBitmap);
                            }
                        }
                    }else if(mapName!=null && !mapName.equals("PathError")){
                        toast("首次加载较慢，请稍等");
                        Logger.e("广域网未获取到地图，请查看网络连接");
                    }

                    for (int i=0;i<pathLines.size();i++){
                        Logger.e("任务"+taskName);
                        if (pathLines.get(i).equals(taskName)){
                            LineView.getInstance(getApplication()).setPoints(pathLines.get(i).getPathPoints());
                        }else if (pathLines.get(i).equals("PathError")){
                            Logger.e("当前无任务");
                        }
                    }
                    Logger.e("列表长度"+mapFileStatus.getTaskModes().size()+"-----");
                    groupList=new ArrayList<>();
                    for (TaskMode taskMode:mapFileStatus.getTaskModes()){
                        groupList.add(taskMode.getName());
                    }
                    Logger.e("列表长度"+groupList);
                    taskCheckAdapter.setNewData(groupList);
//                    iv_all_mapImage1.setMapImageView0(iv_all_mapImage);
//                    iv_all_mapImage1.startThread();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case updateMapList:
                Logger.e("-------------updateMapList");
                getMapInfo();
                break;
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_auto_retail;
    }

    @Override
    protected void initView() {
        super.initView();
        taskCheckAdapter=new StringAdapter(R.layout.item_recycle_task_check);
    }

    @Override
    protected void initData() {
        super.initData();
        tcpClient=TcpClient.getInstance(getActivity(), ClientMessageDispatcher.getInstance());
        Logger.e("------111111");
        tcpClient.requestFile();     //请求所有地图
        globalParameter = GlobalParameter.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyBaseStatusExTwo = NotifyBaseStatusExTwo.getInstance();
        notifyEnvInfoTwo = NotifyEnvInfoTwo.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        lineView=LineView.getInstance(getApplication());
//        setMapImage();
        isAllChecked();
//        for (int i=0;i<mapFileStatus.getcTaskModes().size();i++){
//            groupList.add(mapFileStatus.getcTaskModes().get(i).getName());
//        }
//        taskCheckAdapter.setNewData(groupList);
        mv_sell_auto.setRotaion(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_all_quit, R.id.tv_all_reference, R.id.tv_all_start, R.id.tv_all_stop, R.id.tv_all_end,R.id.tv_now_task})
    public void onViewClicked(View view) {
        Logger.e("----mapName:"+mapName+"taskName:"+taskName);
        switch (view.getId()) {
            case R.id.tv_all_quit:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否退出")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                onBack();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
                break;
            case R.id.tv_all_reference:
                showPopupWindowReference(tvAllReference);
                break;
            case R.id.tv_all_start:
               onClickStart();
               tvAllStart.isChecked(true);
               tvAllStop.isChecked(false);
               tvAllEnd.isChecked(false);
                break;
            case R.id.tv_all_stop:
               onClickStop();
                tvAllStart.isChecked(false);
                tvAllStop.isChecked(true);
                tvAllEnd.isChecked(false);
                break;
            case R.id.tv_all_end:
              onClickEnd();
                tvAllStart.isChecked(false);
                tvAllStop.isChecked(false);
                tvAllEnd.isChecked(true);
                break;
            case R.id.tv_now_task:
                showTaskPopupWindow(tv_now_task);
                tv_now_task.setBackgroundResource(R.drawable.task_check_bg);
                break;
        }
    }

    /**
     * 判断哪个页面是否被选中
     */
    private void isAllChecked() {
        tvAllStart.setType(1);
        tvAllStop.setType(1);
        tvAllEnd.setType(1);
    }

    private void initStatusBar() {
        targetPointList=new ArrayList<>();
        if (notifyBaseStatusEx != null) {
            try {
                DecimalFormat df = new DecimalFormat("0");
                DecimalFormat format = new DecimalFormat("0.00");
                mapName = notifyBaseStatusEx.getCurroute();
                taskName = notifyBaseStatusEx.getCurrpath();
//                lsNum=notifyBaseStatusEx.getTemopTaskNum();
//                Logger.e("电量"+notifyEnvInfo.getBatt());
//                Logger.d("xy"+notifyBaseStatusEx.getPosX()+"----"+notifyBaseStatusEx.getPosY());
                TargetPoint targetPoint=new TargetPoint();
                x1=notifyBaseStatusEx.getPosX();
                y1=notifyBaseStatusEx.getPosY();
                float angel=radianToangle(notifyBaseStatusEx.getPosDirection());
                if (x1!=0 && y1!=0){
//                    x1=(float) (Math.random() * 3 );
//                    y1=(float) (Math.random() * 2 );
                    targetPoint.setY(x1);
                    targetPoint.setY(y1);
                    targetPoint.setTheta((int)angel);
                    targetPoint.setName("机器一");
                    targetPointList.add(targetPoint);
                }else {
//                    Logger.e("没有收到设备1当前坐标");
                }
                if (notifyEnvInfo.getBatt()>0 && String.valueOf(notifyEnvInfo.getBatt())!=null){
                    batteryNum=Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
                    tvAllNum1.setText(batteryNum+"%");
                }else {
                    tvAllNum1.setText("");
                }
                if (notifyBaseStatusEx.getRobotid()!=null){
                    tvAllName1.setText(notifyBaseStatusEx.getRobotid());
                }else {
                    tvAllName1.setText("");
                }
                switch (notifyBaseStatusEx.getStopStat()) {
                    case 4:
                        ivAllJt.setImageResource(R.mipmap.jt_check);
                        break;
                    case 8:
                        ivAllJt.setImageResource(R.mipmap.jt_default);
                        break;
                    case 12:
                        ivAllJt.setImageResource(R.mipmap.jt_check);
                        break;
                    case 0:
                        ivAllJt.setImageResource(R.mipmap.jt_default);
                        break;
                }
                switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
                    case 0:
//                        tvAllStatus.setText("自标定中");
                        break;
                    case 1:
                        switch (notifyBaseStatusEx.getMode()) {
                            case 1:
                                //Logger.e("待命模式" + modeView.getText());
                                tvAllStatus.setText("待命中");
                                break;
                            case 3:
                                tvAllStatus.setText("运动中");
                                switch (notifyBaseStatusEx.getSonMode()){
                                    case 3:
                                        tvAllStatus.setText("异常");
                                        break;
                                    case 15:
                                        tvAllStatus.setText("重定位中");
                                        break;
                                }
                                break;
                        }
                        break;
                }
                if(notifyBaseStatusEx.isChargingStatus()) {
                    ivAllCd.setImageResource(R.mipmap.chongd_check);
                }else {
                    ivAllCd.setImageResource(R.mipmap.chongd_def);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if (notifyBaseStatusExTwo != null) {
            try {
                DecimalFormat df = new DecimalFormat("0");
                DecimalFormat format = new DecimalFormat("0.00");
//                mapName = notifyBaseStatusExTwo.getCurroute();
//                taskName = notifyBaseStatusExTwo.getCurrpath();
                TargetPoint targetPoint1= new TargetPoint();
                x2=notifyBaseStatusExTwo.getPosX();
                y2=notifyBaseStatusExTwo.getPosY();
                float ange2=radianToangle(notifyBaseStatusExTwo.getPosDirection());
                if (x2!=0 || y2!=0){
//                    Logger.d("xy"+x2+"----"+y2);
                    targetPoint1.setX(x2);
                    targetPoint1.setY(y2);
                    targetPoint1.setTheta((int)ange2);
                    targetPoint1.setName("机器二");
                    targetPointList.add(targetPoint1);
                }else {
                    Logger.e("没有收到设备2当前坐标");
                }
//                Logger.d("电量"+notifyEnvInfoTwo.getBatt()+taskName);
                if (notifyEnvInfoTwo.getBatt()>0 && String.valueOf(notifyEnvInfoTwo.getBatt())!=null){
                    batteryNum2=Integer.parseInt(df.format(notifyEnvInfoTwo.getBatt()));
                    tvAllNum2.setText(batteryNum2+" %");
                }else {
                    tvAllNum2.setText(" ");
                }
                if (notifyBaseStatusExTwo.getRotbotid()!=null){
                    tvAllName2.setText(notifyBaseStatusExTwo.getRotbotid());
                }else {
                    tvAllName2.setText("");
                }
                switch (notifyBaseStatusExTwo.getStopStat()) {
                    case 4:
                        ivAllJt2.setImageResource(R.mipmap.jt_check);
                        break;
                    case 8:
                        ivAllJt2.setImageResource(R.mipmap.jt_default);
                        break;
                    case 12:
                        ivAllJt2.setImageResource(R.mipmap.jt_check);
                        break;
                    case 0:
                        ivAllJt2.setImageResource(R.mipmap.jt_default);
                        break;
                }
                switch (notifyBaseStatusExTwo.geteSelfCalibStatus()) {
                    case 0:
//                        tvAllStatus2.setText("自标定中");
                        //自标定
                        break;
                    case 1:
                        switch (notifyBaseStatusExTwo.getMode()) {
                            case 1:
                                //Logger.e("待命模式" + modeView.getText());
                                tvAllStatus2.setText("待命中");
                                break;
                            case 3:
                                tvAllStatus2.setText("运动中");
                                switch (notifyBaseStatusExTwo.getSonMode()){
                                    case 3:
                                        tvAllStatus2.setText("异常");
                                        break;
                                    case 15:
                                        tvAllStatus2.setText("重定位中");
                                        break;
                                }
                                break;
                        }
                        break;
                }
                if(notifyBaseStatusExTwo.isChargingStatus()) {
                    ivAllCd2.setImageResource(R.mipmap.chongd_check);
                }else {
                    ivAllCd2.setImageResource(R.mipmap.chongd_def);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        for (int i=0;i<targetPointList.size();i++){
            targetPointList.get(i).setMultiple(true);
//            Logger.e("xy"+targetPointList.get(i).getX()+"----"+targetPointList.get(i).getY());
        }
        PointView.getInstance(getActivity()).setTargetPointsAuto(targetPointList);
        mv_sell_auto.invalidate();

    }

    /**
     * 添加或删除临时任务
     * @param routeName
     * @param taskName
     * @param num
     * @param type
     */

    private void addOrDetTemporary(ByteString routeName, ByteString taskName, int num, int type){
        DDRVLNMap.eTaskOperationalType eTaskOperationalType;
        switch (type){
            case 0:
                eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalError;
                break;
            case 1:
                eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalStopTask;
                break;
            case 2:
                eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalAddTemporary;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRVLNMap.reqTaskOperational.OptItem optItem= DDRVLNMap.reqTaskOperational.OptItem.newBuilder()
                .setOnerouteName(routeName)
                .setTaskName(taskName)
                .setRunCount(num)
                .setType(eTaskOperationalType)
                .build();
        DDRVLNMap.reqTaskOperational reqTaskOperational=DDRVLNMap.reqTaskOperational.newBuilder()
                .setOptSet(optItem)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqTaskOperational);
    }

    /**
     * 添加或删除临时任务to指定机器人
     * @param routeName
     * @param taskName
     * @param num
     * @param type
     */

    private void addOrDetTemporary(ByteString routeName, ByteString taskName, int num, int type,String guid){
        DDRVLNMap.eTaskOperationalType eTaskOperationalType;
        switch (type){
            case 0:
                eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalError;
                break;
            case 1:
                eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalStopTask;
                break;
            case 2:
                eTaskOperationalType=DDRVLNMap.eTaskOperationalType.eTaskOperationalAddTemporary;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRVLNMap.reqTaskOperational.OptItem optItem= DDRVLNMap.reqTaskOperational.OptItem.newBuilder()
                .setOnerouteName(routeName)
                .setTaskName(taskName)
                .setRunCount(num)
                .setType(eTaskOperationalType)
                .build();
        DDRVLNMap.reqTaskOperational reqTaskOperational=DDRVLNMap.reqTaskOperational.newBuilder()
                .setOptSet(optItem)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer,guid),reqTaskOperational);
    }

    /**
     * 机器人暂停/重新运动
     * @param value
     */
    private void pauseOrResume(String value){
        BaseCmd.reqCmdPauseResume reqCmdPauseResume=BaseCmd.reqCmdPauseResume.newBuilder()
                .setError(value)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdPauseResume);
        Logger.e("机器人暂停/重新运动");
    }

    /**
     * 点击开始事件
     */
    private void onClickStart(){
        if (notifyBaseStatusEx!=null){
            switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
                case 0:
                    toast("设备1正在自标定");
                    break;
                case 1:
                    switch (notifyBaseStatusEx.getMode()) {
                        case 1:
                            if (taskName!=null && !taskName.equals("PathError")){
                                addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2,globalParameter.robotID1);
                            }else {
                                toast("当前无任务，请先右下角选择任务执行");
                            }                        break;
                        case 3:
                            switch (notifyBaseStatusEx.getSonMode()){
                                case 16:
                                    toast("设备1正在运动中");
                                    break;
                                case 17:
                                    pauseOrResume("Resume");
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }
        if (notifyBaseStatusExTwo!=null){
            switch (notifyBaseStatusExTwo.geteSelfCalibStatus()) {
                case 0:
                    toast("设备2正在自标定");
                    break;
                case 1:
                    switch (notifyBaseStatusExTwo.getMode()) {
                        case 1:
                            if (taskName!=null && !taskName.equals("PathError")){
                                addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2,globalParameter.robotID2);
                            }else {
                                toast("当前无任务，请先右下角选择任务执行");
                            }
                            break;
                        case 3:
                            switch (notifyBaseStatusExTwo.getSonMode()){
                                case 16:
                                    toast("设备2正在执行中");
                                    break;
                                case 17:
                                    pauseOrResume("Resume");
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }

    }

    /**
     * 点击暂停
     */
    private void onClickStop(){
        if (notifyBaseStatusEx!=null){
            switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
                case 0:
                    toast("设备1正在自标定");
                    break;
                case 1:
                    switch (notifyBaseStatusEx.getMode()) {
                        case 1:
                            toast("设备1正在待命，请先进入执行状态");
                            break;
                        case 3:
                            switch (notifyBaseStatusEx.getSonMode()){
                                case 16:
                                    pauseOrResume("Pause");
                                    toast("设备1暂停，点击执行可恢复行政");
                                    break;
                                case 17:
                                    toast("设备2已处于暂停状态中");
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }
        if (notifyBaseStatusExTwo!=null){
            switch (notifyBaseStatusExTwo.geteSelfCalibStatus()) {
                case 0:
                    toast("设备2正在自标定");
                    break;
                case 1:
                    switch (notifyBaseStatusExTwo.getMode()) {
                        case 1:
                            toast("设备2正在待命，请先进入执行状态");
                            break;
                        case 3:
                            switch (notifyBaseStatusExTwo.getSonMode()){
                                case 16:
                                    pauseOrResume("Pause");
                                    toast("设备2暂停，点击执行可恢复行政");
                                    break;
                                case 17:
                                    toast("设备2已处于暂停状态中");
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }
    }

    /**
     * 点击退出事件
     */
    private void onClickEnd(){
        if (notifyBaseStatusEx!=null){
            switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
                case 0:
                    toast("设备1正在自标定");
                    break;
                case 1:
                    switch (notifyBaseStatusEx.getMode()) {
                        case 1:
                            toast("设备1正在待命中");
                            break;
                        case 3:
//                                toast("请稍等，正在退出");
                            addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,1,globalParameter.robotID1);
                            break;
                    }
                    break;
            }
        }

//        Logger.e("模式11111"+notifyBaseStatusExTwo.getMode()+"---"+notifyBaseStatusExTwo.geteSelfCalibStatus());
        if (notifyBaseStatusExTwo!=null){
            switch (notifyBaseStatusExTwo.geteSelfCalibStatus()) {
                case 0:
                    toast("设备2正在自标定");
                    break;
                case 1:
                    switch (notifyBaseStatusExTwo.getMode()) {
                        case 1:
                            toast("设备2正在待命中");
                            break;
                        case 3:
                            toast("请稍等，正在退出");
                            addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,1,globalParameter.robotID2);
                            break;
                    }
                    break;
            }
        }
    }

    /**
     * 字节转图片
     * @param temp
     * @return
     */

    public Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }

    private boolean exit=false;       //线程是否被终止
    public void getMapInfo() {
        new Thread(() -> {
            while (!exit) {
                if (mapName != null && !mapName.equals("PathError")) {
                    Logger.e("-----------" + mapName);
                    tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));  //获取某个地图信息
                    exit=true;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 路径选择弹窗
     * @param view
     */
    private void showTaskPopupWindow(View view) {
        Logger.e("---------showTaskPopupWindow");
        View contentView = null;
        contentView = getActivity().getLayoutInflater().from(getActivity()).inflate(R.layout.recycle_task, null);
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .enableOutsideTouchableDissmiss(false)
                .setClippingEnable(false)
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(getActivity(), 0), 5);
        recycler_task_check =contentView.findViewById(R.id.recycler_task_check);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recycler_task_check.setLayoutManager(layoutManager);
        recycler_task_check.setAdapter(taskCheckAdapter);
        onItemClick();
        customPopWindow.setOutsideTouchListener(()->{
            Logger.e("点击外部已关闭");
            customPopWindow.dissmiss();
            tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
        });
    }

    public void onItemClick(){
                //任务列表点击事件
                Logger.e("task列表"+groupList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskCheckAdapter.setOnItemClickListener((adapter, view, position) ->  {
                    new InputDialog.Builder(getActivity())
                            .setTitle("请输入循环次数")
                            .setHint("1")
                            .setConfirm("执行")
                            .setEditNumAndSize(3)
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if (groupList.size()>0){
                                        taskName=groupList.get(position);
                                        String showName=taskName.replaceAll("DDRTask_","");
                                        showName=showName.replaceAll(".task","");
                                        tv_now_task.setText(showName);
                                    }else {
                                        Logger.e("Sen任务名"+groupList.size());
                                    }

//                                    iv_all_mapImage.setTaskName(taskName);
                                    if (!content.isEmpty() && Integer.parseInt(content)>0 && Integer.parseInt(content)<1000 ){
                                        try {
                                            lsNum=Integer.parseInt(content);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else {
                                        lsNum=1;
                                    }
                                    addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2,globalParameter.robotID1);
                                    addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2,globalParameter.robotID2);
                                    Logger.e("当前临时任务状态"+BaseCmd.eCmdRspType.values().length);
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消添加");
                                }
                            }).show();
                    customPopWindow.dissmiss();
                    tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                });

    }

    /**
     * 设置图片的路径
     *
     * @param infoList
     */
    public void transformMapInfo(List<MapInfo> infoList) {
        for (int i = 0; i < infoList.size(); i++) {
            String dirName = infoList.get(i).getMapName();
            Logger.e("地图名"+dirName);
            String pngPath = GlobalParameter.ROBOT_FOLDER + dirName + "/" + "bkPic.png";
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
        mapInfoList = infoList;

    }
    /********************************** start- 显示参考层弹窗******************************/
    private CustomPopuWindow customPopuWindow;
    private List<TargetPoint> targetPoints = new ArrayList<>();                    // 解析后的目标点列表
    private TextView tvGrid,tvTargetPointRe,tvAllPoint,tvPathRe,tvAllPath;
    private LinearLayout layoutGrid,layoutTarget,layoutPath;
    private GridTextView tv025m,tv05m,tv1m,tv2m;
    private RecyclerView recyclerPoints,recyclerPaths;
    private TargetPointAdapter targetReferenceAdapter;
    private PathAdapter pathReferenceAdapter;
    private boolean allShowPoint,allShowPath;               // 是否全部显示
    private int gridStatus;                                // 0 默认 1:0.25m ,2: 0.5m, 3: 1m ,4: 2m
    private void showPopupWindowReference(View view){
        View contentView=getActivity().getLayoutInflater().from(getActivity()).inflate(R.layout.popupwindow_reference,null);
        customPopuWindow=new CustomPopuWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .create()
                .showAsDropDown(view,0,5);
        tvGrid=contentView.findViewById(R.id.tv_grid);
        tvTargetPointRe=contentView.findViewById(R.id.tv_target_point);
        tvPathRe=contentView.findViewById(R.id.tv_path);
        layoutGrid=contentView.findViewById(R.id.layout_grid);
        layoutTarget=contentView.findViewById(R.id.layout_target);
        layoutPath=contentView.findViewById(R.id.layout_path);
        tvAllPoint=contentView.findViewById(R.id.tv_all_point);
        tvAllPath=contentView.findViewById(R.id.tv_all_path);
        tv025m=contentView.findViewById(R.id.tv_025m);
        tv05m=contentView.findViewById(R.id.tv_05m);
        tv1m=contentView.findViewById(R.id.tv_1m);
        tv2m=contentView.findViewById(R.id.tv_2m);
        recyclerPoints=contentView.findViewById(R.id.recycler_target_point);
        recyclerPaths=contentView.findViewById(R.id.recycler_paths);
        handleLogic();
        handleRecycler();

    }

    /**
     * 处理弹出显示内容的点击事件
     */
    private void handleLogic(){
        View.OnClickListener listener=v -> {
            switch (v.getId()){
                case R.id.tv_grid:
                    if (layoutGrid.getVisibility()==View.VISIBLE){
                        layoutGrid.setVisibility(View.GONE);
                    }else {
                        layoutGrid.setVisibility(View.VISIBLE);
                    }
                    layoutTarget.setVisibility(View.GONE);
                    layoutPath.setVisibility(View.GONE);
                    break;
                case R.id.tv_025m:
                    if (!tv025m.getSelected()) {
                        GridLayerView.getInstance(mv_sell_auto).setPrecision((float) 0.25);        //将图片网格化
                        mv_sell_auto.invalidate();
                        setIconDefault1();
                        tv025m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                        tv025m.setSelected(true);
                        gridStatus=1;
                    } else {
                        GridLayerView.getInstance(mv_sell_auto).setPrecision(0);        //取消网格
                        mv_sell_auto.invalidate();
                        setIconDefault1();
                    }
                    break;
                case R.id.tv_05m:
                    if (!tv05m.getSelected()) {
                        GridLayerView.getInstance(mv_sell_auto).setPrecision((float) 0.5);        //将图片网格化
                        mv_sell_auto.invalidate();
                        setIconDefault1();
                        tv05m.setSelected(true);
                        tv05m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv05m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=2;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(mv_sell_auto).setPrecision(0);        //取消网格
                        mv_sell_auto.invalidate();
                    }
                    break;
                case R.id.tv_1m:
                    if (!tv1m.getSelected()) {
                        GridLayerView.getInstance(mv_sell_auto).setPrecision((float) 1);        //将图片网格化
                        mv_sell_auto.invalidate();
                        setIconDefault1();
                        tv1m.setSelected(true);
                        tv1m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv1m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=3;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(mv_sell_auto).setPrecision(0);        //取消网格
                        mv_sell_auto.invalidate();
                    }
                    break;
                case R.id.tv_2m:
                    if (!tv2m.getSelected()) {
                        GridLayerView.getInstance(mv_sell_auto).setPrecision((float) 2);        //将图片网格化
                        mv_sell_auto.invalidate();
                        setIconDefault1();
                        tv2m.setSelected(true);
                        tv2m.setTextColor(Color.parseColor("#FFFFFFFF"));
                        tv2m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        gridStatus=4;
                    } else {
                        setIconDefault1();
                        GridLayerView.getInstance(mv_sell_auto).setPrecision(0);        //取消网格
                        mv_sell_auto.invalidate();
                    }
                    break;
                case R.id.tv_target_point:
                    if (layoutTarget.getVisibility()==View.VISIBLE){
                        layoutTarget.setVisibility(View.GONE);
                    }else {
                        layoutTarget.setVisibility(View.VISIBLE);
                    }
                    layoutGrid.setVisibility(View.GONE);
                    layoutPath.setVisibility(View.GONE);
                    break;
                case R.id.tv_all_point:
                    if (allShowPoint){
                        allShowPoint=false;
                        tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                        tvAllPoint.setTextColor(getResources().getColor(R.color.text_gray));
                        for (TargetPoint targetPoint:targetPoints){
                            targetPoint.setMultiple(false);
                        }
                    }else {
                        allShowPoint=true;
                        tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        tvAllPoint.setTextColor(getResources().getColor(R.color.white));
                        for (TargetPoint targetPoint:targetPoints){
                            targetPoint.setMultiple(true);
                        }
                    }
                    PointView.getInstance(getActivity()).setTargetPoints(targetPoints);
                    mv_sell_auto.invalidate();
                    targetReferenceAdapter.setNewData(targetPoints);
                    break;
                case R.id.tv_path:
                    if (layoutPath.getVisibility()==View.VISIBLE){
                        layoutPath.setVisibility(View.GONE);
                    }else {
                        layoutPath.setVisibility(View.VISIBLE);
                    }
                    layoutGrid.setVisibility(View.GONE);
                    layoutTarget.setVisibility(View.GONE);
                    break;
                case R.id.tv_all_path:
                    if (allShowPath){
                        allShowPath=false;
                        tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                        tvAllPath.setTextColor(getResources().getColor(R.color.text_gray));
                        for (PathLine pathLine:pathLines){
                            pathLine.setMultiple(false);
                        }
                    }else {
                        allShowPath=true;
                        tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        tvAllPath.setTextColor(getResources().getColor(R.color.white));
                        for (PathLine pathLine:pathLines){
                            pathLine.setMultiple(true);
                        }
                    }
                    LineView.getInstance(getActivity()).setPathLines(pathLines);
                    mv_sell_auto.invalidate();
                    pathReferenceAdapter.setNewData(pathLines);
                    break;
            }
        };
        tvGrid.setOnClickListener(listener);
        tv025m.setOnClickListener(listener);
        tv05m.setOnClickListener(listener);
        tv1m.setOnClickListener(listener);
        tv2m.setOnClickListener(listener);
        tvTargetPointRe.setOnClickListener(listener);
        tvPathRe.setOnClickListener(listener);
        tvAllPoint.setOnClickListener(listener);
        tvAllPath.setOnClickListener(listener);
        if (allShowPoint){
            tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
            tvAllPoint.setTextColor(getResources().getColor(R.color.white));
        }else {
            tvAllPoint.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
            tvAllPoint.setTextColor(getResources().getColor(R.color.text_gray));
        }
        if (allShowPath){
            tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
            tvAllPath.setTextColor(getResources().getColor(R.color.white));
        }else {
            tvAllPath.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
            tvAllPath.setTextColor(getResources().getColor(R.color.text_gray));
        }
        switch (gridStatus){
            case 0:
                setIconDefault1();
                break;
            case 1:
                tv025m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_show), null);
                tv025m.setSelected(true);
                break;
            case 2:
                tv05m.setSelected(true);
                tv05m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv05m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                break;
            case 3:
                tv1m.setSelected(true);
                tv1m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv1m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                break;
            case 4:
                tv2m.setSelected(true);
                tv2m.setTextColor(Color.parseColor("#FFFFFFFF"));
                tv2m.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                break;
        }
    }

    /**
     * 处理RecyclerView和点击事件
     */
    private void handleRecycler(){
        targetReferenceAdapter=new TargetPointAdapter(R.layout.item_show_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPoints.setLayoutManager(layoutManager);
        recyclerPoints.setAdapter(targetReferenceAdapter);
        pathReferenceAdapter=new PathAdapter(R.layout.item_show_recycler);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        recyclerPaths.setLayoutManager(layoutManager1);
        recyclerPaths.setAdapter(pathReferenceAdapter);
        targetReferenceAdapter.setNewData(targetPoints);
        pathReferenceAdapter.setNewData(pathLines);

        targetReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (targetPoints.get(position).isMultiple()){
                targetPoints.get(position).setMultiple(false);
                PointView.getInstance(getActivity()).setTargetPoints(targetPoints);
                mv_sell_auto.invalidate();
                targetReferenceAdapter.setNewData(targetPoints);
            }else {
                targetPoints.get(position).setMultiple(true);
                PointView.getInstance(getActivity()).setTargetPoints(targetPoints);
                mv_sell_auto.invalidate();
                targetReferenceAdapter.setNewData(targetPoints);
            }
        });

        pathReferenceAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (pathLines.get(position).isMultiple()){
                pathLines.get(position).setMultiple(false);
                LineView.getInstance(getActivity()).setPathLines(pathLines);
                mv_sell_auto.invalidate();
                pathReferenceAdapter.setNewData(pathLines);
            }else {
                pathLines.get(position).setMultiple(true);
                LineView.getInstance(getActivity()).setPathLines(pathLines);
                mv_sell_auto.invalidate();
                pathReferenceAdapter.setNewData(pathLines);
            }
        });
    }
    /**
     * 设置网格图标默认状态
     */
    private void setIconDefault1(){
        tv025m.setSelected(false);
        tv05m.setSelected(false);
        tv1m.setSelected(false);
        tv2m.setSelected(false);
        tv025m.setTextColor(Color.parseColor("#66ffffff"));
        tv05m.setTextColor(Color.parseColor("#66ffffff"));
        tv1m.setTextColor(Color.parseColor("#66ffffff"));
        tv2m.setTextColor(Color.parseColor("#66ffffff"));
        tv025m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        tv1m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        tv05m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        tv2m.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.item_hide), null);
        gridStatus=0;
    }

    /**********************************end -********************************************/

    /**
     * 弧度转角度
     */
    private float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }

    /**
     * 发送获取商品详情的请求
     */
    private void postGoodProduct(String goodID){
        DDRAIServiceCmd.reqGoodsDetail reqGoodsDetail=DDRAIServiceCmd.reqGoodsDetail.newBuilder()
                .setGoodsID(ByteString.copyFromUtf8(goodID))
                .build();
        if (globalParameter.isLan()){
//            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGoodsDetail);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqGoodsDetail);
        }
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());//系统小时数
        String ss=formatter.format(date);//获取当前时间
        Logger.e("发送货物详情时间"+ ss);
    }

    /**
     * 返回登陆界面
     */
    public void onBack() {
        Intent intent_login = new Intent();
        intent_login.setClass(AllRetailActivity.this, LoginActivity.class);
        intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
        startActivity(intent_login);
        tcpClient.disConnect();
        tcpClient.onDestroy();
        finish();

    }

    @Override
    protected void onDestroy() {
        tcpClient.disConnect();
        super.onDestroy();
    }
}
