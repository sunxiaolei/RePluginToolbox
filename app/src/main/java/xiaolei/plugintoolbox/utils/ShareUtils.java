package xiaolei.plugintoolbox.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by sunxl8 on 2017/8/18.
 */

public class ShareUtils {

    public static void shareFile(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("subject", "");
        intent.putExtra("body", "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (uri.toString().endsWith(".gz")) {
            intent.setType("application/x-gzip");
        } else if (uri.toString().endsWith(".txt")) {
            intent.setType("text/plain");
        } else {
            intent.setType("application/octet-stream");
        }
        context.startActivity(intent);
    }
}
