package com.example.notes;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button addButton;
    ArrayList<String> noticeList;
    ArrayAdapter<String> adapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);

        prefs = getSharedPreferences("NotesData", MODE_PRIVATE);
        noticeList = loadNotices();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noticeList);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddDialog());

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Notice");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText titleInput = new EditText(this);
        titleInput.setHint("Title");
        titleInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(titleInput);

        final EditText descInput = new EditText(this);
        descInput.setHint("Description");
        descInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(descInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            if (!title.isEmpty()) {
                String notice = title + (desc.isEmpty() ? "" : "\n" + desc);
                noticeList.add(notice);
                adapter.notifyDataSetChanged();
                saveNotices();
            } else {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Delete this notice?")
                .setPositiveButton("Delete", (d, w) -> {
                    noticeList.remove(position);
                    adapter.notifyDataSetChanged();
                    saveNotices();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveNotices() {
        JSONArray arr = new JSONArray();
        for (String s : noticeList) arr.put(s);
        prefs.edit().putString("notes_data", arr.toString()).apply();
    }

    private ArrayList<String> loadNotices() {
        ArrayList<String> list = new ArrayList<>();
        String json = prefs.getString("notes_data", "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) list.add(arr.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}