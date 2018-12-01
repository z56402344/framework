package com.k12app.view;

import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import z.down.DMgr;

// 处理下载进度条等逻辑
public abstract class DownloadBar implements Runnable {
	public static Handler mHdler = new Handler(Looper.getMainLooper());
	// 定时器
	public void fireTimer(int ms) {
		mHdler.removeCallbacks(this);
		if (ms<0) return;
		if (ms>0) 
			mHdler.postDelayed(this, 500);
		else 
			mHdler.post(this);
	}
	public String mDownId = null;
	// 初始化
	public void init(String id) {
		mDownId = id;
		fireTimer(0);
	}
	// 取消
	public void destroy(boolean bCancel) {
		if (mDownId!=null) {
			if (bCancel)
				DMgr.removeDownload(mDownId);
			mDownId = null;
			fireTimer(-1);
		}
	}
	@Override
	public void run() {
		if (mDownId == null)
			return;
		// 更新进度条
		int prog = DMgr.queryProgress(mDownId);
		updateProgress(prog);
		if (prog > -1 && prog < 100) {
			fireTimer(100);
		}
	}
	protected void updateProgress(int prog) {
		if (prog < 0) {
			onProgress(0); // 进度条归零
			onFail();
			mDownId = null;
			return;
		}
		onProgress(prog);
		if (prog < 100)
			return;
		onOK();
		// 下载完成 移除
		if (mDownId!=null) {
			DMgr.removeDownload(mDownId);
			mDownId = null;
		}
	}
	// 成功
	protected void onOK() {
	}
	// 更新进度条
	protected abstract boolean onProgress(int prog);
	// 失败
	protected void onFail() {
	}
	// 下载进度条
	public static class PBar extends DownloadBar {
		protected ProgressBar pb;
		public PBar init(String id, ProgressBar pBar) {
			super.init(id);
			pb = pBar;
			return this;
		}
		@Override
		protected boolean onProgress(int prog) {
			if (pb!=null&&pb.getProgress() != prog) {
				pb.setProgress(prog);
				return true;
			}
			return false;
		}
	}
}
