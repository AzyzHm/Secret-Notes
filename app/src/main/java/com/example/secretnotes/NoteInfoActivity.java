package com.example.secretnotes;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteInfoActivity extends AppCompatActivity {

    EditText noteTitle,content;
    ImageButton saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        noteTitle = findViewById(R.id.note_title_text);
        content = findViewById(R.id.note_content_text);
        saveButton = findViewById(R.id.save_note);

        saveButton.setOnClickListener(v -> saveNote());
    }
    void saveNote(){
        String title = noteTitle.getText().toString();
        String content = this.content.getText().toString();
        if(title.isEmpty()){
            noteTitle.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(Timestamp.now());
        saveNoteToFirebase(note);
    }
    void saveNoteToFirebase(Note note){
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes().document();
        documentReference.set(note).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
            }
        });
    }
}