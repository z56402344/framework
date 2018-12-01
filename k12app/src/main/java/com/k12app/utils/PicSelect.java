package com.k12app.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.k12app.R;
import com.k12lib.afast.utils.BitmapUtils;

import z.frame.BaseFragment;
import z.frame.ICommon;


/**
 * 设置头像公共方法
 */
public class PicSelect implements View.OnClickListener, ICommon {
    public static final int Camera = 100;//相机
    public static final int Album = 101;//相册
    public static final int Crop = 102;//相机剪裁
    public static final int Delete = 103;//删除照片
    public static final int Album_19 = 104;//相册剪裁 api>19
    public static final String File = "image.jpg";
    public static final String MsgBGFile = "msgbg.jpg";


    private int mCropW = 480; // 裁剪的头像的宽
    private int mCropH = 480; // 裁剪的头像的高
    public int mType = 0;//0==设置头像,1==设置照片
    public java.io.File mPhotoFile;//头像文件
    public java.io.File mTempFile;
    private BaseFragment mBfrag;
    private boolean mIsCrop = true;
    public PopupWindow mPopWin;

    public PicSelect(BaseFragment bf){
        mBfrag = bf;
    }

    public PicSelect setType(int type){
        mType = type;
        return this;
    }

    public PicSelect setCrop(boolean isCrop){
        mIsCrop = isCrop;
        return this;
    }

    public void setCropWH(int w,int h){
        mCropW = w;
        mCropH = h;
    }

    public void setFile(java.io.File f){
        mPhotoFile = f;
        if (f == null)  {
            mTempFile = null;
            return;
        }
        mTempFile = new File(f.getAbsolutePath()+".1");
        f = f.getParentFile();
        if (f != null && !f.exists()){
            f.mkdirs();
        }
    }

    public void deletePhotoFile() {
        if (mPhotoFile != null && mPhotoFile.exists()) {
            mPhotoFile.delete();
        }
    }

