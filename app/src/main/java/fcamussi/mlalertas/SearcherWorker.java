package fcamussi.mlalertas;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SearcherWorker extends Worker {

    public SearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        return Result.success();
    }

}
