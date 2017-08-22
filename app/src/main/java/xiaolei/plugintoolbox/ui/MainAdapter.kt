package xiaolei.plugintoolbox.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import xiaolei.plugintoolbox.R

/**
 * Created by sunxl8 on 2017/8/14.
 */

class MainAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_main) {

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tv_item, item)
    }
}
