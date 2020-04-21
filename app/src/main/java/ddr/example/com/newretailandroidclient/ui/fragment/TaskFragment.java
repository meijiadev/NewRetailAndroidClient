package ddr.example.com.newretailandroidclient.ui.fragment;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.MapFileStatus;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.newretailandroidclient.entity.point.TaskMode;
import ddr.example.com.newretailandroidclient.helper.ListTool;
import ddr.example.com.newretailandroidclient.other.DpOrPxUtils;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpClient;
import ddr.example.com.newretailandroidclient.ui.activity.HomeActivity;
import ddr.example.com.newretailandroidclient.ui.adapter.TaskAdapter;
import ddr.example.com.newretailandroidclient.ui.dialog.InputDialog;
import ddr.example.com.newretailandroidclient.widget.edit.DDREditText;
import ddr.example.com.newretailandroidclient.widget.textview.GridImageView;
import ddr.example.com.newretailandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.newretailandroidclient.widget.view.PickValueView;

/**
 * time：2019/10/28
 * desc：任务管理界面
 * author: ----
 */
public class TaskFragment extends DDRLazyFragment<HomeActivity> implements PickValueView.onSelectedChangeListener {

    @BindView(R.id.recycle_task_list)
    RecyclerView recycle_task_list;
    @BindView(R.id.tv_task_save)
    TextView tv_task_save;

