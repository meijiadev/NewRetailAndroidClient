package ddr.example.com.newretailandroidclient.protocobuf.dispatcher;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import DDRCommProto.RemoteCmd;
import DDRModuleProto.DDRModuleCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.newretailandroidclient.other.Logger;

import ddr.example.com.newretailandroidclient.protocobuf.processor.NotifyBaseStatusExProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.NotifyEnvInfoProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.NotifyHardStateProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.NotifyLidarPtsProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.NotifyMapGenStatProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.NotifyRelocaStatusProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspAllSellsRecordProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspClientGetMapInfoProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspCmdMoveProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspCmdRelocProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspCmdSetWorkPathProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspDDRVLNMapExProcess;

import ddr.example.com.newretailandroidclient.protocobuf.processor.RspCmdStartActionModelProcessor;

import ddr.example.com.newretailandroidclient.protocobuf.processor.RspDetecLoopProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetChargingRecordsProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetDDRVLNMapExProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetErrorRecordsProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetMapRecordsProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetParameterProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetSensorProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetSysVersionProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGetTaskOperational;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspGoodsDetailProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspHeartBeatProcess;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspLoginProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspMapOperationalProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspObstacleInfoProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspRemoteLoginProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspRemoteServerListProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspRunControlExProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspRunSpecificPoint;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspSelectLSProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.RspStockInfoHuoProcessor;
import ddr.example.com.newretailandroidclient.protocobuf.processor.ServerInformationProcessor;


public class ClientMessageDispatcher extends BaseMessageDispatcher {
    static ClientMessageDispatcher clientMessageDispatcher;

    public static ClientMessageDispatcher getInstance(){
        if (clientMessageDispatcher==null){
            clientMessageDispatcher=new ClientMessageDispatcher();
        }
        return clientMessageDispatcher;
    }

