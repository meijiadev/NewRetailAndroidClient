package ddr.example.com.newretailandroidclient.ui.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.protobuf.ByteString;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRActivity;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.newretailandroidclient.entity.point.XyEntity;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;
import ddr.example.com.newretailandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.newretailandroidclient.widget.view.CollectingView3;
import ddr.example.com.newretailandroidclient.widget.view.CollectingView4;
import ddr.example.com.newretailandroidclient.widget.view.RockerView;

import static ddr.example.com.newretailandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.newretailandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time:  2019/11/5
 * desc:  采集页面
 * modify time: 2020/3/23
 */
public class CollectingActivity extends DDRActivity {
    @BindView(R.id.collect4)
    CollectingView4 collectingView4;
    @BindView(R.id.collect3)
    CollectingView3 collectingView3;
    @BindView(R.id.process_bar)
    NumberProgressBar processBar;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.seek_bar)
    VerticalRangeSeekBar seekBar;
    @BindView(R.id.fixed_speed)
    CheckBox fixedSpeed;
    @BindView(R.id.add_poi)
    TextView addPoi;
    @BindView(R.id.my_rocker)
    RockerView myRocker;
    @BindView(R.id.my_rocker_zy)
    RockerView myRockerZy;
    @BindView(R.id.tv_detection)
    TextView tvDetection;                   //回环检测

    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private TcpClient tcpClient;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String collectName;                  //采集的地图名
    private BaseDialog waitDialog;

    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private NotifyLidarPtsEntity notifyLidarPtsEntity1;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private List<XyEntity>poiPoints=new ArrayList<>();                   //兴趣点列表 采集中生成
    private float posX,posY;        //机器人当前位置
    private float radian;           // 机器人当前朝向 单位弧度
    private float angle;            // 机器人当前朝向 弧度转换后的角度
    private float minX=0,minY=0,maxX=0,maxY=0;  //雷达扫到的最大坐标和最小坐标
    private float ratio=1;         //地图比例
    private int measureWidth=1000, measureHeight=1000;
    private boolean isStartDraw=false;        //是否开始绘制
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case updateBaseStatus:
                Logger.e("-------:" + notifyBaseStatusEx.getSonMode());
                if (notifyBaseStatusEx.geteSelfCalibStatus() == 0) {
                    setTitle("正在自标定中...");
                } else {
                    if (notifyBaseStatusEx.getMode() == 2) {
                        switch (notifyBaseStatusEx.getSonMode()) {
                            case 2:
                                toast("建图异常,即将退出当前模式，本次地图无效");
                                exitModel();
                                finish();
                                break;
                            case 6:
                                setTitle("采集中...");
                                waitDialog.dismiss();
                                myRockerZy.setVisibility(View.VISIBLE);
                                myRocker.setVisibility(View.VISIBLE);
                                addPoi.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
                break;
            case notifyMapGenerateProgress:
                float progress= (float) mainUpDate.getData();
                setAnimation(processBar,(int) (progress*100),0);
                if (progress==1.0f){
                    setTitle("建图完成");
                    finish();

                }
                break;
            case updateDetectionLoopStatus:
                int loopStatus= (int) mainUpDate.getData();
                switch (loopStatus){
                    case -2:                    // 检测错误
                        toast("检测错误，请重新检测");
                        break;
                    case -1:                   // 没有检测到回环
                        toast("没有检测到回环");
                        break;
                    case 0:                   // 回环已存在
                        toast("回环已存在");
                        break;
                    case 1:                  // 新采集基准构成回环
                        toast("新采集基准构成回环");
                        break;
                    case 2:                 //  距离太近不需要检测回环
                        toast("距离太近不需要检测回环");
                        break;
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collecting;
    }

    @Override
    protected void initView() {
        super.initView();
        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
    }

    @Override
    protected void initData() {
        super.initData();
        collectName=getIntent().getStringExtra("CollectName");
        Logger.e("-----采集的地图名");
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        processBar.setMax(100);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        initWaitDialog();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void onLeftClick(View v) {
        new InputDialog.Builder(getActivity())
                .setTitle("是否退出采集")
                .setEditVisibility(View.GONE)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        quitCollect();
                        stopDraw();
                        finish();
                    }
                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                }).show();

    }

    @Override
    public void onRightClick(View v) {
        new InputDialog.Builder(getActivity())
                .setTitle("是否保存当前采集")
                .setEditVisibility(View.GONE)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        exitModel();
                        processBar.setVisibility(View.VISIBLE);
                        stopDraw();
                    }
                    @Override
                    public void onCancel(BaseDialog dialog) {
                    }
                }).show();
    }

    /**
     * 自标定等待弹窗
     */
    public void initWaitDialog(){
        waitDialog= new WaitDialog.Builder(this)
        .setMessage("正在定位中，请勿挪动机器人...")
        .show();
    }


    @SuppressLint("NewApi")
    private void initSeekBar() {
        seekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if (!ishaveChecked) {
                    editor.putFloat("speed", (float) maxSpeed);                 //保存最近的改变速度
                    editor.commit();
                    tvSpeed.setText(String.valueOf(maxSpeed));
                }
                Logger.e("------" + seekBar.getLeftSeekBar().getProgress());
                maxSpeed = seekBar.getLeftSeekBar().getProgress();
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
    }

    @OnClick({R.id.add_poi,R.id.tv_detection})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.add_poi:
                BaseCmd.reqAddPathPointWhileCollecting reqAddPathPointWhileCollecting=BaseCmd.reqAddPathPointWhileCollecting.newBuilder().build();
                tcpClient.sendData(null,reqAddPathPointWhileCollecting);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.addPoiPoint));
                poiPoints.add(new XyEntity(posX,posY));
                toast("标记成功");
                break;
            case R.id.tv_detection:
                BaseCmd.reqDetectLoop reqDetectLoop=BaseCmd.reqDetectLoop.newBuilder()
                        .build();
                tcpClient.sendData(null,reqDetectLoop);
                break;
        }


    }

    private boolean ishaveChecked = false;

    /**
     * 固定速度
     */
    public void setFixedSpeed() {
        fixedSpeed.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                ishaveChecked = isChecked;
                maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
                Logger.e("-----" + maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                seekBar.setEnabled(false);
                toast("锁定");
            } else {
                seekBar.setEnabled(true);
                ishaveChecked = isChecked;
                maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
                seekBar.setProgress((float) maxSpeed);
                tvSpeed.setText(String.valueOf(maxSpeed));
                toast("取消锁定");

            }
        }));
    }


    /**
     * 设置进度条的 进度和动画效果
     *
     * @param view
     *
     * @param mProgressBar
     */
    private void setAnimation(final NumberProgressBar view, final int mProgressBar, int time) {
        ValueAnimator animator = ValueAnimator.ofInt(0, mProgressBar).setDuration(time);

        animator.addUpdateListener((valueAnimator) -> {
            view.setProgress((int) valueAnimator.getAnimatedValue());
        });
        animator.start();
    }

    /**
     * 自定义摇杆View的相关操作
     * 作用：监听摇杆的方向，角度，距离
     */
    private void initRockerView() {
        myRocker.setOnShakeListener(DIRECTION_2_VERTICAL, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {           // "当前方向：中心"
                    //Logger.e("---中心");
                    lineSpeed = 0;
                    myRocker.setmAreaBackground(R.mipmap.rocker_base_default);
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {     // 当前方向：下
                    isforward = false;
                    myRocker.setmAreaBackground(R.mipmap.rocker_backward);
                    //Logger.e("下");
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {    //当前方向：左

                } else if (direction == RockerView.Direction.DIRECTION_UP) {      //当前方向：上
                    isforward = true;
                    myRocker.setmAreaBackground(R.mipmap.rocker_forward);
                    //Logger.e("上");
                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {

                }
            }

            @Override
            public void onFinish() {

            }
        });

        myRockerZy.setOnShakeListener(DIRECTION_2_HORIZONTAL, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {           // "当前方向：中心"
                    // Logger.e("---中心");
                    myRockerZy.setmAreaBackground(R.mipmap.rocker_default_zy);
                    palstance = 0;
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {

                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {    //当前方向：左
                    isGoRight = false;
                    myRockerZy.setmAreaBackground(R.mipmap.rocker_go_left);
                    // Logger.e("左");
                } else if (direction == RockerView.Direction.DIRECTION_UP) {      //当前方向：上

                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    // mTvShake.setText("当前方向：右");
                    //Logger.e("右");
                    isGoRight = true;
                    myRockerZy.setmAreaBackground(R.mipmap.rocker_go_right);
                }
            }

            @Override
            public void onFinish() {

            }
        });

        /*** lambda 表达式 Java8*/
        myRockerZy.setOnDistanceLevelListener((level) -> {
                    DecimalFormat df = new DecimalFormat("#.00");
                    palstance = Float.parseFloat(df.format(maxSpeed * level / 10));
                    if (isGoRight) {
                        palstance = -palstance;
                    }
                }
        );

        myRocker.setOnDistanceLevelListener((level -> {
            DecimalFormat df = new DecimalFormat("#.00");
            lineSpeed = Float.parseFloat(df.format(maxSpeed * level / 10));
            if (!isforward) {
                lineSpeed = -lineSpeed;
            }
        }));

    }


    Timer timer;
    TimerTask task;
    int a = 0;

    /**
     * 定时器，每90毫秒执行一次
     */
    private void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override

            public void run() {
                // Logger.e("线速度，角速度："+lineSpeed+";"+palstance);
                if (lineSpeed == 0 && palstance == 0) {
                    a++;
                    if (a <= 5) {
                        //Logger.e("----a:" + a);
                        sendSpeed(lineSpeed, palstance);
                    }
                } else {
                    a = 0;
                    //Logger.e("线速度，角速度：" + lineSpeed + ";" + palstance);
                    sendSpeed(lineSpeed, palstance);
                }

            }
        };
        timer.schedule(task, 0, 90);
    }


    /**
     * 发送线速度，角速度
     *
     * @param lineSpeed
     * @param palstance
     */
    private void sendSpeed(final float lineSpeed, final float palstance) {
        BaseCmd.reqCmdMove reqCmdMove = BaseCmd.reqCmdMove.newBuilder()
                .setLineSpeed(lineSpeed)
                .setAngulauSpeed(palstance)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdMove);

    }


    /**
     * 退出当前模式
     */
    private void exitModel() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);
    }

    /**
     * 退出采集模式
     */

    private void quitCollect() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .setCancelRec(true)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
        timer.cancel();
        task.cancel();
        tcpClient.requestFile();
        tcpClient.getMapInfo(ByteString.copyFromUtf8(notifyBaseStatusEx.getCurroute()));

    }

    @Override
    public boolean statusBarDarkFont() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void upDateDrawMap(MessageEvent mainUpDate){
        switch (mainUpDate.getType()){
            case receivePointCloud:
                if (NotifyBaseStatusEx.getInstance().getSonMode()==6){
                    posX=notifyLidarPtsEntity.getPosX();
                    posY=notifyLidarPtsEntity.getPosY();
                    radian=notifyLidarPtsEntity.getPosdirection();
                    angle=radianToangle(radian);
                    notifyLidarPtsEntity1=new NotifyLidarPtsEntity();
                    notifyLidarPtsEntity1.setPosX(notifyLidarPtsEntity.getPosX());
                    notifyLidarPtsEntity1.setPosY(notifyLidarPtsEntity.getPosY());
                    notifyLidarPtsEntity1.setPositionList(notifyLidarPtsEntity.getPositionList());
                    ptsEntityList.add(notifyLidarPtsEntity1);
                    maxOrmin(notifyLidarPtsEntity.getPositionList());
                    if (!isStartDraw){
                        collectingView4.startThread();
                        collectingView3.startThread();
                    }
                    collectingView4.setData(ptsEntityList,poiPoints,ratio);
                    collectingView3.setData(ptsEntityList,ratio,angle);
                    isStartDraw=true;
                }
                break;
        }
    }
    /**
     * 弧度转角度
     */
    private float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }
    /**
     * 计算缩放比例
     * @param list
     */
    private void maxOrmin(List<BaseCmd.notifyLidarPts.Position> list){
        long startTime=System.currentTimeMillis();
        if (list!=null){
            int listSize=list.size();
            for (int i=0;i<listSize;i++){
                if (maxX<list.get(i).getPtX()) maxX=list.get(i).getPtX();
                if (maxY<list.get(i).getPtY()) maxY=list.get(i).getPtY();
                if (minX>list.get(i).getPtX()) minX=list.get(i).getPtX();
                if (minY>list.get(i).getPtY()) minY=list.get(i).getPtY();
            }
            if (maxX<posX) maxX=posX;
            if (maxY<posY) maxY=posY;
            if (minX>posX) minX=posX;
            if (minY>posY) minY=posY;
            float xy=Math.max(Math.max(maxX,Math.abs(minX)),Math.max(maxY,Math.abs(minY)));
            if (xy<=0){
                ratio=1;
            }else {
                if (measureWidth>measureHeight){
                    ratio=measureWidth/(xy)/2*1;
                }else {
                    ratio=measureHeight/(xy)/2*1;
                }
            }
        }
        long endTime=System.currentTimeMillis();
    }

    /**
     * 停止绘制
     */
    public void stopDraw(){
        collectingView4.onStop();
        collectingView3.onStop();
    }

}
