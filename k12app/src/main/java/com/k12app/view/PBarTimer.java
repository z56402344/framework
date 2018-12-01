package com.k12app.view;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

public  class PBarTimer implements Runnable {
	private ProgressBar pb;
	private long begTime;
	private long endTime;

	private static Handler mHdler = new Handler(Looper.getMainLooper());
	
	@Override
	public void run() {
		if (pb==null) return;
		if (endTime == 0) {
			pb.setVisibility(View.INVISIBLE);
			pb = null;
			return; // 隐藏进度条
		}
		// 如果没有显示让其显示
		if (pb.getVisibility()!= View.VISIBLE) {
			pb.setVisibility(View.VISIBLE);
		}
		long cur = System.currentTimeMillis();
		if (cur >= endTime) {
			pb.setProgress((int) (endTime - begTime));
			return;
		}
		pb.setProgress((int) (cur - begTime));
		post(true);
	}

	public PBarTimer start(ProgressBar p, long dur) {
        return start(p,0,dur);
	}

    // cur=已经过去的时间
    public PBarTimer start(ProgressBar p, int cur, long dur) {
        pb = p;
        pb.setVisibility(View.VISIBLE);
        pb.setMax((int) dur);
        pb.setProgress(cur);
        begTime = System.currentTimeMillis()-cur;
        endTime = begTime + dur;
        post(true);
        return this;
    }

    private void post(boolean bStart) {
		mHdler.removeCallbacks(this);
		if (bStart) {
			mHdler.postDelayed(this, 20);
		}
	}

	public void stop() {
		if (pb == null || endTime == 0)
			return; // 没有控制进度条
		begTime = endTime = 0; // 隐藏进度条
		post(true);
	}
	public void cancel() {
		if (pb == null) return;
		post(false);
		pb.setVisibility(View.INVISIBLE);
		pb.setProgress(0);
		begTime = endTime = 0; // 隐藏进度条
		pb = null;
	}
	
	// 暂停 返回已经过去的时间
	public int pause() {
		pb = null;
        return (int)(System.currentTimeMillis()-begTime);
	}
	// 恢复
	public void resume(ProgressBar p) {
		long cur = System.currentTimeMillis();
		if (cur>endTime) return; // 已经结束了
		pb = p;
		post(true);
	}
};

