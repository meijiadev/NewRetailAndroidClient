package ddr.example.com.newretailandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.protobuf.ByteString;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import com.yhao.floatwindow.FloatWindow;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;

import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRActivity;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;

import ddr.example.com.newretailandroidclient.common.GlobalParameter;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusExTwo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfoTwo;
import ddr.example.com.newretailandroidclient.entity.other.UdpIp;
import ddr.example.com.newretailandroidclient.glide.ImageLoader;
import ddr.example.com.newretailandroidclient.helper.ActivityStackManager;
import ddr.example.com.newretailandroidclient.helper.DoubleClickHelper;
import ddr.example.com.newretailandroidclient.other.DpOrPxUtils;
import ddr.example.com.newretailandroidclient.other.KeyboardWatcher;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.newretailandroidclient.socket.UdpClient;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;
import ddr.example.com.newretailandroidclient.ui.fragment.MapFragment;
import ddr.example.com.newretailandroidclient.ui.fragment.SellDataFrament;
import ddr.example.com.newretailandroidclient.ui.fragment.SetUpFragment;
import ddr.example.com.newretailandroidclient.ui.fragment.StatusFragment;
import ddr.example.com.newretailandroidclient.ui.fragment.TaskFragment;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.newretailandroidclient.widget.view.DDRViewPager;
import ddr.example.com.newretailandroidclient.widget.textview.LineTextView;
import ddr.example.com.newretailandroidclient.widget.view.RockerView;

import static ddr.example.com.newretailandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.newretailandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time:2019/10/26
 * desc: 主页界面
 */
public class HomeActivity extends DDRActivity implements ViewPager.OnPageChangeListener,KeyboardWatcher.SoftKeyboardStateListener {
    @BindView(R.id.vp_home_pager)
    DDRViewPager vpHomePager;
    @BindView(R.id.status)
    LineTextView tv_status;
    @BindView(R.id.mapmanager)
    LineTextView tv_mapManager;
    @BindView(R.id.taskmanager)
    LineTextView tv_taskManager;
    @BindView(R.id.highset)
    LineTextView tv_highSet;
    @BindView(R.id.sell_data)
    LineTextView sell_data;
    @BindView(R.id.tv_quit)
    TextView tv_quit;
    @BindView(R.id.iv_jt_def)
    TextView iv_jt_def;
    @BindView(R.id.iv_yk_def)
    TextView iv_yk_def;
    @BindView(R.id.tv_auto_retail)
    TextView tv_auto_retail;
    @BindView(R.id.tv_allControl)
    TextView tv_allControl;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private String currentMap;     //当前运行的地图名
    private String currentTask;   //当前运行的任务
    private CustomPopuWindow customPopuWindow;
    private DpOrPxUtils dpOrPxUtils;
    private NotifyEnvInfo notifyEnvInfo;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private VerticalRangeSeekBar seekBar;
    private CheckBox fixedSpeed;
    private  RockerView myRocker;
    private RockerView myRockerZy;
    private TextView tvSpeed;
    private ImageView iv_quit_yk;
    private TextView tv_xsu;//线速度
    private TextView tv_jsu;//角速度
    private String xsu;
    private String jsu;
    private boolean ishaveChecked = false;
    private boolean isAutoRetail=false;
    private GlobalParameter globalParameter;
    private NotifyBaseStatusExTwo notifyBaseStatusExTwo;
    private int batteryNum=0;
    private int batteryNum2=0;
    private NotifyEnvInfoTwo notifyEnvInfoTwo;
    private boolean isWritePassword=false;


