package xiaolei.plugintoolbox.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Arrays;
import java.util.List;

import sunxl8.myutils.AppUtils;
import sunxl8.myutils.FileUtils;
import sunxl8.myutils.TimeUtils;
import xiaolei.plugintoolbox.R;
import xiaolei.plugintoolbox.base.BaseActivity;

/**
 * Created by sunxl8 on 2017/8/17.
 */

public class AppDetailActivity extends BaseActivity {

    private String mAppPkg;
    private PackageInfo mInfo;

    private TextView tvBasic, tvPermissions, tvActivities, tvServices, tvReceivers;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_appdetail;
    }

    @Override
    protected void init() {
        mAppPkg = getIntent().getStringExtra("package");
        try {
            mInfo = getPackageManager().getPackageInfo(mAppPkg, PackageManager.MATCH_UNINSTALLED_PACKAGES
                    | PackageManager.GET_PERMISSIONS | PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES
                    | PackageManager.GET_RECEIVERS | PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        tvTitle.setText(AppUtils.getAppName(mInfo.packageName));
        tvBasic = (TextView) findViewById(R.id.tv_appdetail_basic);
        tvBasic.setText("包名：" + mInfo.packageName + "\n"
                + "Apk大小：" + FileUtils.getFileSize(mInfo.applicationInfo.sourceDir) + "\n"
                + "版本名：" + mInfo.versionName + "\n"
                + "版本号：" + mInfo.versionCode + "\n"
                + "minSdkVersion：" + mInfo.applicationInfo.minSdkVersion + "\n"
                + "targetSdkVersion：" + mInfo.applicationInfo.targetSdkVersion + "\n"
                + "安装时间：" + TimeUtils.millis2String(mInfo.firstInstallTime) + "\n"
                + "更新时间：" + TimeUtils.millis2String(mInfo.lastUpdateTime) + "\n"
                + "SignatureSHA1：" + AppUtils.getAppSignatureSHA1(mAppPkg)
        );
        tvPermissions = (TextView) findViewById(R.id.tv_appdetail_permission);
        tvPermissions.setText("权限列表(" + (mInfo.requestedPermissions == null ? 0 : mInfo.requestedPermissions.length) + ")");
        RxView.clicks(tvPermissions)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> {
                    if (mInfo.requestedPermissions != null) {
                        new MaterialDialog.Builder(AppDetailActivity.this)
                                .title(tvPermissions.getText().toString())
                                .adapter(new DialogPermissionAdapter(Arrays.asList(mInfo.requestedPermissions)),
                                        new LinearLayoutManager(AppDetailActivity.this))
                                .positiveText("确定")
                                .build()
                                .show();
                    }
                });
        tvActivities = (TextView) findViewById(R.id.tv_appdetail_activity);
        tvActivities.setText("注册的Activity(" + (mInfo.activities == null ? 0 : mInfo.activities.length) + ")");
        RxView.clicks(tvActivities)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> {
                    if (mInfo.activities != null) {
                        new MaterialDialog.Builder(AppDetailActivity.this)
                                .title(tvActivities.getText().toString())
                                .adapter(new DialogActivityAdapter(Arrays.asList(mInfo.activities)),
                                        new LinearLayoutManager(AppDetailActivity.this))
                                .positiveText("确定")
                                .build()
                                .show();
                    }
                });
        tvServices = (TextView) findViewById(R.id.tv_appdetail_service);
        tvServices.setText("注册的Service(" + (mInfo.services == null ? 0 : mInfo.services.length) + ")");
        RxView.clicks(tvServices)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> {
                    if (mInfo.services != null) {
                        new MaterialDialog.Builder(AppDetailActivity.this)
                                .title(tvServices.getText().toString())
                                .adapter(new DialogServiceAdapter(Arrays.asList(mInfo.services)),
                                        new LinearLayoutManager(AppDetailActivity.this))
                                .positiveText("确定")
                                .build()
                                .show();
                    }
                });
        tvReceivers = (TextView) findViewById(R.id.tv_appdetail_receiver);
        tvReceivers.setText("注册的Receiver(" + (mInfo.receivers == null ? 0 : mInfo.receivers.length) + ")");
        RxView.clicks(tvReceivers)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> {
                    if (mInfo.receivers != null) {
                        new MaterialDialog.Builder(AppDetailActivity.this)
                                .title(tvReceivers.getText().toString())
                                .adapter(new DialogReceiverAdapter(Arrays.asList(mInfo.receivers)),
                                        new LinearLayoutManager(AppDetailActivity.this))
                                .positiveText("确定")
                                .build()
                                .show();
                    }
                });
    }

    public static void startThisActivity(Context context, String pkg) {
        Intent intent = new Intent(context, AppDetailActivity.class);
        intent.putExtra("package", pkg);
        context.startActivity(intent);
    }

    class DialogPermissionAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public DialogPermissionAdapter(@Nullable List<String> data) {
            super(R.layout.item_dialog, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_item, item);
        }

    }

    class DialogActivityAdapter extends BaseQuickAdapter<ActivityInfo, BaseViewHolder> {

        public DialogActivityAdapter(@Nullable List<ActivityInfo> data) {
            super(R.layout.item_dialog, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ActivityInfo item) {
            String launchMode = "";
            switch (item.launchMode) {
                case 0:
                    launchMode = "standard";
                    break;
                case 1:
                    launchMode = "single_top";
                    break;
                case 2:
                    launchMode = "single_task";
                    break;
                case 3:
                    launchMode = "single_instance";
                    break;
            }
            helper.setText(R.id.tv_item, "name: " + item.name + "\n" +
                    "launchMode: " + launchMode);
        }

    }

    class DialogServiceAdapter extends BaseQuickAdapter<ServiceInfo, BaseViewHolder> {

        public DialogServiceAdapter(@Nullable List<ServiceInfo> data) {
            super(R.layout.item_dialog, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ServiceInfo item) {
            helper.setText(R.id.tv_item, item.name);
        }

    }

    class DialogReceiverAdapter extends BaseQuickAdapter<ActivityInfo, BaseViewHolder> {

        public DialogReceiverAdapter(@Nullable List<ActivityInfo> data) {
            super(R.layout.item_dialog, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ActivityInfo item) {
            helper.setText(R.id.tv_item, item.name);
        }

    }
}
