package com.liupeng.viewpager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.liupeng.view.ViewPagerIndicator;
import com.liupeng.view.ViewPagerIndicator.PagerOnchangeListener;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.Window;
import android.widget.Adapter;

public class MainActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private ViewPagerIndicator mIndicator;
	private List<String> mTitles=Arrays.asList("ҳ��1","ҳ��2","ҳ��3","ҳ��4","ҳ��5","ҳ��6","ҳ��7","ҳ��8","ҳ��9","ҳ��10","ҳ��11","ҳ��12");
	private List<VpSimpleFragment> mContent=new ArrayList<VpSimpleFragment>();
	private FragmentPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        
        initViews();
        initData();
        
        mIndicator.setVisibleTabCount(4);//����Tab�ĸ���
        mIndicator.setTabItemTitle(mTitles);
        
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager, 0);
        
        //����ViewPager�Ĺ���ʱ
        mIndicator.setOnPagerChangeListener(new PagerOnchangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
    }
	private void initData() {
		for (String title : mTitles) {
			VpSimpleFragment vpSimpleFragment=VpSimpleFragment.newInstance(title);
		    mContent.add(vpSimpleFragment);
		}
		mAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return mContent.size();
			}
			
			@Override
			public Fragment getItem(int position) {
				return mContent.get(position);
			}
		};
	}
	private void initViews() {
		mViewPager=(ViewPager) findViewById(R.id.id_ViewPager);
		mIndicator=(ViewPagerIndicator) findViewById(R.id.id_indcator);
	}
    
}
