package xiaolei.plugintoolbox.base;

import android.app.Application;

import sunxl8.myutils.Utils;

/**
 * Created by sunxl8 on 2017/7/31.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
