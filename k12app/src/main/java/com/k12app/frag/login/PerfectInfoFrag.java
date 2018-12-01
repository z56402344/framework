package com.k12app.frag.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.k12app.R;
import com.k12app.bean.CityBean;
import com.k12app.bean.UserBean;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.FileCenter;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.db.dao.IUser;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.utils.CourseUtil;
import com.k12app.utils.EditListener;
import com.k12app.utils.ImageLoderUtil;
import com.k12app.utils.PicSelect;
import com.k12app.view.CustomButton;
import com.k12app.view.CustomEditText;
import com.k12app.view.CustomTextView;
import com.k12lib.afast.utils.NetUtil;
import com.k12lib.afast.view.RecycleImageView;

import java.io.File;

import z.image.universal_image_loader.core.DisplayImageOptions;
import z.image.universal_image_loader.core.display.RoundedBitmapDisplayer;

/**
 * 学生-完善/编辑资料页面
 */
public class PerfectInfoFrag extends TitleFrag implements IAct, HttpItem.IOKErrLis, IKey {

    public static final int FID = FidAll.PerfectInfo;
    public static final int IA_UserInfoRefresh = FID + 1;
    public static final int Http_Info = FID + 2;
    public static final int Http_Photo = FID + 3;

    private CustomTextView mTvSex, mTvCity, mTvHint;
    private RecycleImageView mIvPhoto;
    private CustomEditText mEtNickName, mEtSchool, mEtGrade, mEtClass;
    private CustomButton mBtnSave;

    private int mSex = 1;
    private EditListener mNameL = new EditListener();
    private EditListener mSchoolL = new EditListener();
    private EditListener mGradeL = new EditListener();
    private EditListener mClassL = new EditListener();

