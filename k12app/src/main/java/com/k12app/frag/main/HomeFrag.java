package com.k12app.frag.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHostFixs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.k12app.R;
import com.k12app.common.GlobaleParms;
import com.k12app.core.IUmEvt;

import java.util.ArrayList;

import z.frame.BaseFragment;
import z.frame.DelayAction;

// 主界面
public class HomeFrag extends BaseFragment implements TabHost.OnTabChangeListener {

    private static final String EvtID = IUmEvt.HomeTab;
    public static final int FID = 1900;
    public static final int IA_SelectTab = FID + 1;
    public static final int IA_AutoLogin = FID + 2;
    public static final int IA_Tab = FID + 3;

    public static final String kSelectTab = "select_tab";

    private DelayAction mDelay = new DelayAction(); // 防止点击太频繁
    private long mPressedTime;

    private Class mFrags[] = {TabHomeFrag.class, TabSubscribeFrag.class, TabSchoolFrag.class, TabAccountFrag.class};
    private int mIcons[] = {R.drawable.selector_tab_home, R.drawable.selector_tab_subscribe,
            R.drawable.selector_tab_school, R.drawable.selector_tab_account};

    private String[] mNames = null;
    private ArrayList<ImageView> mIvList = new ArrayList<>();

    private FragmentTabHostFixs mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_home, null);
            setName("主界面(Home)");
            initView();
            initData();
            handleArgs(getArguments());
        }
        return mRoot;
    }

    private void initView() {
        if (mRoot == null) return;
        mTabHost = (FragmentTabHostFixs) findViewById(android.R.id.tabhost);
    }

    private void initData() {
        if (GlobaleParms.isTeacher){
            mNames = new String[]{"首页", "即时答", "学堂", "我的"};
        }else{
            mNames = new String[]{"首页", "约课", "学堂", "我的"};
        }

        mTabHost.setup(mRoot.getContext(), mTabHost.fix(getChildFragmentManager()), R.id.mFlGroup);
        mTabHost.setOnTabChangedListener(this);
        int size = mFrags == null ? 0 : mFrags.length;
        for (int i = 0; i < size; i++) {
            //设置Tab按钮图标,文字
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mNames[i]).setIndicator(getTabView(i));
            //设置Tab内容
            mTabHost.addTab(tabSpec, mFrags[i], null);
        }
        registerLocal(IA_Tab);
    }


    // 给Tab按钮设置图标和文字
    private View getTabView(int i) {
        if (mRoot == null) {
            return null;
        }
        View view = View.inflate(mRoot.getContext(), R.layout.item_tab, null);
        View tabView = view.findViewById(R.id.ll_tab_item);
        if (tabView != null){
            tabView.setOnClickListener(this);
            tabView.setTag(i);
        }
        ImageView mTvIcon = (ImageView) view.findViewById(R.id.iv_tab_icon);
        mTvIcon.setImageResource(mIcons[i]);
        mTvIcon.setSelected(false);
        Util.setText(view, R.id.tv_tab_name, mNames[i]);
        mIvList.add(mTvIcon);
        return view;
    }

    public void handleArgs(Bundle args) {
        if (args != null) {
            int page = args.getInt(kSelectTab, -1);
            if (page > -1) {
                commitAction(IA_SelectTab, page, null, 20);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        long curTs = System.currentTimeMillis();
        if (mPressedTime == 0 || curTs - mPressedTime > 2000) {
            mPressedTime = curTs;
            Toast.makeText(getActivity(), R.string.string_exit,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (curTs - mPressedTime < 2000) {
//            System.exit(0);
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
            return false;
        }
        return false;
    }

    // 异步操作统一入口 避免许多Runnable
    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
            case IA_Tab:
                mTabIndex = arg;
                mTabHost.setCurrentTab(arg);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (mDelay.invalid()) return;
        switch (view.getId()) {
            case R.id.ll_tab_item:
                Object obj = view.getTag();
                if (!(obj instanceof Integer)) {
                    return;
                }

                int index = (Integer) obj;
                if (index != mTabIndex) {
                    mTabIndex = index;
                    mTabHost.setCurrentTab(index);

                } else {
                    String tag = mTabHost.getCurrentTabTag();
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
                    if (fragment instanceof BaseFragment) {
                        ((BaseFragment) fragment).clickRefresh();
                    }
                }
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    private int mTabIndex = 0;

    @Override
    public void onTabChanged(String tabId) {
        if (mRoot == null) {
            return;
        }
        int size = mIvList == null ? 0 : mIvList.size();
        for (int i = 0; i < size; i++) {
            mIvList.get(i).setSelected(false);
        }
        mIvList.get(mTabIndex).setSelected(true);
    }
}
