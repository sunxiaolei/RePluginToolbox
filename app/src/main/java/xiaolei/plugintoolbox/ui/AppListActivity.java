package xiaolei.plugintoolbox.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import sunxl8.myutils.AppUtils;
import sunxl8.myutils.FileUtils;
import xiaolei.plugintoolbox.R;
import xiaolei.plugintoolbox.base.BaseActivity;
import xiaolei.plugintoolbox.model.AppModel;
import xiaolei.plugintoolbox.utils.SchedulersCompat;

/**
 * Created by sunxl8 on 2017/8/14.
 */

public class AppListActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;

    private MaterialDialog mDialog;

    @Override
    protected int setContentViewId() {
        return R.layout.p4_activity_applist;
    }

    @Override
    protected void init() {
        showLoading();
        tvTitle.setText("应用列表");
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_applist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AppListAdapter();
        mRecyclerView.setAdapter(mAdapter);

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
                    mAdapter.setNewData(models);
                });


        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            AppModel app = (AppModel) adapter.getItem(position);
            if (mDialog == null) {
                mDialog = new MaterialDialog.Builder(AppListActivity.this)
                        .title("")
                        .items(new String[]{"Open", "Uninstall", "Detail"})
                        .build();
            }
            mDialog.setTitle(app.getAppName());
            mDialog.getBuilder().itemsCallback((dialog, itemView, position1, text) -> {
                switch (position1) {
                    case 0:
                        AppUtils.launchApp(app.getAppPackage());
                        break;
                    case 1:
                        AppUtils.uninstallApp(app.getAppPackage());
                        break;
                    case 2:
                        break;
                }
            });
            mDialog.show();
        });

    }

}
