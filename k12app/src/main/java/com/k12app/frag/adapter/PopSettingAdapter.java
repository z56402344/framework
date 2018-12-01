package com.k12app.frag.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;

import com.k12app.R;
import com.k12app.view.CustomTextView;

import z.frame.BaseFragment;

//老师设置Pop适配器
public class PopSettingAdapter extends BaseAdapter {

    private String[] mPopStr = {"调整课时费","预约时段设置","创建微课堂"};
    private BaseFragment mBf;
    private PopupWindow mPop;

    public PopSettingAdapter(BaseFragment bf, PopupWindow pop){
        mBf = bf;
        mPop = pop;
    }

    @Override
    public int getCount() {
        return mPopStr.length;
    }

    @Override
    public Object getItem(int i) {
        return mPopStr[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            v = View.inflate(parent.getContext(), R.layout.item_pop_setting, null);
            holder = new ViewHolder();
            holder.init(v);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.update(i);
        return v;
    }

    public class ViewHolder implements View.OnClickListener {
        private CustomTextView mTvName;

        public void init(View v) {
            mTvName = (CustomTextView) v.findViewById(R.id.mTvName);
            mTvName.setOnClickListener(this);
        }

        public void update(int i) {
            mTvName.setText(mPopStr[i]);
            mTvName.setTag(i);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mTvName:
                    Object tag = mTvName.getTag();
                    int index = (int) tag;
                    if (index == 0){
//                        new BaseFragment.Builder(v.getContext(), SecondAct.class, SetupCostFrag.FID).show();
                    }else if (index == 1){
//                        new BaseFragment.Builder(v.getContext(), SecondAct.class, BusyIdleFrag.FID).show();
                    }
                    if (mPop == null)return;
                    mPop.dismiss();
                    break;
            }
        }
    }
}
