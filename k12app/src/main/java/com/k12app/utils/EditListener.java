package com.k12app.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.k12app.core.FidAll;

import z.frame.BaseFragment;

public class EditListener implements View.OnFocusChangeListener,View.OnClickListener,TextWatcher  {
	public static final int FID = FidAll.EditListenerFID;
	public static final int IA_Change = FID + 1;
	public EditText mEdit;
	public View mDel;
	public BaseFragment mBf;
	public String getTrimString() {
		return mEdit.getText().toString().trim();
	}
	public void init(View parent,int editId,int delIId) {
		mEdit = (EditText)parent.findViewById(editId);
		mDel = parent.findViewById(delIId);
		setup();
	}
	public void init(EditText edit,View del) {
		mEdit = edit;
		mDel = del;
		setup();
	}

	public void setFragmet(BaseFragment bf){
		mBf = bf;
	}

	public void setText(String str){
		if (mEdit == null || TextUtils.isEmpty(str))return;
		mEdit.setText(str);
	}
	// 安装
	private void setup() {
		mEdit.addTextChangedListener(this);
		mEdit.setOnFocusChangeListener(this);
		mDel.setOnClickListener(this);
	}
	// 销毁
	public void destroy() {
		if (mEdit!=null) {
			mEdit.removeTextChangedListener(this);
			mEdit.setOnFocusChangeListener(null);
			mEdit = null;
		}
		if (mDel!=null) {
			mDel.setOnClickListener(null);
			mDel = null;
		}
	}
	@Override
	public void afterTextChanged(Editable s) {
		if (mBf != null){
			mBf.handleAction(IA_Change,0,null);
		}
		if (mDel==null) return;
		setVisibility(!TextUtils.isEmpty(s));
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	@Override
	public void onClick(View v) {
		if (mDel==null||mEdit==null) return;
		setVisibility(false);
		mEdit.setText("");
	}
	private void setVisibility(boolean bVis) {
		int vis = bVis?View.VISIBLE:View.INVISIBLE;
		if (mDel.getVisibility()!=vis) {
			mDel.setVisibility(vis);
		}
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (mDel==null||mEdit==null) return;
		setVisibility(hasFocus&&!TextUtils.isEmpty(mEdit.getText()));
	}
}
