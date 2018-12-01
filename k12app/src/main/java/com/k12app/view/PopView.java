package com.k12app.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.k12app.R;
import com.k12app.frag.adapter.PopSettingAdapter;

import z.frame.BaseFragment;

//老师端-首页Tab 右上角的PopWindow
public class PopView {

    public static PopupWindow initPop(BaseFragment bf) {
        if (bf == null)return null;
        View popupView = View.inflate(bf.getContext(), R.layout.layout_pop_setting, null);

        ListView mLvSetting = (ListView) popupView.findViewById(R.id.mLvSetting);
        PopupWindow mPop = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, 500);
        PopSettingAdapter adapter = new PopSettingAdapter(bf, mPop);
        mLvSetting.setAdapter(adapter);
        mPop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        mPop.setFocusable(true);
        mPop.setOutsideTouchable(true);
        mLvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mPop.update();
        return mPop;
    }
}
