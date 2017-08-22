package xiaolei.plugintoolbox.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.android.ActivityEvent

import java.util.Arrays

import sunxl8.myutils.AppUtils
import sunxl8.myutils.FileUtils
import sunxl8.myutils.TimeUtils
import xiaolei.plugintoolbox.R
import xiaolei.plugintoolbox.base.BaseActivity

/**
 * Created by sunxl8 on 2017/8/17.
 */

class AppDetailActivity : BaseActivity() {

    private var mAppPkg: String? = null
    private var mInfo: PackageInfo? = null

    private var tvBasic: TextView? = null
    private var tvPermissions: TextView? = null
    private var tvActivities: TextView? = null
    private var tvServices: TextView? = null
    private var tvReceivers: TextView? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_appdetail
    }

    override fun init() {
        mAppPkg = intent.getStringExtra("package")
        try {
            mInfo = packageManager.getPackageInfo(mAppPkg, PackageManager.MATCH_UNINSTALLED_PACKAGES or
                    PackageManager.GET_PERMISSIONS or PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or PackageManager.GET_RECEIVERS or PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        initView()
    }

    private fun initView() {
        tvTitle?.text = AppUtils.getAppName(mInfo!!.packageName)
        tvBasic = findViewById(R.id.tv_appdetail_basic) as TextView
        tvBasic!!.text = "包名：" + mInfo!!.packageName + "\n"+
                "Apk大小：" + FileUtils.getFileSize(mInfo!!.applicationInfo.sourceDir) + "\n"+
                "版本名：" + mInfo!!.versionName + "\n"+
                "版本号：" + mInfo!!.versionCode + "\n"+
                "minSdkVersion：" + mInfo!!.applicationInfo.minSdkVersion + "\n"+
                "targetSdkVersion：" + mInfo!!.applicationInfo.targetSdkVersion + "\n"+
                "安装时间：" + TimeUtils.millis2String(mInfo!!.firstInstallTime) + "\n"+
                "更新时间：" + TimeUtils.millis2String(mInfo!!.lastUpdateTime) + "\n"+
                "SignatureSHA1：" + AppUtils.getAppSignatureSHA1(mAppPkg)
        tvPermissions = findViewById(R.id.tv_appdetail_permission) as TextView
        tvPermissions!!.text = "权限列表(" + (if (mInfo!!.requestedPermissions == null) 0 else mInfo!!.requestedPermissions.size) + ")"
        RxView.clicks(tvPermissions!!)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe {
                    if (mInfo!!.requestedPermissions != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tvPermissions!!.text.toString())
                                .adapter(DialogPermissionAdapter(Arrays.asList(*mInfo!!.requestedPermissions)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
        tvActivities = findViewById(R.id.tv_appdetail_activity) as TextView
        tvActivities!!.text = "注册的Activity(" + (if (mInfo!!.activities == null) 0 else mInfo!!.activities.size) + ")"
        RxView.clicks(tvActivities!!)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe { o ->
                    if (mInfo!!.activities != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tvActivities!!.text.toString())
                                .adapter(DialogActivityAdapter(Arrays.asList(*mInfo!!.activities)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
        tvServices = findViewById(R.id.tv_appdetail_service) as TextView
        tvServices!!.text = "注册的Service(" + (if (mInfo!!.services == null) 0 else mInfo!!.services.size) + ")"
        RxView.clicks(tvServices!!)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe { o ->
                    if (mInfo!!.services != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tvServices!!.text.toString())
                                .adapter(DialogServiceAdapter(Arrays.asList(*mInfo!!.services)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
        tvReceivers = findViewById(R.id.tv_appdetail_receiver) as TextView
        tvReceivers!!.text = "注册的Receiver(" + (if (mInfo!!.receivers == null) 0 else mInfo!!.receivers.size) + ")"
        RxView.clicks(tvReceivers!!)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe { o ->
                    if (mInfo!!.receivers != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tvReceivers!!.text.toString())
                                .adapter(DialogReceiverAdapter(Arrays.asList(*mInfo!!.receivers)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
    }

    internal inner class DialogPermissionAdapter(data: List<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_dialog, data) {

        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.tv_item, item)
        }

    }

    internal inner class DialogActivityAdapter(data: List<ActivityInfo>?) : BaseQuickAdapter<ActivityInfo, BaseViewHolder>(R.layout.item_dialog, data) {

        override fun convert(helper: BaseViewHolder, item: ActivityInfo) {
            var launchMode = ""
            when (item.launchMode) {
                0 -> launchMode = "standard"
                1 -> launchMode = "single_top"
                2 -> launchMode = "single_task"
                3 -> launchMode = "single_instance"
            }
            helper.setText(R.id.tv_item, "name: " + item.name + "\n" +
                    "launchMode: " + launchMode)
        }

    }

    internal inner class DialogServiceAdapter(data: List<ServiceInfo>?) : BaseQuickAdapter<ServiceInfo, BaseViewHolder>(R.layout.item_dialog, data) {

        override fun convert(helper: BaseViewHolder, item: ServiceInfo) {
            helper.setText(R.id.tv_item, item.name)
        }

    }

    internal inner class DialogReceiverAdapter(data: List<ActivityInfo>?) : BaseQuickAdapter<ActivityInfo, BaseViewHolder>(R.layout.item_dialog, data) {

        override fun convert(helper: BaseViewHolder, item: ActivityInfo) {
            helper.setText(R.id.tv_item, item.name)
        }

    }

    companion object {

        fun startThisActivity(context: Context, pkg: String) {
            val intent = Intent(context, AppDetailActivity::class.java)
            intent.putExtra("package", pkg)
            context.startActivity(intent)
        }
    }
}
