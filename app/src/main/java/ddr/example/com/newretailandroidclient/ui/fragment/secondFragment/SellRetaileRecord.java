package ddr.example.com.newretailandroidclient.ui.fragment.secondFragment;

import android.graphics.Color;
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
import ddr.example.com.newretailandroidclient.entity.other.PageNum;
import ddr.example.com.newretailandroidclient.entity.other.RetailRecord;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.ui.adapter.PageAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.RetailRecordAdapter;

/**
 * time：2020/04/01
 * desc 零售记录界面
 */
public class SellRetaileRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_sell_retail)
    RecyclerView recycle_sell_retail;
    @BindView(R.id.recycle_pages_y)
    RecyclerView recycle_pages_y;

    private RetailRecord retailRecord;
    private RetailRecordAdapter retailRecordAdapter;
    private List<RetailRecord> retailRecordList;
    private List<List<RetailRecord>> nRetaillist = new ArrayList<>();

    private PageNum pageNum;
    private PageAdapter pageAdapter;
    private List<PageNum> pageNumList;

    private int pageNumber;//页数
    private int allPageNumber;//总数量
    private int nowPagepostion=0;

    public static SellRetaileRecord newInstance(){
        return new SellRetaileRecord();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_retailr;
    }

    @Override
    protected void initView() {
        retailRecordAdapter = new RetailRecordAdapter(R.layout.item_retail_record);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_sell_retail.setLayoutManager(layoutManager);
        recycle_sell_retail.setAdapter(retailRecordAdapter);

        pageAdapter=new PageAdapter(R.layout.item_page_num);
        LinearLayoutManager layoutManager1 =new LinearLayoutManager(getAttachActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycle_pages_y.setLayoutManager(layoutManager1);
        recycle_pages_y.setAdapter(pageAdapter);


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
        retailRecordList=new ArrayList<>();
        for (int i=0;i<150;i++){
            retailRecord=new RetailRecord();
            retailRecord.setId(""+i);
            retailRecord.setName("name"+i);
            retailRecord.setSettlement("结算"+i);
            retailRecord.setB_num("编号"+i);
            retailRecord.setPrice(""+i);
            retailRecord.setNumber(""+i);
            retailRecord.setTotal(""+i*10);
            retailRecordList.add(retailRecord);
        }
        allPageNumber=retailRecordList.size();
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
            List<RetailRecord> value;
            boolean is=i==offset;
            Logger.e("是否----"+is+offset+"---"+i);
            if (i==offset && ysnum>0){
                value=retailRecordList.subList(i*8+i,(i*8+i)+ysnum);
            }else {
                value=retailRecordList.subList(i*8+i,(i+1)*8+i+1);
            }
            nRetaillist.add(value);
        }
        Logger.e("数据------"+nRetaillist.get(1).get(1).getId()+"大小"+nRetaillist.size());
    }

    /**
     * 插入数据
     */
    private void setData(int page){
        retailRecordAdapter.setNewData(nRetaillist.get(page));
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
