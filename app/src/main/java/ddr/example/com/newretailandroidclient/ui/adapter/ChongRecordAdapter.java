package ddr.example.com.newretailandroidclient.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.ChongRecord;

public class ChongRecordAdapter extends BaseAdapter<ChongRecord> {
    private TextView tv_id;
    private TextView tv_cd_order;
    private TextView tv_dj_result;
    private TextView tv_cd_start_time;
    private TextView tv_cd_start_num;
    private TextView tv_cd_end_time;
    private TextView tv_cd_end_num;
    private TextView tv_cd_h_time;

    public ChongRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    public ChongRecordAdapter(int layoutResId, @Nullable List<ChongRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<ChongRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull ChongRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull ChongRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ChongRecord item) {
        super.convert(helper, item);
        tv_id=helper.getView(R.id.tv_chong_id);
        tv_cd_order=helper.getView(R.id.tv_chong_time);
        tv_dj_result=helper.getView(R.id.tv_chong_result);
        tv_cd_start_time=helper.getView(R.id.tv_chong_start_time);
        tv_cd_start_num=helper.getView(R.id.tv_chong_start_num);
        tv_cd_end_time=helper.getView(R.id.tv_chong_end_time);
        tv_cd_end_num=helper.getView(R.id.tv_chong_end_num);
        tv_cd_h_time=helper.getView(R.id.tv_chong_h_time);
        helper.setText(R.id.tv_chong_id,item.getId())
                .setText(R.id.tv_chong_time,item.getCd_order())
                .setText(R.id.tv_chong_result,item.getDj_result())
                .setText(R.id.tv_chong_start_time,item.getStart_cd_time())
                .setText(R.id.tv_chong_start_num,item.getStart_cd_num())
                .setText(R.id.tv_chong_end_time,item.getEnd_cd_time())
                .setText(R.id.tv_chong_end_num,item.getEns_cd_num())
                .setText(R.id.tv_chong_h_time,item.getCd_h_time());

    }

    @Nullable
    @Override
    public ChongRecord getItem(int position) {
        return super.getItem(position);
    }
}
