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
	private Paint mPaint;//���ʹ���
	private Path mPath;//��������

	private int mTriangleWidth;//���������εĿ�
	private int mTriangleHeight;//���������εĸ�
	private static final float RADIO_TRIANGLE_WIDTH=1/6F;
	//���������������ĵױ߿��
	private final int TRIANGLE_BOTTOM_MAX=(int) (getScreenWidth()/3*RADIO_TRIANGLE_WIDTH);
	private int mInitTransltionX; //��ʼ����X��ƫ��λ��
	private int mTraslationX;//�ƶ�ʱ��ʵʱλ��
	
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
		//��ȡ�ɼ�Tab������
		TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
		mTabVisibleCount=array.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
		//�жϻ�ȡ���Ƿ�Ϊ0
		if(mTabVisibleCount<0)
		{
			mTabVisibleCount=COUNT_DEFAULT_TAB;
			
		}
		array.recycle();
		//��ʼ������
		mPaint=new Paint();
		mPaint.setAntiAlias(true);//���û��ʿ����
		mPaint.setColor(Color.BLUE);//���û�����ɫColor.parseColor("#008AD4")
		mPaint.setStyle(Style.FILL);//���û��ʵķ��
		//���û��ʲ�Ҫ̫�������ó��и�Բ��������Ч����
		mPaint.setPathEffect(new CornerPathEffect(3));
	}
	/*
	 * ��ʼ����������
	 * */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();//ƽ��֮ǰ����save
		//��ʼƽ����
		canvas.translate(mInitTransltionX+mTraslationX, getHeight()+2);
		//����������
		canvas.drawPath(mPath, mPaint);
		canvas.restore();//�����λ������
		super.dispatchDraw(canvas);
	}

	//��onSizeChanged���������������εĴ�С
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// w�������ؼ���� w/3��ʾÿ���ӿؼ����
		super.onSizeChanged(w, h, oldw, oldh);
		//���������εױߵĿ�ȣ�����Ϊÿ���ӿռ�1/6�Ŀ�ȣ�
		mTriangleWidth=(int) (w/mTabVisibleCount*RADIO_TRIANGLE_WIDTH);
		//�����ֵ�͵�ǰֵ֮����һ����С��
		mTriangleWidth=Math.min(mTriangleWidth, TRIANGLE_BOTTOM_MAX);
		
		//�������ƶ��ǵ�ƫ����������Ϊÿ���ӿؼ����м�λ�ü�ȥ�����εĿ�ȣ�
		mInitTransltionX=w/mTabVisibleCount/2-mTriangleWidth;
		initTrangle();//��ʼ��������
	}
	//��XML������ɺ����ô˷������ڴ˷������жϼ��صĸ���
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//�õ���Ԫ�صĸ���
		int cCount=getChildCount();
		//
		if (cCount==0) return;
		for (int i = 0; i < cCount; i++) {
			//�õ�ÿ����View
			View view=getChildAt(i);
			//ͨ��view
			LinearLayout.LayoutParams lp=(LayoutParams) view.getLayoutParams();
			lp.weight=0;
			//��Ļ��ȳ��Կɼ�tab����(ÿ��Tab�Ŀ��)
			lp.width=getScreenWidth()/mTabVisibleCount;
			view.setLayoutParams(lp);
		}
		setItemClickEvent();
	}
	//�����Ļ�Ŀ��
	private int getScreenWidth() {
		//���ϵͳ����
		WindowManager wmManager=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics=new DisplayMetrics();
		wmManager.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/*
	 * ��ʼ��������
	 * */
	private void initTrangle() {
		//���û��������εĸ߶�Ϊ�ױ߿�ȵ�һ�루������Ϊ45�ȵĵ��������Σ�
		mTriangleHeight=mTriangleWidth/2;
		mPath=new Path();
		mPath.moveTo(0,0);//���û��Ƶ���ʼ����
		mPath.lineTo(mTriangleWidth, 0);//���������εĵױ�
		mPath.lineTo(mTriangleWidth/2, -mTriangleHeight);//���������εĸ߶�
		mPath.close();
		
	}
	/*
	 * ָʾ��������ָ����
	 * */
	public void scrollBy(int position, float offset) {
		int tabWidth=getWidth()/mTabVisibleCount;
		mTraslationX=(int) (tabWidth*(position+offset));
		//�����ƶ�����tab�����ƶ������һ��ʱ
		if (position>=(mTabVisibleCount-2)&&offset>0&&getChildCount()>mTabVisibleCount) {
			
			this.scrollTo(
					(position-(mTabVisibleCount-2))*tabWidth+(int)(tabWidth*offset), 0);
		}
		
		invalidate();
	}
	//�Զ���������tab��ǩ
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
	 * ���ÿɼ���Tab����
	 * */
   public void setVisibleTabCount(int count)
	{
		mTabVisibleCount=count;
		
	}
	//����title����Tab
	private View generateTextView(String title) {
		TextView tv=new TextView(getContext());//new��TextView���ڴ���tab
		//��ʼ��lp
		LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		//����ÿ��Tab�Ŀ��
		lp.width=getScreenWidth()/mTabVisibleCount;
		tv.setText(title);
		tv.setGravity(Gravity.CENTER);//�ַ����뷽ʽ
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);//�����ַ���ʽ���ַ���С
		tv.setTextColor(TAB_COLORTEXT_NORMAL);
		tv.setLayoutParams(lp);
		return tv;
	}
	private ViewPager mViewPager;
	//ͨ���˽ӿڻص�changeListener
	public interface PagerOnchangeListener
	{
		public void onPageSelected(int position);
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels); 
		public void onPageScrollStateChanged(int state);
		
	}
	//���˼�����������������Ҫһ����Ա����
	public PagerOnchangeListener mListener;
	public void setOnPagerChangeListener(PagerOnchangeListener listener)
	{
		this.mListener=listener;
	}
	/*
	 * ���ù�����ViewPager
	 * */
	public void setViewPager(ViewPager viewPager,int pos)
	{
		mViewPager=viewPager;
		//�����λ���ҳ�����
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if (mListener!=null) 
				{
					mListener.onPageSelected(position);
				}
				highLightTextView(position);//���ø���
			}
			//positionOffset��������ֵ
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
	 * ����Tab�ı���ɫ
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
	
	//Tab��������ı�����
	public void highLightTextView(int pos)
	{
		restTextViewColor();
		View view=getChildAt(pos);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(TAB_COLORTEXT_HIGH);
		}
	}
	//����Tab����¼�����л�viewPager
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
