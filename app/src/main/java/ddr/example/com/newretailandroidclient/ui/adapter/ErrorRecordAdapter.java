package ddr.example.com.newretailandroidclient.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.ErrorRecord;

public class ErrorRecordAdapter extends BaseAdapter<ErrorRecord> {
    private TextView tv_id;
    private TextView tv_time;
    private TextView tv_type;
    public ErrorRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    public ErrorRecordAdapter(int layoutResId, @Nullable List<ErrorRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<ErrorRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull ErrorRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull ErrorRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ErrorRecord item) {
        super.convert(helper, item);
        tv_id=helper.getView(R.id.tv_error_id);
        tv_time=helper.getView(R.id.tv_error_time);
        tv_type=helper.getView(R.id.tv_error_type);
        helper.setText(R.id.tv_error_id,item.getId())
                .setText(R.id.tv_error_time,item.getTime())
                .setText(R.id.tv_error_type,item.getError_type());
    }

    @Nullable
    @Override
    public ErrorRecord getItem(int position) {
        return super.getItem(position);
    }
}