    private CustomPopuWindow customPopuWindow;
    private PickValueView pickValueViewNum;
    private  TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;
    private TaskAdapter taskAdapter;
    private List<TaskMode> taskModeList =new ArrayList<>();


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
//                Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
                taskModeList=mapFileStatus.getcTaskModes();
                taskAdapter.setNewData(taskModeList);
                break;
        }
    }

    public static TaskFragment newInstance(){
        return new TaskFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initView() {
        taskAdapter=new TaskAdapter(R.layout.item_recycle_tasklist);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        recycle_task_list.setLayoutManager(layoutManager);
        recycle_task_list.setAdapter(taskAdapter);



    }
    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        try {
            taskModeList=ListTool.deepCopy(mapFileStatus.getcTaskModes());
            taskAdapter.setNewData(taskModeList);
//            submissionTask();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        onItemClick(1);
    }
    TextView tv_task_time;
    TextView tv_task_pause;
    GridImageView gridImageView;
    DDREditText task_num_check;
    private int mPosition;
    public void onItemClick(int type){
        switch (type){
            case 1:
                Logger.e("task列表"+taskModeList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskAdapter.setOnItemChildClickListener((adapter, view, position) ->  {
                    mPosition=position;
                    Logger.e("task列表对应"+taskModeList.get(position).getName());
                            switch (view.getId()){
                                case R.id.tv_task_time:
                                    Logger.e("点击----");
                                    tv_task_time= (TextView) view;
                                    showTimePopupWindow(tv_task_time,1);
                                    break;
                                case R.id.iv_check:
                                    gridImageView= (GridImageView) view;
                                    Logger.e("gggg"+gridImageView.getSelected());
                                    if (!gridImageView.getSelected()){
                                        Logger.e("未在列表中");
                                        gridImageView.setSelected(true);
                                        gridImageView.setBackgroundResource(R.mipmap.intask_check);
                                        taskModeList.get(position).setType(2);
                                        taskModeList.get(position).setTaskState(1);
                                        toast("加入定时队列，记得点保存哦");
                                    }else {
                                        Logger.e("在列表中");
                                        toast("退出定时队列，记得点保存哦");
                                        gridImageView.setSelected(false);
                                        gridImageView.setBackgroundResource(R.mipmap.intask_def);
                                        taskModeList.get(position).setType(0);
                                        taskModeList.get(position).setTaskState(3);
                                    }
                                    break;
                                case R.id.tv_task_pause:
                                    tv_task_pause= (TextView) view;
                                    if (tv_task_pause.getText().equals("暂停")) {
                                        pauseOrResume("Pause");
                                        tv_task_pause.setText("开始");
                                    }else {
                                        pauseOrResume("Resume");
                                        tv_task_pause.setText("暂停");
                                    }
                                    break;
                                case R.id.tv_task_stop:
                                    if (taskModeList.get(position).getTaskState()==2){
                                        toast("终止当前任务");
                                        exitModel();
                                        taskModeList.get(position).setTaskState(3);
                                        tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
                                    }else if (taskModeList.get(position).getTaskState()==1){
                                        toast("终止未开始的任务");
                                        taskModeList.get(position).setTaskState(3);
                                        tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
                                    }else if(taskModeList.get(position).getType()==0){
                                        toast("不在定时任务中");
                                    }
                                    break;
                                case R.id.task_num_check:
                                    task_num_check=(DDREditText) view;
                                    Logger.e("输入数字"+((int) task_num_check.getFloatText())+"原来数字"+ taskModeList.get(position).getRunCounts());
                                    int count=(int) task_num_check.getFloatText();
                                    if (count>999 || count<=0){
                                        toast("输入次数范围有误");
                                    }else {
                                        taskModeList.get(position).setRunCounts(count);
                                    }
                                    break;
                            }

                });
                break;
        }
        taskAdapter.setNewData(taskModeList);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.tv_task_save})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_task_save:
                for (int i=0;i<taskModeList.size();i++){
                    Logger.e("队列"+taskModeList.get(i).getType());
                }
                new InputDialog.Builder(getAttachActivity()).setTitle("提交任务")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                submissionTask();
                                toast("保存成功");
                        }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消提交任务");
                            }
                        }).show();
                break;

        }
    }

    /**
     * 时间弹窗
     * @param view
     */
    private void showTimePopupWindow(View view,int type) {
        Integer value[] = new Integer[24];
        for (int i = 0; i < value.length; i++) {
            value[i] = i + 1;
        }
        Integer middle[] = new Integer[60];
        for (int i = 0; i < middle.length; i++) {
            middle[i] = i ;
        }
        Integer right[] = new Integer[60];
        for (int i = 0; i < right.length; i++) {
            right[i] = i;
        }
        Integer three[] = new Integer[24];
        for (int i = 0; i < three.length; i++) {
            three[i] = i;
        }
        View contentView = null;
        switch (type){
            case 1:
                contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.dialog_num_check, null);
                customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                        .setView(contentView)
                        .create()
                        .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
                pickValueViewNum =contentView.findViewById(R.id.pickValueNum);
                pickValueViewNum.setOnSelectedChangeListener(this);
                pickValueViewNum.setValueData(three, (int)taskModeList.get(mPosition).getStartHour(), middle, (int)taskModeList.get(mPosition).getStartMin(),
                        right, (int)taskModeList.get(mPosition).getEndMin(),three,(int)taskModeList.get(mPosition).getEndHour());
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
     * 退出当前模式
     */
    private void exitModel() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eModuleServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);
    }

    /**
     * 上传任务列表
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submissionTask(){
        taskModeList.sort(Comparator.comparing(TaskMode::getType).reversed().thenComparing(TaskMode::getEndHour).thenComparing(TaskMode::getEndMin));
        taskAdapter.setNewData(taskModeList);
        int j=0;
        boolean isCheckTime;
        if (taskModeList.size()>1){
            for (int i=0;i<taskModeList.size();i++){
                Logger.e("列表排序后"+taskModeList.get(i).getName());
                if (taskModeList.get(i).getType()==2){
                    j++;
                }
            }
            Logger.e("选中列数"+j);
            if (j>1){
                for (int i=0;i<j-1;i++){
                    if (taskModeList.get(i+1).getStartHour()>taskModeList.get(i).getEndHour()){
                        isCheckTime=true;
                    }else {
                        Logger.e("前"+taskModeList.get(i).getEndHour()+"后"+taskModeList.get(i+1).getStartHour());
                        isCheckTime=false;
                        toast("定时列表第"+(i+2)+"行时间设置有误");
                    }
                    if (isCheckTime){
                        tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
                    }

                }
            }else {
                tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
            }

        }else if (taskModeList.size()==1){
            tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
        }else {
            toast("暂无定时任务");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        taskModeList=mapFileStatus.getcTaskModes();
        taskAdapter.setNewData(taskModeList);
        submissionTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e("------onPause");
    }

    @Override
    public void onSelected(PickValueView view, Object leftValue, Object middleValue, Object rightValue, Object threeValue) {
       if (view == pickValueViewNum) {
            int starth = (int) leftValue;
            int startm = (int) middleValue;
            int endh = (int) threeValue;
            int endm = (int) rightValue;
            TaskMode taskMode1=taskModeList.get(mPosition);
                taskMode1.setStartHour(starth);
                taskMode1.setStartMin(startm);
            if (endh==starth && endm > startm){
                taskMode1.setEndHour(endh);
                taskMode1.setEndMin(endm);
            }else if (endh > starth){
                taskMode1.setEndHour(endh);
                taskMode1.setEndMin(endm);
            }else {
                toast("结束时间必须大于开始时间");
            }
            taskAdapter.setData(mPosition,taskMode1);
        } else {
            String selectedStr = (String) leftValue;
        }

    }

}
