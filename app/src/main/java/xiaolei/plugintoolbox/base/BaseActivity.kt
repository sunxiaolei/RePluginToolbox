package xiaolei.plugintoolbox.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.TextView

import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.wang.avi.indicators.PacmanIndicator

import sunxl8.library.swipeback.SwipeBackActivityHelper
import sunxl8.library.swipeback.SwipeBackLayout
import sunxl8.library.swipeback.Utils
import xiaolei.library.LoadingDialog
import xiaolei.plugintoolbox.R


/**
 * Created by sunxl8 on 2017/8/2.
 */

abstract class BaseActivity : RxAppCompatActivity() {

    private var mLoadingDialog: LoadingDialog? = null

    protected abstract fun setContentViewId(): Int

    protected abstract fun init()

    private var mHelper: SwipeBackActivityHelper? = null

    protected var tvTitle: TextView? = null

    protected var ivBack: AppCompatImageView? = null
    protected var ivMenu: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper!!.onActivityCreate()
        setContentView(setContentViewId())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        tvTitle = findViewById(R.id.tv_title) as? TextView?
        ivBack = findViewById(R.id.iv_back) as? AppCompatImageView?
        ivMenu = findViewById(R.id.iv_menu) as? AppCompatImageView?
        if (ivBack != null) {
            RxView.clicks(ivBack!!)
                    .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                    .subscribe { finish() }
        }
        init()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper!!.onPostCreate()
    }

    override fun findViewById(id: Int): View? {
        val v = super.findViewById(id)
        return if (v == null && mHelper != null) mHelper!!.findViewById(id) else v
    }

    val swipeBackLayout: SwipeBackLayout
        get() = mHelper!!.swipeBackLayout

    fun setSwipeBackEnable(enable: Boolean) {
        this.swipeBackLayout.setEnableGesture(enable)
    }

    fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        this.swipeBackLayout.scrollToFinishActivity()
    }

    fun showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.Builder(this)
                    .setIndicator(PacmanIndicator())
                    .setIndicatorColor(resources.getColor(R.color.colorAccent))
                    .create()
        }
        mLoadingDialog!!.show()
    }

    fun dismissDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog!!.dismiss()
        }
    }

}
