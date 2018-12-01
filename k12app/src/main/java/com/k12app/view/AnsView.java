package com.k12app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.k12app.R;
import com.k12app.core.FileCenter;
import com.k12app.core.IAct;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.utils.PicSelect;
import com.k12lib.afast.utils.NetUtil;

import java.io.File;

import z.frame.BaseFragment;
import z.frame.DelayAction;
import z.frame.ICommon;
import z.frame.UmBuilder;
import z.image.universal_image_loader.core.display.RoundedBitmapDisplayer;

/**
 * 回复编辑框
 */
public class AnsView extends LinearLayout implements HttpItem.IOKErrLis, IAct,View.OnClickListener, Animation.AnimationListener, View.OnTouchListener, ICommon, View.OnFocusChangeListener {

    public static final int FID = 5300;
    public static final int IA_Ans = FID + 1;
    public static final int Type_Post = FID + 2;//帖子楼主的回复
    public static final int Type_PostComment = FID + 3;//帖子楼层回复
    public static final int IA_SysSoftHide = FID + 4;
    public static final int IA_SysSoftShow = FID + 5;
    public static final int IA_Scroll = FID + 6;//编辑框滑动事件
    public static final int IA_Scroll2 = FID + 7;//编辑框滑动事件2
    public static final int IA_Dlg = FID + 8;//重录的Dialog事件
    private static String mHint = "输入聊天内容";
    private String IEvt;

    public View mV;
    public EditText mEtText;
    public boolean isPic = false;
    public PicSelect mPic;//照片相关的公共类
//    public PostReplyBean mComment;
    public View mEditCover;
    private Context mCtx;
    private BaseFragment mBf;
    private RelativeLayout mRlPic, mRlImage;
    private ImageView mIvPic,  mIvShowPic;
    private Button mBtnCom;

    private TextView mTvPic, mTvCamera;
    private View  mVBg, mVBg2, mLine;
    private DelayAction mDelay = new DelayAction();
    private int mSoftInputMode = -1;
    private String mToast;
//    private ViewMgr.Msg mMsg;
    private String mImgUrl;

    private TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() >= 150) {
                mBf.showShortToast("最多可以输入150个字哦");
            }
            showCommentBtn(temp.length() > 0);
        }
    };

    private GestureDetector mGesture = new GestureDetector(new GestureDetector.OnGestureListener() {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            onExit();
            mRlPic.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent ev, MotionEvent ev2, float dx, float dy) {
            mBf.commitAction(IA_Scroll, (int) dy, null, 0);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent ev1, MotionEvent ev2, float dx, float dy) {
            int y = (int) (ev1.getY() - ev2.getY());
            if (Math.abs(dy) > 500) {
                mBf.commitAction(IA_Scroll2, y, null, 0);
            }
            return false;
        }
    });

    public AnsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnsView setEvtId(String evtId) {
        IEvt = evtId;
        return AnsView.this;
    }

    public AnsView setToast(String str) {
        mToast = str;
        return AnsView.this;
    }

    @SuppressLint("NewApi")
    public AnsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnsView(BaseFragment bf) {
        super(bf.getContext());
        mCtx = bf.getContext();
        mBf = bf;
        initView();
    }

    private void initView() {
        mV = View.inflate(mCtx, R.layout.custom_ansview, null);
        mEtText = (EditText) mV.findViewById(R.id.mEtText);
        mRlPic = (RelativeLayout) mV.findViewById(R.id.mRlPic);
        mRlImage = (RelativeLayout) mV.findViewById(R.id.mRlImage);

        mTvPic = (TextView) mV.findViewById(R.id.mTvPic);
        mTvCamera = (TextView) mV.findViewById(R.id.mTvCamera);

        mIvPic = (ImageView) mV.findViewById(R.id.mIvPic);
        mIvShowPic = (ImageView) mV.findViewById(R.id.mIvShowPic);
        mVBg = mV.findViewById(R.id.mVBg);
        mVBg2 = mV.findViewById(R.id.mVBg2);
        mLine = mV.findViewById(R.id.mLine);

        mBtnCom = (Button) mV.findViewById(R.id.mBtnCom);

        mPic = new PicSelect(mBf);
        mPic.setCropWH(480, 560);
        mPic.setFile(new File(FileCenter.getPhotoDir(), PicSelect.File));
        initShow();
        mEtText.addTextChangedListener(mTextWatcher);
        mVBg.setOnTouchListener(this);

        mIvShowPic.setOnClickListener(this);
        mEtText.setOnClickListener(this);
        mBtnCom.setOnClickListener(this);
        mTvPic.setOnClickListener(this);
        mTvCamera.setOnClickListener(this);

        ICommon.Util.setOnClick(mV, R.id.mIvPicDel, this);

        mEditCover = mV.findViewById(R.id.mEditCover);
        mEditCover.setOnClickListener(this);
        mEtText.setFocusable(false);
        addView(mV);
    }

    public void initShow() {
        mRlPic.setVisibility(View.GONE);
        mIvShowPic.setSelected(isPic);
    }

    public void setHint(String hint) {
        if (mEtText == null || TextUtils.isEmpty(hint)) return;
        mHint = hint;
        mEtText.setHint(hint);
    }

