package xiaolei.plugintoolbox.utils;

import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SchedulersCompat {

    private final static FlowableTransformer ioTransformer = upstream ->
            upstream.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    public static <T> FlowableTransformer<T, T> applyIoSchedulers() {
        return (FlowableTransformer<T, T>) ioTransformer;
    }
}
