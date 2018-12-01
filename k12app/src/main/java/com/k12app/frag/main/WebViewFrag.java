package com.k12app.frag.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.k12app.R;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpIFCtx;
import com.k12app.utils.AnimUtil;

import z.frame.UmBuilder;
import z.http.ZCookieUtil;
import z.http.ZHttpCenter;
import z.http.ZHttpParams;

// 通用webView页面
public class WebViewFrag extends TitleFrag implements IAct {
    public static final int FID = 3300;
    public static final int IA_LoadUrl = FID + 1;
    public static final int IA_SHOW_EVALUATE = FID + 2;//显示评价di

    public static final int TYPE_HIDE_SHARE = 0;//隐藏分享按钮

    public static final String kTitle = "title"; // 设置标题
    public static final String kUrl = "url"; // 设置url
    public static final String kShareImg = "bannerImg"; //分享的image
    public static final String kShareUrl = "shareUrl";
    public static final String kShareContent = "shareContent"; // 分享content
    public static final String kFlags = "flags"; // 设置flags
    public static final String kPayFrom = "from";
    public static final String kShowRightTxt = "showRightTxt";//是否显示右侧文字

    public static final int fCookies = 0x01; // 需要同步cookies
    public static final int fTitle = 0x02; // 需要同步title
    public static final int fShowClose = 0x04; // 需要显示关闭按钮
    //    public static final int fBuy = 0x06; // 展示购买逻辑
    public static final int fEnterHome = 0x08; // 返回到首页或者Guide页面
    public static final int fUserInfo = 0x10; // url连接需要带上用户信息