    private boolean tmp2Photo() {
        if (mTempFile != null && mPhotoFile != null && mTempFile.exists()) {
            try {
                if (mPhotoFile.exists()) {
                    mPhotoFile.delete();
                }
                mTempFile.renameTo(mPhotoFile);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case Camera:
            // 拍照
            if (tmp2Photo()) {
                if (!mIsCrop) return 0;
                startPhotoZoom(Uri.fromFile(mPhotoFile));
            }
            return 1;
        case Album:
            // 相册
            if (tmp2Photo()) {
                return 0;
            }
            if (getBitmp(data)){
                return 0;
            }
            return 1;
        case Crop:
            // 将进行剪裁后的图片传递到下一个界面上
            if (tmp2Photo()) {
                return 0;
            }
            if (getBitmp(data)){
                return 0;
            }
            return 1;
        case Album_19:{
            // 选择拍照
            if (mPhotoFile == null||data==null)return 1;
            String uri = getPath(mBfrag.getActivity().getContentResolver(),data.getData());
            if (TextUtils.isEmpty(uri))return 1;
            startPhotoZoom(Uri.fromFile(new File(uri)));
            return 1;}
        }
        return 2;
    }

    private boolean getBitmp(Intent data) {
        if (mTempFile != null && !mTempFile.exists() && mPhotoFile != null && data != null) {
            //这个逻辑是MI3调用相册时mTempFile并不存在导致无法正常获取图片的逻辑
            Bitmap bitmap;
            try{
                bitmap = data.getParcelableExtra("data");
                if (bitmap == null)return false;
                BitmapUtils.saveBitmap2Local(mPhotoFile,bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                bitmap = null;
            }
            return true;
        }
        return false;
    }

    /** 开启相机 */
    public void startCamera() {
        if (mTempFile!=null&&mTempFile.exists()) {
            mTempFile.delete();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
        mBfrag.startActivityEx(intent,Camera);
    }

    /** 开启相册 */
    @SuppressLint("InlinedApi")
    public void startAlbum() {
        if (mTempFile!=null&&mTempFile.exists()) {
            mTempFile.delete();
        }
        int type = 0;
        Intent i = new Intent();
        //VIVO 手机相册选择，直接裁剪后crash的问题，用新api调用美柚问题
        if (Build.VERSION.SDK_INT > 19 || (Build.VERSION.SDK_INT >= 19 && Build.MANUFACTURER != null && (Build.MANUFACTURER.compareToIgnoreCase("vivo") == 0 || Build.MANUFACTURER.compareToIgnoreCase("xiaomi") == 0))){
            if (Build.MANUFACTURER.compareToIgnoreCase("xiaomi") == 0){
                i.setAction(Intent.ACTION_PICK);
            }else{
                i.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
            i.setType("image/*");
            type = Album_19;
        }else{
            i.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
            i.putExtra("output", Uri.fromFile(mTempFile));
            i.putExtra("crop", "true");
            i.setAction(Intent.ACTION_PICK);
            if (mType == 0){
                i.putExtra("aspectX", 1);// 裁剪框比例
                i.putExtra("aspectY", 1);
                i.putExtra("outputX", mCropW);// 输出图片大小
                i.putExtra("outputY", mCropH);
            }
            type = Album;
        }

        mBfrag.startActivityEx(i, type);
    }

    private void startPhotoZoom(Uri uri) {
        if (mTempFile!=null&&mTempFile.exists()) {
            mTempFile.delete();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", mCropW);
        intent.putExtra("outputY", mCropH);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //intent.putExtra("output", Uri.fromFile(mTempFile));
        mBfrag.startActivityEx(intent, Crop);
    }

	// 检查cursor是否有效数据
	private static String checkCursor(Cursor cursor, String[] column) {
		String res = null;
		try {
			if (cursor!=null&& cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(column[0]);
				res = cursor.getString(columnIndex);
				return checkPath(res);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			Util.closeSafe(cursor);
		}
		return null;
	}
	// 检查文件是否存在
	private static String checkPath(String res) {
		try {
			if (!TextUtils.isEmpty(res)&&new File(res).exists()) return res;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

    public static String getPath(ContentResolver cr, Uri uri) {
        if(uri == null) {
            return null;
        }
	    String res = null;
	    // 因系统存在不一致性 尝试用多种方式解决路径问题
	    // 1.拼id+cr查方式
	    // 2.直接cr查
	    // 3.直接转换文件路径并检测
	    String[] column = { MediaStore.Images.Media.DATA };
	    // 1.
	    try {
		    String wholeID = DocumentsContract.getDocumentId(uri);
		    String[] id = wholeID.split(":");
		    if (id!=null&&id.length>1) {
			    id = new String[] {id[1]};
			    res = checkCursor(cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, MediaStore.Images.Media._ID + "=?", id, null),column);
			    if (res!=null) return res;
		    }
	    } catch (Throwable e) {
		    e.printStackTrace();
	    }

	    // 2.
	    try { // 避免某些系统上的异常
		    res = checkCursor(cr.query(uri, column, null, null, null),column);
		    if (res!=null) return res;
	    } catch (Throwable e) {
		    e.printStackTrace();
	    }

	    // 3.
	    return checkPath(uri.getPath());
    }

    /**
     * 弹出选择框
     */
    public void showSelectMenu(boolean isDelete,View view) {
//        View view = View.inflate(getActivity(), R.layout.pop_person_menu, null);
        View v = View.inflate(mBfrag.getActivity(), R.layout.pop_photo_menu, null);
        if (isDelete){
	        Util.setVisible(v,R.id.tv_delete, View.VISIBLE);
	        Util.setVisible(v,R.id.line, View.VISIBLE);
            Util.setOnClick(v, R.id.tv_delete, this);
        }
        Util.setOnClick(v, R.id.tv_camera, this);
        Util.setOnClick(v, R.id.tv_album, this);
        Util.setOnClick(v, R.id.mRlMenu, this);
        Util.setOnClick(v, R.id.bt_cancle, this);

        v.startAnimation(AnimationUtils.loadAnimation(mBfrag.getActivity(), R.anim.fade_in));
        LinearLayout ll_popup = (LinearLayout) v.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mBfrag.getActivity(),R.anim.push_bottom_in_yiyue));

        if (mPopWin == null) {
            mPopWin = new PopupWindow(mBfrag.getActivity());
            mPopWin.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopWin.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopWin.setBackgroundDrawable(new BitmapDrawable());
            mPopWin.setFocusable(true);
            mPopWin.setOutsideTouchable(true);
        }

        mPopWin.setContentView(v);
        mPopWin.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mPopWin.update();
    }

    public void onClick(View v) {
        switch (v.getId()){
        case R.id.mRlMenu:
            if (mPopWin.isShowing()) {
                mPopWin.dismiss();
            }
            break;
        case R.id.tv_delete:
            mBfrag.commitAction(Delete,0,null,0);
            if (mPopWin.isShowing()) {
                mPopWin.dismiss();
            }
            break;
        case R.id.tv_camera: // 拍照选取
            startCamera();
            if (mPopWin.isShowing()) {
                mPopWin.dismiss();
            }
            break;
        case R.id.tv_album:// 相册选取
            startAlbum();
            if (mPopWin.isShowing()) {
                mPopWin.dismiss();
            }
            break;
        case R.id.bt_cancle: // 取消popu
            if (mPopWin !=null&& mPopWin.isShowing()) mPopWin.dismiss();
            break;
        }
    }
}
