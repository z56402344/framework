package com.k12app.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;


import z.draw.BaseDrawable;
import z.image.universal_image_loader.core.assist.LoadedFrom;
import z.image.universal_image_loader.core.display.BitmapDisplayer;
import z.image.universal_image_loader.core.imageaware.ImageAware;

/**
 * Created by duguang on 15-9-6.
 */
public class RoundedBitmapCustomDisplayer implements BitmapDisplayer {

    protected final int margin;
    private int zs;
    private int ys;
    private int zx;
    private int yx;

    /**
     * @param zs 左上角角度
     * @param ys 右上角角度
     * @param zx 左下角角度
     * @param yx 右下角角度
     */
    public RoundedBitmapCustomDisplayer(int zs, int ys, int zx, int yx) {
        this(zs, ys, zx, yx, 0);
    }

    public RoundedBitmapCustomDisplayer(int zs, int ys, int zx, int yx, int marginPixels) {
        this.zs = zs;
        this.ys = ys;
        this.zx = zx;
        this.yx = yx;
        this.margin = marginPixels;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
//        if (!(imageAware instanceof ImageViewAware)) {
//            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
//        }

        imageAware.setImageDrawable(new RoundedDrawable(bitmap, zs, ys, zx, yx, margin));
    }

    public static class RoundedDrawable extends BaseDrawable {

        protected final int margin;

        protected final RectF mBitmapRect;
        protected final BitmapShader bitmapShader;
        public float radii[] = new float[8];
        public Path mPath;

        public RoundedDrawable(Bitmap bitmap, int zs, int ys, int zx, int yx, int margin) {
            this.margin = margin;
            radii[0] = radii[1] = zs;
            radii[2] = radii[3] = ys;
            radii[4] = radii[5] = zx;
            radii[6] = radii[7] = yx;

            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapRect = new RectF(margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);

            mPaint.setAntiAlias(true);
            mPaint.setShader(bitmapShader);
            mPaint.setFilterBitmap(true);
            mPaint.setDither(true);

            mPath = new Path();
        }

//        @Override
//        public int getIntrinsicWidth() {
//            return (int)mBitmapRect.width();
//        }
//        @Override
//        public int getIntrinsicHeight() {
//            return (int)mBitmapRect.height();
//        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRectF.set(margin, margin, bounds.width() - margin, bounds.height() - margin);

            // Resize the original bitmap to fit the new bound
            Matrix shaderMatrix = new Matrix();
            float rateW = mRectF.width() / mBitmapRect.width();
            float rateH = mRectF.height() / mBitmapRect.height();
            if (rateW >= rateH) {
                shaderMatrix.setScale(rateW, rateW);
                shaderMatrix.postTranslate(0, -(mBitmapRect.height() * rateW - mRectF.height()) * 0.5f);
            } else {
                shaderMatrix.setScale(rateH, rateH);
                shaderMatrix.postTranslate(-(mBitmapRect.width() * rateH - mRectF.width()) * 0.5f, 0);
            }
            bitmapShader.setLocalMatrix(shaderMatrix);
//	        float b = mBitmapRect.bottom;
//	        float h = mRect.height()*mBitmapRect.width()/mRect.width();
//	        if (h<mBitmapRect.height()) {
//		        mBitmapRect.top += (h-mBitmapRect.height())/2;
//		        mBitmapRect.bottom = mBitmapRect.top+h;
//	        }
//            shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
//            bitmapShader.setLocalMatrix(shaderMatrix);
//	        mBitmapRect.top = margin;
//	        mBitmapRect.bottom = b;
            mPath.reset();
            mPath.addRoundRect(mRectF, radii, Path.Direction.CCW);
        }

        @Override
        public void draw(Canvas canvas) {
//            canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
            canvas.drawPath(mPath, mPaint);
        }
    }
}