    /**
     * ViewPage 适配器
     */
    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                initStatusBar();
                break;
            case updateMapList:
                Logger.e("-------------updateMapList");
                getMapInfo();
                break;
            case switchTaskSuccess:
                Logger.e("--------更新地图");
                getMapInfo();
                Logger.e("---------" + currentMap);
                break;
            case switchMapSucceed:
                postDelayed(()->{
                    getMapInfo();
                },800);
                break;
            case touchFloatWindow:
                showControlPopupWindow();
                break;
            case updateAiPort:
                udpIp1= (UdpIp) messageEvent.getData();
//                Logger.e(globalParameter.isLan()+"是否局域网");
                Logger.e("AIip"+udpIp1.getIp()+"端口"+udpIp1.getPort());
                if (globalParameter.isLan()) {
                    tcpAiClient.createConnect(udpIp1.getIp(),udpIp1.getPort());
                }else {
                    Logger.e("广域网不需要登陆AI");
                }
                break;
            case tcpAiConnected:
                Logger.e("TcpAI服务开始连接");
                tcpAiClient.sendData(null, CmdSchedule.localLogin("admin_android","admin_android",4));
                break;
            case LoginAiSuccess:
                toast("售卖服务连接成功");
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                break;
            case updataRetailAuto:
                toast(globalParameter.getAutoReatilResult());
                break;
            case updataNotifyAuto:
                if (globalParameter.getNotiAutoResult().equals("失能")){
                    tv_auto_retail.setBackgroundResource(R.drawable.bg_task_cz);
                    isAutoRetail=false;
                }else {
                    tv_auto_retail.setBackgroundResource(R.drawable.bg_auto_ratail);
                    isAutoRetail=true;
                }
                break;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        KeyboardWatcher.with(this).setListener(this);
        mPagerAdapter = new BaseFragmentAdapter<DDRLazyFragment>(this);
        mPagerAdapter.addFragment(StatusFragment.newInstance());
        mPagerAdapter.addFragment(MapFragment.newInstance());
        mPagerAdapter.addFragment(TaskFragment.newInstance());
        mPagerAdapter.addFragment(SetUpFragment.newInstance());
        mPagerAdapter.addFragment(SellDataFrament.newInstance());
        vpHomePager.setAdapter(mPagerAdapter);
        //限制页面的数量
        vpHomePager.setOffscreenPageLimit(mPagerAdapter.getCount());
        vpHomePager.addOnPageChangeListener(this);
        isChecked();
    }


    @Override
    protected void initData() {
        globalParameter=GlobalParameter.getInstance();
        receiveAiBroadcast();
        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        tcpAiClient=TcpAiClient.getInstance(context,ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyBaseStatusExTwo=NotifyBaseStatusExTwo.getInstance();
        notifyEnvInfoTwo=NotifyEnvInfoTwo.getInstance();
        notifyEnvInfo=NotifyEnvInfo.getInstance();
        ImageLoader.clear(this); //清除图片缓存
        Logger.e("------144444");
        tcpClient.requestFile();     //请求所有地图
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        Logger.e("AIip"+udpIp1.getIp()+"AI端口"+udpIp1.getPort());



    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //Logger.e("-----当前页面：" + position);
        isChecked();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @OnClick({R.id.status, R.id.mapmanager, R.id.taskmanager, R.id.highset,R.id.tv_quit,R.id.tv_shutdown,R.id.sell_data,R.id.tv_auto_retail,R.id.tv_allControl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.status:
                Logger.e("---------setCurrentItem");
                vpHomePager.setCurrentItem(0);
                break;
            case R.id.mapmanager:
                vpHomePager.setCurrentItem(1);
                break;
            case R.id.taskmanager:
                vpHomePager.setCurrentItem(2);
                break;
            case R.id.highset:
                vpHomePager.setCurrentItem(3);
                break;
            case R.id.sell_data:
                if (!isWritePassword){
                    new InputDialog.Builder(getActivity())
                            .setTitle("请输入密码")
                            .setEditVisibility(View.VISIBLE)
                            .setListener(new InputDialog.OnListener() {
                                @Override
                                public void onConfirm(BaseDialog dialog, String content) {
                                    if (content.equals("admin")){
                                        vpHomePager.setCurrentItem(4);
                                        isWritePassword=true;
                                    }else {
                                        toast("密码错误，请重新输入");
                                    }
                                }
                                @Override
                                public void onCancel(BaseDialog dialog) {
                                    vpHomePager.setCurrentItem(0);
                                }
                            }).show();
                }else {
                    Logger.e("----已輸入密碼");
                    vpHomePager.setCurrentItem(4);
                }

                break;
            case R.id.tv_quit:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否退出")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                onBack();
//                                ActivityStackManager.getInstance().finishAllActivities();
//                                tcpClient.onDestroy();
//                                tcpAiClient.onDestroy();
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();

                break;
            case R.id.tv_shutdown:
                new InputDialog.Builder(this).setEditVisibility(View.GONE).setConfirm("关机").setCancel("重启").setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        tcpClient.reqCmdIpcMethod(BaseCmd.eCmdIPCMode.eShutDown);
                        toast("机器人正在关机中...");
                    }
                    @Override
                    public void onCancel(BaseDialog dialog) {
                        tcpClient.reqCmdIpcMethod(BaseCmd.eCmdIPCMode.eReStart);
                        toast("机器人正在重启中，请退出到登陆页面等待重启完成后再重新连接");
                    }
                }).show();
                break;
            case R.id.tv_auto_retail:
                new InputDialog.Builder(getActivity())
                        .setTitle("自动售卖开关")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if (!isAutoRetail){
                                    postAutoRetail(0);
                                    isAutoRetail=true;
                                }else {
                                    postAutoRetail(1);
                                    isAutoRetail=false;
                                }
                                toast("发送成功");
                            }
                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消发送");
                            }
                        }).show();

                break;
            case R.id.tv_allControl:
