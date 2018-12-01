package com.k12app.frag.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.k12app.R;
import com.k12app.bean.CityBean;
import com.k12app.core.FidAll;
import com.k12app.core.TitleFrag;
import com.k12app.view.CustomEditText;
import com.k12app.view.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 城市选择页面
 */

public class CityFrag extends TitleFrag implements AdapterView.OnItemClickListener {

    public static final int FID = FidAll.CitysFrag;
    private static final int IA_InitData = FID+1;
    public static final int IA_City = FID+2;

    private CustomEditText mEtSearch;
    private ListView mLvCity;
//    private LetterListView mLvLetter; // A-Z listview
    private ListAdapter mAdapter;

    private ArrayList<CityBean> mAllCityList; // 所有城市列表
    private ArrayList<CityBean> mSearchCityList; // 需要显示的城市列表-随搜索而改变
    private ArrayList<CityBean> mCityList;// 城市列表
    private JSONArray mJsonCitys;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_city, null);
            setTitleText("选择城市");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mLvCity = (ListView) findViewById(R.id.list_view);
//        mLvLetter = (LetterListView) findViewById(mLvLetter);
        mEtSearch = (CustomEditText) findViewById(R.id.mEtSearch);
    }

    private void initData() {
        mAllCityList = new ArrayList<>();
        mLvCity.setOnItemClickListener(this);
        showLoading(true);
        commitAction(IA_InitData,0,null,10);
    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        mCityList = getCityList();
        mSearchCityList = mCityList;
    }

    private ArrayList<CityBean> getCityList() {
        ArrayList<CityBean> list = new ArrayList<>();
        try {
            mJsonCitys = new JSONArray(getResources().getString(R.string.citys));
            for(int i = 0; i< mJsonCitys.length(); i++){
                JSONObject jsonObject = mJsonCitys.getJSONObject(i);
                CityBean city = new CityBean(jsonObject.getString("CITY_ID"), jsonObject.getString("CITY_NAME"));
                list.add(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id){
        case IA_InitData:
            if (arg == 0){
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        hotCityInit();
                        commitAction(IA_InitData,1,null,100);
                        super.run();
                    }
                };
                thread.start();
            }else{
                hideLoading();
                mAdapter = new ListAdapter();
                mLvCity.setAdapter(mAdapter);
                mEtSearch.addTextChangedListener(new EtWatch());
            }
            break;
        default:
            super.handleAction(id, arg, extra);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        CityBean cityBean = mSearchCityList.get(i);
        if (cityBean == null)return;
        broadcast(IA_City,0,cityBean);
        pop(false);
    }

    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        final int VIEW_TYPE = 3;

        public ListAdapter() {
            this.inflater = LayoutInflater.from(mRoot.getContext());
        }

        @Override
        public int getCount() {
            return mSearchCityList == null?0:mSearchCityList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSearchCityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            int type = 2;

            if (position == 0&& mEtSearch.getText().length()==0) {//不是在搜索状态下
                type = 0;
            }
            return type;
        }

        @Override
        public int getViewTypeCount() {// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
            return VIEW_TYPE;
        }

        @Override
        public View getView(int i, View v, ViewGroup parent) {
            ViewHolder holder;
            int type = getItemViewType(i);
            if (v == null) {
                v = inflater.inflate(R.layout.item_city, null);
                holder = new ViewHolder();
                holder.init(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.update(i);
            return v;
        }

        private class ViewHolder {
            CustomTextView mTvName; // 城市名字

            public void init(View v){
                mTvName = (CustomTextView) v.findViewById(R.id.mTvName);
            }

            public void update(int i){
                if (mEtSearch.getText().length()==0) {//搜所状态
//					holder.mTvName.setText(list.get(position).getName());
//					holder.mTvAlpha.setVisibility(View.GONE);
//				}else if(position>0){
                    //显示拼音和热门城市，一次检查本次拼音和上一个字的拼音，如果一样则不显示，如果不一样则显示
                    CityBean cityBean = mSearchCityList.get(i);
                    if (cityBean == null)return;
                    mTvName.setText(cityBean.name);
				}
            }
        }
    }


    public class EtWatch implements TextWatcher{
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //搜索符合用户输入的城市名
                if(s.length()>0){
                    ArrayList<CityBean> changecity = new ArrayList<CityBean>();
                    for(int i = 0; i< mCityList.size(); i++){
                        if(mCityList.get(i).name.indexOf(mEtSearch.getText().toString())!=-1){
                            changecity.add(mCityList.get(i));
                        }
                    }
                    mSearchCityList = changecity;
                }else{
                    mSearchCityList = mCityList;
                }
                mAdapter.notifyDataSetChanged();
            }
    }
}