    public ClientMessageDispatcher(){
        BaseCmd.HeartBeat heartBeat=BaseCmd.HeartBeat.newBuilder().build();
        m_ProcessorMap.put(heartBeat.getClass().toString(),new RspHeartBeatProcess());

        BaseCmd.rspLogin rspLogin=BaseCmd.rspLogin.newBuilder().build();
        Logger.e("------"+rspLogin.getClass().toString());
        m_ProcessorMap.put(rspLogin.getClass().toString(),new RspLoginProcessor());

        BaseCmd.bcLSAddr bcLSAddr=BaseCmd.bcLSAddr.newBuilder().build();
        m_ProcessorMap.put(bcLSAddr.getClass().toString(),new ServerInformationProcessor());

        RemoteCmd.rspRemoteServerList rspRemoteServerList=RemoteCmd.rspRemoteServerList.newBuilder().build();
        m_ProcessorMap.put(rspRemoteServerList.getClass().toString(),new RspRemoteServerListProcessor());

        RemoteCmd.rspRemoteLogin rspRemoteLogin=RemoteCmd.rspRemoteLogin.newBuilder().build();
        m_ProcessorMap.put(rspRemoteLogin.getClass().toString(),new RspRemoteLoginProcessor());

        RemoteCmd.rspSelectLS rspSelectLS=RemoteCmd.rspSelectLS.newBuilder().build();
        m_ProcessorMap.put(rspSelectLS.getClass().toString(),new RspSelectLSProcessor());


        BaseCmd.rspCmdMove rspCmdMove=BaseCmd.rspCmdMove.newBuilder().build();
        m_ProcessorMap.put(rspCmdMove.getClass().toString(),new RspCmdMoveProcessor());
        BaseCmd.rspCmdSetWorkPath rspCmdSetWorkPath=BaseCmd.rspCmdSetWorkPath.newBuilder().build();
        m_ProcessorMap.put(rspCmdSetWorkPath.getClass().toString(),new RspCmdSetWorkPathProcessor());

        BaseCmd.notifyBaseStatusEx notifyBaseStatusEx=BaseCmd.notifyBaseStatusEx.newBuilder().build();
        m_ProcessorMap.put(notifyBaseStatusEx.getClass().toString(),new NotifyBaseStatusExProcessor());

        BaseCmd.notifyEnvInfo notifyEnvInfo=BaseCmd.notifyEnvInfo.newBuilder().build();
        m_ProcessorMap.put(notifyEnvInfo.getClass().toString(),new NotifyEnvInfoProcessor());

        DDRVLNMap.rspGetDDRVLNMapEx rspGetDDRVLNMapEx=DDRVLNMap.rspGetDDRVLNMapEx.newBuilder().build();
        m_ProcessorMap.put(rspGetDDRVLNMapEx.getClass().toString(),new RspGetDDRVLNMapExProcessor());

        DDRVLNMap.rspDDRVLNMapEx rspDDRVLNMapEx=DDRVLNMap.rspDDRVLNMapEx.newBuilder().build();
        m_ProcessorMap.put(rspDDRVLNMapEx.getClass().toString(),new RspDDRVLNMapExProcess());

        BaseCmd.rspGetSysVersion rspGetSysVersion=BaseCmd.rspGetSysVersion.newBuilder().build();
        m_ProcessorMap.put(rspGetSysVersion.getClass().toString(),new RspGetSysVersionProcessor());

        BaseCmd.rspClientGetMapInfo rspClientGetMapInfo=BaseCmd.rspClientGetMapInfo.newBuilder().build();
        m_ProcessorMap.put(rspClientGetMapInfo.getClass().toString(),new RspClientGetMapInfoProcessor());

        BaseCmd.notifyLidarPts notifyLidarPts=BaseCmd.notifyLidarPts.newBuilder().build();
        m_ProcessorMap.put(notifyLidarPts.getClass().toString(),new NotifyLidarPtsProcessor());

        BaseCmd.rspCmdStartActionMode rspCmdStartActionMode=BaseCmd.rspCmdStartActionMode.newBuilder().build();
        m_ProcessorMap.put(rspCmdStartActionMode.getClass().toString(),new RspCmdStartActionModelProcessor());

        DDRVLNMap.rspMapOperational rspMapOperational=DDRVLNMap.rspMapOperational.newBuilder().build();
        m_ProcessorMap.put(rspMapOperational.getClass().toString(),new RspMapOperationalProcessor());

        DDRVLNMap.rspRunControlEx rspRunControlEx=DDRVLNMap.rspRunControlEx.newBuilder().build();
        m_ProcessorMap.put(rspRunControlEx.getClass().toString(),new RspRunControlExProcessor());

        DDRModuleCmd.rspObstacleInfo rspObstacleInfo=DDRModuleCmd.rspObstacleInfo.newBuilder().build();
        m_ProcessorMap.put(rspObstacleInfo.getClass().toString(),new RspObstacleInfoProcessor());

        BaseCmd.rspCmdReloc rspCmdReloc=BaseCmd.rspCmdReloc.newBuilder().build();
        m_ProcessorMap.put(rspCmdReloc.getClass().toString(),new RspCmdRelocProcessor());

        BaseCmd.notifyMapGenStat mapGenStat=BaseCmd.notifyMapGenStat.newBuilder().build();
        m_ProcessorMap.put(mapGenStat.getClass().toString(),new NotifyMapGenStatProcessor());

        BaseCmd.rspDetectLoop rspDetectLoop=BaseCmd.rspDetectLoop.newBuilder().build();
        m_ProcessorMap.put(rspDetectLoop.getClass().toString(),new RspDetecLoopProcessor());


        BaseCmd.rspConfigOperational rspConfigOperational=BaseCmd.rspConfigOperational.newBuilder().build();
        m_ProcessorMap.put(rspConfigOperational.getClass().toString(),new RspGetParameterProcessor());


        BaseCmd.notifyHardwareStat notifyHardwareStat=BaseCmd.notifyHardwareStat.newBuilder().build();
        m_ProcessorMap.put(notifyHardwareStat.getClass().toString(),new NotifyHardStateProcessor());

        BaseCmd.rspSensorConfigOperational rspSensorConfigOperational=BaseCmd.rspSensorConfigOperational.newBuilder().build();
        m_ProcessorMap.put(rspSensorConfigOperational.getClass().toString(),new RspGetSensorProcessor());

        DDRVLNMap.rspTaskOperational rspTaskOperational=DDRVLNMap.rspTaskOperational.newBuilder().build();
        m_ProcessorMap.put(rspTaskOperational.getClass().toString(),new RspGetTaskOperational());

        DDRVLNMap.rspRunSpecificPoint rspRunSpecificPoint=DDRVLNMap.rspRunSpecificPoint.newBuilder().build();
        m_ProcessorMap.put(rspRunSpecificPoint.getClass().toString(),new RspRunSpecificPoint());

        BaseCmd.notifyRelocStatus relocStatus=BaseCmd.notifyRelocStatus.newBuilder().build();
        m_ProcessorMap.put(relocStatus.getClass().toString(),new NotifyRelocaStatusProcessor());

        DDRAIServiceCmd.rspAllSellsRecord rspAllSellsRecord=DDRAIServiceCmd.rspAllSellsRecord.newBuilder().build();
        m_ProcessorMap.put(rspAllSellsRecord.getClass().toString(),new RspAllSellsRecordProcessor());

        DDRAIServiceCmd.rspStockInfo rspStockInfo=DDRAIServiceCmd.rspStockInfo.newBuilder().build();
        m_ProcessorMap.put(rspStockInfo.getClass().toString(),new RspStockInfoHuoProcessor());

        DDRAIServiceCmd.rspGoodsDetail rspGoodsDetail=DDRAIServiceCmd.rspGoodsDetail.newBuilder().build();
        m_ProcessorMap.put(rspGoodsDetail.getClass().toString(),new RspGoodsDetailProcessor());

        DDRAIServiceCmd.rspGetErrorRecords rspGetErrorRecords=DDRAIServiceCmd.rspGetErrorRecords.newBuilder().build();
        m_ProcessorMap.put(rspGetErrorRecords.getClass().toString(),new RspGetErrorRecordsProcessor());

        DDRAIServiceCmd.rspGetChargingRecords rspGetChargingRecords=DDRAIServiceCmd.rspGetChargingRecords.newBuilder().build();
        m_ProcessorMap.put(rspGetChargingRecords.getClass().toString(),new RspGetChargingRecordsProcessor());

        DDRAIServiceCmd.rspGetMapRecords rspGetMapRecords=DDRAIServiceCmd.rspGetMapRecords.newBuilder().build();
        m_ProcessorMap.put(rspGetMapRecords.getClass().toString(),new RspGetMapRecordsProcessor());
    }
}
