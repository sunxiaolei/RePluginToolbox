package xiaolei.plugintoolbox.ui;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import xiaolei.plugintoolbox.R;

/**
 * Created by sunxl8 on 2017/8/14.
 */

public class MainAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MainAdapter() {
        super(R.layout.item_main);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_item, item);
    }
}