    public WebView mWb;
    public ImageView mIvLoading;
    public int mFlags = 0;
    public int mType = 0;
    public String mUrl;
    public String mShareTitle;
    public String mShareContent;
    public String mShareImage;
    public int mBoughtFrom = -1;
    public boolean isEnterHome = false;//是否需要返回到首页
    private String mShowRightTxt;
    private TextView mRightTxt;
    private ImageView mIvShare;
    private String mShareUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot == null) {
            try {
                mRoot = inflater.inflate(R.layout.frg_webview, null);
            } catch (Exception e) {
                e.printStackTrace();
                pop(true);
                return null;
            }
            setWebConfig();
            Bundle args = getArguments();
            if (args != null) {
                mShareTitle = args.getString(kTitle);
                mShareContent = args.getString(kShareContent);
                mShareImage = args.getString(kShareImg);
                mShareUrl = args.getString(kShareUrl);
                setTitleText(mShareTitle);
                setName(mShareTitle);
                mUrl = args.getString(kUrl);
                mFlags = args.getInt(kFlags);
                mType = args.getInt(kType, 0);
                mBoughtFrom = args.getInt(kPayFrom);
                mShowRightTxt = args.getString(kShowRightTxt);

                if (!TextUtils.isEmpty(mShowRightTxt)) {
                    Util.setText(mRoot, R.id.tv_right, mShowRightTxt);
                    Util.setVisible(mRoot, R.id.tv_right, View.VISIBLE);
                }

                if (mType == TYPE_HIDE_SHARE) {
                    Util.setVisible(mRoot, R.id.mIvShare, View.GONE);
                } else {
                    Util.setVisible(mRoot, R.id.mIvShare, View.VISIBLE);
                }
                try {
                    if ((mFlags & fCookies) == fCookies) {
                        ZCookieUtil.syncWebViewCookies(mUrl, ZHttpCenter.getCookies());
                    } else {
                        ZCookieUtil.syncWebViewCookies(mUrl, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if ((mFlags & fEnterHome) == fEnterHome) {
                    isEnterHome = true;
                }

                if ((mFlags & fUserInfo) == fUserInfo) {
                    int isSupportWx = UmBuilder.isWXAppInstalledAndSupported(getActivity()) ? 1 : 0;
                    ZHttpParams p = new ZHttpParams();
                    p.put("unWeixin", isSupportWx);
                    HttpIFCtx.instance().addPublicParams(p);
                    mUrl += "?" + p.toOrderString();
                }

                _log("打开Web页面:title=" + mShareTitle + "," + mUrl);
                try {
                    mWb.loadUrl(mUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                pop(true);
            }
        }
        return mRoot;
    }

    public void setWebConfig() {

        registerLocal(IA_SHOW_EVALUATE);

        mWb = (WebView) findViewById(R.id.mWb);
        mRightTxt = (TextView) findViewById(R.id.tv_right);
        mIvShare = (ImageView) findViewById(R.id.mIvShare);

        mIvLoading = (ImageView) findViewById(R.id.mIvLoading);
        WebSettings ws = mWb.getSettings();
        try {
            //不屏蔽混合内容
            //5.0以上加载HttpsUrl中，http链接的图片默认不加载，需要修改下面的方法
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 是否允许脚本支持
        try {
            ws.setJavaScriptEnabled(true);//设置webview支持javascript
//			ws.setJavaScriptCanOpenWindowsAutomatically(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setAppCacheEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setAppCacheMaxSize(102400);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setDatabaseEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setDomStorageEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setLoadsImagesAutomatically(true);    //支持自动加载图片
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setUseWideViewPort(true);    //设置webview推荐使用的窗口，使html界面自适应屏幕
//				ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
//				mWb.setInitialScale(100);
//				ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕，内容将自动缩放
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setLoadWithOverviewMode(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setSaveFormData(true);    //设置webview保存表单数据
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setSavePassword(false);    //设置webview保存密码
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);    //设置中等像素密度，medium=160dpi
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setSupportZoom(true);    //支持缩放
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ws.setPluginState(WebSettings.PluginState.ON_DEMAND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Technical settings
        try {
            ws.setSupportMultipleWindows(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //设置定位的数据库路径
            String dir = getActivity().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
            ws.setGeolocationDatabasePath(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //启用地理定位
            ws.setGeolocationEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            //开启DomStorage缓存
            ws.setDomStorageEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

//			CookieManager.getInstance().setAcceptCookie(true);
        // 是否允许缩放
        ws.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > 10) {
            ws.setDisplayZoomControls(false);
        }

        try {
            mWb.setDrawingCacheEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mWb.setWebViewClient(wvc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mWb.setWebChromeClient(wcc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mWb.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                            String mimetype, long contentLength) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivityEx(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left: // 默认返回上个界面
                if (mWb.canGoBack()) {
                    try {
                        mWb.goBack();
                    } catch (Exception e) {
                        e.printStackTrace();
                        pop(true);
                    }
                    // 返回键退回
                } else {
                    if (isEnterHome) {
                        notifyActivity(SplashFrag.FExit, 0, null);
                    } else {
                        pop(true);
                    }
                }
                break;
            case R.id.mIvShare: //分享
               break;
            case R.id.tv_close: // 关闭页面
                pop(true);
                break;
//            case R.id.mRlNetError: //刷新页面
//                if (mRlNetErr != null) {
//                    Util.setParent(mRlNetErr, null, null);
//                    mRlNetErr = null;
//                }
//                mWb.setVisibility(View.VISIBLE);
//                mWb.reload();
//
//                break;
            default:
                break;
        }
    }

    /**
     * 按键响应，在WebView中查看网页时，按返回键的时候按浏览历史退回,如果不做此项处理则整个WebView返回退出
     */
    @Override
    public boolean onBackPressed() {
        // Check if the key event was the Back button and if there's history
        try {
            if (mWb.canGoBack()) {
                mWb.goBack();
                // 返回键退回
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isEnterHome) {
            notifyActivity(SplashFrag.FExit, 0, null);
        }
        // If it wasn't the Back key or there's no web page history, bubble up
        // to the default
        // system behavior (probably exit the com.k12app.activity)
        return false;
    }

    private WebViewClient wvc = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			view.loadUrl(url);
            return shouldOverrideUrl(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            AnimUtil.clearRotaAnim(mIvLoading, null);
            if (url.contains("dbbackrootrefresh")
                    || url.contains("dbbackroot")) { // 回到积分商城首页 并刷新
                view.clearHistory();
            }

            if ((mFlags & fTitle) == fTitle) {
                setTitleText(view.getTitle());
            }
            Uri uri = Uri.parse(url);
            String path = uri.getPath();

            if ("/wapnew/Service/hotQuestion".equals(path)) {
                Util.setVisible(mRoot, R.id.tv_right, View.VISIBLE);
            } else {
                Util.setVisible(mRoot, R.id.tv_right, View.GONE);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            AnimUtil.startRotaAnim(mIvLoading, null);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            showNetError();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            //handler.cancel(); 默认的处理方式，WebView变成空白页
            handler.proceed();//接受证书
        }
    };

    public boolean replaceAndLoad(WebView view, String url, String path, String key) {
        String str;
        if (path.contains(str = "?" + key)) {
            url = url.replace(str, "");
        } else if (path.contains(str = "&" + key)) {
            url = url.replace(str, "");
        } else return false;
//		AnimUtil.startRotaAnim(mIvLoading, null);
//		fireUrl(url);
//		view.loadUrl(url);
        return true;
    }

    /**
     * 拦截url请求，根据url结尾执行相应的动作。 （重要）
     * 用途：模仿原生应用体验，管理页面历史栈。
     *
     * @return
     */
    protected boolean shouldOverrideUrl(WebView view, String url) {
        _log("shouldOverrideUrl:" + url);
        Uri uri = Uri.parse(url);
        // 处理电话链接，启动本地通话应用。
        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivityEx(intent);
            return true;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        String path = uri.getPath();

        // 截获页面分享请求，分享数据
        if ("/client/dbshare".equals(path)) {
//			String content = uri.getQueryParameter("content");
//			if (creditsListener != null && content != null) {
//				String[] dd = content.split("\\|");
//				if (dd.length == 4) {
//					setShareInfo(dd[0], dd[1], dd[2], dd[3]);
//					mShare.setVisibility(View.VISIBLE);
//					mShare.setClickable(true);
//				}
//			}
        } else if ("/client/dblogin".equals(path)) {
            // 截获页面唤起登录请求。（目前暂时还是用js回调的方式，这里仅作预留。）
            return true;
        } else if (replaceAndLoad(view, url, path, "dbnewopen")) { // 新开页面

        } else if (path.contains("dbbackrefresh")
                || path.contains("dbback")) { // 后退并刷新
            if (view.canGoBack()) {
                view.goBack();
            }
            return true;
        } else if (path.contains("dbbackrootrefresh")
                || path.contains("dbbackroot")) { // 回到积分商城首页 并刷新
            view.clearHistory();
//			fireUrl(url);
//			view.loadUrl(url);
        } else {
            if (url.endsWith(".apk") || url.contains(".apk?")) { // 支持应用链接下载
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (!startActivityEx(viewIntent)) {
                    // 调用下载
                }
                return true;
            } else {
//				fireUrl(url);
//				view.loadUrl(url);
            }
        }
        return false;
    }

    private void fireUrl(String url) {
        commitAction(IA_LoadUrl, 0, url, 500);
    }

    private WebChromeClient wcc = new WebChromeClient() {

        public void onRequestFocus(WebView view) {
            super.onRequestFocus(view);
            view.requestFocus();
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            if ((mFlags & fTitle) == fTitle) {
                setTitleText(title);
                mShareTitle = title;
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            //地理位置回调
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

    };

    public static WebViewFrag create(String title, String url, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFrag.kTitle, title);
        bundle.putString(WebViewFrag.kUrl, url);
        bundle.putInt(WebViewFrag.kFlags, flags);
        WebViewFrag qf = new WebViewFrag();
        qf.setArguments(bundle);
        return qf;
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
            case IA_LoadUrl:
                mWb.loadUrl((String) extra);
                break;
            case IA_SHOW_EVALUATE:
//                if (extra instanceof EvaluateCourBean) {
//                    EvaluateCourBean evaluateBean = (EvaluateCourBean) extra;
//                    new BaseFragment.Builder(this, new EvaluateCourFrag())
//                            .with(EvaluateCourFrag.KEY_EVALUATE_BEAN, evaluateBean)
//                            .with(CourManagerFrag.KEY_COUR_ID, evaluateBean.courseId)
//                            .with(EvaluateCourFrag.KEY_IN_TYPE, EvaluateCourFrag.TYPE_FROM_CLASS).add();
//                }
                break;
            case IA_ShareRes: // 分享结果
                if (arg == TCancel) {
                    showShortToast("取消分享");
                } else if (arg == TFail) {
                    showShortToast("分享失败");
                } else {
                    // 分享成功的回调逻辑
//                    MyUtil.showSquareToast("分享成功");
                }
                break;
            default:
                super.handleAction(id, arg, extra);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                if (mWb != null) mWb.onPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                if (mWb != null) mWb.onResume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (mWb != null) mWb.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private RelativeLayout mRlNetErr;

    private void showNetError() {
//        if (mRoot == null) return;
//        if (mRlNetErr == null) {
//            mRlNetErr = (RelativeLayout) View.inflate(
//                    mRoot.getContext(),
//                    R.layout.network_error, null);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//            ((RelativeLayout) mRoot).addView(mRlNetErr, lp);
//            View mRlNetError = mRlNetErr.findViewById(R.id.mRlNetError);
//            TextView mTvNetError = (TextView) mRlNetErr.findViewById(R.id.mTvNetError);
//            TextView mTvNetError2 = (TextView) mRlNetErr.findViewById(R.id.mTvNetError2);
//            mTvNetError.setTextColor(Color.BLACK);
//            mTvNetError2.setTextColor(Color.BLACK);
//
//            mRlNetError.setOnClickListener(this);
//            Util.setText(mRlNetErr, R.id.mTvNetError, "加载数据失败");
//            Util.setText(mRlNetErr, R.id.mTvNetError2, "点击重新加载");
//        }
//        if (mWb != null) mWb.setVisibility(View.GONE);
//        mRlNetErr.setVisibility(View.VISIBLE);
    }


}
