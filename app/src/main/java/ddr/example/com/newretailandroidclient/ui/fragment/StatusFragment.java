package ddr.example.com.newretailandroidclient.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.info.MapFileStatus;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.newretailandroidclient.entity.point.TargetPoint;
import ddr.example.com.newretailandroidclient.entity.point.TaskMode;
import ddr.example.com.newretailandroidclient.other.DpOrPxUtils;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.newretailandroidclient.ui.activity.HomeActivity;
import ddr.example.com.newretailandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;
import ddr.example.com.newretailandroidclient.widget.view.CircleBarView;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.newretailandroidclient.widget.StatusSwitchButton;
import ddr.example.com.newretailandroidclient.widget.view.MapImageView0;
import ddr.example.com.newretailandroidclient.widget.view.MapImageView1;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public final class StatusFragment extends DDRLazyFragment<HomeActivity>implements StatusSwitchButton.OnStatusSwitchListener,Animation.AnimationListener{

    @BindView(R.id.status_switch_bt)
    StatusSwitchButton statusSwitchButton;
    @BindView(R.id.circle)
    CircleBarView circleBarView;
    @BindView(R.id.iv_shrink)
    ImageView ivShrink;      //点击头部伸缩
    @BindView(R.id.shrink_tail_layout)
    RelativeLayout shrinkTailLayout; // 伸缩的尾部布局
    @BindView(R.id.shrink_layout)
    RelativeLayout shrinkLayout;
    @BindView(R.id.tv_ti_map)
    TextView tvTiMap;
    @BindView(R.id.tv_create_map)
    TextView tvCreateMap;
    //绘制地图+路径
    @BindView(R.id.iv_map)
    MapImageView0 mapImageView;
    @BindView(R.id.iv_map1)
    MapImageView1 mapImageView1;
    @BindView(R.id.tv_now_task)
    TextView tv_now_task;
    @BindView(R.id.tv_now_device)
    TextView tv_now_device;
    @BindView(R.id.tv_now_map)
    TextView tv_now_map;
    @BindView(R.id.tv_work_statue)
    TextView tv_work_statue;
    @BindView(R.id.tv_task_num)
    TextView tv_task_num;
    @BindView(R.id.tv_task_speed)
    TextView tv_task_speed;
    @BindView(R.id.tv_work_time)
    TextView tv_work_time;
    @BindView(R.id.rel_step_description)
    RelativeLayout rel_step_description;
    @BindView(R.id.recycle_gopoint)
    RecyclerView recycle_gopoint;
    @BindView(R.id.iv_cd_xs)
    ImageView iv_cd_xs;
    @BindView(R.id.iv_task_xl)
    ImageView iv_task_xl;
    @BindView(R.id.tv_set_go)
    TextView tv_set_go;
    @BindView(R.id.tv_restart_point)
    TextView tv_restart_point;

   /* @BindView(R.id.tv_switch_mode)
    TextView tv_switch_mode;*/

    @BindView(R.id.left_layout)
    RelativeLayout leftLayout;                //非充电状态下的左侧布局
    @BindView(R.id.charging_layout)
    RelativeLayout chargingLayout;             //充电模式下的左侧布局
    @BindView(R.id.iv_charge)
    ImageView ivCharge;                       //充电状态的图标  附带动画效果
    @BindView(R.id.tv_electric_quantity)
    TextView tvElectricQuantity;              //电池电量充电时的
    @BindView(R.id.bt_exit_charge)
    Button btExitCharge;                      //退出充电模式




    private Animation hideAnimation;  //布局隐藏时的动画
    private Animation showAnimation;  // 布局显示时的动画效果
    private AnimationDrawable chargeAnimation;

    private NotifyEnvInfo notifyEnvInfo;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    private static String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";//特殊字符
    private TcpClient tcpClient;
    private int batteryNum;
    private String mapName;//地图名
    private String taskName;//任务名
    public static String robotID;//机器人ID
    private String workStatus; //工作状态
    private int taskNum;//运行次数
    private int workTimes; //工作时间
    private double taskSpeed; //工作速度
    private int lsNum=1; //临时任务次数
    private List<String> groupList=new ArrayList<>();
    private List<TargetPoint> targetPoints= new ArrayList<>();
    private TargetPointAdapter targetPointAdapter;
    private MapFileStatus mapFileStatus;
    private StringAdapter taskCheckAdapter;
    private CustomPopuWindow customPopWindow;
    //private StringAdapter robotIdAdapter;
    private RecyclerView  recycler_task_check;
    private int modeType;
    public String sPoint;
    private boolean isRunabPoint;               //是否在跑ab点

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                initStatusBar();
                break;
            case getSwitchTaskSuccess:
                toast("添加临时任务成功");
                break;
            case getSwitchTaskFaild:
                toast("添加临时任务失败");
                break;
            case getSpecificPoint:
                toast("开始前往"+sPoint);
                break;
            case getSpecificPoint1:
                toast("添加任务成功，等待前往"+sPoint);
                break;
            case getSpecificPoint2:
                toast("发生未知错误");
                break;
            case getSpecificPoint3:
                toast("当前没有定位");
                break;
            case getSpecificPoint4:
                toast("生成路径失败");
                break;
            case getSpecificPoint5:
                toast("当前处于自标定");
                break;
            case getSpecificPoint8:
                toast("返回待机点");
                break;
            case getSpecificPoint9:
                toast("完成当前任务，开始时段任务");
                break;
            case getSpecificPoint10:
                toast("无任务，原地待命");
                break;
            case getSpecificPoint11:
                Logger.e("AB点"+sPoint);
                toast("开始前往"+sPoint);
            case switchMapSucceed:
                for (int i = 0; i < targetPoints.size(); i++) {
                    targetPoints.get(i).setSelected(false);
                }
                break;
            case responseAbPoint:
                tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));
                break;
            case updateDDRVLNMap:
                Logger.e("------地图名："+mapFileStatus.getMapName()+"当前"+mapName);
                if (mapFileStatus.getMapName().equals(mapName)){
                    Logger.e("group列数"+groupList.size()+"列数1"+mapFileStatus.getTaskModes().size()+" -- "+mapFileStatus.getcTaskModes().size());
                    mapImageView.setMapBitmap(mapName);
                    tvTiMap.setVisibility(View.GONE);
                    tvCreateMap.setVisibility(View.GONE);
                    groupList = new ArrayList<>();
                    targetPoints=new ArrayList<>();
                    for (TaskMode taskMode:mapFileStatus.getcTaskModes()){
                        groupList.add(taskMode.getName());
                    }
                    taskCheckAdapter.setNewData(groupList);
                    targetPoints=mapFileStatus.getcTargetPoints();
                    targetPointAdapter.setNewData(targetPoints);
                    mapImageView.setABPointLine(isRunabPoint);
                }
                break;
        }
    }

    public static StatusFragment newInstance(){
        return new StatusFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_status;
    }

    @Override
    protected void initView() {
        statusSwitchButton.setOnStatusListener(this);
        hideAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_hide);
        showAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_show);
        chargeAnimation= (AnimationDrawable) ivCharge.getBackground();
        taskCheckAdapter=new StringAdapter(R.layout.item_recycle_task_check);
        targetPointAdapter=new TargetPointAdapter(R.layout.item_recycle_gopoint);
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        recycle_gopoint.setLayoutManager(gridLayoutManager);
        recycle_gopoint.setAdapter(targetPointAdapter);
        onItemClick(2);
    }


    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getAttachActivity(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();

        if (taskName!=null && !taskName.equals("PathError")){
            String showName=taskName.replaceAll("DDRTask_","");
            showName=showName.replaceAll(".task","");
            tv_now_task.setText(showName);
        }else {
            tv_now_task.setText("无任务");
        }
        for (int i=0;i<mapFileStatus.getcTaskModes().size();i++){
            groupList.add(mapFileStatus.getcTaskModes().get(i).getName());
        }
        taskCheckAdapter.setNewData(groupList);
        targetPointAdapter.setNewData(targetPoints);
        mapImageView1.setMapImageView0(mapImageView);
        mapImageView1.startThread();
    }

    /**
     * 获取机器人状态信息
     */
    private void initStatusBar() {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat format = new DecimalFormat("0.00");
        int h=60;
        int times=notifyBaseStatusEx.getTaskDuration();
        batteryNum=Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
        mapName = notifyBaseStatusEx.getCurroute();
        taskNum=notifyBaseStatusEx.getTaskCount();
        taskName = notifyBaseStatusEx.getCurrpath();
        lsNum=notifyBaseStatusEx.getTemopTaskNum();
//        Logger.e("路径名字"+taskName);
        if (taskName!=null && !taskName.equals("PathError") && !taskName.equals("DDRTask_temporary.task")){
            String showName=taskName.replaceAll("DDRTask_","");
            showName=showName.replaceAll(".task","");
            tv_now_task.setText(showName);
        }else {
            tv_now_task.setText("无任务");
        }
        workTimes=Integer.parseInt(df.format( times/h));
        taskSpeed=Double.parseDouble(format.format(notifyBaseStatusEx.getPosLinespeed()));
        String showName=mapName.replaceAll("OneRoute_","");
        tv_now_map.setText(showName);
        if (mapName!=null){
            rel_step_description.setVisibility(View.GONE);
            recycle_gopoint.setVisibility(View.VISIBLE);
            tv_set_go.setText("前往目标点");
        }else {
            rel_step_description.setVisibility(View.VISIBLE);
            recycle_gopoint.setVisibility(View.GONE);
            tv_set_go.setText("建立任务步骤：");
        }
        tv_now_device.setText(robotID);
        tv_work_time.setText(String.valueOf(workTimes)+" 分");
        tv_task_speed.setText(String.valueOf(taskSpeed)+" m/s");
        switch (notifyBaseStatusEx.geteTaskMode()){
            case 1:
                tv_task_num.setText(String.valueOf(taskNum)+"/"+lsNum+" 次");
                isRunabPoint=false;
                break;
            case 2:
                tv_task_num.setText(String.valueOf(taskNum)+"/"+mapFileStatus.AllCount+" 次");
                isRunabPoint=false;
                break;
            case 3:
                tv_task_num.setText(" ");
                break;
            case 4:
            case 5:
                tv_task_num.setText(" ");
                isRunabPoint=true;
                break;


        }
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                tv_work_statue.setText("自标定中");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        if (taskName.equals("PathError")){
                            tv_work_statue.setText("待命中");
                            tv_now_task.setClickable(true);
                            tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                            iv_task_xl.setVisibility(View.VISIBLE);
                        }else {
                            tv_work_statue.setText("运动中");
                        }
                        break;
                    case 3:
                        tv_work_statue.setText("运动中");
                        tv_now_task.setClickable(false);
                        tv_now_task.setBackgroundResource(0);
                        iv_task_xl.setVisibility(View.GONE);
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 3:
                                tv_work_statue.setText("异常");
                                break;
                            case 15:
                                tv_work_statue.setText("重定位中");
                                break;
                        }
                        break;
                }
                break;
        }
        switch (notifyBaseStatusEx.getChargingType()){
            case 1:
                btExitCharge.setVisibility(View.VISIBLE);
                break;
            case 2:
                btExitCharge.setVisibility(View.GONE);
                break;
        }
        //Logger.e("------------是否在充电："+notifyBaseStatusEx.isChargingStatus());
        if(notifyBaseStatusEx.isChargingStatus()) {
            iv_cd_xs.setImageResource(R.mipmap.cd_green);
            circleBarView.setProgress(batteryNum,0,Color.parseColor("#54E361"));
            tvElectricQuantity.setText(String.valueOf(batteryNum)+"%");
            if (chargingLayout.getVisibility()!=View.VISIBLE){         // 如果当前处于充电模式，但充电布局不可见
                chargingLayout.setVisibility(View.VISIBLE);
                leftLayout.setVisibility(View.GONE);
                if (!chargeAnimation.isRunning()){
                    chargeAnimation.start();
                }
            }
        }else {
            if (leftLayout.getVisibility()!=View.VISIBLE){            //如果当前处于非充电模式 
                chargingLayout.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
                if (chargeAnimation.isRunning()){
                    chargeAnimation.stop();                           // 如果动画正在运行 则停止
                }
            }
            iv_cd_xs.setImageResource(R.mipmap.sd_def);
            circleBarView.setProgress(batteryNum,0,Color.parseColor("#0399FF"));
        }
    }

    /**
     * 将机器id赋值
     */
    public static void setRobotID(String robotid){
        String stringd="DDR";
        if(!Pattern.compile(regEx).matcher(robotid).find()&&!robotid.contains("BLANK")){
            robotID =robotid;
        }else {
            robotID=stringd+"001";
        }
    }
    /**
     * 路径选择弹窗
     * @param view
     */
    private void showTaskPopupWindow(View view) {
        Logger.e("---------showTaskPopupWindow");
        View contentView = null;
        contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.recycle_task, null);
        customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                .setView(contentView)
                .enableOutsideTouchableDissmiss(false)
                .setClippingEnable(false)
                .create()
                .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
        recycler_task_check =contentView.findViewById(R.id.recycler_task_check);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        recycler_task_check.setLayoutManager(layoutManager);
        recycler_task_check.setAdapter(taskCheckAdapter);
        onItemClick(1);
        customPopWindow.setOutsideTouchListener(()->{
            Logger.e("点击外部已关闭");
            customPopWindow.dissmiss();
            tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
            iv_task_xl.setImageResource(R.mipmap.xlright_5);
        });
    }


    @OnClick({R.id.iv_shrink,R.id.tv_now_task,R.id.tv_create_map,R.id.tv_restart_point})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_shrink:
                if (shrinkTailLayout.getVisibility()==View.VISIBLE){
                    shrinkLayout.startAnimation(hideAnimation);
                    hideAnimation.setAnimationListener(this);
                    ivShrink.setImageResource(R.mipmap.iv_shrink);
                }else if (shrinkTailLayout.getVisibility()==View.GONE){
                    shrinkTailLayout.setVisibility(View.VISIBLE);
                    shrinkLayout.startAnimation(showAnimation);
                    ivShrink.setImageResource(R.mipmap.iv_back);
                }
                break;
            case R.id.tv_now_task:
                showTaskPopupWindow(tv_now_task);
                tv_now_task.setBackgroundResource(R.drawable.task_check_bg);
                iv_task_xl.setImageResource(R.mipmap.xl_5);
                break;
            case R.id.tv_create_map:
                new InputDialog.Builder(getAttachActivity())
                        .setTitle("采集地图")
                        .setHint("输入地图名称")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (!content.isEmpty()){
                                    content=content.replaceAll(" ","");
                                    String name="OneRoute_"+content;
                                    BaseCmd.reqCmdStartActionMode reqCmdStartActionMode=BaseCmd.reqCmdStartActionMode.newBuilder()
                                            .setMode(BaseCmd.eCmdActionMode.eRec)
                                            .setRouteName(ByteString.copyFromUtf8(name))
                                            .build();
                                    tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdStartActionMode);
                                    startActivity(CollectingActivity.class);
                                }else {
                                    toast("请输入地图名字");
                                }
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消新建地图");
                            }
                        }).show();
                break;
            case R.id.tv_restart_point:
                float theat= (float) 1.0;
                float x= (float) 1.0;
                float y= (float) 1.0;
                new InputDialog.Builder(getAttachActivity())
                        .setEditVisibility(View.GONE)
                        .setTitle("确认本次送料完成")
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                mapImageView.clearDraw();
                                goPointLet(x,y,theat,ByteString.copyFromUtf8("one"),ByteString.copyFromUtf8(mapName),2);
