package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.graphics.Color;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.newretailandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.newretailandroidclient.entity.other.Parameter;
import ddr.example.com.newretailandroidclient.entity.other.Parameters;
import ddr.example.com.newretailandroidclient.other.InputFilterMinMax;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.other.SlideButton;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpClient;

/**
 * time: 2020/03/24
 * desc: 高级设置自动充电界面
 */
public class AutoChargingSet extends DDRLazyFragment implements SlideButton.SlideButtonOnCheckedListener{
    @BindView(R.id.slideButton)
    SlideButton slideButton;
    @BindView(R.id.ed_trigger_auto)
    EditText ed_trigger_auto;
    @BindView(R.id.ed_out_auto)
    EditText ed_out_auto;
    @BindView(R.id.tv_save_auto_char)
    TextView tv_save_auto_char;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private Parameter parameter;
    private Parameters parameters;
    private List<Parameter> parameterList=new ArrayList<>();
    private String triggerAutoKey="MR_Params.RECHARGING_BATT_LO_PER";//自动充电下限
    private String outAutoKey="MR_Params.RECHARGING_BATT_HI_PER";//自动充电上限
    private String switchAutoKey="Common_Params.AUTO_ENTER_RECHARGING"; //自动充电开关
    private String autoValue="1";

    public static AutoChargingSet newInstance(){
        return new AutoChargingSet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataParameter:
                setNaparmeter();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_autocharg;
    }

    @Override
    protected void initView() {
        slideButton.setSmallCircleModel(
                Color.parseColor("#00FFFFFF"), Color.parseColor("#999999"),Color.parseColor("#49c265"),
                Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        ed_out_auto.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});
        ed_trigger_auto.setFilters(new InputFilter[]{new InputFilterMinMax("1","100")});

    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        parameters=Parameters.getInstance();
        getNaparmeter(1);
        getChosseStatus();
    }

    @OnClick({R.id.slideButton,R.id.tv_save_auto_char})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.slideButton:
                Logger.e("点击切换-----");
                getChosseStatus();
                break;
            case R.id.tv_save_auto_char:
                int tr_auto = (int)(Float.parseFloat(ed_trigger_auto.getText().toString())*100);
                int out_auto=(int)(Float.parseFloat(ed_out_auto.getText().toString())*100);
                postNaparmeter(ByteString.copyFromUtf8(switchAutoKey),ByteString.copyFromUtf8(autoValue),2,3);
                postNaparmeter(ByteString.copyFromUtf8(triggerAutoKey),ByteString.copyFromUtf8(String.valueOf(tr_auto)),2,3);
                postNaparmeter(ByteString.copyFromUtf8(outAutoKey),ByteString.copyFromUtf8(String.valueOf(out_auto)),2,3);
                getNaparmeter(1);
                toast("保存成功");
                break;
        }
    }

    //获取导航参数
    private void getNaparmeter(int type){
        Logger.e("-------------获取");
        slideButton.setOnCheckedListener(isChecked -> getChosseStatus());
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        switch (type){
            case 0:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeError;//全部
                break;
            case 1:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
                break;
            case 2:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeResumeData;//恢复数据
                break;
            case 3:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeSetData;//设置数据
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqConfigOperational);
    }
    //设置导航参数
    private void setNaparmeter(){
        parameterList=parameters.getParameterList();
        for (int i=0;i<parameterList.size();i++){
            if(parameterList.get(i).getKey().contains(triggerAutoKey)){
                int trigger_auto=Integer.parseInt(parameterList.get(i).getValue())/100;
                ed_trigger_auto.setText(String.valueOf(trigger_auto));
            }
            if(parameterList.get(i).getKey().contains(outAutoKey)){
                int out_auto=Integer.parseInt(parameterList.get(i).getValue())/100;
                Logger.e("电量值"+out_auto);
                ed_out_auto.setText(String.valueOf(out_auto));
            }
            if(parameterList.get(i).getKey().contains(switchAutoKey)){
                Logger.e("充电值"+parameterList.get(i).getValue());
                if (parameterList.get(i).getValue().equals("1")){
                    Logger.e("开启充电-----");
                    slideButton.setChecked(true);
                }else {
                    Logger.e("关闭充电-----");
                    slideButton.setChecked(false);
                }
            }
            getChosseStatus();

        }
    }

    //发送导航参数
    private void postNaparmeter(ByteString key, ByteString value, int type, int optType){
        BaseCmd.eConfigItemType eConfigItemType;
        BaseCmd.eConfigItemOptType eConfigItemOptType;
        switch (type){
            case 0:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeError;
                break;
            case 1:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeCore;
                break;
            case 2:
                eConfigItemType=BaseCmd.eConfigItemType.eConfigTypeLogic;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        switch (optType){
            case 0:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeError;//全部
                break;
            case 1:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
                break;
            case 2:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeResumeData;//恢复数据
                break;
            case 3:
                eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeSetData;//设置数据
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + optType);
        }
        BaseCmd.configItem configItem=BaseCmd.configItem.newBuilder()
                .setKey(key)
                .setValue(value)
                .build();
        BaseCmd.configData configData=BaseCmd.configData.newBuilder()
                .setType(eConfigItemType)
                .setData(configItem)
                .build();
        List<BaseCmd.configData> configDataList=new ArrayList<>();
        configDataList.add(configData);
        BaseCmd.reqConfigOperational reqConfigOperational=BaseCmd.reqConfigOperational.newBuilder()
                .setType(eConfigItemOptType)
                .addAllData(configDataList)
                .build();
        tcpClient.sendData(null,reqConfigOperational);

    }

    //获取选择的状态
    private void getChosseStatus(){
        boolean isChecked=slideButton.isChecked;
        if (isChecked==true){
            Logger.e("点击勾中---");
            autoValue="1";
            ed_trigger_auto.setCursorVisible(true);
            ed_out_auto.setCursorVisible(true);
            ed_out_auto.setFocusable(true);
            ed_trigger_auto.setFocusable(true);
            ed_trigger_auto.setFocusableInTouchMode(true);
            ed_out_auto.setFocusableInTouchMode(true);
            ed_out_auto.requestFocus();
            ed_trigger_auto.requestFocus();
        }else {
            Logger.e("点击未选择");
            autoValue="0";
            ed_trigger_auto.setCursorVisible(false);
            ed_out_auto.setCursorVisible(false);
            ed_out_auto.setFocusable(false);
            ed_trigger_auto.setFocusable(false);
            ed_trigger_auto.setFocusableInTouchMode(false);
            ed_out_auto.setFocusableInTouchMode(false);
        }
        Logger.e("是否选择"+isChecked+"-----"+autoValue);
    }


    @Override
    public void onCheckedChangeListener(boolean isChecked) {
        Logger.e("状态"+isChecked);
    }
}
