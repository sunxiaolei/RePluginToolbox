package xiaolei.plugintoolbox.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.wang.avi.indicators.BallBeatIndicator;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;
import com.wang.avi.indicators.PacmanIndicator;

import sunxl8.library.swipeback.SwipeBackActivityHelper;
import sunxl8.library.swipeback.SwipeBackLayout;
import sunxl8.library.swipeback.Utils;
import xiaolei.library.LoadingDialog;
import xiaolei.plugintoolbox.R;


/**
 * Created by sunxl8 on 2017/8/2.
 */

public abstract class BaseActivity extends RxAppCompatActivity {

    private LoadingDialog mLoadingDialog;

    protected abstract int setContentViewId();

    protected abstract void init();

    private SwipeBackActivityHelper mHelper;

    protected TextView tvTitle;

    protected AppCompatImageView ivBack, ivMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mHelper = new SwipeBackActivityHelper(this);
        this.mHelper.onActivityCreate();
        setContentView(setContentViewId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (AppCompatImageView) findViewById(R.id.iv_back);
        ivMenu = (AppCompatImageView) findViewById(R.id.iv_menu);
        if (ivBack != null) {
            RxView.clicks(ivBack)
                    .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(o -> finish());
        }
        init();
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.mHelper.onPostCreate();
    }

    public View findViewById(int id) {
        View v = super.findViewById(id);
        return v == null && this.mHelper != null ? this.mHelper.findViewById(id) : v;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return this.mHelper.getSwipeBackLayout();
    }

    public void setSwipeBackEnable(boolean enable) {
        this.getSwipeBackLayout().setEnableGesture(enable);
    }

    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        this.getSwipeBackLayout().scrollToFinishActivity();
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog.Builder(this)
                    .setIndicator(new PacmanIndicator())
                    .setIndicatorColor(getResources().getColor(R.color.colorAccent))
                    .create();
        }
        mLoadingDialog.show();
    }

    public void dismissDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

}
