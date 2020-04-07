package ddr.example.com.newretailandroidclient.ui.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseFragmentAdapter;
import ddr.example.com.newretailandroidclient.common.DDRLazyFragment;
import ddr.example.com.newretailandroidclient.ui.activity.HomeActivity;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellBackRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellChongRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellErrorRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellHuoRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellMapSportRecord;
import ddr.example.com.newretailandroidclient.ui.fragment.secondFragment.SellRetaileRecord;
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
    LineTextView tv_retail_record;
    @BindView(R.id.tv_back_record)
    LineTextView tv_back_record;
    @BindView(R.id.tv_huo_stork)
    LineTextView tv_huo_stork;
    @BindView(R.id.relative_sport)
    RelativeLayout relative_sport;
    @BindView(R.id.tv_sport_record)
    LineTextView tv_sport_record;
    @BindView(R.id.tv_error_record)
    LineTextView tv_error_record;
    @BindView(R.id.tv_chong_record)
    LineTextView tv_chong_record;

    private BaseFragmentAdapter<DDRLazyFragment> mPagerAdapter;

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

    }
    boolean isopen_relative=true;
    boolean isopen_sport=false;
    @OnClick({R.id.tv_retail_count,R.id.tv_retail_record,R.id.tv_back_record,R.id.tv_huo_stork,R.id.tv_sport_count,R.id.tv_sport_record,R.id.tv_error_record,R.id.tv_chong_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_retail_count://售卖统计
                if (isopen_relative){
                    relative_count.setVisibility(View.GONE);
                    isopen_relative=false;
                }else {
                    relative_count.setVisibility(View.VISIBLE);
                    isopen_relative=true;
                }
                break;
            case R.id.tv_sport_count:
                if (isopen_sport){
                    relative_sport.setVisibility(View.GONE);
                    isopen_sport=false;
                }else {
                    relative_sport.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
