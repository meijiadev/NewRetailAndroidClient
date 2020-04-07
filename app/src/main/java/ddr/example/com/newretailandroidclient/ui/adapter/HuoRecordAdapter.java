package ddr.example.com.newretailandroidclient.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;

public class HuoRecordAdapter extends BaseAdapter<HuoRecord>{
    private TextView tv_huo_num;
    private ImageView iv_huo_image;
    private TextView tv_huo_name;
    private OnItemClickListener mOnItemClickListener;

    public HuoRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    public HuoRecordAdapter(int layoutResId, @Nullable List<HuoRecord> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<HuoRecord> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull HuoRecord data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull HuoRecord data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HuoRecord item) {
        super.convert(helper, item);
        tv_huo_num=helper.getView(R.id.tv_huo_num);
        iv_huo_image=helper.getView(R.id.iv_huo_image);
        tv_huo_name=helper.getView(R.id.tv_huo_name);
        helper.setText(R.id.tv_huo_num,item.getHuoNum())
                .setText(R.id.tv_huo_name,item.getHuoName());
    }
    public interface OnItemClickListener{ 
        void onClick(int position);
        void onLongClick(int position);
}
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }

    @Nullable
    @Override
    public HuoRecord getItem(int position) {
        return super.getItem(position);
    }
    
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }
}
