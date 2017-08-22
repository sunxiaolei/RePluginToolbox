package xiaolei.plugintoolbox.ui;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import xiaolei.plugintoolbox.R;
import xiaolei.plugintoolbox.model.AppModel;

/**
 * Created by sunxl8 on 2017/8/14.
 */

public class AppListAdapter extends BaseQuickAdapter<AppModel, BaseViewHolder> {

    public AppListAdapter() {
        super(R.layout.item_applist);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppModel item) {
        helper.setImageDrawable(R.id.iv_item_icon, item.getAppIcon())
                .setText(R.id.tv_item_name, item.getAppName())
                .setText(R.id.tv_item_pkg, "包名：" + item.getAppPackage())
                .setText(R.id.tv_item_version, "版本：" + item.getAppVersionName())
                .setText(R.id.tv_item_permission, "权限：" + String.valueOf(item.getPermission() == null ? 0 : item.getPermission().size()))
                .setText(R.id.tv_item_size, item.getSize())
                .setVisible(R.id.tv_item_system, item.isSystemApp());
    }
}
