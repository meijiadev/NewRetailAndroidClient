package ddr.example.com.newretailandroidclient.download;

import java.io.File;

/**
 * create by Ezreal mei
 * 下载结果的回调接口
 */
public interface DownLoadCallBack {
    void onDownLoadSuccess(File file, File target);
    void onDownLoadFailed();
}
