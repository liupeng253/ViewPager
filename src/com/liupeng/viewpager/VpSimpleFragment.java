package com.liupeng.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VpSimpleFragment extends Fragment {
	private String mTitle;
	public static final String BUNDLE_STRING="title";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle=getArguments();
		if (bundle!=null) {
			mTitle=bundle.getString(BUNDLE_STRING);
		}
		TextView tView=new TextView(getActivity());
		tView.setText(mTitle);
		tView.setGravity(Gravity.CENTER);
		
		return tView;
	}
	
	public static VpSimpleFragment newInstance(String title)
	{
		Bundle bundle=new Bundle();
		bundle.putString(BUNDLE_STRING, title);
		VpSimpleFragment vpSimpleFragment=new VpSimpleFragment();
		vpSimpleFragment.setArguments(bundle);
		return vpSimpleFragment;
		
		
	}

}
