package xiaolei.plugintoolbox.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.promeg.pinyinhelper.Pinyin;

import java.io.Serializable;
import java.util.List;


/**
 * Created by sunxl8 on 2017/8/14.
 */

public class AppModel implements Comparable<AppModel> {

    private String appName;

    private String appNamePinyin;

    private String appPackage;

    private int appVersionCode;

    private String appVersionName;

    private Drawable appIcon;

    private boolean isSystemApp;

    private String sourcePath;

    private String size;

    private long firstInstallTime;

    private long lastUpdateTime;

    private List<String> permission;

    public String getAppNamePinyin() {
        return appNamePinyin;
    }

    public void setAppNamePinyin(String appNamePinyin) {
        this.appNamePinyin = appNamePinyin;
    }

    public List<String> getPermission() {
        return permission;
    }

    public void setPermission(List<String> permission) {
        this.permission = permission;
    }

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppPinyinHead() {
        String[] s = getAppNamePinyin().split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length; i++) {
            if (s[i].length() > 0) {
                sb.append(s[i].substring(0, 1));
            }
        }
        return sb.toString().toUpperCase();
    }

    @Override
    public int compareTo(@NonNull AppModel o) {
        return getAppNamePinyin().toUpperCase()
                .compareTo(o.getAppNamePinyin().toUpperCase());
    }
}
