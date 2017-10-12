package xiaolei.plugintoolbox.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.afollestad.materialdialogs.MaterialDialog
import com.github.promeg.pinyinhelper.Pinyin
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import com.trello.rxlifecycle2.android.ActivityEvent

import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import kotlinx.android.synthetic.main.activity_applist.*
import sunxl8.myutils.AppUtils
import sunxl8.myutils.FileUtils
import sunxl8.myutils.ToastUtils
import xiaolei.plugintoolbox.R
import xiaolei.plugintoolbox.base.BaseActivity
import xiaolei.plugintoolbox.model.AppModel
import xiaolei.plugintoolbox.utils.SchedulersCompat
import xiaolei.plugintoolbox.utils.ShareUtils
import java.util.Collections.sort

/**
 * Created by sunxl8 on 2017/8/14.
 */

class AppListActivity : BaseActivity() {

    private var mAdapterApp: AppListAdapter? = null

    private var mDialog: MaterialDialog? = null

    private var listAll: List<AppModel>? = null
    private var listFilterAll: List<AppModel>? = null
    private var showSysApp = true

    private var mUninstallReceiver: BroadcastReceiver? = null
    private var uninstallPosition = 0

    override fun setContentViewId(): Int {
        return R.layout.activity_applist
    }

    override fun init() {
        showLoading()
        initView()
        registerBroadcast()
        getAppList()
    }

    private fun initView() {
        tvTitle?.text = "应用列表"
        et_search.isEnabled = false
        //搜索
        RxTextView.afterTextChangeEvents(et_search)
                .compose<TextViewAfterTextChangeEvent>(this.bindUntilEvent<TextViewAfterTextChangeEvent>(ActivityEvent.DESTROY))
                .subscribe { filterApp() }
        val rootView = findViewById(R.id.layout_applist_root)
        //监听键盘
        rootView!!.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            et_search.isCursorVisible = heightDiff > 100
        }

        //排序
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                arrayOf("名称升序", "名称降序", "权限升序", "权限降序"))
        spinner_sort.adapter = arrayAdapter
        spinner_sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val list = mAdapterApp!!.data
                when (position) {
                    0 -> sort(list)
                    1 -> {
                        sort(list)
                        Collections.reverse(list)
                    }
                    2 -> sort(list) { o1, o2 -> o1.permission!!.size - o2.permission!!.size }
                    3 -> sort(list) { o1, o2 -> o2.permission!!.size - o1.permission!!.size }
                }
                mAdapterApp!!.setNewData(list)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        //显示系统应用
        checkbox_sys.setOnCheckedChangeListener { _, isChecked ->
            showSysApp = isChecked;
            filterApp();
        }

        recyclerview_list.layoutManager = LinearLayoutManager(this)
        mAdapterApp = AppListAdapter()
        recyclerview_list.adapter = mAdapterApp
        mAdapterApp!!.setOnItemClickListener { adapter, _, position ->
            val app = adapter.getItem(position) as AppModel?
            if (mDialog == null) {
                mDialog = MaterialDialog.Builder(this@AppListActivity)
                        .title("")
                        .items(*if (app!!.isSystemApp) arrayOf("打开", "详情", "分享") else arrayOf("打开", "卸载", "详情", "分享"))
                        .build()
            }
            mDialog!!.setTitle(app!!.appName)
            mDialog!!.builder.itemsCallback { _, _, _, text ->
                when (text.toString()) {
                    "打开" -> try {
                        AppUtils.launchApp(app.appPackage)
                    } catch (e: Exception) {
                        ToastUtils.shortShow("打开失败")
                    }

                    "卸载" -> {
                        uninstallPosition = position
                        AppUtils.uninstallApp(app.appPackage)
                    }
                    "详情" -> AppDetailActivity.startThisActivity(this, app.appPackage!!)
                    "分享" -> ShareUtils.shareFile(this@AppListActivity, Uri.fromFile(File(app.sourcePath)))
                }
            }
            mDialog!!.show()
        }
    }

    private fun registerBroadcast() {//监听软件卸载
        mUninstallReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val uninstallPkg = intent.dataString.substring(8)
                if (mAdapterApp!!.getItem(uninstallPosition)!!.appPackage == uninstallPkg) {
                    mAdapterApp!!.remove(uninstallPosition)
                }
            }
        }
        val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")//必加不然监听不到
        registerReceiver(mUninstallReceiver, intentFilter)
    }

    private fun getAppList() {

        var v = FlowableOnSubscribe<List<AppModel>> {
            e ->
            run {
                val infos = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.GET_PERMISSIONS)
                val apps = ArrayList<AppModel>()
                for (info in infos) {
                    val app = AppModel()
                    app.appPackage = info.packageName
                    app.appVersionCode = info.versionCode
                    app.appVersionName = info.versionName
                    app.appName = AppUtils.getAppName(info.packageName)
                    app.appNamePinyin = Pinyin.toPinyin(AppUtils.getAppName(info.packageName), " ")
                    app.appIcon = AppUtils.getAppIcon(info.packageName)
                    app.isSystemApp = info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0
                    app.sourcePath = info.applicationInfo.sourceDir
                    app.size = FileUtils.getFileSize(info.applicationInfo.sourceDir)
                    app.firstInstallTime = info.firstInstallTime
                    app.lastUpdateTime = info.lastUpdateTime
                    app.permission = if (info.requestedPermissions == null) ArrayList<String>() else Arrays.asList(*info.requestedPermissions)
                    apps.add(app)
                }
                e.onNext(apps)
            }
        };
        Flowable.create(v, BackpressureStrategy.BUFFER)
                .compose(this.bindUntilEvent<List<AppModel>>(ActivityEvent.DESTROY))
                .compose(SchedulersCompat.applyIoSchedulers<List<AppModel>>())
                .subscribe { models ->
                    dismissDialog()
                    layout_top.visibility = View.VISIBLE
                    et_search.isEnabled = true
                    sort(models)
                    mAdapterApp!!.setNewData(models)
                    listAll = models
                }
    }

    private fun filterApp() {
        if (listAll == null) {
            return
        }
        if (TextUtils.isEmpty(et_search.text.toString())) {
            val listNoSys = ArrayList<AppModel>()
            Flowable.fromIterable(listAll)
                    .filter { model -> !model.isSystemApp }
                    .subscribe { model -> listNoSys.add(model) }
            mAdapterApp!!.setNewData(if (showSysApp) listAll else listNoSys)
            return
        }
        val list = ArrayList<AppModel>()
        Flowable.fromIterable(listAll!!)
                .filter { model ->
                    model.appName!!.contains(et_search.text.toString().toUpperCase())
                            || model.appPinyinHead.contains(et_search.text.toString().toUpperCase())
                }
                .subscribe { model -> list.add(model) }
        val listNoSys = ArrayList<AppModel>()
        Flowable.fromIterable(list)
                .filter { model -> !model.isSystemApp }
                .subscribe { model -> listNoSys.add(model) }
        mAdapterApp!!.setNewData(if (showSysApp) list else listNoSys)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mUninstallReceiver != null) {
            unregisterReceiver(mUninstallReceiver)
        }
    }
}
