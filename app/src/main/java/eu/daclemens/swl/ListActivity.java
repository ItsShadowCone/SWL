package eu.daclemens.swl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListActivity extends BaseActivity {

    private static final String TAG = "ListActivity";

    public class AppDetail {
        AppDetail(ApplicationInfo info) {
            label = info.loadLabel(manager).toString();
            packageName = info.packageName;
            icon = info.loadIcon(manager);
        }

        String label;
        String packageName;
        Drawable icon;
    }

    private PackageManager manager;
    private ArrayList<AppDetail> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        manager = getPackageManager();
        apps = new ArrayList<>();

        List<ApplicationInfo> availableActivities = manager.getInstalledApplications(0);
        for (ApplicationInfo info : availableActivities) {
            if(manager.getLaunchIntentForPackage(info.packageName) != null) {
                apps.add(new AppDetail(info));
            }
        }
        apps.sort(Comparator.comparing(o -> o.label, Collator.getInstance()));

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<>(this, R.layout.item_list, apps) {
            @Override
            @NonNull
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_list, null);

                TextView textView = convertView.findViewById(R.id.textView);
                textView.setText(apps.get(position).label);

                ImageView imageView = convertView.findViewById(R.id.imageView);
                imageView.setImageDrawable(apps.get(position).icon);
                return convertView;
            }
        };

        GridView gridView = findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = getIntent();
            Mode mode = (Mode) intent.getSerializableExtra("mode");

            switch (mode) {
                case DEFAULT -> {
                    Log.d(TAG, "Launching: " + apps.get(position).packageName);
                    startApp(apps.get(position).packageName);
                }
                case ADDING -> {
                    setResult(RESULT_OK, getIntent().putExtra("packageName", apps.get(position).packageName));
                    finish();
                }
            }
        });
        gridView.setAdapter(adapter);
        gridView.getSelector().setAlpha(0);
    }
}
