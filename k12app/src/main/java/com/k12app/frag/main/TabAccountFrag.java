package com.k12app.frag.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.k12app.R;
import com.k12app.activity.SecondAct;
import com.k12app.bean.UserBean;
import com.k12app.common.ContantValue;
import com.k12app.common.GlobaleParms;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.IUmEvt;
import com.k12app.core.TitleFrag;
import com.k12app.core.UserLogin;
import com.k12app.db.dao.IUser;
import com.k12app.frag.acc.SettingFrag;
import com.k12app.frag.login.PerfectInfoFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.utils.ImageLoderUtil;
import com.k12app.view.CustomTextView;
import com.k12lib.afast.utils.NetUtil;
import com.k12lib.afast.view.RecycleImageView;

import z.frame.DelayAction;
import z.frame.ICommon;
import z.image.universal_image_loader.core.DisplayImageOptions;

import static com.k12app.net.IKey.RES;

/**
 * Tab-4 个人中心
 *
 * 1.保存 个人主页 UI + 个人信息接口调用及刷新逻辑
 */
public class TabAccountFrag extends TitleFrag implements IAct, HttpItem.IOKErrLis, ICommon {

    public static final String EvtID = IUmEvt.setting;
    public static final int FID = FidAll.TabAccountFrag;
    public static final int Http_GetInfo = FID + 1;

    private CustomTextView mTvName, mTvMoney, mTvCouponNum, mTvNum, mTvClass;
    private CustomTextView mTvMobile, mTvType;
    private RecycleImageView mIvPhoto;
    private RatingBar mRbar;
    private ImageView mIvArrow;

    private DisplayImageOptions mOptions;
    private DelayAction mDelayAct = new DelayAction();
    private UserBean mUserBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_tab_account, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        Util.setText(mRoot, R.id.tv_title, "我");
        mIvPhoto = (RecycleImageView) findViewById(R.id.mIvPhoto);
        mTvName = (CustomTextView) findViewById(R.id.mTvName);
        mTvClass = (CustomTextView) findViewById(R.id.mTvClass);
        mTvNum = (CustomTextView) findViewById(R.id.mTvNum);
        mTvMoney = (CustomTextView) findViewById(R.id.mTvMoney);
        mTvCouponNum = (CustomTextView) findViewById(R.id.mTvCouponNum);