//    public void setCommentInfo(PostReplyBean c) {
//        mComment = c;
//        if (c == null) {
//            mEtText.setHint(mHint);
//        } else {
//            mEtText.setHint("@" + c.reviewNick + ":");
//        }
//        enableEdit(true);
//    }

    private void setInputMode(boolean isShow) {
        if (mBf == null) return;
        FragmentActivity act = mBf.getActivity();
        if (act != null) {
            Window window = act.getWindow();
            if (window != null) {
                WindowManager.LayoutParams att = window.getAttributes();
                if (mSoftInputMode == -1) mSoftInputMode = att.softInputMode;
                window.setSoftInputMode(isShow ? WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE : mSoftInputMode);
            }
        }
    }

    private void enableEdit(boolean bOn) {
        setInputMode(bOn);
        if (bOn) {
            mEditCover.setVisibility(GONE);
            mEtText.setFocusable(true);
            mEtText.setFocusableInTouchMode(true);
            mEtText.setOnFocusChangeListener(this);
            mBf.showImm(true, mEtText);
        } else {
            mEditCover.setVisibility(VISIBLE);
            mBf.showImm(false, null);
            mEtText.setOnFocusChangeListener(null);
            mEtText.clearFocus();
            mEtText.setFocusable(false);
        }
        showBg(bOn);
    }

    public void setEditHint(String hint) {
        if (TextUtils.isEmpty(hint) || mEtText == null) return;
        mEtText.setHint(hint);
    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) {
            return;
        }
        switch (v.getId()) {
        case R.id.mIvShowPic:
            //展示选择照片面板
            UmBuilder.reportSimple(IEvt, "回复图片点击");
            showBg(true);
            mRlPic.setVisibility(View.VISIBLE);
            mBf.commitAction(IA_SysSoftHide, 0, null, 100);
            break;
        case R.id.mBtnCom:
            mBf._log("发表评论 >>> ");
            UmBuilder.reportSimple(IEvt, "发表点击");
            if (TextUtils.isEmpty(mEtText.getText().toString().trim())) {
                mBf.showShortToast("评论内容不能为空");
                return;
            }
//            mBf.commitAction(LiveMsgFrag.IA_Msg_Text, 0, mEtText.getText().toString(), 0);
            mEtText.setText("");
            onExit();
            break;
        case R.id.mEditCover:
            UmBuilder.reportSimple(IEvt, "输入框点击");
            enableEdit(true);
        case R.id.mEtText:
//            mEtText.setOnFocusChangeListener(new OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    mBf._log("onFocusChange");
//                    showBg(b);
//                }
//            });
            initShow();
            showBg(true);
            break;
        case R.id.mTvPic:
            mPic.startAlbum();
            break;
        case R.id.mTvCamera:
            mPic.startCamera();
            break;
        case R.id.mIvPicDel:
            UmBuilder.reportSimple(IEvt, "删除图片");
            deletePic();
            break;
        }
    }

    public void deletePic() {
        mRlImage.setVisibility(View.GONE);
        mTvPic.setVisibility(View.VISIBLE);
        mTvCamera.setVisibility(View.VISIBLE);
        isPic = false;
        mIvShowPic.setSelected(isPic);
        showCommentBtn(false);
        mPic.setFile(new File(FileCenter.getPhotoDir(), PicSelect.File));
    }

    public void onExit() {
        enableEdit(false);
    }

    public void showBg(boolean isShow) {
        if (mVBg == null) return;
        mVBg.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }


    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case IA_SysSoftHide:
            //隐藏系统键盘
            mBf._log("隐藏系统键盘 >>> ");
            mBf.showImm(false, null); //强制隐藏键盘
            break;
        case IA_SysSoftShow:
            //显示系统键盘
            mBf.showImm(true, mEtText);
            break;
        default:
            break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        int result = mPic.onActivityResult(requestCode, resultCode, data);
        mBf._log("result >>>" + result);
        switch (result) {
        case 0:
            if (mPic.mType == 0) {
                dispAvatarAndSend();
            }
        case 1:
            return;
        }
    }

    //设置图片
    private void dispAvatarAndSend() {
        try {
//            mMsg = new ViewMgr.Msg();
            mImgUrl = mPic.mPhotoFile.getAbsolutePath();
//            // 立即下载1遍 否则如果断网 首页看不到
            Bitmap bmp = BitmapFactory.decodeFile(mPic.mPhotoFile.getAbsolutePath());
            if (bmp == null) return;
//            mMsg.w = bmp.getWidth();
//            mMsg.h = bmp.getHeight();
            mIvPic.setImageDrawable(new RoundedBitmapDisplayer.RoundedDrawable(bmp, 10, 0));
            mRlImage.setVisibility(View.VISIBLE);
            mTvPic.setVisibility(View.GONE);
            mTvCamera.setVisibility(View.GONE);
            isPic = true;
            showCommentBtn(true);
            httpUpload();
        } catch (Exception e) {
            deletePic();
            e.printStackTrace();
        }
    }

    public void clearEdit() {
        if (mEtText != null) {
            mEtText.setText("");
        }
        if (isPic) {
            deletePic();
        }
        initShow();
    }

    public void showCommentBtn(boolean isSelecte) {
        mIvShowPic.setSelected(isPic);
        if (!isSelecte) {
            if (mEtText.getText().toString().length() > 0 || isPic)
                return;
        }
        mBtnCom.setSelected(isSelecte);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void onDestry() {
        isPic = false;
        mEtText.setText("");
        onExit();

    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        if (mGesture.onTouchEvent(ev)) {
            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        showBg(hasFocus);
    }

    private void httpUpload() {
        if (!NetUtil.checkNet(mBf.getActivity())) {
            mBf.showShortToast(getResources().getString(R.string.net_error));
            return;
        }
//        if (mMsg == null){
//            mBf._log("mMsg == null");
//        }
        HttpItem hi = new HttpItem();
//        hi.setUrl(ContantValue.F_UploadFile).setId(LiveMsgFrag.Http_Photo).put("file", new File(mImgUrl));
        hi.setListener(this).post(this);

    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        mBf.hideLoading();
        switch (id) {
        default:
            break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        mBf.hideLoading();
        switch (resp.id) {
        default:
            break;
        }
        return false;
    }
}
