package ddr.example.com.newretailandroidclient.protocobuf.dispatcher;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import java.util.HashMap;
import java.util.Map;

import DDRCommProto.BaseCmd;
import ddr.example.com.newretailandroidclient.protocobuf.GuIdInfo;
import ddr.example.com.newretailandroidclient.protocobuf.processor.BaseProcessor;

/**
 * 事件分发基类
 */
public class BaseMessageDispatcher {
    protected Map<String,BaseProcessor> m_ProcessorMap=new HashMap<>();

    public void dispatcher(Context context, BaseCmd.CommonHeader commonHeader, String typeName, GeneratedMessageLite msg){
        //Logger.w("事件分发基类"+typeName);
        if (m_ProcessorMap.containsKey(typeName)){
          //  Logger.w("事件分发基类"+typeName);
            m_ProcessorMap.get(typeName).process(context,commonHeader,msg);
            GuIdInfo.getInstance().setGuId(commonHeader.getGuid());
            GuIdInfo.getInstance().setMessageLite(msg);

        }

    }
}