//                                tv_restart_point.setVisibility(View.GONE);
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
     * 机器人暂停/重新运动
     * @param value
     */
    private void pauseOrResume(String value){
        BaseCmd.reqCmdPauseResume reqCmdPauseResume=BaseCmd.reqCmdPauseResume.newBuilder()
                .setError(value)
                .build();
        BaseCmd.CommonHeader commonHeader=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eModuleServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader,reqCmdPauseResume);
        Logger.e("机器人暂停/重新运动");
    }

    /**
     * 添加或删除临时任务
     * @param routeName
     * @param taskName
     * @param num
     * @param type
     */

    private void addOrDetTemporary(ByteString routeName, ByteString taskName,int num,int type){
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
     * 导航去目标点或者恢复
     * @param x
     * @param y
     * @param theta
     * @param pname
     * @param routeName
     */

    private void goPointLet(float x,float y,float theta,ByteString pname, ByteString routeName,int type){
        DDRVLNMap.eRunSpecificPointType eRunSpecificPointTyp;
        switch (type){
            case 1:
                eRunSpecificPointTyp=DDRVLNMap.eRunSpecificPointType.eRunSpecificPointTypeAdd;
                break;
            case 2:
                eRunSpecificPointTyp=DDRVLNMap.eRunSpecificPointType.eRunSpecificPointTypeResume;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
                .setX(x)
                .setY(y)
                .setTheta(theta)
                .build();
        DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
                .setPtName(pname)
                .setPtData(space_pointEx)
                .build();
        List<DDRVLNMap.targetPtItem> targetPtItemList=new ArrayList<>();
        targetPtItemList.add(targetPtItem);
        DDRVLNMap.reqRunSpecificPoint reqRunSpecificPoint=DDRVLNMap.reqRunSpecificPoint.newBuilder()
                .setOnerouteName(routeName)
                .addAllTargetPt(targetPtItemList)
                .setBIsDynamicOA(true)
                .setOptType(eRunSpecificPointTyp)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqRunSpecificPoint);

    }



    public void onItemClick(int type){
        switch (type){
            case 1:
                //任务列表点击事件
                Logger.e("task列表"+groupList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskCheckAdapter.setOnItemClickListener((adapter, view, position) ->  {
                    new InputDialog.Builder(getAttachActivity())
                            .setTitle("请输入循环次数")
                            .setHint("1")
                            .setConfirm("执行")
                            .setEditNumAndSize(3)
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    taskName=groupList.get(position);
                                    String showName=taskName.replaceAll("DDRTask_","");
                                    showName=showName.replaceAll(".task","");
                                    tv_now_task.setText(showName);
                                    mapImageView.setTaskName(taskName);
                                    if (!content.isEmpty() && Integer.parseInt(content)>0 && Integer.parseInt(content)<1000 ){
                                        try {
                                            lsNum=Integer.parseInt(content);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else {
                                        lsNum=1;
                                    }
                                    addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2);
                                    Logger.e("当前临时任务状态"+BaseCmd.eCmdRspType.values().length);
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    toast("取消添加");
                                }
                            }).show();
                    customPopWindow.dissmiss();
                    tv_now_task.setBackgroundResource(R.drawable.bt_bg__map);
                    iv_task_xl.setImageResource(R.mipmap.xlright_5);
                });
                break;
            case 2:
                //标记点列表点击事件
                    targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
                    float x=targetPoints.get(position).getX();
                    float y=targetPoints.get(position).getY();
                    float theta=targetPoints.get(position).getTheta();
                    mapImageView.setTargetPoint(targetPoints.get(position));
                    Logger.e("当前点的名字" + targetPoints.get(position).getName());
                        new InputDialog.Builder(getAttachActivity()).setEditVisibility(View.GONE)
                                .setTitle("是否前往" + targetPoints.get(position).getName())
                                .setListener(new InputDialog.OnListener() {
                                    @Override
                                    public void onConfirm(BaseDialog dialog, String content) {
                                        goPointLet(x, y, theta, ByteString.copyFromUtf8(targetPoints.get(position).getName()), ByteString.copyFromUtf8(mapName), 1);
                                        tv_restart_point.setVisibility(View.VISIBLE);
                                        for (int i = 0; i < targetPoints.size(); i++) {
                                            targetPoints.get(i).setSelected(false);
                                        }
                                        targetPoints.get(position).setSelected(true);
                                        targetPointAdapter.setNewData(targetPoints);
                                        sPoint = targetPoints.get(position).getName();
                                    }

                                    @Override
                                    public void onCancel(BaseDialog dialog) {
                                        toast("取消去目标点");
                                    }
                                })
                                .show();
                });
                break;
        }

    }
    @Override
    public void onLeftClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
