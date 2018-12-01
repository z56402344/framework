package z.adapter;

import android.view.View;
import android.view.ViewGroup;

import z.adapter.ViewHolderFactory;

/**
 * Created by "wapilyded" on 2016/8/4.
 * 占位用的Facy  传进来一个view占位用， 主要用于不需要复用的header，footer
 */
public class PlaceViewHolderFacy extends ViewHolderFactory {

    public View mView;
    public PlaceViewHolderFacy(View v){
        mView = v;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        return mView;
    }
}
