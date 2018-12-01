package com.k12app.frag.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.k12app.R;
import com.k12app.activity.SecondAct;
import com.k12app.bean.home.HomeBean;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.utils.ImageLoderUtil;
import com.k12app.view.CustomTextView;
import com.k12app.view.PullRefreshListView;
import com.k12lib.afast.utils.NetUtil;

import java.util.ArrayList;

import z.frame.DelayAction;
import z.image.universal_image_loader.core.DisplayImageOptions;

/**
 * Tab-1 首页 fragment
 *
 * 1.保存 PullRefreshListView UI+加载逻辑
 */
public class TabHomeFrag extends TitleFrag implements IAct,
        HttpItem.IOKErrLis, IKey, PullRefreshListView.PullRefreshListener {

    // 通知点击跳转至主页，添加新 FID，避免重新再初始化
    public static final int FID_PUT_ON_TOP = -1800;
    public static final int FID = FidAll.TabHomeFrag;
    public static final int IC_CheckVer = FID + 3;//检查版本事件
    public static final int IA_HttpData = FID + 4;//查询首页数据

    private PullRefreshListView mLvInfo;
    public CourseAdapter mCurAdapter;
    private DelayAction mDelay = new DelayAction();
    private View mErrorView;
    private View mFootView;

    private DisplayImageOptions mOptPhoto;
    private ArrayList<HomeBean.Course> mCurCourse;//当前某日期下的预约课列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_tab_home, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        Util.setText(mRoot, R.id.tv_title, "课程表");
        mLvInfo = (PullRefreshListView) findViewById(R.id.mLvInfo);

        View head = LayoutInflater.from(mRoot.getContext()).inflate(R.layout.layout_tabhome_head, mLvInfo, false);

        mFootView = LayoutInflater.from(mRoot.getContext()).inflate(R.layout.layout_footview, mLvInfo, false);

        mLvInfo.addHeaderView(head);
        mLvInfo.addFooterView(mFootView);
    }

    private void initData() {
        commitAction(IC_CheckVer, 0, 0, 0);
        mOptPhoto = ImageLoderUtil.setImageRoundLogder(R.mipmap.bg_default_photo, app.px(200));
        mLvInfo.setCanRefresh(true);
        mLvInfo.setCanLoadMore(false);
//        mLvInfo.setOnItemClickListener(this);
        mLvInfo.setPullRefreshListener(this);

        mErrorView.setOnClickListener(this);
        mCurAdapter = new CourseAdapter();
        mLvInfo.setAdapter(mCurAdapter);

        httpAll();
    }

    private void httpAll() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast("请检查你的网络状况~");
            return;
        }
        showLoading(true);
        httpData();
    }

    private void httpData() {
        HttpItem hi = new HttpItem().setListener(this);
        hi.setId(IA_HttpData).setUrl(ContantValue.F_QueryIndexData).post(this);
    }

    private void updateUI() {
        mCurAdapter.notifyDataSetChanged();

        //展示是否有数据的UI
//        if (mCurCourse == null || mCurCourse.size() == 0) {
//            mErrorView.setVisibility(View.VISIBLE);
//            mFootView.setVisibility(View.GONE);
//        } else {
//            mErrorView.setVisibility(View.GONE);
//            mFootView.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case IC_CheckVer:
            UpdateVerFrag.checkAndShow(this);
            break;
        default:
            super.handleAction(id, arg, extra);
            break;
        }
    }

    //双击刷新
    @Override
    public void clickRefresh() {
        super.clickRefresh();
        showLoading(true);
        httpAll();
    }

    @Override
    public void onClick(View view) {
        if (mDelay.invalid()) {
            return;
        }
        switch (view.getId()) {
        case R.id.rl_error:
            showLoading(true);
            httpAll();
            break;
        case R.id.left:
            new Builder(getContext(), SecondAct.class, MsgFrag.FID).show();
            break;
        default:
            super.onClick(view);
            break;
        }
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        switch (id) {
        case IA_HttpData:
            mLvInfo.onComplete();
            updateUI();
            break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        switch (resp.id) {
        case IA_HttpData:
            if (mCurCourse != null && mCurCourse.size() > 0) {
                mCurCourse.clear();
            }
            mCurCourse = resp.getArray(HomeBean.Course.class, RES);
            break;
        }
        updateUI();
        return false;
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mLvInfo.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        httpAll();
    }

    @Override
    public void onLoadMore() {

    }

    public class CourseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCurCourse == null ? 0 : mCurCourse.size();
        }

        @Override
        public Object getItem(int position) {
            return mCurCourse == null ? null : mCurCourse.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                v = View.inflate(parent.getContext(), R.layout.item_home, null);
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

            }

            public void update(int i) {
                HomeBean.Course bean = mCurCourse.get(i);
                if (bean == null) return;
                mTvName.setText(bean.teacher_name);
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

}
