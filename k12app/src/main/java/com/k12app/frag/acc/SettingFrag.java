package com.k12app.frag.acc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.k12app.R;
import com.k12app.activity.HomeAct;
import com.k12app.activity.SecondAct;
import com.k12app.bean.UserBean;
import com.k12app.common.ContantValue;
import com.k12app.common.GlobaleParms;
import com.k12app.core.FidAll;
import com.k12app.core.FileCenter;
import com.k12app.core.TitleFrag;
import com.k12app.core.UserLogin;
import com.k12app.db.dao.IUser;
import com.k12app.frag.login.GuideFrag;
import com.k12app.frag.main.GoodsDetailFrag;
import com.k12app.frag.main.UpdateVerFrag;
import com.k12app.net.SyncMgr;
import com.k12app.utils.PicSelect;
import com.k12app.view.MyUtil;
import com.k12lib.afast.utils.DataCleanUtil;
import com.k12lib.afast.utils.IntentUtils;

import z.frame.BaseFragment;
import z.frame.UmBuilder;

import static com.k12app.frag.main.TabAccountFrag.EvtID;

/**
 * 设置页面
 */
public class SettingFrag extends TitleFrag {

    public static final int FID = FidAll.Setting;
    public static final int IC_CheckVer = FID + 1;

    private UserLogin mUserLogin;
    private PopupWindow mPopWin;
    private PicSelect mPic;//照片相关的公共类


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_setting, null);
            setTitleText("设置");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {

    }

    public void initData() {
        mPic = new PicSelect(this);
        mPic.setFile(FileCenter.getMsgBgFile(PicSelect.MsgBGFile));
        SyncMgr.fireAppVersion();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mTvMsg:
            //消息开关页面
            new Builder(getContext(), SecondAct.class, MsgSwitchFrag.FID).show();
            break;
        case R.id.mTvCache://清理缓存
            UmBuilder.reportSimple(EvtID, "清除缓存");
            showClear();
            break;
        case R.id.mTvPwd://修改密码
            new Builder(mRoot.getContext(), SecondAct.class, ResetPwdFrag.FID).show();
            break;
        case R.id.mTvFeedback://投诉与建议
            new Builder(mRoot.getContext(), SecondAct.class, CallCenterFrag.FID).show();
            break;
        case R.id.mTvScore://评分
            IntentUtils.startScore(getActivity());
            break;
        case R.id.mTvAbout://关于我们
            UmBuilder.reportSimple(EvtID, "关于我们");
//            new Builder(mRoot.getContext(), SecondAct.class, AboutusFrag.FID).show();
            new BaseFragment.Builder(getContext(), SecondAct.class, GoodsDetailFrag.FID)
                    .with(GoodsDetailFrag.kUrl, ContantValue.F_AppAboutus)
                    .with(GoodsDetailFrag.kTitle, "关于我们")
                    .show();
            break;
        case R.id.mTvVer:
            UpdateVerFrag.checkAndShow(this);
            SyncMgr.fireAppVersion();
            break;
        case R.id.mTvShare:
            //分享下载
            UserBean user = IUser.Dao.getUser();
            if (user == null)return;
            String no = GlobaleParms.isTeacher?user.teacher_no:user.student_no;
//            Bitmap bmp = BitmapFactory.decodeResource(mRoot.getResources(),R.mipmap.ic_share_code);
            shareEnter(TextImage)
                    .title("北京课十二教育科技有限公司,依托移动互联网，为学生打造全方位的移动互联网在线答疑新方式。")
                    .content("北京课十二教育科技有限公司,依托移动互联网，为学生打造全方位的移动互联网在线答疑新方式。")
                    .url(ContantValue.F_AppShare+"code="+no)
//                    .image(bmp)
                    .withCircleShareType(1)
                    .layout(R.layout.dialog_share)
                    .show();
            break;
        case R.id.btn_exit:
            if (mUserLogin == null) {
                mUserLogin = new UserLogin(mRoot.getContext());
            }
            mUserLogin.doLogout();
//                IUser.Dao.updateLogin(0);
            exitAccount();
            break;
        case R.id.mBtnConfirm:
            clearCach();
            if (mPopWin == null)return;
            mPopWin.dismiss();
            break;
        case R.id.mBtnVerCancel:
            if (mPopWin == null)return;
            mPopWin.dismiss();
            break;
        case R.id.mTvMsgBg:
            mPic.showSelectMenu(false, mRoot);

            break;
        default:
            super.onClick(v);
            break;
        }
    }

    private void exitAccount() {
        Intent it = new Intent(mRoot.getContext(), HomeAct.class);
        it.putExtra(HomeAct.kFID, GuideFrag.FID);
        startActivity(it);
        pop(false);
    }

    //清除手机缓存Dialog
    private void showClear() {
        if (mRoot == null) return;
        View v = View.inflate(mRoot.getContext(), R.layout.pop_version_updata_menu, null);

        Util.setText(v, R.id.mBtnConfirm, "清除缓存");
        Util.setOnClick(v, R.id.mBtnConfirm, this);
        Util.setOnClick(v, R.id.mBtnVerCancel, this);

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

    /**
     * 清除手机缓存
     */
    private void clearCach() {
        DataCleanUtil.deleteAllFiles(FileCenter.getDownloadDir());
        DataCleanUtil.deleteAllFiles(FileCenter.getLogDir());
        MyUtil.showSquareToast("清除成功");
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id){
        case IC_CheckVer:
            UpdateVerFrag.checkAndShow(this);
            break;
        default:
            super.handleAction(id, arg, extra);
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        int result = mPic.onActivityResult(requestCode, resultCode, data);
        _log("result >>>" + result);
        switch (result) {
            case 0:
                if (mPic.mType == 0) {
                    showShortToast("设置上课交流区背景图成功~");
                    _log("设置上课交流区背景图成功");
                }
            case 1:
                return;
        }
    }

}
