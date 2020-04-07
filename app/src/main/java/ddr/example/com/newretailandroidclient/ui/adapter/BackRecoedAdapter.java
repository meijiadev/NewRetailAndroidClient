package ddr.example.com.newretailandroidclient.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.BackRecord;

public class BackRecoedAdapter extends BaseAdapter<BackRecord> {
    private TextView tv_id;
    private TextView tv_time;
    private TextView tv_pay_f;
    private TextView tv_back_f;
    private TextView tv_pay_num;
    private TextView tv_back_result;

    public BackRecoedAdapter(int layoutResId) {
        super(layoutResId);
    }

    public BackRecoedAdapter(int layoutResId, @Nullable List<BackRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<BackRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull BackRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull BackRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BackRecord item) {
        super.convert(helper, item);
        tv_id=helper.getView(R.id.tv_back_id);
        tv_time=helper.getView(R.id.tv_back_time);
        tv_pay_f=helper.getView(R.id.tv_back_pay_f);
        tv_back_f=helper.getView(R.id.tv_back_back_f);
        tv_pay_num=helper.getView(R.id.tv_back_pay_num);
        tv_back_result=helper.getView(R.id.tv_back_result);
        helper.setText(R.id.tv_back_id,item.getId())
                .setText(R.id.tv_back_time,item.getTime())
                .setText(R.id.tv_back_pay_f,item.getPay_f())
                .setText(R.id.tv_back_back_f,item.getBack_f())
                .setText(R.id.tv_back_pay_num,item.getPay_num())
                .setText(R.id.tv_back_result,item.getBack_result());
    }

    @Nullable
    @Override
    public BackRecord getItem(int position) {
        return super.getItem(position);
    }
}
