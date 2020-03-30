package ddr.example.com.newretailandroidclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import DDRVLNMapProto.DDRVLNMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRActivity;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.MapFileStatus;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.point.PathLine;
import ddr.example.com.newretailandroidclient.entity.point.SpaceItem;
import ddr.example.com.newretailandroidclient.entity.point.TargetPoint;
import ddr.example.com.newretailandroidclient.helper.ListTool;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.adapter.PathAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.TargetPointAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;
import ddr.example.com.newretailandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.newretailandroidclient.widget.textview.GridTextView;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.newretailandroidclient.widget.view.GridLayerView;
import ddr.example.com.newretailandroidclient.widget.view.LineView;
import ddr.example.com.newretailandroidclient.widget.view.PointView;
import ddr.example.com.newretailandroidclient.widget.view.RockerView;
import ddr.example.com.newretailandroidclient.widget.view.ZoomImageView;

import static ddr.example.com.newretailandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_HORIZONTAL;
import static ddr.example.com.newretailandroidclient.widget.view.RockerView.DirectionMode.DIRECTION_2_VERTICAL;

/**
 * time  : 2019/10/29
 * desc  : 地图编辑页面
 * remark：包括 编辑虚拟墙 、添加目标点、添加路径、添加任务、编辑任务等功能
 */