//                showAllControlPopupWindow();
                break;
        }
        isChecked();
    }


    /**
     * 判断哪个页面是否被选中
     */
    protected void isChecked() {
        switch (vpHomePager.getCurrentItem()) {
            case 0:
                tv_status.isChecked(true);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(false);
                sell_data.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_check), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                sell_data.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.retail_data_def), null, null, null);
                break;
            case 1:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(true);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(false);
                sell_data.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_check), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                sell_data.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.retail_data_def), null, null, null);
                break;
            case 2:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(true);
                sell_data.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_check), null, null, null);
                sell_data.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.retail_data_def), null, null, null);
                break;
            case 3:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(true);
                tv_taskManager.isChecked(false);
                sell_data.isChecked(false);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_check), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                sell_data.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.retail_data_def), null, null, null);
                break;
            case 4:
                tv_status.isChecked(false);
                tv_mapManager.isChecked(false);
                tv_highSet.isChecked(false);
                tv_taskManager.isChecked(false);
                sell_data.isChecked(true);
                tv_status.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.status_def), null, null, null);
                tv_mapManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.map_def), null, null, null);
                tv_highSet.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.hightset_def), null, null, null);
                tv_taskManager.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.version_def), null, null, null);
                sell_data.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.retail_data_chack), null, null, null);
                break;
        }
    }

    private void initStatusBar() {
        if (notifyBaseStatusEx != null) {
            try {
                DecimalFormat df = new DecimalFormat("0");
                DecimalFormat format = new DecimalFormat("0.00");
                currentMap = notifyBaseStatusEx.getCurroute();
                currentTask = notifyBaseStatusEx.getCurrpath();
                xsu=String.valueOf(format.format(notifyBaseStatusEx.getPosLinespeed()));
                jsu=String.valueOf(format.format(notifyBaseStatusEx.getPosAngulauspeed()));
                if(tv_xsu!=null && tv_jsu!=null){
                    tv_xsu.setText("线速度："+xsu+" m/s");
                    tv_jsu.setText("角速度："+jsu+" 弧度/秒");
                }
//                if (notifyEnvInfo.getBatt()!=0 && String.valueOf(notifyEnvInfo.getBatt())!=null){
//                    batteryNum=Integer.parseInt(df.format(notifyEnvInfo.getBatt()));
//                    tv_all_num1.setText(batteryNum+"%");
//                }
//                if (notifyBaseStatusEx.getRobotid()!=null){
//                    tv_all_name1.setText(notifyBaseStatusEx.getRobotid());
//                }
                switch (notifyBaseStatusEx.getStopStat()) {
                    case 4:
                        iv_jt_def.setVisibility(View.VISIBLE);
                        iv_jt_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.jt_nodef),null,null);
                        iv_yk_def.setVisibility(View.GONE);
                        iv_jt_def.setTextColor(getResources().getColor(R.color.white));
                        iv_yk_def.setTextColor(getResources().getColor(R.color.text_gray));
//                        iv_all_jt1.setImageResource(R.mipmap.jt_check);
                        break;
                    case 8:
                        iv_yk_def.setVisibility(View.VISIBLE);
                        iv_jt_def.setVisibility(View.GONE);
                        iv_yk_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.yk_nodef),null,null);
                        iv_jt_def.setTextColor(getResources().getColor(R.color.text_gray));
                        iv_yk_def.setTextColor(getResources().getColor(R.color.white));