//                        sendModel(BaseCmd.eCmdActionMode.eAutoDynamic);
                        //Logger.e("待命模式" + modeView.getText());
                        Logger.e("----mapName:"+mapName+"taskName:"+taskName);
                        if (mapName!=null && taskName!=null && !taskName.equals("PathError")){
                            toast("请稍等，正在进入");
                            addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,2);
                        }else {
                            toast("请先建立任务");
                        }
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast("正在执行中");
                                break;
                            case 17:
                                toast("开始");
                                pauseOrResume("Resume");
                                break;
                        }
                        break;
                }
                break;
        }


    }

    @Override
    public void onCentreClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("正在待命，请先进入执行状态");
                        break;
                    case 3:
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                toast("暂停");
                                pauseOrResume("Pause");
                                break;
                            case 17:
                                toast("暂停状态中");
                                break;
                        }
                        break;
                }
                break;
        }

    }

    @Override
    public void onRightClick() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                toast("请稍等，正在自标定");
                //自标定
                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //Logger.e("待命模式" + modeView.getText());
                        toast("正在待命中");
                        break;
                    case 3:
                        toast("请稍等，正在退出");
//                        exitModel();
                        addOrDetTemporary(ByteString.copyFromUtf8(mapName),ByteString.copyFromUtf8(taskName),lsNum,1);
                        for (int i = 0; i < targetPoints.size(); i++) {
                            targetPoints.get(i).setSelected(false);
                        }
                        break;
                }
                break;
        }

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        shrinkTailLayout.setVisibility(View.GONE);
        Logger.e("----动画效果结束");
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    @Override
    public void onPause() {
        super.onPause();
        Logger.e("---------statusFragment onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("---------statusFragment onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("---------statusFragment onRestart");
    }
    /**
     * setUserVisibleHint的使用场景:FragmentPagerAdapter+ViewPager
     * 这种方式我们还是比较常见的,譬如,谷歌自带的TabLayout控件,此种场景下,当我们切换fragment的时候,会调用setUserVisibleHint方法,
     * 不会调用onHiddenChanged方法,也不会走fragment的生命周期方法(fragment初始化完成之后,注意这里需要重写viewpager中使用的适配器的方法,让fragment不会被销毁,不然还是会遇到问题)
     */

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            // 相当于onResume()方法--获取焦点
            Logger.e("可见");
            if (mapImageView1!=null){
                if (!mapImageView1.drawThread.isAlive()){
                    mapImageView.invalidate();
                    mapImageView1.startThread();
                    mapImageView.setMapBitmap(notifyBaseStatusEx.getCurroute());
                }
            }
        }else {
            // 相当于onpause()方法---失去焦点
            Logger.e("不可见");
            if (mapImageView1!=null){
                if (mapImageView1.drawThread.isAlive()){
                    mapImageView1.onStop();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            statusSwitchButton.onDestroy();
            mapImageView1.onStop();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    } 




}
