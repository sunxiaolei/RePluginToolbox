package xiaolei.plugintoolbox.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.support.v7.widget.LinearLayoutManager

import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.android.ActivityEvent
import kotlinx.android.synthetic.main.activity_appdetail.*

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
        tv_basic.text = "包名：" + mInfo!!.packageName + "\n" +
                "Apk大小：" + FileUtils.getFileSize(mInfo!!.applicationInfo.sourceDir) + "\n" +
                "版本名：" + mInfo!!.versionName + "\n" +
                "版本号：" + mInfo!!.versionCode + "\n" +
                "minSdkVersion：" + mInfo!!.applicationInfo.minSdkVersion + "\n" +
                "targetSdkVersion：" + mInfo!!.applicationInfo.targetSdkVersion + "\n" +
                "安装时间：" + TimeUtils.millis2String(mInfo!!.firstInstallTime) + "\n" +
                "更新时间：" + TimeUtils.millis2String(mInfo!!.lastUpdateTime) + "\n" +
                "SignatureSHA1：" + AppUtils.getAppSignatureSHA1(mAppPkg)
        tv_permission.text = "权限列表(" + (if (mInfo!!.requestedPermissions == null) 0 else mInfo!!.requestedPermissions.size) + ")"
        RxView.clicks(tv_permission)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe {
                    if (mInfo!!.requestedPermissions != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tv_permission.text.toString())
                                .adapter(DialogPermissionAdapter(Arrays.asList(*mInfo!!.requestedPermissions)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
        tv_activity.text = "注册的Activity(" + (if (mInfo!!.activities == null) 0 else mInfo!!.activities.size) + ")"
        RxView.clicks(tv_activity)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe {
                    if (mInfo!!.activities != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tv_activity.text.toString())
                                .adapter(DialogActivityAdapter(Arrays.asList(*mInfo!!.activities)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
        tv_service.text = "注册的Service(" + (if (mInfo!!.services == null) 0 else mInfo!!.services.size) + ")"
        RxView.clicks(tv_service)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe {
                    if (mInfo!!.services != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tv_service.text.toString())
                                .adapter(DialogServiceAdapter(Arrays.asList(*mInfo!!.services)),
                                        LinearLayoutManager(this@AppDetailActivity))
                                .positiveText("确定")
                                .build()
                                .show()
                    }
                }
        tv_receiver.text = "注册的Receiver(" + (if (mInfo!!.receivers == null) 0 else mInfo!!.receivers.size) + ")"
        RxView.clicks(tv_receiver)
                .compose(this.bindUntilEvent<Any>(ActivityEvent.DESTROY))
                .subscribe {
                    if (mInfo!!.receivers != null) {
                        MaterialDialog.Builder(this@AppDetailActivity)
                                .title(tv_receiver.text.toString())
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
