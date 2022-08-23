package fcamussi.mlalertas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class SearchesActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> addSearchLauncher;
    private DataBase dataBase;
    private Cursor cursor;
    private ProgressBar pb;
    private ListView lv;
    private SearchCursorAdapter adapter;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searches);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        dataBase = new DataBase(this);
        pb = findViewById(R.id.pb_searches);
        pb.setIndeterminate(true);
        pb.setVisibility(View.GONE);
        lv = findViewById(R.id.lv_searches);
        cursor = dataBase.getCursorForAdapterSearch();
        adapter = new SearchCursorAdapter(this, cursor);
        lv.setAdapter(adapter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = dataBase.getCursorForAdapterSearch();
                adapter.changeCursor(cursor);
                pb.setVisibility(View.GONE);
                Toast.makeText(context, "Búsqueda agregada", Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter filter = new IntentFilter(Constants.ADD_SEARCH_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String words = intent.getStringExtra("words");
                String siteId = intent.getStringExtra("site_id");
                pb.setVisibility(View.GONE);
                System.out.println("No se pudo agregar la búsqueda para: "
                        + words + ", en el sitio " + siteId);
            }
        };
        filter = new IntentFilter(Constants.ADD_SEARCH_CONNECTION_FAILED);
        this.registerReceiver(broadcastReceiver, filter);

        addSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent intent = result.getData();
                    resultAddSearch(result.getResultCode(), intent);
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

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(SearcherWorker.class,
                15, TimeUnit.MINUTES).build();
        // ExistingPeriodicWorkPolicy.KEEP: conserva el trabajo existente e ignora el nuevo
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("PeriodicSearchWorker",
                ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searches, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_search:
                if (pb.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getBaseContext(), "Espere...", Toast.LENGTH_SHORT).show();
                } else {
                    showAddSearch();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddSearch() {
        Intent intent = new Intent(this, AddSearchActivity.class);
        addSearchLauncher.launch(intent);
    }

    private void resultAddSearch(int result, Intent intent) {
        if (result == Activity.RESULT_OK) {
            String words = intent.getStringExtra("words");
            String siteId = intent.getStringExtra("site_id");
            Data workerData = new Data.Builder()
                    .putString("words", words)
                    .putString("site_id", siteId)
                    .build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(AddSearchWorker.class).setInputData(workerData).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            pb.setVisibility(View.VISIBLE);
        }
    }

    private void showItems(int search_id) {
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("search_id", search_id);
        startActivity(intent);
    }

    private void setDeletedSearch(int searchId) {
        Search search = dataBase.getSearch(searchId);
        search.setDeleted(true);
        dataBase.updateSearch(search);
        cursor = dataBase.getCursorForAdapterSearch();
        adapter.changeCursor(cursor);
    }

}