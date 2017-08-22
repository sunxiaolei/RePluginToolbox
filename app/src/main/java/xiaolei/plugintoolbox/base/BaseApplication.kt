package xiaolei.plugintoolbox.base

import android.app.Application
import sunxl8.myutils.Utils


/**
 * Created by sunxl8 on 2017/7/31.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}
