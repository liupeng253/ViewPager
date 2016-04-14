package com.liupeng.view;

import java.util.List;

import com.liupeng.viewpager.R;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewPagerIndicator extends LinearLayout {
	private Paint mPaint;//画笔工具
	private Path mPath;//画布工具

	private int mTriangleWidth;//绘制三角形的宽
	private int mTriangleHeight;//绘制三角形的高
	private static final float RADIO_TRIANGLE_WIDTH=1/6F;
	//给三角行设置最大的底边宽度
	private final int TRIANGLE_BOTTOM_MAX=(int) (getScreenWidth()/3*RADIO_TRIANGLE_WIDTH);
	private int mInitTransltionX; //初始化的X轴偏移位置
	private int mTraslationX;//移动时的实时位置
	
	private List<String> mTitles;
	
	private int mTabVisibleCount;
	private static final int COUNT_DEFAULT_TAB=4;
	private static final int TAB_COLORTEXT_NORMAL=0x77FFFFFF;
	private static final int TAB_COLORTEXT_HIGH=0x77FF8C32;
	
	public ViewPagerIndicator(Context context) {
		this(context,null);
	}
	
	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		//获取可见Tab的数量
		TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
		mTabVisibleCount=array.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
		//判断获取的是否为0
		if(mTabVisibleCount<0)
		{
			mTabVisibleCount=COUNT_DEFAULT_TAB;
			
		}
		array.recycle();
		//初始化画笔
		mPaint=new Paint();
		mPaint.setAntiAlias(true);//设置画笔抗锯齿
		mPaint.setColor(Color.BLUE);//设置画笔颜色Color.parseColor("#008AD4")
		mPaint.setStyle(Style.FILL);//设置画笔的风格
		//设置画笔不要太尖锐（设置成有个圆角弯曲的效果）
		mPaint.setPathEffect(new CornerPathEffect(3));
	}
	/*
	 * 开始绘制三角形
	 * */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();//平移之前调用save
		//初始平移量
		canvas.translate(mInitTransltionX+mTraslationX, getHeight()+2);
		//绘制三角形
		canvas.drawPath(mPath, mPaint);
		canvas.restore();//三角形绘制完成
		super.dispatchDraw(canvas);
	}

	//在onSizeChanged方法中设置三角形的大小
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// w表整个控件宽度 w/3表示每个子控件宽度
		super.onSizeChanged(w, h, oldw, oldh);
		//设置三角形底边的宽度（设置为每个子空间1/6的宽度）
		mTriangleWidth=(int) (w/mTabVisibleCount*RADIO_TRIANGLE_WIDTH);
		//在最大值和当前值之间挑一个最小的
		mTriangleWidth=Math.min(mTriangleWidth, TRIANGLE_BOTTOM_MAX);
		
		//三角形移动是的偏移量（设置为每个子控件的中间位置减去三角形的宽度）
		mInitTransltionX=w/mTabVisibleCount/2-mTriangleWidth;
		initTrangle();//初始化三角形
	}
	//在XML加载完成后会调用此方法，在此方法中判断加载的个数
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//拿到子元素的个数
		int cCount=getChildCount();
		//
		if (cCount==0) return;
		for (int i = 0; i < cCount; i++) {
			//拿到每个子View
			View view=getChildAt(i);
			//通过view
			LinearLayout.LayoutParams lp=(LayoutParams) view.getLayoutParams();
			lp.weight=0;
			//屏幕宽度除以可见tab数量(每个Tab的宽度)
			lp.width=getScreenWidth()/mTabVisibleCount;
			view.setLayoutParams(lp);
		}
		setItemClickEvent();
	}
	//获得屏幕的宽度
	private int getScreenWidth() {
		//获得系统服务
		WindowManager wmManager=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics=new DisplayMetrics();
		wmManager.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/*
	 * 初始化三角形
	 * */
	private void initTrangle() {
		//设置绘制三角形的高度为底边宽度的一半（三角形为45度的等腰三角形）
		mTriangleHeight=mTriangleWidth/2;
		mPath=new Path();
		mPath.moveTo(0,0);//设置绘制的起始坐标
		mPath.lineTo(mTriangleWidth, 0);//绘制三角形的底边
		mPath.lineTo(mTriangleWidth/2, -mTriangleHeight);//绘制三角形的高度
		mPath.close();
		
	}
	/*
	 * 指示器跟随手指滚动
	 * */
	public void scrollBy(int position, float offset) {
		int tabWidth=getWidth()/mTabVisibleCount;
		mTraslationX=(int) (tabWidth*(position+offset));
		//容器移动，在tab处于移动至最后一个时
		if (position>=(mTabVisibleCount-2)&&offset>0&&getChildCount()>mTabVisibleCount) {
			
			this.scrollTo(
					(position-(mTabVisibleCount-2))*tabWidth+(int)(tabWidth*offset), 0);
		}
		
		invalidate();
	}
	//自动添加任意个tab标签
	public void setTabItemTitle(List<String> titles)
	{
		if (titles!=null&&titles.size()>0) {
			this.removeAllViews();
			mTitles=titles;
			for (String title : mTitles)
			{
				addView(generateTextView(title));
			}
			setItemClickEvent();
			
		}
	}
	/*
	 * 设置可见的Tab数量
	 * */
   public void setVisibleTabCount(int count)
	{
		mTabVisibleCount=count;
		
	}
	//根据title创建Tab
	private View generateTextView(String title) {
		TextView tv=new TextView(getContext());//new出TextView用于创建tab
		//初始化lp
		LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		//设置每个Tab的宽度
		lp.width=getScreenWidth()/mTabVisibleCount;
		tv.setText(title);
		tv.setGravity(Gravity.CENTER);//字符对齐方式
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);//设置字符格式和字符大小
		tv.setTextColor(TAB_COLORTEXT_NORMAL);
		tv.setLayoutParams(lp);
		return tv;
	}
	private ViewPager mViewPager;
	//通过此接口回调changeListener
	public interface PagerOnchangeListener
	{
		public void onPageSelected(int position);
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels); 
		public void onPageScrollStateChanged(int state);
		
	}
	//有了监听的三个方法后需要一个成员变量
	public PagerOnchangeListener mListener;
	public void setOnPagerChangeListener(PagerOnchangeListener listener)
	{
		this.mListener=listener;
	}
	/*
	 * 设置关联的ViewPager
	 * */
	public void setViewPager(ViewPager viewPager,int pos)
	{
		mViewPager=viewPager;
		//三角形滑动页面监听
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (mListener!=null) 
				{
					mListener.onPageSelected(position);
				}
				highLightTextView(position);//设置高亮
			}
			//positionOffset滑动进度值
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
				scrollBy(position,positionOffset);//tabWidth*positionOffset+tabWidth*position
				
				if (mListener!=null) 
				{
					mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
				if (mListener!=null) 
				{
					mListener.onPageScrollStateChanged(state);
				}
				
			}
		});
		mViewPager.setCurrentItem(pos);
		highLightTextView(pos);
	}
	/*
	 * 重置Tab文本颜色
	 * */
	private void restTextViewColor()
	{
		for (int i = 0; i < getChildCount(); i++) 
		{
			View view=getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(TAB_COLORTEXT_NORMAL);
			}
		}
		
	}
	
	//Tab字体高亮文本方法
	public void highLightTextView(int pos)
	{
		restTextViewColor();
		View view=getChildAt(pos);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(TAB_COLORTEXT_HIGH);
		}
	}
	//设置Tab点击事件点击切换viewPager
	public void setItemClickEvent()
	{
		int cCount=getChildCount();
		for (int i = 0; i < cCount; i++) 
		{
			final int j=i;
			View view=getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(j);
					
				}
			});
		}
		
	}
}
