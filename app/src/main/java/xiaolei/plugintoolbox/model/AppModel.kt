package xiaolei.plugintoolbox.model

import android.graphics.drawable.Drawable


/**
 * Created by sunxl8 on 2017/8/14.
 */

class AppModel : Comparable<AppModel> {

    var appName: String? = null

    var appNamePinyin: String? = null

    var appPackage: String? = null

    var appVersionCode: Int = 0

    var appVersionName: String? = null

    var appIcon: Drawable? = null

    var isSystemApp: Boolean = false

    var sourcePath: String? = null

    var size: String? = null

    var firstInstallTime: Long = 0

    var lastUpdateTime: Long = 0

    var permission: List<String>? = null

    val appPinyinHead: String
        get() {
            val s = appNamePinyin!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sb = StringBuffer()
            s.indices
                    .filter { s[it].length > 0 }
                    .forEach { sb.append(s[it].substring(0, 1)) }
            return sb.toString().toUpperCase()
        }

    override fun compareTo(o: AppModel): Int {
        return appNamePinyin!!.toUpperCase()
                .compareTo(o.appNamePinyin!!.toUpperCase())
    }
}
