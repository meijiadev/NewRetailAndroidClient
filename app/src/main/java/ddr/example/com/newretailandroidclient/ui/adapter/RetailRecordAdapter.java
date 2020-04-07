package ddr.example.com.newretailandroidclient.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.RetailRecord;

public class RetailRecordAdapter extends BaseAdapter<RetailRecord> {
    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_settment;
    private TextView tv_number;
    private TextView tv_price;
    private TextView tv_b_num;
    private TextView tv_total;
    public RetailRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    public RetailRecordAdapter(int layoutResId, @Nullable List<RetailRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<RetailRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull RetailRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull RetailRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RetailRecord item) {
        super.convert(helper, item);
        tv_name=helper.getView(R.id.tv_commodity_name_item);
        tv_id=helper.getView(R.id.tv_id_item);
        tv_settment=helper.getView(R.id.tv_Settlement_item);
        tv_b_num=helper.getView(R.id.tv_b_number_item);
        tv_price=helper.getView(R.id.tv_Price_item);
        tv_number=helper.getView(R.id.tv_number_item);
        tv_total=helper.getView(R.id.tv_Total_item);
        helper.setText(R.id.tv_number_item,item.getNumber())
                .setText(R.id.tv_id_item,item.getId())
                .setText(R.id.tv_commodity_name_item,item.getName())
                .setText(R.id.tv_Settlement_item,item.getSettlement())
                .setText(R.id.tv_b_number_item,item.getB_num())
                .setText(R.id.tv_Price_item,item.getPrice())
                .setText(R.id.tv_Total_item,item.getTotal());

    }

    @Nullable
    @Override
    public RetailRecord getItem(int position) {
        return super.getItem(position);
    }
}
