package xiaolei.plugintoolbox.utils

import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


object SchedulersCompat {

    private val ioTransformer = FlowableTransformer<Any, Any> { upstream ->
        upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> applyIoSchedulers(): FlowableTransformer<T, T> {
        return ioTransformer as FlowableTransformer<T, T>
    }
}
