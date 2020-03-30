package ddr.example.com.newretailandroidclient.ui.adapter;


import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.info.DevicesInfo;

public class DevicesAdapter extends BaseAdapter<DevicesInfo.Device> {
    public DevicesAdapter(int layoutResId) {
        super(layoutResId);
    }

    public DevicesAdapter(int layoutResId, @Nullable List<DevicesInfo.Device> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<DevicesInfo.Device> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DevicesInfo.Device item) {
        super.convert(helper, item);
        helper.setText(R.id.tv_device_name,item.getName());
        if (item.isUsing()){
            helper.setBackgroundRes(R.id.device_layout,R.drawable.item_device_bg_blue)
                    .setText(R.id.tv_connect,"已连接");
        }else {
            helper.setBackgroundRes(R.id.device_layout,R.drawable.item_device_bg_blue)
                    .setText(R.id.tv_connect,"已连接");
        }
    }
}
