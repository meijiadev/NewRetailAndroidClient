package ddr.example.com.newretailandroidclient.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseAdapter;
import ddr.example.com.newretailandroidclient.entity.point.PathLine;

/**
 *  time : 2019/11/12
 *  desc : 路径列表适配器
 */
public class PathAdapter extends BaseAdapter<PathLine> {
    public PathAdapter(int layoutResId) {
        super(layoutResId);
    }

    public PathAdapter(int layoutResId, @Nullable List<PathLine> data) {
        super(layoutResId, data);
    }


    @Override
    public void setNewData(@Nullable List<PathLine> data) {
        super.setNewData(data);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, PathLine item) {
        super.convert(helper, item);
        switch (viewType){
            case R.layout.item_target_point:
                if (item.isSelected()){
                    helper.setText(R.id.tv_target_name,item.getName()).setTextColor(R.id.tv_target_name,Color.parseColor("#0399ff"));
                }else {
                    helper.setText(R.id.tv_target_name,item.getName())
                            .setTextColor(R.id.tv_target_name,Color.parseColor("#ffffff"));
                }
                break;
            case R.layout.item_task_select:
                helper.setText(R.id.tv_name,item.getName());
                ImageView iv_select=helper.getView(R.id.iv_select);
                if (item.isInTask()){
                    iv_select.setImageResource(R.mipmap.checkedwg);
                }else {
                    iv_select.setImageResource(R.mipmap.nocheckedwg);
                }
                break;
            case R.layout.item_show_recycler:
                if (item.isMultiple()){
                    helper.setText(R.id.tv_show_name,item.getName())
                            .setTextColor(R.id.tv_show_name,Color.parseColor("#FFFFFFFF"))
                            .setImageResource(R.id.iv_select,R.mipmap.item_show);
                }else {
                    helper.setText(R.id.tv_show_name,item.getName())
                            .setTextColor(R.id.tv_show_name,Color.parseColor("#66ffffff"))
                            .setImageResource(R.id.iv_select,R.mipmap.item_hide);
                }
                break;
        }

    }

    @Override
    public void setData(int index, @NonNull PathLine data) {
        super.setData(index, data);
    }
}
