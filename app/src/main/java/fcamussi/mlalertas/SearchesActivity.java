package fcamussi.mlalertas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SearchesActivity extends AppCompatActivity {

    SharedPreferences preferences;
    boolean wifi;
    boolean batteryNotLow;
    ActionMenuItemView actionMenuItemView;
    private ActivityResultLauncher<Intent> addSearchLauncher;
    private ActivityResultLauncher<Intent> configurationLauncher;
    private DataBase dataBase;
    private Cursor cursor;
    private ProgressBar pb;
    private ListView lv;
    private SearchCursorAdapter adapter;
    private BroadcastReceiver brAddSearchFinished;
    private BroadcastReceiver brAddSearchConnectionFailed;
    private BroadcastReceiver brAddSearchMaxResultCountExceeded;
    private BroadcastReceiver brCursorRefresh;
    private static UUID addSearchWorkerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searches);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        dataBase = new DataBase(this);
        pb = findViewById(R.id.searches_pb);
        pb.setIndeterminate(true);
        if (addSearchWorkerIsRunning()) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
        }
        lv = findViewById(R.id.searches_lv);
        cursor = dataBase.getCursorForAdapterSearch();
        adapter = new SearchCursorAdapter(this, cursor);
        lv.setAdapter(adapter);

        addSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    resultAddSearch(result.getResultCode(), intent);
                    if (actionMenuItemView != null) {  // para evitar multiples clicks
                        actionMenuItemView.setEnabled(true);
                    }
                });

        configurationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    resultConfiguration(result.getResultCode(), intent);
                });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showItems(Integer.parseInt(view.getTag().toString()));
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String msg = "¿Desea eliminar la búsqueda?";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int searchId = Integer.parseInt(view.getTag().toString());
                        setDeletedSearch(searchId);
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        brAddSearchFinished = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterSearch();
                adapter.changeCursor(cursor);
                pb.setVisibility(View.GONE);
                Toast.makeText(context, "Búsqueda agregada", Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter filter = new IntentFilter(Constants.ADD_SEARCH_FINISHED);
        this.registerReceiver(brAddSearchFinished, filter);

        brAddSearchConnectionFailed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pb.setVisibility(View.GONE);
                String msg = "Falló la conexión con Mercado Libre.";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
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
                String msg = "Ésta búsqueda produce demasiados resultados. " +
                        "Por favor, intente ser más específico.";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
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
        if (pb.getVisibility() == View.GONE) {
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searches, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_searches_add_search:
                if (pb.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getBaseContext(), "Espere...", Toast.LENGTH_SHORT).show();
                } else {
                    if (actionMenuItemView == null) {
                        actionMenuItemView = findViewById(R.id.menu_searches_add_search);
                    }
                    actionMenuItemView.setEnabled(false); // para evitar multiples clicks
                    showAddSearch();
                }
                return true;
            case R.id.menu_searches_unset_notifications:
                unsetNotifications();
                return true;
            case R.id.menu_searches_configuration:
                showConfiguration();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        addSearchLauncher.launch(intent);
    }

    private void resultAddSearch(int result, Intent intent) {
        if (result == Activity.RESULT_OK) {
            String words = intent.getStringExtra("words");
            String siteId = intent.getStringExtra("site_id");
            String frequencyId = intent.getStringExtra("frequency_id");
            Data workerData = new Data.Builder()
                    .putString("words", words)
                    .putString("site_id", siteId)
                    .putString("frequency_id", frequencyId)
                    .build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(AddSearchWorker.class).setInputData(workerData).build();
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
            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            enqueueSearcherWorker(wifi, batteryNotLow, true);
        }
    }

    private void showItems(int search_id) {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("search_id", search_id);
        startActivity(intent);
    }

    private void setDeletedSearch(int searchId) {
        dataBase.beginTransaction();
        try {
            Search search = dataBase.getSearch(searchId);
            search.setDeleted(true);
            dataBase.updateSearch(search);
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
        cursor = dataBase.getCursorForAdapterSearch();
        adapter.changeCursor(cursor);
        Toast.makeText(this, "Búsqueda eliminada", Toast.LENGTH_SHORT).show();
    }

    private void unsetNotifications() {
        dataBase.beginTransaction();
        try {
            List<Search> searchList = dataBase.getAllSearches();
            for (Search search : searchList) {
                search.setNewItem(false);
                dataBase.updateSearch(search);
                dataBase.unsetAllNewItem(search.getId());
            }
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
        cursor = dataBase.getCursorForAdapterSearch();
        adapter.changeCursor(cursor);
        Toast.makeText(this, "Notificaciones desmarcadas", Toast.LENGTH_SHORT).show();
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
