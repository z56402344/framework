package com.k12app.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.k12app.R;
import com.k12app.bean.MsgListBean;

import java.util.ArrayList;


public class MsgAdapter extends BaseAdapter {

    private ArrayList<MsgListBean.MsgBean> mMsgList;

    public MsgAdapter(ArrayList<MsgListBean.MsgBean> list) {
        mMsgList = list;
    }

    @Override
    public int getCount() {
        return mMsgList == null ? 0 : mMsgList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMsgList == null ? null : mMsgList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        MsgListBean.MsgBean msgBean = mMsgList.get(position);

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = View.inflate(viewGroup.getContext(), R.layout.item_msg, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.fillData(msgBean);
        return view;
    }

    public static class ViewHolder {

        public TextView mTvTitle;
        public TextView mTvDate;

        public ViewHolder(View view) {
            mTvTitle = (TextView) view.findViewById(R.id.tv_msg_content);
            mTvDate = (TextView) view.findViewById(R.id.tv_msg_date);
        }

        public void fillData(MsgListBean.MsgBean bean) {
            mTvTitle.setText(bean.content);
            mTvDate.setText(bean.dateTime);
        }
    }
}
