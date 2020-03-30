package ddr.example.com.newretailandroidclient.ui.adapter;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.ComputerEdition;
import ddr.example.com.newretailandroidclient.other.Logger;

public class VersionAdapter extends BaseAdapter<ComputerEdition> {
    public VersionAdapter(int layoutResId) {
        super(layoutResId);
    }

    public VersionAdapter(int layoutResId, @Nullable List<ComputerEdition> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<ComputerEdition> data) {
        super.setNewData(data);
        for (int i=0;i<data.size();i++){
            Logger.e("------"+data.get(i).getType());
        }
    }

    @Override
    public void addData(int position, @NonNull ComputerEdition data) {
        super.addData(position, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ComputerEdition item) {
        super.convert(helper, item);
        String type = null;
        switch (item.getType()){
            case 0:
                type="上位机";
                break;
            case 1:
                type="雷达模块";
                break;
            case 2:
                type="视觉模块";
                break;
            case 3:
                type="路径规划";
                break;
            case 4:
                type="设备管理";
                break;
            case 5:
                type="嵌入式";
                break;
        }
        helper.setText(R.id.tv_type,type)
                .setText(R.id.tv_version,String.valueOf(item.getVersion()))
                .setText(R.id.tv_data,String.valueOf(item.getData()));
    }



    @Nullable
    @Override
    public ComputerEdition getItem(int position) {
        Logger.e("------:");
        return super.getItem(position);
    }


}
