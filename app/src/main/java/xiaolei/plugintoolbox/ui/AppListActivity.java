package xiaolei.plugintoolbox.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.promeg.pinyinhelper.Pinyin;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import sunxl8.myutils.AppUtils;
import sunxl8.myutils.FileUtils;
import sunxl8.myutils.ToastUtils;
import xiaolei.plugintoolbox.R;
import xiaolei.plugintoolbox.base.BaseActivity;
import xiaolei.plugintoolbox.model.AppModel;
import xiaolei.plugintoolbox.utils.SchedulersCompat;
import xiaolei.plugintoolbox.utils.ShareUtils;

/**
 * Created by sunxl8 on 2017/8/14.
 */

public class AppListActivity extends BaseActivity {

    private AppCompatEditText mEtSearch;
    private AppCompatSpinner mSpinnerSort;
    private AppCompatCheckBox mCheckBoxSys;
    private RecyclerView mRvApps;
    private AppListAdapter mAdapterApp;

    private MaterialDialog mDialog;

    private List<AppModel> listAll;

    private BroadcastReceiver mUninstallReceiver;
    private int uninstallPosition = 0;

    @Override
    protected int setContentViewId() {
        return R.layout.p4_activity_applist;
    }

    @Override
    protected void init() {
        showLoading();
        initView();
        registerBroadcast();
        getAppList();
    }

    private void initView() {
        tvTitle.setText("应用列表");
        mEtSearch = (AppCompatEditText) findViewById(R.id.et_search);
        mEtSearch.setEnabled(false);
        //搜索
        RxTextView.afterTextChangeEvents(mEtSearch)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(event -> {
                    searchApp();
                });
        View rootView = findViewById(R.id.layout_applist_root);
        //监听键盘
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                    if (heightDiff > 100) {
                        mEtSearch.setCursorVisible(true);
                    } else {
                        mEtSearch.setCursorVisible(false);
                    }
                }
        );

        //排序
        mSpinnerSort = (AppCompatSpinner) findViewById(R.id.spinner_applist_sort);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"名称升序", "名称降序", "权限升序", "权限降序"});
        mSpinnerSort.setAdapter(arrayAdapter);
        mSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<AppModel> list = mAdapterApp.getData();
                switch (position) {
                    case 0:
                        Collections.sort(list);
                        break;
                    case 1:
                        Collections.sort(list);
                        Collections.reverse(list);
                        break;
                    case 2:
                        Collections.sort(list, (o1, o2) -> o1.getPermission().size() - o2.getPermission().size());
                        break;
                    case 3:
                        Collections.sort(list, (o1, o2) -> o2.getPermission().size() - o1.getPermission().size());
                        break;

                }
                mAdapterApp.setNewData(list);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //显示系统应用
        mCheckBoxSys = (AppCompatCheckBox) findViewById(R.id.cb_applist_showsys);
        mCheckBoxSys.setOnCheckedChangeListener((buttonView, isChecked) -> {
            List<AppModel> listNoSys = new ArrayList<>();
            Flowable.fromIterable(mAdapterApp.getData())
                    .filter(model -> !model.isSystemApp())
                    .subscribe(model -> listNoSys.add(model));
            mAdapterApp.setNewData(isChecked ? mAdapterApp.getData() : listNoSys);

        });

        mRvApps = (RecyclerView) findViewById(R.id.rv_applist);
        mRvApps.setLayoutManager(new LinearLayoutManager(this));
        mAdapterApp = new AppListAdapter();
        mRvApps.setAdapter(mAdapterApp);
        mAdapterApp.setOnItemClickListener((adapter, view, position) -> {
            AppModel app = (AppModel) adapter.getItem(position);
            if (mDialog == null) {
                mDialog = new MaterialDialog.Builder(AppListActivity.this)
                        .title("")
                        .items(app.isSystemApp() ? new String[]{"打开", "详情", "分享"} : new String[]{"打开", "卸载", "详情", "分享"})
                        .build();
            }
            mDialog.setTitle(app.getAppName());
            mDialog.getBuilder().itemsCallback((dialog, itemView, position1, text) -> {
                switch (text.toString()) {
                    case "打开":
                        try {
                            AppUtils.launchApp(app.getAppPackage());
                        } catch (Exception e) {
                            ToastUtils.shortShow("打开失败");
                        }
                        break;
                    case "卸载":
                        uninstallPosition = position;
                        AppUtils.uninstallApp(app.getAppPackage());
                        break;
                    case "详情":
                        AppDetailActivity.startThisActivity(this, app.getAppPackage());
                        break;
                    case "分享":
                        ShareUtils.shareFile(AppListActivity.this, Uri.fromFile(new File(app.getSourcePath())));
                        break;
                }
            });
            mDialog.show();
        });
    }

    private void registerBroadcast() {//监听软件卸载
        mUninstallReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String uninstallPkg = intent.getDataString().substring(8);
                if (mAdapterApp.getItem(uninstallPosition).getAppPackage().equals(uninstallPkg)) {
                    mAdapterApp.remove(uninstallPosition);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");//必加不然监听不到
        registerReceiver(mUninstallReceiver, intentFilter);
    }

    private void getAppList() {
        Flowable.create((FlowableOnSubscribe<List<AppModel>>) e -> {
            List<PackageInfo> infos = getPackageManager().getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
            List<AppModel> apps = new ArrayList<>();
            for (PackageInfo info : infos) {
                AppModel app = new AppModel();
                app.setAppPackage(info.packageName);
                app.setAppVersionCode(info.versionCode);
                app.setAppVersionName(info.versionName);
                app.setAppName(AppUtils.getAppName(info.packageName));
                app.setAppNamePinyin(Pinyin.toPinyin(AppUtils.getAppName(info.packageName), " "));
                app.setAppIcon(AppUtils.getAppIcon(info.packageName));
                app.setSystemApp(((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) ? false : true);
                app.setSourcePath(info.applicationInfo.sourceDir);
                app.setSize(FileUtils.getFileSize(info.applicationInfo.sourceDir));
                app.setFirstInstallTime(info.firstInstallTime);
                app.setLastUpdateTime(info.lastUpdateTime);
                app.setPermission(info.requestedPermissions == null ? new ArrayList<>() : Arrays.asList(info.requestedPermissions));
                apps.add(app);
            }
            e.onNext(apps);
        }, BackpressureStrategy.BUFFER)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(models -> {
                    dismissDialog();
                    findViewById(R.id.layout_applist_top).setVisibility(View.VISIBLE);
                    mEtSearch.setEnabled(true);
                    Collections.sort(models);
                    mAdapterApp.setNewData(models);
                    listAll = models;
                });
    }

    private void searchApp() {
        if (listAll == null) {
            return;
        }
        if (TextUtils.isEmpty(mEtSearch.getText().toString())) {
            mAdapterApp.setNewData(listAll);
            return;
        }
        List<AppModel> list = new ArrayList<>();
        Flowable.fromIterable(listAll)
                .filter(model -> model.getAppName().contains(mEtSearch.getText().toString().toUpperCase())
                        || model.getAppPinyinHead().contains(mEtSearch.getText().toString().toUpperCase()))
                .subscribe(model -> list.add(model));
        mAdapterApp.setNewData(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUninstallReceiver != null) {
            unregisterReceiver(mUninstallReceiver);
        }
    }
}
