package xiaolei.plugintoolbox.ui

import android.os.Build
import kotlinx.android.synthetic.main.activity_deviceinfo.*

import xiaolei.plugintoolbox.R
import xiaolei.plugintoolbox.base.BaseActivity

/**
 * Created by sunxl8 on 2017/8/21.
 */

class DeviceInfoActivity : BaseActivity() {

    override fun setContentViewId(): Int {
        return R.layout.activity_deviceinfo
    }

    override fun init() {

        tv_deviceinfo.text = "设备名：" + Build.MODEL + "\n" +
                "设备厂商：" + Build.BRAND + "\n" +
                "SDK版本号：" + Build.VERSION.SDK_INT + "\n" +
                "SDK版本号：" + Build.VERSION.SDK_INT + "\n"
    }
}
