package xiaolei.plugintoolbox.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.promeg.pinyinhelper.Pinyin;
import com.trello.rxlifecycle2.android.ActivityEvent;

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

/**
 * Created by sunxl8 on 2017/8/14.
 */

public class AppListActivity extends BaseActivity {

    private AppCompatSpinner mSpinner;
    private AppCompatCheckBox mCheckBox;
    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;

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
        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner_applist_sort);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"名称正序", "名称倒序"});
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<AppModel> list = mAdapter.getData();
                switch (position) {
                    case 0:
                        Collections.sort(list);
                        break;
                    case 1:
                        Collections.sort(list);
                        Collections.reverse(list);
                        break;

                }
                mAdapter.setNewData(list);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCheckBox = (AppCompatCheckBox) findViewById(R.id.cb_applist_showsys);
        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            List<AppModel> listNoSys = new ArrayList<>();
            Flowable.fromIterable(listAll)
                    .filter(model -> !model.isSystemApp())
                    .subscribe(model -> listNoSys.add(model));
            mAdapter.setNewData(isChecked ? listAll : listNoSys);

        });
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_applist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AppListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
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
                        break;
                    case "分享":
                        break;
                }
            });
            mDialog.show();
        });
    }

    private void registerBroadcast() {
        mUninstallReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String uninstallPkg = intent.getDataString().substring(8);
                if (mAdapter.getItem(uninstallPosition).getAppPackage().equals(uninstallPkg)) {
                    mAdapter.remove(uninstallPosition);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");//必加不然监听不到
        registerReceiver(mUninstallReceiver, intentFilter);
    }

    private void getAppList() {
        Flowable.create((FlowableOnSubscribe<List<AppModel>>) e -> {
            List<PackageInfo> infos = getPackageManager().getInstalledPackages(0);
            List<AppModel> apps = new ArrayList<>();
            for (PackageInfo info : infos) {
                AppModel app = new AppModel();
                app.setAppPackage(info.packageName);
                app.setAppVersionCode(info.versionCode);
                app.setAppVersionName(info.versionName);
                app.setAppName(AppUtils.getAppName(info.packageName));
                app.setAppIcon(AppUtils.getAppIcon(info.packageName));
                app.setSystemApp(((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) ? false : true);
                app.setSourcePath(info.applicationInfo.sourceDir);
                app.setSize(FileUtils.getFileSize(info.applicationInfo.sourceDir));
                app.setFirstInstallTime(info.firstInstallTime);
                app.setLastUpdateTime(info.lastUpdateTime);
                apps.add(app);
            }
            e.onNext(apps);
        }, BackpressureStrategy.BUFFER)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(models -> {
                    dismissDialog();
                    findViewById(R.id.layout_applist_top).setVisibility(View.VISIBLE);
                    Collections.sort(models);
                    mAdapter.setNewData(models);
                    listAll = models;
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUninstallReceiver != null) {
            unregisterReceiver(mUninstallReceiver);
        }
    }
}
