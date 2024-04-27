package com.example.secretnotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addNoteButton;
    RecyclerView recyclerView;
    ImageButton menuButton;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addNoteButton = findViewById(R.id.add_note);
        recyclerView = findViewById(R.id.notes_list);
        menuButton = findViewById(R.id.menu_btn);

        addNoteButton.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NoteInfoActivity.class)));
        menuButton.setOnClickListener((v) -> showMenu());
        setupRecyclerView();
    }
    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(this, menuButton);
        popupMenu.getMenu().add("Sign Out");
        popupMenu.setOnMenuItemClickListener((item) -> {
            Utility.signOut();
            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("registered", false);
            PinManager pinManager = new PinManager(this);
            pinManager.storePin(null);
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        });
        popupMenu.show();
    }
    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }
    @Override protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}