//                        iv_all_jt1.setImageResource(R.mipmap.jt_default);
                        break;
                    case 12:
                        iv_jt_def.setVisibility(View.VISIBLE);
                        iv_yk_def.setVisibility(View.VISIBLE);
                        iv_jt_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.jt_nodef),null,null);
                        iv_yk_def.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.mipmap.yk_nodef),null,null);
                        iv_jt_def.setTextColor(getResources().getColor(R.color.white));
                        iv_yk_def.setTextColor(getResources().getColor(R.color.white));
//                        iv_all_jt1.setImageResource(R.mipmap.jt_check);
                        break;
                    case 0:
                        iv_jt_def.setVisibility(View.GONE);
                        iv_yk_def.setVisibility(View.GONE);
                        iv_jt_def.setTextColor(getResources().getColor(R.color.text_gray));
                        iv_yk_def.setTextColor(getResources().getColor(R.color.text_gray));
//                        iv_all_jt1.setImageResource(R.mipmap.jt_default);
                        break;
                }
//                switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
//                    case 0:
//                        tv_all_status2.setText("自标定中");
//                        //自标定
//                        break;
//                    case 1:
//                        switch (notifyBaseStatusEx.getMode()) {
//                            case 1:
//                                //Logger.e("待命模式" + modeView.getText());
//                                tv_all_status2.setText("待命中");
//                                break;
//                            case 3:
//                                tv_all_status2.setText("运动中");
//                                switch (notifyBaseStatusEx.getSonMode()){
//                                    case 3:
//                                        tv_all_status2.setText("异常");
//                                        break;
//                                    case 15:
//                                        tv_all_status2.setText("重定位中");
//                                        break;
//                                }
//                                break;
//                        }
//                        break;
//                }
//                if(notifyBaseStatusEx.isChargingStatus()) {
//                    iv_all_cd2.setImageResource(R.mipmap.chongd_check);
//                }else {
//                    iv_all_cd2.setImageResource(R.mipmap.chongd_def);
//                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
//        if (notifyBaseStatusExTwo != null) {
//            try {
//                DecimalFormat df = new DecimalFormat("0");
//                DecimalFormat format = new DecimalFormat("0.00");
//                if (notifyEnvInfoTwo.getBatt()!=0 && String.valueOf(notifyEnvInfoTwo.getBatt())!=null){
//                    batteryNum2=Integer.parseInt(df.format(notifyEnvInfoTwo.getBatt()));
//                    tv_all_num2.setText(batteryNum2+" %");
//                }
//                if (notifyBaseStatusExTwo.getRotbotid()!=null){
//                    tv_all_name2.setText(notifyBaseStatusExTwo.getRotbotid());
//                }
//                switch (notifyBaseStatusExTwo.getStopStat()) {
//                    case 4:
//                        iv_all_jt2.setImageResource(R.mipmap.jt_check);
//                        break;
//                    case 8:
//                        iv_all_jt2.setImageResource(R.mipmap.jt_default);
//                        break;
//                    case 12:
//                        iv_all_jt2.setImageResource(R.mipmap.jt_check);
//                        break;
//                    case 0:
//                        iv_all_jt2.setImageResource(R.mipmap.jt_default);
//                        break;
//                }
//                switch (notifyBaseStatusExTwo.geteSelfCalibStatus()) {
//                    case 0:
//                        tv_all_status2.setText("自标定中");
//                        //自标定
//                        break;
//                    case 1:
//                        switch (notifyBaseStatusExTwo.getMode()) {
//                            case 1:
//                                //Logger.e("待命模式" + modeView.getText());
//                                tv_all_status2.setText("待命中");
//                                break;
//                            case 3:
//                                tv_all_status2.setText("运动中");
//                                switch (notifyBaseStatusExTwo.getSonMode()){
//                                    case 3:
//                                        tv_all_status2.setText("异常");
//                                        break;
//                                    case 15:
//                                        tv_all_status2.setText("重定位中");
//                                        break;
//                                }
//                                break;
//                        }
//                        break;
//                }
//                if(notifyBaseStatusExTwo.isChargingStatus()) {
//                    iv_all_cd2.setImageResource(R.mipmap.chongd_check);
//                }else {
//                    iv_all_cd2.setImageResource(R.mipmap.chongd_def);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
    }

    /**
     * 设置状态栏颜色为浅色
     *
     * @return 返回为true 为黑色
     */
    @Override
    public boolean statusBarDarkFont() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (DoubleClickHelper.isOnDoubleClick()) {
            //移动到上一个任务栈，避免侧滑引起的不良反应
            moveTaskToBack(false);
            postDelayed(new Runnable() {

                @Override
                public void run() {
                    // 进行内存优化，销毁掉所有的界面
                    ActivityStackManager.getInstance().finishAllActivities();
                    tcpClient.onDestroy();
                    // 销毁进程（请注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
                    // System.exit(0);
                }
            }, 300);
        } else {
            toast("再按一次退出");
        }
        tcpAiClient.disConnect();
    }

    /**
     * 返回登陆界面
     */
    public void onBack() {
        Intent intent_login = new Intent();
        intent_login.setClass(HomeActivity.this, LoginActivity.class);
        intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
        startActivity(intent_login);
        tcpAiClient.disConnect();
        tcpAiClient.onDestroy();
        finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPagerAdapter.getCurrentFragment().onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        vpHomePager.removeOnPageChangeListener(this);
        vpHomePager.setAdapter(null);
        tcpClient.disConnect();
        tcpAiClient.disConnect();
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
        super.onDestroy();
    }

    private boolean exit=false;       //线程是否被终止
    public void getMapInfo() {
        new Thread(() -> {
            while (!exit) {
                if (currentMap != null && !currentMap.equals("PathError")) {
                    Logger.e("-----------" + currentMap);
                    tcpClient.getMapInfo(ByteString.copyFromUtf8(currentMap));  //获取某个地图信息
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
     * 遥控弹窗
     */
    private void showControlPopupWindow() {
        View contentView = null;
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_yk_check, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()
                .showAsDropDown(findViewById(R.id.taskmanager),0,-10);
        seekBar=contentView.findViewById(R.id.seek_bar_control);
        fixedSpeed=contentView.findViewById(R.id.fixed_speed);
        myRocker=contentView.findViewById(R.id.my_rocker_control);
        myRockerZy=contentView.findViewById(R.id.my_rocker_zy_control);
        tvSpeed=contentView.findViewById(R.id.tv_speed_control);
        iv_quit_yk=contentView.findViewById(R.id.iv_quit_yk);
        tv_xsu=contentView.findViewById(R.id.robot_xsu);//线速度
        tv_jsu=contentView.findViewById(R.id.robot_jsu);//角速度
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
        maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        iv_quit_yk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopuWindow.dissmiss();
                timer.cancel();
                task.cancel();
                try {
                    FloatWindow.get().show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 集控弹窗
     */
//    private TextView tv_all_name1;
//    private TextView tv_all_num1;
//    private TextView tv_all_status1;
//    private ImageView iv_all_jt1;
//    private ImageView iv_all_cd1;
//    private TextView tv_all_name2;
//    private TextView tv_all_num2;
//    private TextView tv_all_status2;
//    private ImageView iv_all_jt2;
//    private ImageView iv_all_cd2;
//    private BasrTextView tv_all_start;
//    private BasrTextView tv_all_stop;
//    private BasrTextView tv_all_end;
//    private TextView tv_all_quit;
//    private TextView tv_all_reference;
//    private ZoomImageView zoomImageView;
//    private void showAllControlPopupWindow() {
//        View contentView = null;
//        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_auto_retail, null);
//        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(this)
//                .setView(contentView)
//                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//                .enableOutsideTouchableDissmiss(true)// 设置点击PopupWindow之外的地方，popWindow不关闭，如果不设置这个属性或者为true，则关闭
//                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
//                .create()
//                .showAsDropDown(findViewById(R.id.taskmanager), 0, -10);
//        tv_all_name1=contentView.findViewById(R.id.tv_all_name1);
//        tv_all_num1=contentView.findViewById(R.id.tv_all_num1);
//        tv_all_status1=contentView.findViewById(R.id.tv_all_status);
//        iv_all_jt1=contentView.findViewById(R.id.iv_all_jt);
//        iv_all_cd1=contentView.findViewById(R.id.iv_all_cd);
//        tv_all_name2=contentView.findViewById(R.id.tv_all_name2);
//        tv_all_num2=contentView.findViewById(R.id.tv_all_num2);
//        tv_all_status2=contentView.findViewById(R.id.tv_all_status2);
//        iv_all_jt2=contentView.findViewById(R.id.iv_all_jt2);
//        iv_all_cd2=contentView.findViewById(R.id.iv_all_cd2);
//        tv_all_start=contentView.findViewById(R.id.tv_all_start);
//        tv_all_stop=contentView.findViewById(R.id.tv_all_stop);
//        tv_all_end=contentView.findViewById(R.id.tv_all_end);
//        tv_all_quit=contentView.findViewById(R.id.tv_all_quit);
//        tv_all_reference=contentView.findViewById(R.id.tv_all_reference);
//        zoomImageView=contentView.findViewById(R.id.zoom_all);
//        isAllChecked();
//        tv_all_quit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customPopuWindow.dissmiss();
//                try {
//                    FloatWindow.get().show();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    /**
//     * 判断哪个页面是否被选中
//     */
//    private void isAllChecked() {
//        tv_all_start.setType(1);
//        tv_all_stop.setType(1);
//        tv_all_end.setType(1);
//    }

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
                        tcpClient.sendSpeed(lineSpeed, palstance);
                    }
                } else {
                    a = 0;
                    //Logger.e("线速度，角速度：" + lineSpeed + ";" + palstance);
                    tcpClient.sendSpeed(lineSpeed, palstance);
                }

            }
        };
        timer.schedule(task, 0, 50);
    }


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

    @Override
    public void onSoftKeyboardOpened(int keyboardHeight) {

    }

    @Override
    public void onSoftKeyboardClosed() {

    }
    public UdpClient udpClient;
    private int aiPort=18888;
    public TcpAiClient tcpAiClient;
    private UdpIp udpIp1=new UdpIp();
    /**
     * 接收AIServer广播
     */
    private void receiveAiBroadcast(){
        Logger.e("接受AI广播");
            udpClient= UdpClient.getInstance(this,ClientMessageDispatcher.getInstance());
            try {
                if (globalParameter.isLan()){
                    udpClient.connect(aiPort);
                }else {
                    Logger.e("广域网不需要广播");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 发送自动售卖请求
     */
    private void postAutoRetail(int type){
        DDRAIServiceCmd.eRetailEnableType enableType;
        switch (type){
            case 0:
                enableType=DDRAIServiceCmd.eRetailEnableType.eEnable;
                break;
            case 1:
                enableType=DDRAIServiceCmd.eRetailEnableType.eDisable;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        DDRAIServiceCmd.reqRetailRecognizing reqRetailRecognizing=DDRAIServiceCmd.reqRetailRecognizing.newBuilder()
                .setType(enableType)
                .build();
        if (globalParameter.isLan()){
            tcpAiClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqRetailRecognizing);
        }else {
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer),reqRetailRecognizing);
        }
    }

}

