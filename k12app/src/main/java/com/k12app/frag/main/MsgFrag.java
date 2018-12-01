package com.k12app.frag.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.k12app.R;
import com.k12app.adapter.MsgAdapter;
import com.k12app.bean.MsgListBean;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.TitleFrag;
import com.k12app.db.dao.IUser;
import com.k12app.frag.acc.MsgSwitchFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.view.PullRefreshListView;

import java.util.ArrayList;

import z.db.ShareDB;

/**
 * Tab-2 分享页 fragment
 */
public class MsgFrag extends TitleFrag implements PullRefreshListView.PullRefreshListener, HttpItem.IOKErrLis, IKey {

    public static final int FID = FidAll.MsgFrag;
    public static final int FIRST_PAGE = 1;
    private int mCurrPageNum;
    private MsgAdapter mAdapter;
    private View mErrorView;
    private TextView mTvError1;
    private TextView mTvError2;
    private PullRefreshListView mLvMsg;
    private ArrayList<MsgListBean.MsgBean> mMsgBeanList = new ArrayList<>();
    private ShareDB.Sec mSec;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_msg, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        setTitleText("我的消息");
        mErrorView = findViewById(R.id.rl_error);
        mTvError1 = (TextView) mErrorView.findViewById(R.id.tv_msg1);
        mTvError2 = (TextView) mErrorView.findViewById(R.id.tv_msg2);
        mLvMsg = (PullRefreshListView) findViewById(R.id.lv_msg);
        mErrorView.setOnClickListener(this);
        mLvMsg.setCanRefresh(true);
        mAdapter = new MsgAdapter(mMsgBeanList);
        mLvMsg.setAdapter(mAdapter);
        mLvMsg.setPullRefreshListener(this);
    }


    private String mUid = "";

    private void initData() {
        mUid = IUser.Dao.getUserId();
        mSec = new ShareDB.Sec(MsgSwitchFrag.SwiSec);
        mSec.load();
        int isFirst = mSec.getInt(MsgSwitchFrag.IsFirst, 0);
        boolean load = mSec.getBoolean(MsgSwitchFrag.SwiSec + "1");
        if (load || isFirst == 0){
            showLoading(true);
            loadData(FIRST_PAGE);
        }else{
            showShortToast("您已关闭消息开关，在设置页面重新打开可以获取最新消息");
        }
    }

    private void loadData(int page) {
        mCurrPageNum = page;
        HttpItem hi = new HttpItem().setListener(this);
        hi.put("pageId", page);
        hi.put("pageSize", "10");
        hi.put("mUid", mUid);
        hi.setUrl(ContantValue.F_GET_MSG);
        hi.post(this);

    }

    private void showErrorLayout(String errorMsg1, String errorMsg2) {
        mErrorView.setVisibility(View.VISIBLE);
        mLvMsg.setVisibility(View.GONE);

        mTvError1.setText(errorMsg1);
        mTvError2.setText(errorMsg2);
    }


    @Override
    public void onRefresh() {
        loadData(FIRST_PAGE);
    }

    @Override
    public void onLoadMore() {
        loadData(mCurrPageNum + 1);
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        showErrorLayout(TextUtils.isEmpty(errMsg) ? "数据加载失败" : errMsg, "点击刷新");
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        mLvMsg.onRefreshComplete(null);
        mLvMsg.onLoadMoreComplete();
        MsgListBean listBean = resp.getObject(MsgListBean.class, RES);
        if (listBean == null) {
            showErrorLayout("暂无消息数据", "");
            return false;
        }
        int size = listBean.list == null ? 0 : listBean.list.size();
        if (mCurrPageNum == FIRST_PAGE && size == 0) {
            showErrorLayout("暂无消息数据", "");
            return false;
        }

        if (mCurrPageNum == FIRST_PAGE) {
            mMsgBeanList.clear();
        }
        mLvMsg.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        ArrayList<MsgListBean.MsgBean> list = listBean.list;
        mMsgBeanList.addAll(list);
        mLvMsg.setCanLoadMore(mCurrPageNum < listBean.totalPageNum);
        mAdapter.notifyDataSetChanged();
        return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_msg1:
            case R.id.tv_msg2:
            case R.id.rl_error:
                showLoading(true);
                loadData(FIRST_PAGE);
                break;
            default:
                super.onClick(view);
                break;
        }
    }


}
