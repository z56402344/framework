package com.k12app.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;


import com.k12app.view.RoundedBitmapCustomDisplayer;

import z.image.universal_image_loader.core.DisplayImageOptions;
import z.image.universal_image_loader.core.ImageLoader;
import z.image.universal_image_loader.core.display.FadeInBitmapDisplayer;
import z.image.universal_image_loader.core.display.RoundedBitmapDisplayer;
import z.image.universal_image_loader.core.display.SimpleBitmapDisplayer;
import z.image.universal_image_loader.core.listener.ImageLoadingListener;
import z.image.universal_image_loader.core.listener.SimpleImageLoadingListener;

/**
 * 说明：ImageLode的工具类
 *
 * @author Duguang
 * @version 创建时间：2014-12-26  下午1:41:13
 */
public class ImageLoderUtil {

    public static ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();
    public static ImageLoader mImageLoader = ImageLoader.getInstance();


    public static DisplayImageOptions setImageRoundLogder(int draw, int cornerRadiusPixels) {
        return setImageRoundLogder(draw, draw, draw, cornerRadiusPixels);
    }

    /**
     * @param lodingDraw
     * @param emptyDraw
     * @param errorDraw
     * @param cornerRadiusPixels 4个圆角角度
     * @return
     */
    public static DisplayImageOptions setImageRoundLogder(int lodingDraw, int emptyDraw, int errorDraw, int cornerRadiusPixels) {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        return new DisplayImageOptions.Builder().showStubImage(lodingDraw) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(emptyDraw) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(errorDraw) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(cornerRadiusPixels)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }


    public static DisplayImageOptions setImageCustomRoundLogder(int draw, int zs, int ys, int zx, int yx) {
        return setImageCustomRoundLogder(draw, draw, draw, zs, ys, zx, yx);
    }

    /**
     * @param lodingDraw
     * @param emptyDraw
     * @param errorDraw
     * @param zs         左上角角度
     * @param ys         右上角角度
     * @param zx         左下角角度
     * @param yx         右下角角度
     * @return
     */
    public static DisplayImageOptions setImageCustomRoundLogder(int lodingDraw, int emptyDraw, int errorDraw, int zs, int ys, int zx, int yx) {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        return new DisplayImageOptions.Builder().showStubImage(lodingDraw) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(emptyDraw) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(errorDraw) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapCustomDisplayer(zs, ys, zx, yx)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public static DisplayImageOptions setImageLogder(int draw) {
        return setImageLogder(draw, draw, draw);
    }

    public static DisplayImageOptions setImageLogder(int lodingDraw, int emptyDraw, int errorDraw) {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        return new DisplayImageOptions.Builder().showStubImage(lodingDraw) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(emptyDraw) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(errorDraw) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public static DisplayImageOptions setImage(int lodingDraw) {
        return new DisplayImageOptions.Builder().showStubImage(lodingDraw) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(lodingDraw) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(lodingDraw) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new SimpleBitmapDisplayer())
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public static void displayImageFirst(String url, ImageView iv, DisplayImageOptions options) {
        mImageLoader.displayImage(url, iv, options, mAnimateFirstListener);
    }

    public static void displayImage(int resId, ImageView iv, DisplayImageOptions options) {
        mImageLoader.displayImage("drawable://" + resId, iv, options);
    }

    /**
     * 图片加载第一次显示监听器
     *
     * @author Administrator
     */
    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
