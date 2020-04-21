package ddr.example.com.newretailandroidclient.ui.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import DDRAIServiceProto.DDRAIServiceCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.entity.MessageEvent;
import ddr.example.com.newretailandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.newretailandroidclient.socket.TcpAiClient;
import ddr.example.com.newretailandroidclient.ui.activity.HomeActivity;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellBackRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellChongRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellErrorRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellHuoRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellMapSportRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellRetaileRecord;
import ddr.example.com.newretailandroidclient.widget.textview.BasrTextView;
import ddr.example.com.newretailandroidclient.widget.textview.LineTextView;
import ddr.example.com.newretailandroidclient.widget.view.DDRViewPager;
/**
 * time: 2020/04/01
 * desc: 售卖数据界面
 */
public class SellDataFrament extends DDRLazyFragment<HomeActivity> implements ViewPager.OnPageChangeListener{
    @BindView(R.id.vp_home_pager)
    DDRViewPager viewPager;
    @BindView(R.id.tv_retail_count)
    TextView tv_retail_count;
    @BindView(R.id.relative_count)
    RelativeLayout relative_count;
    @BindView(R.id.tv_retail_record)
    BasrTextView tv_retail_record;
    @BindView(R.id.tv_back_record)
    BasrTextView tv_back_record;
    @BindView(R.id.tv_huo_stork)
    BasrTextView tv_huo_stork;
    @BindView(R.id.relative_sport)
    RelativeLayout relative_sport;
    @BindView(R.id.tv_sport_record)
    BasrTextView tv_sport_record;
    @BindView(R.id.tv_error_record)
    BasrTextView tv_error_record;
    @BindView(R.id.tv_chong_record)
    BasrTextView tv_chong_record;
    @BindView(R.id.tv_sport_count)
    TextView tv_sport_count;

    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;
    private TcpAiClient tcpAiClient;

    public static SellDataFrament newInstance(){
        return new SellDataFrament();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sell_data;
    }


    @Override
    protected void initView() {
        mPagerAdapter = new BaseFragmentAdapter<DDRLazyFragment>(this);
        mPagerAdapter.addFragment(SellRetaileRecord.newInstance());
        mPagerAdapter.addFragment(SellBackRecord.newInstance());
        mPagerAdapter.addFragment(SellHuoRecord.newInstance());
        mPagerAdapter.addFragment(SellMapSportRecord.newInstance());
        mPagerAdapter.addFragment(SellErrorRecord.newInstance());
        mPagerAdapter.addFragment(SellChongRecord.newInstance());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void initData() {
        tcpAiClient=TcpAiClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        isChecked();

    }
    boolean isopen_relative=true;
    boolean isopen_sport=false;
    @OnClick({R.id.tv_retail_count,R.id.tv_retail_record,R.id.tv_back_record,R.id.tv_huo_stork,R.id.tv_sport_count,R.id.tv_sport_record,R.id.tv_error_record,R.id.tv_chong_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_retail_count://售卖统计
                if (isopen_relative){
                    relative_count.setVisibility(View.GONE);
                    tv_retail_count.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.back_right),null);
                    isopen_relative=false;
                }else {
                    relative_count.setVisibility(View.VISIBLE);
                    tv_retail_count.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.back_xia),null);
                    isopen_relative=true;
                }
                break;
            case R.id.tv_sport_count:
                if (isopen_sport){
                    relative_sport.setVisibility(View.GONE);
                    tv_sport_count.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.back_right),null);
                    isopen_sport=false;
                }else {
                    relative_sport.setVisibility(View.VISIBLE);
                    tv_sport_count.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.back_xia),null);
                    isopen_sport=true;
                }
                break;
            case R.id.tv_retail_record:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_back_record:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_huo_stork:
                viewPager.setCurrentItem(2);
                break;
            case R.id.tv_sport_record:
                viewPager.setCurrentItem(3);
                break;
            case R.id.tv_error_record:
                viewPager.setCurrentItem(4);
                break;
            case R.id.tv_chong_record:
                viewPager.setCurrentItem(5);
                break;
        }
        isChecked();
    }

    /**
     * 判断哪个页面是否被选中
     */
    protected void isChecked() {
        tv_retail_record.setType(0);
        tv_back_record.setType(0);
        tv_huo_stork.setType(0);
        tv_sport_record.setType(0);
        tv_error_record.setType(0);
        tv_chong_record.setType(0);
        switch (viewPager.getCurrentItem()){
            case 0:
                tv_retail_record.isChecked(true);
                tv_back_record.isChecked(false);
                tv_huo_stork.isChecked(false);
                tv_sport_record.isChecked(false);
                tv_error_record.isChecked(false);
                tv_chong_record.isChecked(false);
                break;
            case 1:
                tv_retail_record.isChecked(false);
                tv_back_record.isChecked(true);
                tv_huo_stork.isChecked(false);
                tv_sport_record.isChecked(false);
                tv_error_record.isChecked(false);
                tv_chong_record.isChecked(false);
                break;
            case 2:
                tv_retail_record.isChecked(false);
                tv_back_record.isChecked(false);
                tv_huo_stork.isChecked(true);
                tv_sport_record.isChecked(false);
                tv_error_record.isChecked(false);
                tv_chong_record.isChecked(false);
                break;
            case 3:
                tv_retail_record.isChecked(false);
                tv_back_record.isChecked(false);
                tv_huo_stork.isChecked(false);
                tv_sport_record.isChecked(true);
                tv_error_record.isChecked(false);
                tv_chong_record.isChecked(false);
                break;
            case 4:
                tv_retail_record.isChecked(false);
                tv_back_record.isChecked(false);
                tv_huo_stork.isChecked(false);
                tv_sport_record.isChecked(false);
                tv_error_record.isChecked(true);
                tv_chong_record.isChecked(false);
                break;
            case 5:
                tv_retail_record.isChecked(false);
                tv_back_record.isChecked(false);
                tv_huo_stork.isChecked(false);
                tv_sport_record.isChecked(false);
                tv_error_record.isChecked(false);
                tv_chong_record.isChecked(true);
                break;

        }
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        isChecked();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
