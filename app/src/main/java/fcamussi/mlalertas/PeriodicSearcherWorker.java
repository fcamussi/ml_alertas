package fcamussi.mlalertas;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PeriodicSearcherWorker extends Worker {

    public PeriodicSearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        return Result.success();
    }

}
