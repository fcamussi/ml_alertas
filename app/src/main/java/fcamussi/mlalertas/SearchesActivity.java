package fcamussi.mlalertas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Clase SearchesActivity
 *
 * @author Fernando Camussi
 */
public class SearchesActivity extends AppCompatActivity {

    private static UUID addSearchWorkerId;
    SharedPreferences preferences;
    boolean wifi;
    boolean batteryNotLow;
    private ActivityResultLauncher<Intent> addSearchLauncher;
    private ActivityResultLauncher<Intent> configurationLauncher;
    private DataBase dataBase;
    private Cursor cursor;
    private ProgressBar pb;
    private SearchCursorAdapter adapter;
    private BroadcastReceiver brAddSearchFinished;
    private BroadcastReceiver brAddSearchConnectionFailed;
    private BroadcastReceiver brAddSearchMaxResultCountExceeded;
    private BroadcastReceiver brCursorRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searches);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        dataBase = new DataBase(this);
        pb = findViewById(R.id.searches_pb);
        pb.setIndeterminate(true);
        if (addSearchWorkerIsRunning()) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
        }
        ListView lv = findViewById(R.id.searches_lv);
        cursor = dataBase.getCursorForAdapterSearch();
        adapter = new SearchCursorAdapter(this, cursor);
        lv.setAdapter(adapter);

        addSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    resultAddSearch(result.getResultCode(), intent);
                });

        configurationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    resultConfiguration(result.getResultCode(), intent);
                });

        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            showItems(Integer.parseInt(view.getTag().toString())); // se obtiene el id de la búsqueda del tag del view
        });

        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setMessage(getString(R.string.want_delete_search));
            alertDialogBuilder.setPositiveButton(getString(R.string.yes), (dialogInterface, i1) -> {
                int searchId = Integer.parseInt(view.getTag().toString()); // se obtiene el id de la búsqueda del tag del view
                setDeletedSearch(searchId);
                dialogInterface.dismiss();
            });
            alertDialogBuilder.setNegativeButton(getString(R.string.no), (dialogInterface, i12) -> dialogInterface.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        });

        brAddSearchFinished = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterSearch();
                adapter.changeCursor(cursor);
                pb.setVisibility(View.GONE);
                Toast.makeText(context, getString(R.string.search_added), Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter filter = new IntentFilter(Constants.ADD_SEARCH_FINISHED);
        this.registerReceiver(brAddSearchFinished, filter);

        brAddSearchConnectionFailed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pb.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(getString(R.string.connection_failed));
                alertDialogBuilder.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };
        filter = new IntentFilter(Constants.ADD_SEARCH_CONNECTION_FAILED);
        this.registerReceiver(brAddSearchConnectionFailed, filter);

        brAddSearchMaxResultCountExceeded = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pb.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(getString(R.string.search_produces_too_many_results));
                alertDialogBuilder.setPositiveButton("Aceptar", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };
        filter = new IntentFilter(Constants.ADD_SEARCH_MAX_RESULT_COUNT_EXCEEDED);
        this.registerReceiver(brAddSearchMaxResultCountExceeded, filter);

        brCursorRefresh = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterSearch();
                adapter.changeCursor(cursor);
            }
        };
        filter = new IntentFilter(Constants.SEARCHER_FINISHED);
        this.registerReceiver(brCursorRefresh, filter);

        preferences = getSharedPreferences("searches_activity", Context.MODE_PRIVATE);
        wifi = preferences.getBoolean("wifi", false);
        batteryNotLow = preferences.getBoolean("battery_not_low", true);
        enqueueSearcherWorker(wifi, batteryNotLow, false); // replace=true para reemplazar el SearchWorker
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pb.getVisibility() == View.GONE) { // si la barra de progreso no está visible...
            cursor = dataBase.getCursorForAdapterSearch();
            adapter.changeCursor(cursor);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(brAddSearchFinished);
        unregisterReceiver(brAddSearchConnectionFailed);
        unregisterReceiver(brAddSearchMaxResultCountExceeded);
        unregisterReceiver(brCursorRefresh);
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searches, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_searches_add_search) {
            if (pb.getVisibility() == View.VISIBLE) { // si la barra de progreso está visible...
                Toast.makeText(getBaseContext(), getString(R.string.wait_), Toast.LENGTH_SHORT).show();
            } else {
                showAddSearch();
            }
            return true;
        } else if (itemId == R.id.menu_searches_unset_notifications) {
            unsetNotifications();
            return true;
        } else if (itemId == R.id.menu_searches_configuration) {
            showConfiguration();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enqueueSearcherWorker(boolean wifi, boolean batteryNotLow, boolean replace) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(wifi ? NetworkType.UNMETERED : NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(batteryNotLow)
                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SearcherWorker.class,
                Constants.SEARCHER_FREQUENCY_MINUTES, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(Constants.SEARCHER_FREQUENCY_MINUTES, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("PeriodicSearchWorker",
                replace ? ExistingPeriodicWorkPolicy.REPLACE : ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }

    private void showAddSearch() {
        Intent intent = new Intent(this, AddSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP); // para evitar multiples clicks
        addSearchLauncher.launch(intent);
    }

    private void resultAddSearch(int result, Intent intent) {
        if (result == Activity.RESULT_OK) {
            String words = intent.getStringExtra("words");
            String siteId = intent.getStringExtra("site_id");
            if (words.equals("MLALERTAS1234567890")) {
                dataBase.insertFrequencySpecialMode();
                Toast.makeText(this, getString(R.string.special_mode_enabled),
                        Toast.LENGTH_LONG).show();
                return;
            }
            String frequencyId = intent.getStringExtra("frequency_id");
            Data workerData = new Data.Builder()
                    .putString("words", words)
                    .putString("site_id", siteId)
                    .putString("frequency_id", frequencyId)
                    .build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(AddSearchWorker.class).
                    setInputData(workerData).build();
            addSearchWorkerId = workRequest.getId();
            WorkManager.getInstance(this).enqueue(workRequest);
            pb.setVisibility(View.VISIBLE);
        }
    }

    private void showConfiguration() {
        Intent intent = new Intent(this, ConfigurationActivity.class);
        intent.putExtra("wifi", wifi);
        intent.putExtra("battery_not_low", batteryNotLow);
        configurationLauncher.launch(intent);
    }

    private void resultConfiguration(int result, Intent intent) {
        if (result == Activity.RESULT_OK) {
            wifi = intent.getBooleanExtra("wifi", true);
            batteryNotLow = intent.getBooleanExtra("battery_not_low", true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("wifi", wifi);
            editor.putBoolean("battery_not_low", batteryNotLow);
            editor.apply();
            Toast.makeText(this, getString(R.string.configuration_saved), Toast.LENGTH_SHORT).show();
            enqueueSearcherWorker(wifi, batteryNotLow, true);
        }
    }

    private void showItems(int search_id) {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("search_id", search_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // para evitar multiples clicks
        startActivity(intent);
    }

    private void setDeletedSearch(int searchId) {
        dataBase.beginTransaction();
        try {
            Search search = dataBase.getSearch(searchId);
            search.setDeleted(true); /* se marca como borrada, SearcherWorker se encarga de borrarla */
            dataBase.updateSearch(search);
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
        cursor = dataBase.getCursorForAdapterSearch();
        adapter.changeCursor(cursor);
        Toast.makeText(this, getString(R.string.search_deleted), Toast.LENGTH_SHORT).show();
    }

    private void unsetNotifications() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            dataBase.beginTransaction();
            try {
                List<Search> searchList = dataBase.getAllSearches(false);
                for (Search search : searchList) {
                    search.setNewItem(false);
                    dataBase.updateSearch(search);
                    dataBase.unsetAllNewItem(search.getId());
                }
                dataBase.setTransactionSuccessful();
            } finally {
                dataBase.endTransaction();
            }
            handler.post(() -> {
                cursor = dataBase.getCursorForAdapterSearch();
                adapter.changeCursor(cursor);
                Toast.makeText(this, getString(R.string.notifications_unchecked), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private boolean addSearchWorkerIsRunning() {
        boolean isRunning = false;
        if (addSearchWorkerId != null) {
            ListenableFuture<WorkInfo> lf = WorkManager.getInstance(this).getWorkInfoById(addSearchWorkerId);
            try {
                WorkInfo workInfo = lf.get();
                if (workInfo.getState() == WorkInfo.State.ENQUEUED ||
                        workInfo.getState() == WorkInfo.State.RUNNING) {
                    isRunning = true;
                }
            } catch (Exception ignored) {
            }
        }
        return isRunning;
    }

}
