package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    Toolbar toolbar;
    DbHelper dbHelper;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);

        fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(v -> showDialog());
        loadData();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));

        setToolbar();
    }
    //Changes i made t=  9.41  no= 10
    private void loadData() {
        Cursor cursor = dbHelper.getClassTable();

        classItems.clear();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst(); // Move to the first row
                do {
                    int idColumnIndex = cursor.getColumnIndex(DbHelper.C_ID);
                    int classNameColumnIndex = cursor.getColumnIndex(DbHelper.CLASS_NAME_KEY);
                    int subjectNameColumnIndex = cursor.getColumnIndex(DbHelper.SUBJECT_NAME_KEY);

                    if (idColumnIndex != -1 && classNameColumnIndex != -1 && subjectNameColumnIndex != -1) {
                        int id = cursor.getInt(idColumnIndex);
                        String className = cursor.getString(classNameColumnIndex);
                        String subjectName = cursor.getString(subjectNameColumnIndex);

                        classItems.add(new ClassItem(id, className, subjectName));
                    } else {
                        // Handle the case where one or more columns do not exist
                        Log.e("loadData", "One or more columns do not exist in the Cursor");
                    }
                } while (cursor.moveToNext()); // Move to the next row
            } else {
                // Handle the case where the Cursor is empty
                Log.e("loadData", "Cursor is empty");
            }
            cursor.close(); // Don't forget to close the Cursor
        } else {
            // Handle the case where the Cursor is null
            Log.e("loadData", "Cursor is null");
        }
    }


    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("Attendance app");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this, StudentActivity.class);
        intent.putExtra("className", classItems.get(position).getClassName());
        intent.putExtra("subjectName", classItems.get(position).getSubjectName());
        intent.putExtra("position", position);
        intent.putExtra("cid", classItems.get(position).getCid());
        startActivity(intent);
    }

    private void showDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className, subjectName) -> addClass(className, subjectName));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addClass(String className, String subjectName) {
        long cid = dbHelper.addClass(className,subjectName);
        ClassItem classItem = new ClassItem(cid,className, subjectName);
        classItems.add(classItem);
        classAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(int position) {

        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className,subjectName)->updateClass(position,className,subjectName));
    }

    private void updateClass(int position, String className, String subjectName) {
        dbHelper.updateClass(classItems.get(position).getCid(),className,subjectName);
        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }

    private void deleteClass(int position) {
        dbHelper.deleteClass(classItems.get(position).getCid());
        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);

    }
}