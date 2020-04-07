package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.other.BackRecord;
import ddr.example.com.newretailandroidclient.entity.other.MapRecord;
import ddr.example.com.newretailandroidclient.entity.other.PageNum;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.ui.adapter.BackRecoedAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.MapRecordAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.PageAdapter;

public class SellMapSportRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_pages_y_map)
    RecyclerView recycle_pages_y_map;
    @BindView(R.id.recycle_sell_map)
    RecyclerView recycle_sell_map;

    private PageNum pageNum;
    private PageAdapter pageAdapter;
    private List<PageNum> pageNumList;

    private MapRecord mapRecord;
    private MapRecordAdapter mapRecordAdapter;
    private List<MapRecord> mapRecordList;
    private List<List<MapRecord>> nMapRecordList=new ArrayList<>();

    private int pageNumber;//页数
    private int allPageNumber;//总数量
    private int nowPagepostion=0;//当前页

    public static SellMapSportRecord newInstance(){
        return new SellMapSportRecord();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_map;
    }

    @Override
    protected void initView() {
        mapRecordAdapter = new MapRecordAdapter(R.layout.item_map_record);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_sell_map.setLayoutManager(layoutManager);
        recycle_sell_map.setAdapter(mapRecordAdapter);

        pageAdapter=new PageAdapter(R.layout.item_page_num);
        LinearLayoutManager layoutManager1 =new LinearLayoutManager(getAttachActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_pages_y_map.setLayoutManager(layoutManager1);
        recycle_pages_y_map.setAdapter(pageAdapter);

    }

    @Override
    protected void initData() {
        getData();
        setPageData();
        onClick();
        setData(0);
        isCheckPage();
    }
    /**
     * 点击事件
     */
    @OnClick({R.id.iv_add_y,R.id.iv_trigger_y})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.iv_add_y:
                Logger.e("--"+nowPagepostion+"++"+pageNumber);
                if (nowPagepostion<pageNumber-1){
                    setData(nowPagepostion+1);
                    nowPagepostion++;
                    isCheckPage();
                }else {
                    toast("已经是最后一页了");
                }
                break;
            case R.id.iv_trigger_y:
                if (nowPagepostion>1){
                    setData(nowPagepostion-1);
                    nowPagepostion--;
                    isCheckPage();
                }else {
                    toast("已经是第一页了");
                }
                break;
        }
    }
    /**
     * 获取数据并拆分
     */
    private void getData(){
        mapRecordList=new ArrayList<>();
        for (int i=0;i<150;i++){
            mapRecord=new MapRecord();
            mapRecord.setId(""+i);
            mapRecord.setStart_time("启动"+i);
            mapRecord.setEnd_time("结束"+i);
            mapRecord.setRun_time("运行"+i);
            mapRecord.setRetail_map("地图名"+i);
            mapRecord.setRetail_num("次数"+i);
            mapRecord.setHandle("查看坐标");
            mapRecordList.add(mapRecord);
        }
        allPageNumber=mapRecordList.size();
        int ysnum=allPageNumber%9;//余数
        if(ysnum==0){
            pageNumber=allPageNumber/9;
        }else {
            pageNumber=(allPageNumber/9)+1;
        }
        Logger.e("页数"+pageNumber);
        Logger.e("余数"+ysnum);
        //偏移量
        int offset = pageNumber-1;
        for (int i = 0; i < pageNumber; i++) {
            List<MapRecord> value;
            boolean is=i==offset;
            Logger.e("是否----"+is+offset+"---"+i);
            if (i==offset && ysnum>0){
                value=mapRecordList.subList(i*8+i,(i*8+i)+ysnum);
            }else {
                value=mapRecordList.subList(i*8+i,(i+1)*8+i+1);
            }
            nMapRecordList.add(value);
        }
        Logger.e("数据------"+nMapRecordList.get(1).get(1).getId()+"大小"+nMapRecordList.size());
    }

    /**
     * 插入数据
     */
    private void setData(int page){
        mapRecordAdapter.setNewData(nMapRecordList.get(page));
    }
    /**
     * 插入页数
     */
    private void setPageData(){
        pageNumList=new ArrayList<>();
        Logger.e("数据"+pageNumber);
        for (int i=0;i<pageNumber;i++){
            pageNum=new PageNum();
            pageNum.setPagenum(i+1);
            pageNumList.add(pageNum);
        }
//        pageAdapter.getItem(0).setSelected(true);
        pageAdapter.setNewData(pageNumList);
    }

    /**
     * 判断页数是否选中
     */
    private void isCheckPage(){
        for (int i=0;i<pageNumList.size();i++){
            pageNumList.get(i).setSelected(false);
        }
        pageNumList.get(nowPagepostion).setSelected(true);
        pageAdapter.setNewData(pageNumList);
    }

    private TextView tv_page_num;
    /**
     * 列表下的点击事件
     */
    private void onClick(){
        pageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            nowPagepostion=position;
            switch (view.getId()){
                case R.id.tv_page_num:
                    tv_page_num=(TextView) view;
                    Logger.e("点击第"+position+"页");
                    isCheckPage();
                    setData(position);
                    break;
            }
        });
    }
}
