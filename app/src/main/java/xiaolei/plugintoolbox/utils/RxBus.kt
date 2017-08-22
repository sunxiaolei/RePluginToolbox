package xiaolei.plugintoolbox.utils

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor

/**
 * Created by sunxl8 on 2017/6/27.
 */

class RxBus private constructor() {

    private val bus: FlowableProcessor<Any> = PublishProcessor.create<Any>().toSerialized()

    private object RxBusHolder {
        internal val sInstance = RxBus()
    }


    fun post(o: Any) {
        bus.onNext(o)
    }

    fun <T> toFlowable(eventType: Class<T>): Flowable<T> {
        return bus.ofType(eventType)
    }

    fun <T> toDefaultFlowable(eventType: Class<T>, act: Consumer<T>): Disposable {
        return bus.ofType(eventType).compose(SchedulersCompat.applyIoSchedulers<T>()).subscribe(act)
    }

    companion object {

        val default: RxBus
            get() = RxBusHolder.sInstance
    }
}
