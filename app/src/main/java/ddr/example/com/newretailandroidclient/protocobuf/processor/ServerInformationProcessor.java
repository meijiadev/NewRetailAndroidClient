package ddr.example.com.newretailandroidclient.protocobuf.processor;

import android.content.Context;
import com.google.protobuf.GeneratedMessageLite;
import org.greenrobot.eventbus.EventBus;
import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.entity.other.UdpIp;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.ui.fragment.StatusFragment;


/**
 * 当解析出IP和端口就关闭Udp连接并连接tcp
 */
public class ServerInformationProcessor extends BaseProcessor {
    private String robotId;
    private UdpIp udpIp;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        Logger.w("udp广播处理程序");
        BaseCmd.bcLSAddr bcLSAddr=(BaseCmd.bcLSAddr) msg;
        for (BaseCmd.bcLSAddr.ServerInfo serverInfo:bcLSAddr.getLSInfosList()) {
            for (String ip:serverInfo.getIpsList()){
                udpIp=new UdpIp();
                udpIp.setIp(ip);
                udpIp.setPort(serverInfo.getPort());
                switch (serverInfo.getStypeValue()){
                    case 0:
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePort,udpIp));
                        break;
                    case 4:
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateAiPort,udpIp));
                        break;
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateIPList,ip));
//                Logger.e("ip"+ip+"状态"+serverInfo.getStypeValue()+"端口"+serverInfo.getPort()+"----"+serverInfo.getIpsList().size()+"--"+bcLSAddr.getLSInfosList().size());
            }
            robotId = serverInfo.getRobotid();
            StatusFragment.setRobotID(robotId);
//            Logger.e("端口"+serverInfo.getPort()+"----"+serverInfo.getIpsList().size()+"--"+bcLSAddr.getLSInfosList().size());
            //TaskPerformActivity.setRobotID(robotId,context);
           // DatabaseHelper.setRobotID(robotId,context);
//            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePort,serverInfo.getPort()));

        }
    }
}
