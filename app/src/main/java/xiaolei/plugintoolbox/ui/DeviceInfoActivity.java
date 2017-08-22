package xiaolei.plugintoolbox.ui;

import android.os.Build;
import android.widget.TextView;

import xiaolei.plugintoolbox.R;
import xiaolei.plugintoolbox.base.BaseActivity;

/**
 * Created by sunxl8 on 2017/8/21.
 */

public class DeviceInfoActivity extends BaseActivity {

    @Override
    protected int setContentViewId() {
        return R.layout.activity_deviceinfo;
    }

    @Override
    protected void init() {

        TextView tvInfo = (TextView) findViewById(R.id.tv_deviceinfo);
        tvInfo.setText("设备名：" + Build.MODEL + "\n"
                + "设备厂商：" + Build.BRAND + "\n"
                + "SDK版本号：" + Build.VERSION.SDK_INT + "\n"
                + "SDK版本号：" + Build.VERSION.SDK_INT + "\n"
        );
    }
}