public class MapEditActivity extends DDRActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_target_point)
    TextView tvTargetPoint;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_025m)
    GridTextView tv025m;
    @BindView(R.id.tv_05m)
    GridTextView tv05m;
    @BindView(R.id.tv_1m)
    GridTextView tv1m;
    @BindView(R.id.tv_2m)
    GridTextView tv2m;
    @BindView(R.id.speed_layout)
    LinearLayout speedLayout;         //速度调节布局
    @BindView(R.id.zmap)
    ZoomImageView zmap;
    @BindView(R.id.iv_center)
    ImageView ivCenter;
    @BindView(R.id.tv_mark_current)
    TextView tvMarkCurrent;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.seek_bar)
    VerticalRangeSeekBar seekBar;
    @BindView(R.id.fixed_speed)
    CheckBox fixedSpeed;

    @BindView(R.id.my_rocker)
    RockerView myRocker;
    @BindView(R.id.my_rocker_zy)
    RockerView myRockerZy;
    @BindView(R.id.iv_add_path)
    TextView tvAddPath;
    @BindView(R.id.delete_point)
    TextView tvDeletePoint;
    @BindView(R.id.save_path)
    TextView tvSavePath;

    @BindView(R.id.bt_delete_wall)
    Button btDeleteWall;


  /*  @BindView(R.id.recycler_target)
    RecyclerView recyclerTarget;        // 勾选目标点组建路径 ,目标点列表*/
    @BindView(R.id.tv_selected_point)
    TextView tvSelectedPoint;          // 是否显示目标点列表
    private boolean checkablePoint=false;    //是否勾选目标点建路径

    private boolean ishaveChecked = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private float lineSpeed, palstance;  //线速度 ，角速度
    private double maxSpeed = 0.4;       //设置的最大速度
    private boolean isforward, isGoRight; //左右摇杆当前的方向
    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private MapFileStatus mapFileStatus;
    private List<TargetPoint> newPoints = new ArrayList<>();
    private List<PathLine> newPaths = new ArrayList<>();
    private List<PathLine.PathPoint> pathPoints=new ArrayList<>();
    private List<TargetPoint> targetPoints,selectPoints;
    private List<PathLine> pathLines;                 // 显示在界面上的路径列表
    private TargetPointAdapter targetPointAdapter;
    // private TargetPointAdapter selectPointAdapter;    勾选目标点形成路径的列表适配器
    private PathAdapter pathAdapter;
    private StringAdapter editTypeAdapter,graphTypeAdapter;           //编辑类型 、图形类型适配器
    private List<String> editTypes=new ArrayList<>();
    private List<String> graphTypes=new ArrayList<>();
    private Bitmap lookBitmap;
    private boolean isFreeHand=true;          //是否是手绘点 ,即不是通过移动机器人获取的点坐标

    public static final int CREATE_POINT=1;   //新建目标点
    public static final int CREATE_PATH=2;    //新建路径
    public static final int EDIT_MAP=3;       //编辑地图 -虚拟墙等
    private int activityType;                         //界面的类型



    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_edit;
    }


    @Override
    protected void initView() {
        super.initView();
        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        initSeekBar();
        initRockerView();
        initTimer();
        setFixedSpeed();
        targetPointAdapter = new TargetPointAdapter(R.layout.item_show_recycler);
        //selectPointAdapter=new TargetPointAdapter(R.layout.item_point_to_path);
        pathAdapter = new PathAdapter(R.layout.item_show_recycler);
        editTypeAdapter=new StringAdapter(R.layout.item_show_recycler);
        graphTypeAdapter=new StringAdapter(R.layout.item_show_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recyclerTarget.setLayoutManager(layoutManager);
        //recyclerTarget.setAdapter(selectPointAdapter);
        GridLayerView.getInstance(zmap).onDestroy();

    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        activityType=intent.getIntExtra("type",0);
        String bitmap=intent.getStringExtra("bitmapPath");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(bitmap);
            lookBitmap= BitmapFactory.decodeStream(fis);
            zmap.setImageBitmap(lookBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mapFileStatus = MapFileStatus.getInstance();
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        maxSpeed = sharedPreferences.getFloat("speed", (float) 0.4);
        seekBar.setProgress((float) maxSpeed);
        tvSpeed.setText(String.valueOf(maxSpeed));
        try {
            targetPoints = ListTool.deepCopy((List<TargetPoint>)intent.getSerializableExtra("targetList"));
            pathLines = ListTool.deepCopy((List<PathLine>)intent.getSerializableExtra("pathList"));
            selectPoints=ListTool.deepCopy(targetPoints);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
       // selectPointAdapter.setNewData(selectPoints);
        editTypes.add("虚拟墙");
        graphTypes.add("直线");
        //graphTypes.add("圆");
        //graphTypes.add("多边形");
        initType(activityType);
        onShowItemClick();
        //onItemSelectClick();

    }


    @Override
    public boolean statusBarDarkFont() {
        return false;
    }


    @OnClick({R.id.iv_back,R.id.tv_target_point, R.id.tv_path, R.id.tv_025m, R.id.tv_05m, R.id.tv_1m, R.id.tv_2m, R.id.tv_mark_current,R.id.tv_selected_point, R.id.fixed_speed,R.id.iv_add_path
    ,R.id.delete_point,R.id.save_path,R.id.bt_delete_wall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_target_point:
                if (tvTargetPoint.getText().toString().contains("目标点")){
                    showPopupWindow(tvTargetPoint, 0);
                }else {
                    showPopupWindow(tvTargetPoint,2);
                }
                break;
            case R.id.tv_path:
                if (tvPath.getText().toString().contains("路径")){
                    showPopupWindow(tvPath, 1);
                }else {
                    showPopupWindow(tvPath,3);
                }
                break;
            case R.id.tv_025m:
                if (!tv025m.getSelected()) {
                    GridLayerView.getInstance(zmap).setPrecision((float) 0.25);        //将图片网格化
                    zmap.invalidate();
                    setIconDefault1();
                    tv025m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                    tv025m.setSelected(true);
                } else {
                    GridLayerView.getInstance(zmap).setPrecision(0);        //取消网格
                    zmap.invalidate();
                    setIconDefault1();

                }
                break;
            case R.id.tv_05m:
                if (!tv05m.getSelected()) {
                    GridLayerView.getInstance(zmap).setPrecision((float) 0.5);        //将图片网格化
                    zmap.invalidate();
                    setIconDefault1();
                    tv05m.setSelected(true);
                    tv05m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                } else {
                    setIconDefault1();
                    GridLayerView.getInstance(zmap).setPrecision(0);        //取消网格
                    zmap.invalidate();

                }
                break;
            case R.id.tv_1m:
                if (!tv1m.getSelected()) {
                    GridLayerView.getInstance(zmap).setPrecision((float) 1);        //将图片网格化
                    zmap.invalidate();
                    setIconDefault1();
                    tv1m.setSelected(true);
                    tv1m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                } else {
                    setIconDefault1();
                    GridLayerView.getInstance(zmap).setPrecision(0);        //取消网格
                    zmap.invalidate();
                }
                break;
            case R.id.tv_2m:
                if (!tv2m.getSelected()) {
                    GridLayerView.getInstance(zmap).setPrecision((float) 2);        //将图片网格化
                    zmap.invalidate();
                    setIconDefault1();
                    tv2m.setSelected(true);
                    tv2m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg),null,null,null);
                } else {
                    setIconDefault1();
                    GridLayerView.getInstance(zmap).setPrecision(0);        //取消网格
                    zmap.invalidate();
                }
                break;
            case R.id.tv_mark_current:
                if (speedLayout.getVisibility() == View.VISIBLE) {
                    speedLayout.setVisibility(View.GONE);
                    myRocker.setVisibility(View.GONE);
                    ivCenter.setVisibility(View.VISIBLE);
                    myRockerZy.setVisibility(View.INVISIBLE);
                    isFreeHand=true;
                    PointView.getInstance(this).isRuning=false;
                    zmap.invalidate();
                    tvMarkCurrent.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
                } else {
                    ivCenter.setVisibility(View.VISIBLE);
                    speedLayout.setVisibility(View.VISIBLE);
                    myRocker.setVisibility(View.VISIBLE);
                    myRockerZy.setVisibility(View.VISIBLE);
                    ivCenter.setVisibility(View.GONE);
                    PointView.getInstance(this).isRuning=true;
                    isFreeHand=false;
                    zmap.invalidate();
                    tvMarkCurrent.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                }
                break;
            /*** 勾选目标点建路径**/
            case R.id.tv_selected_point:
                if (!checkablePoint){
                    checkablePoint=true;
                    tvAddPath.setVisibility(View.GONE);
                    tvDeletePoint.setVisibility(View.GONE);
                    ivCenter.setVisibility(View.GONE);
                    PointView.getInstance(context).set2TouchPoints(selectPoints);
                    PointView.getInstance(context).setIsTouch(checkablePoint);
                    tvSelectedPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                    zmap.invalidate();
                }else {
                    checkablePoint=false;
                    PointView.getInstance(context).set2TouchPoints(null);
                    PointView.getInstance(context).setIsTouch(checkablePoint);
                    zmap.invalidate();
                    tvAddPath.setVisibility(View.VISIBLE);
                    tvDeletePoint.setVisibility(View.VISIBLE);
                    ivCenter.setVisibility(View.VISIBLE);
                    tvSelectedPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
                }
                pathPoints.clear();
                for (TargetPoint targetPoint:selectPoints){
                    targetPoint.setMultiple(false);
                }
                break;
            case R.id.fixed_speed:
                break;

            case R.id.iv_add_path:
                switch (activityType){
                    case CREATE_POINT:
                        addPoint();
                        break;
                    case CREATE_PATH:
                        PathLine.PathPoint pathPoint=new PathLine().new PathPoint();
                        pathPoint.setY(zmap.getTargetPoint().getY());
                        pathPoint.setX(zmap.getTargetPoint().getX());
                        pathPoints.add(pathPoint);
                        LineView.getInstance(getApplication()).setPoints(pathPoints);
                        zmap.invalidate();
                        break;
                    case EDIT_MAP:
                        addVirtualWall();
                        break;
                }
                break;
            case R.id.delete_point:
                if (activityType==CREATE_PATH){
                    if (pathPoints.size()>0){
                        pathPoints.remove(pathPoints.size()-1);
                        LineView.getInstance(getApplication()).setPoints(pathPoints);
                        zmap.invalidate();
                    }else {
                        toast("请先添加点");
                    }
                }else {
                    deleteVirtualWall();
                }
                break;
            case R.id.save_path:
                if (activityType==CREATE_PATH){
                    if (pathPoints.size()>1){
                       inputDialog=new InputDialog.Builder(this).setTitle("添加路径名")
                                .setHint("请输入")
                                .setAutoDismiss(false)
                                .setListener(new InputDialog.OnListener() {
                                    @Override
                                    public void onConfirm(BaseDialog dialog, String content) {
                                        if (!content.isEmpty()){
                                            if (checkPathName(content)){
                                                toast("路径名字重复，请重新命名！");
                                            }else {
                                                PathLine pathLine=new PathLine();
                                                pathLine.setName(content);
                                                if (!checkablePoint){
                                                    for (int i=0;i<pathPoints.size();i++){
                                                        pathPoints.get(i).setName(content+"_"+i);
                                                    }
                                                    pathLine.setPathType(1);
                                                }else {
                                                    pathLine.setPathType(2);
                                                }
                                                List<PathLine.PathPoint> pathPoints1=new ArrayList<>();
                                                try {
                                                    pathPoints1=ListTool.deepCopy(pathPoints);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (ClassNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                pathLine.setPathPoints(pathPoints1);
                                                pathLine.setVelocity(0.4f);
                                                pathLine.setPathModel(64);
                                                newPaths.add(pathLine);
                                                pathLines.add(pathLine);
                                                pathPoints.clear();
                                                for (TargetPoint targetPoint:selectPoints){
                                                    targetPoint.setMultiple(false);
                                                }
                                                //selectPointAdapter.setNewData(selectPoints);
                                                tvPath.setText("路径" + "(" + pathLines.size() + ")");
                                                toast("保存成功!");
                                                inputDialog.dismiss();
                                            }
                                        }else {
                                            toast("请先输入名称");
                                        }
                                    }
                                    @Override
                                    public void onCancel(BaseDialog dialog) {
                                        pathPoints.clear();
                                        toast("取消添加");
                                        inputDialog.dismiss();
                                    }
                                })
                                .show();
                    }else {
                        toast("至少选择两个点组成一条路径");
                    }
                }else {
                    saveVirtualWall();
                }
                break;
            case R.id.bt_delete_wall:
                new InputDialog.Builder(getActivity())
                        .setTitle("是否删除")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                int position=LineView.getInstance(getApplication()).selectPosition;
                                if (position!=-1){
                                    List<DDRVLNMap.space_pointEx> space_pointExes=new ArrayList<>();
                                    spaceItems.get(position).setLines(space_pointExes);
                                    LineView.getInstance(getApplication()).selectPosition=-1;
                                    zmap.invalidate();
                                }else {
                                    toast("请先选择要删除的虚拟墙哦");
                                }
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {

                            }
                        }).show();

                break;


        }
    }

     private BaseDialog inputDialog;
    /**
     * 添加目标点
     */
    private void addPoint() {
        Logger.e("--------?");
        inputDialog=new InputDialog.Builder(this).setTitle("添加目标名")
                .setHint("目标点-" + targetPoints.size())
                .setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        TargetPoint targetPoint = new TargetPoint(2);
                        if (!content.isEmpty()) {
                            targetPoint.setName(content);
                        } else {
                            targetPoint.setName("目标点-" + targetPoints.size());
                        }
                        if (!isFreeHand) {
                            targetPoint.setX(notifyBaseStatusEx.getPosX());
                            targetPoint.setY(notifyBaseStatusEx.getPosY());
                        } else {
                            targetPoint.setX(zmap.getTargetPoint().getX());
                            targetPoint.setY(zmap.getTargetPoint().getY());
                        }
                        targetPoint.setInTask(true);  //方便显示
                        targetPoint.setTheta(0);
                        if (checkPointName(targetPoint)){
                            toast("目标点名字重复，请重新命名");
                        }else {
                            newPoints.add(targetPoint);
                            try {
                                List<TargetPoint> points = ListTool.deepCopy(newPoints);
                                PointView.getInstance(getApplicationContext()).setPoints(points);
                                zmap.invalidate();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            targetPoints.add(targetPoint);
                            tvTargetPoint.setText("目标点" + "(" + targetPoints.size() + ")");
                            inputDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast("取消添加");
                        inputDialog.dismiss();
                    }
                })
                .show();

    }

    private List<DDRVLNMap.space_pointEx> lines=new ArrayList<>();       //线段
    private List<DDRVLNMap.space_pointEx> polygons=new ArrayList<>();    //多边形
    private void addVirtualWall(){
        switch (mPosition){
            case 0:
                DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
                        .setX(zmap.getTargetPoint().getX())
                        .setY(zmap.getTargetPoint().getY())
                        .build();
                lines.add(space_pointEx);
                LineView.getInstance(getApplication()).setLines(lines);
                zmap.invalidate();
                break;
            case 2:
                DDRVLNMap.space_pointEx space_pointEx1=DDRVLNMap.space_pointEx.newBuilder()
                        .setX(zmap.getTargetPoint().getX())
                        .setY(zmap.getTargetPoint().getY())
                        .build();
                polygons.add(space_pointEx1);
                LineView.getInstance(getApplication()).setPolygons(polygons);
                zmap.invalidate();
                break;
        }
    }

    /**
     * 防止目标点重名
     * @param targetPoint
     * @return true 表示重名
     */
    private boolean checkPointName(TargetPoint targetPoint){
       for (TargetPoint targetPoint1:targetPoints){
           if (targetPoint1.getName().equals(targetPoint.getName())){
               return true;
           }
       }
       return false;
    }

    /**
     * 防止路径重名
     * @param pathName
     * @return true 表示重名
     */
    private boolean checkPathName(String pathName){
        for (PathLine pathLine:pathLines){
            if (pathLine.getName().equals(pathName)){
                return true;
            }
        }
        return false;
    }

    private void deleteVirtualWall(){
        switch (mPosition){
            case 0:
                if (lines.size()>0){
                    lines.remove(lines.size()-1);
                    zmap.invalidate();
                }else {
                    toast("请先添加点");
                }
                break;
            case 2:
                if (polygons.size()>0){
                    polygons.remove(polygons.size()-1);
                    zmap.invalidate();
                }else {
                    toast("请先添加点");
                }
                break;
        }
    }
    private List<SpaceItem> spaceItems;
    private void saveVirtualWall(){
        switch (mPosition){
            case 0:
                SpaceItem spaceItem=new SpaceItem();
                spaceItem.setName("space-"+spaceItems.size());
                spaceItem.setType(1);
                spaceItem.setLines(lines);
                spaceItems.add(spaceItem);
                BaseDialog dialog=new WaitDialog.Builder(this)
                        .setMessage("保存中")
                        .show();
                postDelayed(() -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        lines=new ArrayList<>();
                        LineView.getInstance(this).setLines(null);
                        zmap.invalidate();
                    }
                }, 500);
                break;
            case 2:

                break;
        }
    }

    /**
     * 设置网格图标默认状态
     */
    private void setIconDefault1(){
        tv025m.setSelected(false);
        tv05m.setSelected(false);
        tv1m.setSelected(false);
        tv2m.setSelected(false);
        tv025m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tv05m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tv1m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tv2m.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
    }


    /*************************************显示选择目标点和路径的弹窗*********************************************/
    private CustomPopuWindow customPopuWindow;
    private RecyclerView showRecycler;
    private TextView tv_all_selected;
    private boolean allShowPoint,allShowPath;

    private void showPopupWindow(View view, int type) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.window_point, null);
        customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .create()
                .showAsDropDown(view, 0, 0);
        showRecycler = contentView.findViewById(R.id.show_Recycler);
        tv_all_selected=contentView.findViewById(R.id.tv_all_select);
        if(type!=0&&type!=1){ tv_all_selected.setVisibility(View.GONE); }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        showRecycler.setLayoutManager(layoutManager);
        switch (type){
            case 0:
                showRecycler.setAdapter(targetPointAdapter);
                targetPointAdapter.setNewData(targetPoints);
                if (allShowPoint){
                    tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                    tv_all_selected.setTextColor(getResources().getColor(R.color.white));
                }else {
                    tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                    tv_all_selected.setTextColor(getResources().getColor(R.color.text_gray));
                }
                break;
            case 1:
                showRecycler.setAdapter(pathAdapter);
                pathAdapter.setNewData(pathLines);
                if (allShowPath){
                    tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                    tv_all_selected.setTextColor(getResources().getColor(R.color.white));
                }else {
                    tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                    tv_all_selected.setTextColor(getResources().getColor(R.color.text_gray));
                }
                break;
            case 2:
                showRecycler.setAdapter(editTypeAdapter);
                editTypeAdapter.setNewData(editTypes);
                break;
            case 3:
                showRecycler.setAdapter(graphTypeAdapter);
                graphTypeAdapter.setNewData(graphTypes);
                break;
        }
        //全选的点击事件
        tv_all_selected.setOnClickListener((v)->{
            switch (type){
                case 0:
                    if (allShowPoint){
                        allShowPoint=false;
                        tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                        tv_all_selected.setTextColor(getResources().getColor(R.color.text_gray));
                        for (TargetPoint targetPoint:targetPoints){
                            targetPoint.setMultiple(false);
                        }
                    }else {
                        allShowPoint=true;
                        tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        tv_all_selected.setTextColor(getResources().getColor(R.color.white));
                        for (TargetPoint targetPoint:targetPoints){
                            targetPoint.setMultiple(true);
                        }
                    }
                    PointView.getInstance(this).setTargetPoints(targetPoints);
                    zmap.invalidate();
                    targetPointAdapter.setNewData(targetPoints);
                    break;
                case 1:
                    if (allShowPath){
                        allShowPath=false;
                        tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_hide),null);
                        tv_all_selected.setTextColor(getResources().getColor(R.color.text_gray));
                        for (PathLine pathLine:pathLines){
                            pathLine.setMultiple(false);
                        }
                    }else {
                        allShowPath=true;
                        tv_all_selected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.item_show),null);
                        tv_all_selected.setTextColor(getResources().getColor(R.color.white));
                        for (PathLine pathLine:pathLines){
                            pathLine.setMultiple(true);
                        }
                    }
                    LineView.getInstance(this).setPathLines(pathLines);
                    zmap.invalidate();
                    pathAdapter.setNewData(pathLines);
                    break;
            }
        });


    }

    private int mPosition;
    /**
     * 弹窗事件点击事件
     */
    private void onShowItemClick() {
        targetPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (targetPoints.get(position).isMultiple()){
                targetPoints.get(position).setMultiple(false);
                PointView.getInstance(this).setTargetPoints(targetPoints);
                zmap.invalidate();
                targetPointAdapter.setNewData(targetPoints);
            }else {
                targetPoints.get(position).setMultiple(true);
                PointView.getInstance(this).setTargetPoints(targetPoints);
                zmap.invalidate();
                targetPointAdapter.setNewData(targetPoints);
            }
        });

        pathAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (pathLines.get(position).isMultiple()){
                pathLines.get(position).setMultiple(false);
                LineView.getInstance(this).setPathLines(pathLines);
                zmap.invalidate();
                pathAdapter.setNewData(pathLines);
            }else {
                pathLines.get(position).setMultiple(true);
                LineView.getInstance(this).setPathLines(pathLines);
                zmap.invalidate();
                pathAdapter.setNewData(pathLines);
            }
        });

        editTypeAdapter.setOnItemClickListener((adapter, view, position) -> {
            tvTargetPoint.setText(editTypes.get(position));
        });

        graphTypeAdapter.setOnItemClickListener((adapter, view, position) -> {
            mPosition=position;
            switch (position){
                case 0:
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_line_blue),null,null,null);
                    break;
                case 1:
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_circle_blue),null,null,null);
                    break;
                case 2:
                    tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_polygon_blue),null,null,null);
                    break;
            }

        });

    }
    /***************************************************end*************************************************************/


   /* *//**
     * 点击勾选目标点组建路径
     *//*
    private void onItemSelectClick(){
        selectPointAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (selectPoints.get(position).isMultiple()){
                selectPoints.get(position).setMultiple(false);
                for (int i=0;i<pathPoints.size();i++){
                    if (selectPoints.get(position).getName().equals(pathPoints.get(i).getName())){
                        pathPoints.remove(i);
                        LineView.getInstance(getApplication()).
                                setPoints(pathPoints);
                        zmap.invalidate();
                    }
                }
                selectPointAdapter.setNewData(selectPoints);
            }else {
                selectPoints.get(position).setMultiple(true);
                PathLine.PathPoint pathPoint=new PathLine().new PathPoint();
                pathPoint.setName(selectPoints.get(position).getName());
                pathPoint.setY(selectPoints.get(position).getY());
                pathPoint.setX(selectPoints.get(position).getX());
                pathPoints.add(pathPoint);
                LineView.getInstance(getApplication()).
                        setPoints(pathPoints);
                zmap.invalidate();
                selectPointAdapter.setNewData(selectPoints);
            }
        });
    }*/

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
    private boolean isRuning;      // 是否在遥控机器人运行

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
                    if (a <= 10) {
                        //Logger.e("----a:" + a);
                        tcpClient.sendSpeed(lineSpeed, palstance);
                    }else {
                        isRuning=false;
                    }
                } else {
                    a = 0;
                    //Logger.e("线速度，角速度：" + lineSpeed + ";" + palstance);
                    tcpClient.sendSpeed(lineSpeed, palstance);
                    isRuning=true;
                }

            }
        };
        timer.schedule(task, 0, 90);
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
    protected void onDestroy() {
        super.onDestroy();
        editor.putFloat("speed", (float) maxSpeed);
        editor.commit();
        timer.cancel();
        task.cancel();
    }

    /**
     * 判断进来的是何种操作（添加目标点、添加路径、或者添加虚拟墙）
     * @param type
     */
    public void initType(int type){
        switch (type){
            case 1:
                Logger.e("新建点");
                tvAddPath.setVisibility(View.VISIBLE);
                tvMarkCurrent.setVisibility(View.VISIBLE);
                ivCenter.setVisibility(View.VISIBLE);
                tvTargetPoint.setText("目标点" + "(" + targetPoints.size() + ")");
                tvPath.setText("路径" + "(" + pathLines.size() + ")");
                LineView.getInstance(this).clearDraw();
                break;
            case 2:
                ivCenter.setVisibility(View.VISIBLE);
                tvAddPath.setVisibility(View.VISIBLE);
                tvDeletePoint.setVisibility(View.VISIBLE);
                tvSavePath.setVisibility(View.VISIBLE);
                tvSelectedPoint.setVisibility(View.VISIBLE);
                tvTargetPoint.setText("目标点" + "(" + targetPoints.size() + ")");
                tvPath.setText("路径" + "(" + pathLines.size() + ")");
                LineView.getInstance(this).clearDraw();
                zmap.invalidate();
                break;
            case 3:
                mapFileStatus = MapFileStatus.getInstance();
                tvTargetPoint.setText(editTypes.get(0));
                tvTargetPoint.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.virtual_wall_blue),null,null,null);
                tvPath.setText(graphTypes.get(0));
                tvPath.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_line_blue),null,null,null);
                btDeleteWall.setVisibility(View.VISIBLE);
                ivCenter.setVisibility(View.VISIBLE);
                tvAddPath.setVisibility(View.VISIBLE);
                tvDeletePoint.setVisibility(View.VISIBLE);
                tvSavePath.setVisibility(View.VISIBLE);
                spaceItems=mapFileStatus.getSpaceItems();
                if (spaceItems==null){
                    Logger.e("列表为空");
                    spaceItems=new ArrayList<>();
                }
                PointView.getInstance(getApplication()).clearDraw();
                LineView.getInstance(getApplication()).clearDraw();
                LineView.getInstance(getApplication()).setSpaceItems(spaceItems);
                zmap.invalidate();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                if (isRuning){
                    zmap.invalidate();
                }
                break;
            case touchSelectPoint:
                int position= (int) messageEvent.getData();
                if (selectPoints.get(position).isMultiple()){
                    selectPoints.get(position).setMultiple(false);
                    for (int i=0;i<pathPoints.size();i++){
                        if (selectPoints.get(position).getName().equals(pathPoints.get(i).getName())){
                            pathPoints.remove(i);
                            LineView.getInstance(getApplication()).
                                    setPoints(pathPoints);
                            zmap.invalidate();
                        }
                    }

                }else {
                    selectPoints.get(position).setMultiple(true);
                    PathLine.PathPoint pathPoint=new PathLine().new PathPoint();
                    pathPoint.setName(selectPoints.get(position).getName());
                    pathPoint.setY(selectPoints.get(position).getY());
                    pathPoint.setX(selectPoints.get(position).getX());
                    pathPoints.add(pathPoint);
                    LineView.getInstance(getApplication()).
                            setPoints(pathPoints);
                    zmap.invalidate();
                }
                break;
        }
    }



    /**
     * 系统退出按键
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logger.e("-------退出");
        PointView.getInstance(context).clearDraw();
        LineView.getInstance(context).clearDraw();
        isRuning=false;
        toPostData();
    }

    /**
     * 给原始页面传递数据
     */
    private void toPostData() {
        if (activityType==CREATE_POINT){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePoints, newPoints));
        }else if (activityType==CREATE_PATH){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePaths,newPaths));
        }else if (activityType==EDIT_MAP){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateVirtualWall));
        }
    }

}
