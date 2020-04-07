package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.other.HuoRecord;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.ui.adapter.ChongRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.HuoRecordAdapter;

public class SellHuoRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_sell_huo)
    RecyclerView recycle_sell_huo;

    private HuoRecord huoRecord;
    private HuoRecordAdapter huoRecordAdapter;
    private List<HuoRecord> huoRecordList;

    public static SellHuoRecord newInstance(){
        return new SellHuoRecord();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_huo;
    }

    @Override
    protected void initView() {
        huoRecordAdapter = new HuoRecordAdapter(R.layout.item_sell_huo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getAttachActivity(), 4, LinearLayoutManager.VERTICAL, false);
        recycle_sell_huo.setLayoutManager(gridLayoutManager);
        recycle_sell_huo.setAdapter(huoRecordAdapter);

    }

    @Override
    protected void initData() {
        getData();
        onItemClick();

    }
    /**
     * 获取数据
     */
    private void getData(){
        huoRecordList=new ArrayList<>();
        for (int i=0;i<15;i++){
            huoRecord=new HuoRecord();
            huoRecord.setHuoNum("货道"+i+"("+i+"/6)");
            huoRecord.setHuoName("可口可乐");
            huoRecordList.add(huoRecord);
        }
        huoRecordAdapter.setNewData(huoRecordList);
    }
    /**
     * 列表点击事件
     */
    private void onItemClick(){
       huoRecordAdapter.setOnItemClickListener(new HuoRecordAdapter.OnItemClickListener() {
           @Override
           public void onClick(int position) {
               Logger.e("点击"+position);
               toast("点击"+position);
           }

           @Override
           public void onLongClick(int position) {

           }
       });
    }
}
