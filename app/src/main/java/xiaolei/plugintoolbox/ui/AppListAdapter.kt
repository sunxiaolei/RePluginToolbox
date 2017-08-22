package xiaolei.plugintoolbox.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import xiaolei.plugintoolbox.R
import xiaolei.plugintoolbox.model.AppModel

/**
 * Created by sunxl8 on 2017/8/14.
 */

class AppListAdapter : BaseQuickAdapter<AppModel, BaseViewHolder>(R.layout.item_applist) {

    override fun convert(helper: BaseViewHolder, item: AppModel) {
        helper.setImageDrawable(R.id.iv_item_icon, item.appIcon)
                .setText(R.id.tv_item_name, item.appName)
                .setText(R.id.tv_item_pkg, "包名：" + item.appPackage)
                .setText(R.id.tv_item_version, "版本：" + item.appVersionName)
                .setText(R.id.tv_item_permission, "权限：" + (if (item.permission == null) 0 else item.permission!!.size).toString())
                .setText(R.id.tv_item_size, item.size)
                .setVisible(R.id.tv_item_system, item.isSystemApp)
    }
}
