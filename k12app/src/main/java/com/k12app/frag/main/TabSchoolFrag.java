package com.k12app.frag.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.k12app.R;
import com.k12app.adapter.school.SchoolAdapter;
import com.k12app.bean.school.SchoolBean;
import com.k12app.common.ContantValue;
import com.k12app.common.GlobaleParms;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.view.PullRefreshListView;
import com.k12lib.afast.utils.NetUtil;

import java.util.ArrayList;


/**
 * Tab-3
 *
 * 以下是此页面保存的代码说明
 * 1.保存 ErrorView UI+逻辑
 * 2.保存 PullRefreshListView UI + 下拉加载更多onLoadMore(),上拉重新加载逻辑onRefresh()
 * 3.保存 httpAll() 接口请求 模板
 * 4.保存 双击 tab 响应 clickRefresh() 事件
 * 5.保存 OnItemClickListener监听 响应 onItemClick() 事件
 */
public class TabSchoolFrag extends TitleFrag implements HttpItem.IOKErrLis, IKey, IAct, PullRefreshListView.PullRefreshListener, AdapterView.OnItemClickListener{

    public static final int FID = FidAll.TabSchoolFrag;
    public static final int Http_School = FID + 1;

    private View mErrorView;
    private PullRefreshListView mLv;
    private LinearLayout mLlSearch;
    private View mFootView;

    public static final int FIRST_PAGE = 1;
    private int mCurrStuPage = FIRST_PAGE;
    private SchoolAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_tab_school, null);
//            setTitleText("学堂");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mLv = (PullRefreshListView) findViewById(R.id.mLv);
        mErrorView = findViewById(R.id.rl_error);

        View head = View.inflate(mRoot.getContext(), R.layout.item_school_head, null);
        mLlSearch = (LinearLayout) head.findViewById(R.id.mLlSearch);

        mErrorView.setOnClickListener(this);
        mLlSearch.setOnClickListener(this);

        mFootView = LayoutInflater.from(mRoot.getContext()).inflate(R.layout.layout_footview, mLv, false);
        mLv.addHeaderView(head);
        mLv.addFooterView(mFootView);
    }

    private void initData() {
        mAdapter = new SchoolAdapter(this);
        mLv.setAdapter(mAdapter);
        mLv.setCanRefresh(true);
        mLv.setCanLoadMore(false);
        mLv.setPullRefreshListener(this);
        mLv.setOnItemClickListener(this);
        httpAll();
    }


    private void httpAll() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast("请检查你的网络状况~");
            return;
        }
        showLoading(true);
        httpSchool(FIRST_PAGE);
    }

    private void httpSchool(int page) {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        if (page == FIRST_PAGE ){
                mCurrStuPage = FIRST_PAGE;
        }
        showLoading(true);
        HttpItem hi = new HttpItem().setListener(this);

        hi.setUrl(ContantValue.F_QueryXtList)
                .setId(Http_School)
                .put("pagesize", GlobaleParms.QueryNum)
                .put("pageno", page);
        hi.post(this);
    }

    //双击tab刷新逻辑
    @Override
    public void clickRefresh() {
        super.clickRefresh();
        initData();
    }

    @Override
    public void onRefresh() {
        httpSchool(FIRST_PAGE);
    }

    @Override
    public void onLoadMore() {
            httpSchool(++mCurrStuPage);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_error:
                mErrorView.setVisibility(View.GONE);
                initData();
                break;
            case R.id.mLlSearch:
                //搜索跳转到制定页面
//                new Builder(getContext(), SecondAct.class, SearchMirClassFrag.FID).with(kType,0).show();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    //通过 commitAction()或者广播broadcast()方法发出,响应对应回调的方法
    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
            default:
                super.handleAction(id, arg, extra);
                break;
        }
    }


    private void showErrorLayout() {
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        mLv.onComplete();
        switch (id) {
            case Http_School:
                showErrorLayout();
                break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        mLv.onComplete();
        switch (resp.id) {
            case Http_School:
                ArrayList<SchoolBean> list = resp.getArray(SchoolBean.class, RES);
                ArrayList<SchoolBean> tempList = null;
//                tempList = mType == 0 ? mTeaList : mStuList;
                if (list != null && list.size() > 0) {
                    int curPage =  mCurrStuPage;
                    if (curPage == FIRST_PAGE && tempList != null && tempList.size() > 0) {
                        tempList.clear();
                    }
                }
                if (list == null || list.size() == 0){
                    mLv.setCanLoadMore(false);
                    mFootView.setVisibility(View.VISIBLE);
                }else{
                    mLv.setCanLoadMore(true);
                    mFootView.setVisibility(View.GONE);
                    tempList.addAll(list);
                }
                mAdapter.updateData(tempList,0);
        }
        return false;
    }

}
