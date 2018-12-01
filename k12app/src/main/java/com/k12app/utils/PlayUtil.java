package com.k12app.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.text.TextUtils;

import com.k12lib.afast.log.Logger;

import z.media.IMedia;

/**
 * 说明：播放音频文件的工具类
 */
public class PlayUtil implements OnSeekCompleteListener, OnPreparedListener, MediaPlayer.OnErrorListener, OnCompletionListener, IMedia {
	public static interface ILPlay {
		/**
		 * 当语音播放时,执行的方法
		 */
		void play(MediaPlayer player, Object user);
		/**
		 * 当语音停止时,执行的方法
		 */
		void stop(Object user, int err);
	}
	public static class NullLis implements ILPlay {
		@Override
		public void play(MediaPlayer player, Object user) {
		}
		@Override
		public void stop(Object user, int err) {
		}
	}
	private MediaPlayer mPlay;
	private ILPlay mPlayIL;
	private Object mUser;
	private int mLastPos = 0;
	private int mState = Null;

	private static PlayUtil mPlayUtil;

	public static PlayUtil getInstance() {
		if (mPlayUtil == null) {
			mPlayUtil = new PlayUtil();
		}
		return mPlayUtil;
	}

	public static MediaPlayer learn(Context ctx, String uri, ILPlay lis, Object user) {
		return getInstance().playInner(ctx, uri, lis, user, 0);
	}

	public static MediaPlayer play(Context ctx, int resId, ILPlay lis) {
		return getInstance().playInner(ctx,resId,lis);
	}

	public static MediaPlayer play(Context ctx, String uri, ILPlay lis, Object user) {
		return getInstance().playInner(ctx, uri, lis, user, -1);
	}
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		Logger.i("PlayUtil","err=ccccccc");
//		stopInner(false,0);
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		if (mPlayIL!=null) {
			mPlayIL.play(mp,mUser);
			mState = Playing;
			mPlay.start();
		} else {
			mState = Prepared;
			Logger.i("PlayUtil","err=bbbbbb");
			stopInner(true,0);
		}
	}
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mState = Error;
		stopInner(false,what);
		return false;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		mState = Complete;
		Logger.i("PlayUtil","err=aaaaaa");
		stopInner(false,0);
	}
	private void createPlayer() {
		if (mPlay == null) {
			mPlay = new MediaPlayer();
		} else {
			mPlay.stop();
			mPlay.release();
			mPlay = null;
			mPlay = new MediaPlayer();
		}
		mState = Null;
		mPlay.setOnSeekCompleteListener(this);
		mPlay.setOnPreparedListener(this);
		mPlay.setOnErrorListener(this);
		mPlay.setOnCompletionListener(this);
	}
	/**
	 * 播放网络音频
	 */
	public MediaPlayer playInner(Context ctx, String uri, ILPlay lis, Object user, int pos) {
		if (TextUtils.isEmpty(uri)) return null;
		mPlayIL = lis;
		mUser = user;
		mLastPos = pos;
//		android.util.Log.e("uploadPlayTime","start="+pos);
		try {
			createPlayer();
			try {
				mState = Init;
				if (ctx==null) {
					mPlay.setDataSource(uri);
				} else {
					Uri parseUrl = Uri.parse(uri);
					mPlay.setDataSource(ctx, parseUrl);
				}
				mPlay.prepareAsync();
			} catch (Exception e) {
				mState = Error;
				stopInner(false, -100001);
				return null;
			}
		} catch (Exception e) {
			mState = Error;
			e.printStackTrace();
		}
		return mPlay;
	}

    public static MediaPlayer play(Context ctx, int resid){
        return getInstance().playInner(ctx, resid,null);
    }

    public MediaPlayer playInner(Context ctx, int resid, ILPlay lis){
        mPlayIL = lis;
        mUser = null;
        mLastPos = -1;
        try {
            createPlayer();
            try {
                AssetFileDescriptor afd = ctx.getResources().openRawResourceFd(resid);
                if (afd == null) return null;
	            mState = Init;
                mPlay.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
				if (mPlayIL == null) mPlayIL = new NullLis();
                mPlay.prepareAsync();
            } catch (Exception e) {
	            mState = Error;
                stopInner(false, -100001);
                return null;
            }
        } catch (Exception e) {
	        mState = Error;
            e.printStackTrace();
        }
        return mPlay;
    }

	public static boolean resume() {
		if (getInstance().mPlay==null) return false;
		getInstance().mPlay.start();
		return true;
	}

	public static void pause() {
		if (getInstance().mPlay==null) return;
		getInstance().mPlay.pause();
	}
	public static void stop(boolean bCancel) {
		if (mPlayUtil==null) return;
        mPlayUtil.stopInner(bCancel,0);
	}
	public static void stopAndUpload(boolean bCancel) {
		if (mPlayUtil==null) return;
		Object type = mPlayUtil.mUser;
	}
	/**
	 * 停止播放加载的网络音频
	 */
	public int stopInner(boolean bCancel,int err) {
		Logger.i("PlayUtil","err="+err);
		if (bCancel)
			mPlayIL = null;
		if (mPlay != null) {
			// 记录学习时间
			if (mLastPos>-1) {
				mLastPos = 0;
				if (mState>Prepared) {
					mLastPos = mPlay.getCurrentPosition();
					if (mLastPos>mPlay.getDuration()||mLastPos>600000) {
						mLastPos = 0;
					}
				}
			}
			mPlay.reset();
			mPlay.release();
			mState = Null;
			mPlay = null;
		}

		if (mPlayIL != null) {
			ILPlay cb = mPlayIL;
			mPlayIL = null;
			cb.stop(mUser,err);
		}
		err = mLastPos;
		mLastPos = -1;
		mUser = null;
		return err;
	}

}
