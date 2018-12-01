package com.k12app.view;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.k12app.R;
import com.k12app.core.MainApplication;

import z.frame.ICommon;


public class MyUtil implements ICommon {

    public static final int DIFWEEK = 0;
    public static final int SAMEWEEK = 1;//同一周


    public static final int IS_TODAY = 0;//今天
    public static final int IS_TOMORROW = 1;//明天
    public static final int IS_YESTERDAY = -1;//昨天
    public static final int IS_OTHERDAY = -1000;//昨天

    public static final long ONE_DAY_MILLISECOND = 86400000;//一天的毫秒数

    public MyUtil() { }

    /**
     * @param packageName 包名
     * @return
     */
    public static void launchApp(String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return;
        }

        Intent intent = new Intent();
        PackageManager packageManager = MainApplication.mApplication.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MainApplication.mApplication.startActivity(intent);
    }


    /**
     * 传进来的日期是昨天、今天 or 明天
     *
     * @param date
     * @return
     */
    public static int isTodayOrYesterday(long date) {
        //所在时区时8，系统初始时间是1970-01-01 08:00:00，注意是从八点开始，计算的时候要加回去
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (System.currentTimeMillis() + offSet) / ONE_DAY_MILLISECOND;
        long start = (date + offSet) / ONE_DAY_MILLISECOND;
        long intervalTime = start - today;
        //-2:前天,-1：昨天,0：今天,1：明天,2：后天
        if (intervalTime == 0) {
            return IS_TODAY;
        } else if (intervalTime == 1) {
            return IS_TOMORROW;
        } else if (intervalTime == -1) {
            return IS_YESTERDAY;
        } else {
            return IS_OTHERDAY;
        }
    }


    /**
     * 两个时间段是否在同一周
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int isSameWeek(long time1, long time2) {

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        mCalendar.setTimeInMillis(time1);
        int weekNum1 = mCalendar.get(Calendar.WEEK_OF_YEAR);

        mCalendar.setTimeInMillis(time2);
        int weekNum2 = mCalendar.get(Calendar.WEEK_OF_YEAR);

        int difWeekNum = weekNum2 - weekNum1;

        if (difWeekNum == 0) {
            return SAMEWEEK;
        } else {
            return DIFWEEK;
        }

    }

    /**
     * @param oldBmp
     * @return 压缩图片
     */
    public static Bitmap zoomOutBitmap(Bitmap oldBmp, float xScale, float yScale) {
        if (oldBmp == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);
        Bitmap newBmp = Bitmap.createBitmap(oldBmp, 0, 0, oldBmp.getWidth(),
                oldBmp.getHeight(), matrix, true);
        return newBmp;

    }


    /**
     * 获取课程列表指定格式的日期--今天、明天、昨天 、周五 、7月20等
     *
     * @param courTime
     * @return
     */
    public static String getCourListParseDate(long courTime) {
        Calendar mCalendar = Calendar.getInstance();
        //加一个数，不然不起作用
        long currentMilliseconds = System.currentTimeMillis() + 2;
        //课程时间和今天是否在同一周内
        int isSameWeek = isSameWeek(currentMilliseconds, courTime);

//        Log.i("lyy", "isSameWeek >>>> " + isSameWeek);
        int isTodayOrYesterday = isTodayOrYesterday(courTime);
        if (isTodayOrYesterday == IS_TODAY) {//今天
            return "今天";
        }
        if (isTodayOrYesterday == IS_TOMORROW) {//明天
            return "明天";
        }

        mCalendar.setTimeInMillis(courTime);

        String parseStr = "";

        //是在同一周，显示周几
        if (isSameWeek == SAMEWEEK) {
            parseStr = (String) DateFormat.format("EE", mCalendar);
        } else {
            parseStr = (String) DateFormat.format("MM月dd日", mCalendar);
        }
        return parseStr;
    }

    /**
     * 获取时间 格式：hh:mm
     *
     * @param courTime
     * @return
     */
    public static String getTime(long courTime) {

//        Calendar mCalendar = Calendar.getInstance();
//        mCalendar.setTimeInMillis(courTime);
//        String timeStr = (String) DateFormat.format("HH:mm", mCalendar);

        //在小米2(4.1.1)手机上，上面的方法不起作用
        Date date = new Date();
        date.setTime(courTime);
        //Locale.getDefault() = us,导致有些用户手机非中国时区，时间格式化错误
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeStr = sdf.format(date);

        return timeStr;
    }


    //设置字体大小span

    public static SpannableString getRelativeLayoutSpan(String strContent, int start, int end, float textSize) {

        SpannableString ss = new SpannableString(strContent);

        try {
            ss.setSpan(new RelativeSizeSpan(textSize), start,
                    end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ss;
    }


    //设置颜色span

    public static SpannableString getForegroundColorSpan(String strContent, int start, int end, int color) {

        SpannableString ss = new SpannableString(strContent);

        try {
            ss.setSpan(new ForegroundColorSpan(color), start,
                    end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ss;
    }

    //从本地读取文件
    public static byte[] imageToByteArray(String imgPath) {
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(imgPath));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int size = 0;
            byte[] temp = new byte[1024];
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //正方形居中的toast
    public static void showSquareToast(String txt) {

        View view = View.inflate(app.ctx, R.layout.toast_square, null);
        TextView mTvContent = (TextView) view.findViewById(R.id.tv_toast_cont);
        mTvContent.setText(TextUtils.isEmpty(txt) ? "成功" : txt);

        Toast toast = new Toast(app.ctx);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;

    /**
     * 判断是是否有录音权限
     */
    public static boolean isHasPermission() {
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }


}
