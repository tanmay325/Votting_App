package com.example.attendanceapp;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.AppBarLayout; // Import this
import androidx.appcompat.widget.Toolbar; // Correct import for Toolbar

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SheetListActivity extends AppCompatActivity {
    private ListView sheetList;
    private ArrayAdapter<String> adapter; // Use generics for ArrayAdapter
    private ArrayList<String> listItems = new ArrayList<>();
    private long cid;
    private Toolbar toolbar; // Use the correct Toolbar class
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list);

        cid = getIntent().getLongExtra("cid", -1);
        setToolbar();
        loadListItems();

        sheetList = findViewById(R.id.sheetList);
        adapter = new ArrayAdapter<>(this, R.layout.sheet_list, R.id.date_list_item, listItems);
        sheetList.setAdapter(adapter);

        sheetList.setOnItemClickListener((parent, view, position, id) -> openSheetActivity(position));
    }

    private void openSheetActivity(int position) {
        long[] idArray = getIntent().getLongArrayExtra("idArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        int[] rollArray = getIntent().getIntArrayExtra("rollArray");
        Intent intent = new Intent(this, SheetActivity.class);
        intent.putExtra("idArray", idArray);
        intent.putExtra("rollArray", rollArray);
        intent.putExtra("nameArray", nameArray);
        intent.putExtra("month", listItems.get(position));

        startActivity(intent);
    }

    private void loadListItems() {
        Cursor cursor = new DbHelper(this).getDistinctMonths(cid);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DbHelper.DATE_KEY));
            listItems.add(date.substring(3));
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar); // Use the Toolbar ID directly

        setSupportActionBar(toolbar); // Set the Toolbar as the action bar

        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setVisibility(View.INVISIBLE);

        title.setText("Month Wise Attendance");
        subtitle.setText("Sheet List");

        back.setOnClickListener(v -> onBackPressed());
    }
}