        mTvMobile = (CustomTextView) findViewById(R.id.mTvMobile);
        mTvType = (CustomTextView) findViewById(R.id.mTvType);
        mRbar = (RatingBar) findViewById(R.id.mRbar);
        mIvArrow = (ImageView) findViewById(R.id.mIvArrow);
    }

    private void initData() {
        registerLocal(UserLogin.Login_Success);
        registerLocal(PerfectInfoFrag.IA_UserInfoRefresh);
        mUserBean = IUser.Dao.getUser();
        mOptions = ImageLoderUtil.setImageRoundLogder(R.mipmap.ic_my_photo, 300);

        Util.setOnClick(mRoot, R.id.mRlInfo, this);
        Util.setOnClick(mRoot, R.id.mTvCourse, this);
        Util.setOnClick(mRoot, R.id.mTvCollect, this);
        Util.setOnClick(mRoot, R.id.mTvCoupon, this);
        Util.setOnClick(mRoot, R.id.mTvWallet, this);
        Util.setOnClick(mRoot, R.id.mTvSetting, this);
        Util.setOnClick(mRoot, R.id.mTvXT, this);
        Util.setOnClick(mRoot, R.id.mTvShare, this);
        Util.setOnClick(mRoot, R.id.mIvArrow, this);

        updateUI();
        httpMyInfo(true);
    }

    @Override
    public void onResume() {
        super.onResume();
//        httpMyInfo();
    }

    public void updateUI() {
        if (GlobaleParms.isTeacher) {
            Util.setVisible(mRoot, R.id.mLineCoupon, View.GONE);
            Util.setVisible(mRoot, R.id.mRlCoupon, View.GONE);
            Util.setVisible(mRoot, R.id.mTvCollect, View.GONE);
            Util.setVisible(mRoot, R.id.mLineCollect, View.GONE);
//            Util.setVisible(mRoot, mTvShare, View.VISIBLE);
            Util.setVisible(mRoot, R.id.mLineShare, View.VISIBLE);
            Util.setVisible(mRoot, R.id.mLineCourseware, View.VISIBLE);
            Util.setVisible(mRoot, R.id.mTvCourseware, View.VISIBLE);
            mTvMobile.setVisibility(View.VISIBLE);
            mTvType.setVisibility(View.VISIBLE);
            mRbar.setVisibility(View.VISIBLE);
        } else {

        }
        if (mUserBean == null) {
            return;
        }

        mTvClass.setText(mUserBean.city_name + " " + mUserBean.school + " " + mUserBean.grade + "年级 " + mUserBean.myClass+"班");
        mTvCouponNum.setText(TextUtils.isEmpty(mUserBean.coupon_num) ? "无" : mUserBean.coupon_num + "张");
        mTvNum.setText("编号：" + mUserBean.student_no);
        mTvName.setText(mUserBean.name);
        mIvPhoto.init(mUserBean.head_img_url, mOptions);
        mTvMoney.setText(String.format("%1$.1f",mUserBean.balance) + "");
    }

    @Override
    public void clickRefresh() {
        super.clickRefresh();
        httpMyInfo(true);
    }

    @Override
    public void onClick(View v) {
        if (mDelayAct.invalid()) return;
        switch (v.getId()) {
//        case R.id.mRlInfo://个人资料
//            new Builder(mRoot.getContext(), SecondAct.class, PerfectInfoFrag.FID).with(kType, 1).show();
//            break;
//        case R.id.mTvXT://我的学堂
//            new Builder(mRoot.getContext(), SecondAct.class, MySchoolFrag.FID).with(kType, 1).show();
//            break;
//        case R.id.mTvCourse://我的课程
//            new Builder(mRoot.getContext(), SecondAct.class, MyCourseFrag.FID).show();
//            break;
//        case R.id.mTvCollect://我的关注
//            new Builder(mRoot.getContext(), SecondAct.class, MyCollectFrag.FID).show();
//            break;
//        case R.id.mTvWallet:
//            //我的钱包
//            new Builder(mRoot.getContext(), SecondAct.class, WalletFrag.FID).show();
//            break;
//        case R.id.mTvCoupon:
//            //我的优惠券
//            new Builder(mRoot.getContext(), SecondAct.class, MyCouponFrag.FID).show();
//            break;
        case R.id.mTvSetting://设置
            new Builder(mRoot.getContext(), SecondAct.class, SettingFrag.FID).show();
            break;
        default:
            super.onClick(v);
            break;
        }
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case UserLogin.Login_Success:
            _log("更新用户UI信息 >>>");
            updateUI();
            break;
        case PerfectInfoFrag.IA_UserInfoRefresh:
            httpMyInfo(false);
            break;
        default:
            super.handleAction(id, arg, extra);
            break;
        }
    }

    public void httpMyInfo(boolean isShow) {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast("请检查你的网络状况~");
            return;
        }
        if (isShow){
            showLoading(true);
        }
        HttpItem hi = new HttpItem().setListener(this).setId(Http_GetInfo);
        hi.setUrl(ContantValue.F_Info).post(this);
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        switch (id) {
        case Http_GetInfo:
            showShortToast(TextUtils.isEmpty(errMsg) ? "获取用户信息失败" : errMsg);
            break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        switch (resp.id) {
        case Http_GetInfo:
            UserBean bean = resp.getObject(UserBean.class, RES);
            if (bean != null) {
                mUserBean = bean;
                UserBean user = IUser.Dao.getUser();
                if (user != null){
                    if (!TextUtils.isEmpty(user.token)){
                        mUserBean.token = user.token;
                    }
                }
                IUser.Dao.saveUser(mUserBean);
            }
            updateUI();
            break;
        }
        return false;
    }
}
