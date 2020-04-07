package ddr.example.com.newretailandroidclient.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.PageNum;

public class PageAdapter extends BaseAdapter<PageNum> {

    private TextView tv_page;

    public PageAdapter(int layoutResId) {
        super(layoutResId);
    }

    public PageAdapter(int layoutResId, @Nullable List<PageNum> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<PageNum> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull PageNum data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull PageNum data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PageNum item) {
        super.convert(helper, item);
        tv_page=helper.getView(R.id.tv_page_num);
        helper.setText(R.id.tv_page_num,String.valueOf(item.getPagenum()))
        .addOnClickListener(R.id.tv_page_num);
        if (item.isSelected()){
            tv_page.setBackgroundColor(Color.parseColor("#0399ff"));
        }else {
            tv_page.setBackgroundColor(Color.parseColor("#161718"));
        }
    }

    @Nullable
    @Override
    public PageNum getItem(int position) {
        return super.getItem(position);
    }
}
