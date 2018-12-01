package com.k12app.adapter.school;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.k12app.R;
import com.k12app.bean.school.SchoolBean;
import com.k12app.core.IAct;
import com.k12app.net.IKey;
import com.k12app.utils.ImageLoderUtil;
import com.k12app.view.CustomTextView;

import java.util.ArrayList;

import z.frame.BaseFragment;
import z.frame.ICommon;
import z.image.universal_image_loader.core.DisplayImageOptions;

/**
 * 学堂的适配器
 */
public class SchoolAdapter extends BaseAdapter implements ICommon, IAct, IKey {

    private DisplayImageOptions mOpt;
    private ArrayList<SchoolBean> mList = new ArrayList<>();
    private BaseFragment mBf;

    public SchoolAdapter(BaseFragment bf) {
        mBf = bf;
        mOpt = ImageLoderUtil.setImageRoundLogder(R.mipmap.bg_default_photo, app.px(200));
    }

    public void updateData(ArrayList<SchoolBean> list, int type) {
        updateData(list,type,false);
    }

    public void updateData(ArrayList<SchoolBean> list, int type, boolean isdown) {
        mList = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList == null ? null : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            v = View.inflate(parent.getContext(), R.layout.item_school, null);
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
        private CustomTextView mTvTitle;

        public void init(View v) {
            mTvTitle = (CustomTextView) v.findViewById(R.id.mTvTitle);
        }

        public void update(int i) {
            SchoolBean bean = mList.get(i);
            if (bean == null) return;

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            default:
                break;
            }
        }

    }
}