package com.k12app.view;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.k12app.R;
import com.k12app.pay.InputPayPwdFrag;

import z.frame.BaseFragment;
import z.frame.DelayAction;

//简密输入框
public class SecurityPasswordEditText extends LinearLayout implements View.OnKeyListener {
    private EditText mEditText;
    private ImageView oneTextView;
    private ImageView twoTextView;
    private ImageView threeTextView;
    private ImageView fourTextView;
    private ImageView fiveTextView;
    private ImageView sixTextView;
    private LayoutInflater inflater;
    private ImageView[] imageViews;
    private View contentView;
    private StringBuilder builder;
    private DelayAction mDelay = new DelayAction();
    private BaseFragment mBf;


    public SecurityPasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        builder = new StringBuilder();
        initView();
        initData();
    }

    private void initData() {
        mDelay.setInner(300);
    }

    public void setBaseFragment(BaseFragment bf){
        mBf = bf;
        mBf.showImm(true,mEditText);
    }

    private void initView() {
        contentView = inflater.inflate(R.layout.layout_input_pwd, null);
        mEditText = (EditText) contentView
                .findViewById(R.id.sdk2_pwd_edit_simple);
        oneTextView = (ImageView) contentView
                .findViewById(R.id.sdk2_pwd_one_img);
        twoTextView = (ImageView) contentView
                .findViewById(R.id.sdk2_pwd_two_img);
        fourTextView = (ImageView) contentView
                .findViewById(R.id.sdk2_pwd_four_img);
        fiveTextView = (ImageView) contentView
                .findViewById(R.id.sdk2_pwd_five_img);
        sixTextView = (ImageView) contentView
                .findViewById(R.id.sdk2_pwd_six_img);
        threeTextView = (ImageView) contentView
                .findViewById(R.id.sdk2_pwd_three_img);
        LayoutParams lParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mEditText.addTextChangedListener(mTextWatcher);
        imageViews = new ImageView[]{oneTextView, twoTextView, threeTextView,
                fourTextView, fiveTextView, sixTextView};
        mEditText.setOnKeyListener(this);
        this.addView(contentView, lParams);
    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().length() == 0) {
                return;
            }

            if (builder.length() < 6) {
                builder.append(s.toString());
                setTextValue();
            }
            s.delete(0, s.length());
        }

    };


    private void setTextValue() {
        String pwd = builder.toString();
        int len = pwd.length();

        if (len <= 6) {
            imageViews[len - 1].setVisibility(View.VISIBLE);
        }

        if (len >= 6) {
            Log.i("简密框", "回调 支付密码" + pwd);
            if (mListener != null) {
                mListener.onNumCompleted(pwd);
            }
            if (mBf != null){
                mBf.broadcast(InputPayPwdFrag.IA_PayPWD,0,pwd);
            }
        }
    }

    public void delTextValue() {
        String str = builder.toString();
        int len = str.length();
        if (len == 0) {
            return;
        }
        if (len > 0 && len <= 6) {
            builder.delete(len - 1, len);
            Log.e("===", builder.toString()+"len = "+len);
        }
        imageViews[len - 1].setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent e) {
        if (mDelay.invalid()) {
            return false;
        }
        Log.i("===", "onKey"+" keyCode = "+keyCode);
        switch (keyCode){
            case Keyboard.KEYCODE_CANCEL:

                break;
            case KeyEvent.KEYCODE_DEL:
                delTextValue();
                break;
        }
        return false;
    }


    public interface SecurityEditCompleListener {
        public void onNumCompleted(String num);
    }

    public SecurityEditCompleListener mListener;

    public void setSecurityEditCompleListener(
            SecurityEditCompleListener mListener) {
        this.mListener = mListener;
    }

    public void clearSecurityEdit() {
        if (builder != null) {
            if (builder.length() == 6) {
                builder.delete(0, 6);
            }
        }
        for (ImageView tv : imageViews) {
            tv.setVisibility(View.INVISIBLE);
        }

    }

    public EditText getSecurityEdit() {
        return this.mEditText;
    }
}  