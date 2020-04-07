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
import ddr.example.com.newretailandroidclient.entity.other.PageNum;
import ddr.example.com.newretailandroidclient.other.Logger;
import ddr.example.com.newretailandroidclient.ui.adapter.BackRecoedAdapter;
import ddr.example.com.newretailandroidclient.ui.adapter.PageAdapter;

public class SellBackRecord extends DDRLazyFragment {
    @BindView(R.id.recycle_pages_y_back)
    RecyclerView recycle_pages_y;
    @BindView(R.id.recycle_sell_back)
    RecyclerView recycle_sell_back;

    private PageNum pageNum;
    private PageAdapter pageAdapter;
    private List<PageNum> pageNumList;

    private BackRecord backRecord;
    private BackRecoedAdapter backRecoedAdapter;
    private List<BackRecord> backRecordList;
    private List<List<BackRecord>> nBackRrcordList=new ArrayList<>();

    private int pageNumber;//页数
    private int allPageNumber;//总数量
    private int nowPagepostion=0;//当前页

    public static SellBackRecord newInstance(){
        return new SellBackRecord();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_sell_back;
    }

    @Override
    protected void initView() {
        backRecoedAdapter = new BackRecoedAdapter(R.layout.item_back_record);
        LinearLayoutManager layoutManager =new LinearLayoutManager(getAttachActivity());
        recycle_sell_back.setLayoutManager(layoutManager);
        recycle_sell_back.setAdapter(backRecoedAdapter);

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
        backRecordList=new ArrayList<>();
        for (int i=0;i<150;i++){
            backRecord=new BackRecord();
            backRecord.setId(""+i);
            backRecord.setTime("time"+i);
            backRecord.setPay_f("结算"+i);
            backRecord.setBack_f("退款"+i);
            backRecord.setPay_num("编号"+i);
            backRecord.setBack_result("成功"+i);
            backRecordList.add(backRecord);
        }
        allPageNumber=backRecordList.size();
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
            List<BackRecord> value;
            boolean is=i==offset;
            Logger.e("是否----"+is+offset+"---"+i);
            if (i==offset && ysnum>0){
                value=backRecordList.subList(i*8+i,(i*8+i)+ysnum);
            }else {
                value=backRecordList.subList(i*8+i,(i+1)*8+i+1);
            }
            nBackRrcordList.add(value);
        }
        Logger.e("数据------"+nBackRrcordList.get(1).get(1).getId()+"大小"+nBackRrcordList.size());
    }

    /**
     * 插入数据
     */
    private void setData(int page){
        backRecoedAdapter.setNewData(nBackRrcordList.get(page));
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
