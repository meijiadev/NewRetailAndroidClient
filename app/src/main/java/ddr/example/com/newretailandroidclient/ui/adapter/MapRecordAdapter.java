package ddr.example.com.newretailandroidclient.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.MapRecord;

public class MapRecordAdapter extends BaseAdapter<MapRecord> {
    private TextView tv_id;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private TextView tv_run_time;
    private TextView tv_retail_map;
    private TextView tv_retail_num;
    private TextView tv_handle;
    public MapRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    public MapRecordAdapter(int layoutResId, @Nullable List<MapRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<MapRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull MapRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull MapRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MapRecord item) {
        super.convert(helper, item);
        tv_id=helper.getView(R.id.tv_map_id);
        tv_start_time=helper.getView(R.id.tv_map_start_time);
        tv_end_time=helper.getView(R.id.tv_map_end_time);
        tv_run_time=helper.getView(R.id.tv_map_run_time);
        tv_retail_map=helper.getView(R.id.tv_map_retail);
        tv_retail_num=helper.getView(R.id.tv_map_retail_num);
        tv_handle=helper.getView(R.id.tv_map_handle);
        helper.setText(R.id.tv_map_id,item.getId())
                .setText(R.id.tv_map_start_time,item.getStart_time())
                .setText(R.id.tv_map_end_time,item.getEnd_time())
                .setText(R.id.tv_map_run_time,item.getRun_time())
                .setText(R.id.tv_map_retail,item.getRetail_map())
                .setText(R.id.tv_map_retail_num,item.getRetail_num())
                .setText(R.id.tv_map_handle,"查看坐标")
                .addOnClickListener(R.id.tv_map_handle);
    }

    @Nullable
    @Override
    public MapRecord getItem(int position) {
        return super.getItem(position);
    }
}