    private PopupWindow mPopWin;
    private DisplayImageOptions mOptions;
    private PicSelect mPic;//照片相关的公共类
    private String mCityId;
    private int mType;//0=完善个人资料，1=编辑个人资料
    private String mImgUrl;
    private String mResultUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_perfectinfo, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mTvSex = (CustomTextView) findViewById(R.id.mTvSex);
        mTvCity = (CustomTextView) findViewById(R.id.mTvCity);
        mTvHint = (CustomTextView) findViewById(R.id.mTvHint);
        mEtNickName = (CustomEditText) findViewById(R.id.mEtNickName);
        mEtSchool = (CustomEditText) findViewById(R.id.mEtSchool);
        mEtGrade = (CustomEditText) findViewById(R.id.mEtGrade);
        mEtClass = (CustomEditText) findViewById(R.id.mEtClass);
        mIvPhoto = (RecycleImageView) findViewById(R.id.mIvPhoto);
        mBtnSave = (CustomButton) findViewById(R.id.mBtnSave);
    }

    private void initData() {
        registerLocal(CityFrag.IA_City);
        Bundle arg = getArguments();
        if (arg == null) return;
        mType = arg.getInt(kType, 0);
        setTitleText(mType == 0 ? "完善资料" : "编辑个人资料");
        mTvHint.setVisibility(mType == 0 ? View.VISIBLE : View.INVISIBLE);
        mNameL.init(mRoot, R.id.mEtNickName, R.id.iv_nickName_x);
        mSchoolL.init(mRoot, R.id.mEtSchool, R.id.iv_school);
        mGradeL.init(mRoot, R.id.mEtGrade, R.id.iv_grade);
        mClassL.init(mRoot, R.id.mEtClass, R.id.iv_class);
        mNameL.setFragmet(this);
        mSchoolL.setFragmet(this);
        mGradeL.setFragmet(this);
        mClassL.setFragmet(this);
        mOptions = ImageLoderUtil.setImageRoundLogder(R.mipmap.ic_my_photo, 300);
        mPic = new PicSelect(this);
        mPic.setFile(FileCenter.getDFile(PicSelect.File));
        updateUI();
    }

    private void updateUI() {
        UserBean userBean = IUser.Dao.getUser();
        if (userBean == null) return;
        mTvCity.setText(userBean.city_name);
        mEtNickName.setText(userBean.name);
        mEtSchool.setText(userBean.school);
        mEtGrade.setText(userBean.grade);
        mEtClass.setText(userBean.myClass);
        mTvSex.setText(CourseUtil.getSex(userBean.gender));
        mIvPhoto.init(userBean.head_img_url, mOptions);
        mSex = userBean.gender;
        mCityId = userBean.city_id;
        mResultUrl = userBean.head_img_url;
        setBtnColor(mType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mIvPhoto:
            mPic.showSelectMenu(false, mRoot);
            break;
        case R.id.tv_man:
            setSex(1);
            break;
        case R.id.tv_women:
            setSex(2);
            break;
        case R.id.tv_Secret:
            setSex(3);
            break;
        case R.id.bt_cancel:
            if (mPopWin != null) {
                mPopWin.dismiss();
            }
            break;
        case R.id.mBtnSave:
            httpSave();
            break;
        case R.id.mRlSex:
            showSex();
            break;
        case R.id.mRlCity:
            //选择城市
            new Builder(this, new CityFrag()).show();
            break;
        default:
            super.onClick(v);
            break;
        }
    }

    private void setSex(int sex) {
        if (mPopWin != null) {
            mPopWin.dismiss();
        }
        mSex = sex;
        mTvSex.setText(CourseUtil.getSex(sex));
        handleAction(EditListener.IA_Change,0,null);
    }

    private void httpSave() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        String name = mNameL.getTrimString();
        if (TextUtils.isEmpty(name)) {
            showShortToast("姓名不能为空");
            return;
        }
        String school = mSchoolL.getTrimString();
        if (TextUtils.isEmpty(school)) {
            showShortToast("学校不能为空");
            return;
        }
        String grade = mGradeL.getTrimString();
        if (TextUtils.isEmpty(grade)) {
            showShortToast("年级不能为空");
            return;
        }
        String mClass = mClassL.getTrimString();
        if (TextUtils.isEmpty(mClass)) {
            showShortToast("班级不能为空");
            return;
        }
        int gradeInt = 0;
        int mClassInt = 0;
        try{
            gradeInt = Integer.parseInt(grade);
        }catch (Exception e){
            e.printStackTrace();
            showShortToast("年级请填写数字");
        }
        try{
            mClassInt = Integer.parseInt(mClass);
        }catch (Exception e){
            e.printStackTrace();
            showShortToast("班级请填写数字");
        }
        showLoading(true);
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        HttpItem hi = new HttpItem();
        hi.setUrl(ContantValue.F_Edit).setId(Http_Info);
        hi.put("name", name)
                .put("gender", mSex)
                .put("school", school)
                .put("grade", gradeInt)
                .put("class", mClassInt)
                .put("city_id", mCityId);
        if (!TextUtils.isEmpty(mResultUrl)){
            hi.put("head_img_url", mResultUrl);
        }
        hi.setListener(this).post(this);
    }

    private void httpUpload() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        HttpItem hi = new HttpItem();
        hi.setUrl(ContantValue.F_UploadFile)
                .setId(Http_Photo)
                .put("file", new File(mImgUrl));
        hi.setListener(this).post(this);

    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        switch (id) {
        case Http_Photo:
            showShortToast(!TextUtils.isEmpty(errMsg) ? errMsg : "上传头像失败");
            break;
        case Http_Info:
            showShortToast(!TextUtils.isEmpty(errMsg) ? errMsg : "保存个人信息失败");
            break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        switch (resp.id) {
        case Http_Photo:
            mResultUrl = resp.getString(RES, "file_url");
            handleAction(EditListener.IA_Change,0,null);
            break;
        case Http_Info:
            if (mType == 1) {
                //通知刷新个人资料
                showShortToast("保存个人资料成功");
                broadcast(IA_UserInfoRefresh, 0, null);
                handleAction(EditListener.IA_Change,1,null);
            } else {
                //完善个人资料后 进入首页
                notifyActivity(LoginAs, 0, "");
            }
            break;
        }
        return false;
    }

    private void showSex() {
        if (mRoot == null) return;
        View v = View.inflate(mRoot.getContext(), R.layout.pop_sex_menu, null);
        Util.setOnClick(v, R.id.tv_man, this);
        Util.setOnClick(v, R.id.tv_women, this);
        Util.setOnClick(v, R.id.tv_Secret, this);
        Util.setOnClick(v, R.id.bt_cancel, this);

        v.startAnimation(AnimationUtils.loadAnimation(mRoot.getContext(), R.anim.fade_in));
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
            }
        });
        if (mPopWin == null) {
            mPopWin = new PopupWindow(mRoot.getContext());
            mPopWin.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopWin.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopWin.setBackgroundDrawable(new BitmapDrawable());

            mPopWin.setFocusable(true);
            mPopWin.setOutsideTouchable(true);
        }
        mPopWin.setContentView(v);
        mPopWin.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
        mPopWin.update();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast("请检查你的网络状况~");
            return;
        }
        int result = mPic.onActivityResult(requestCode, resultCode, data);
        _log("result >>>" + result);
        switch (result) {
        case 0:
            if (mPic.mType == 0) {
                dispAvatarAndSend();
            }
        case 1:
            return;
        }
    }

    // 1. 设置头像
    private void dispAvatarAndSend() {
        try {
//            ImageLoader.getInstance().clearMemoryCache();
//            ImageLoader.getInstance().clearDiskCache();
            mImgUrl = mPic.mPhotoFile.getAbsolutePath();
//            // 立即下载1遍 否则如果断网 首页看不到
            Bitmap bmp = BitmapFactory.decodeFile(mPic.mPhotoFile.getAbsolutePath());
            if (bmp == null) return;
            mIvPhoto.setImageDrawable(new RoundedBitmapDisplayer.RoundedDrawable(bmp, 300, 0));
//            mIvPhoto.init("file://"+mImgUrl, mOptions);
            httpUpload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case CityFrag.IA_City:
            if (extra instanceof CityBean) {
                CityBean cityBean = (CityBean) extra;
                if (cityBean == null) return;
                mCityId = cityBean.id;
                mTvCity.setText(cityBean.name);
            }
            handleAction(EditListener.IA_Change,0,null);
            break;
        case EditListener.IA_Change:
            setBtnColor(arg);
            break;
        default:
            super.handleAction(id, arg, extra);
            break;
        }
    }

    private void setBtnColor(int arg) {
        if (arg == 0){
            //有改变，按钮设置成橙色
            mBtnSave.setTextColor(mRoot.getResources().getColor(R.color.selector_greenbtn_white));
            mBtnSave.setBackgroundResource(R.drawable.selector_yellow_btn);
        }else{
            //保存后，按钮设置成灰色
            mBtnSave.setTextColor(mRoot.getResources().getColor(R.color.selector_greenbtn_white));
            mBtnSave.setBackgroundResource(R.drawable.selector_gray_btn);
        }
    }
}
