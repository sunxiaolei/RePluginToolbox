package xiaolei.plugintoolbox.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Created by sunxl8 on 2017/8/18.
 */

object ShareUtils {

    fun shareFile(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra("subject", "")
        intent.putExtra("body", "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        if (uri.toString().endsWith(".gz")) {
            intent.type = "application/x-gzip"
        } else if (uri.toString().endsWith(".txt")) {
            intent.type = "text/plain"
        } else {
            intent.type = "application/octet-stream"
        }
        context.startActivity(intent)
    }
}
