package com.example.secretnotes;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    TextView pageTitle;
    String title, content_text, docId;
    boolean isEdit = false;
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
        pageTitle = findViewById(R.id.title);

        // Get the data from the intent
        title = getIntent().getStringExtra("title");
        content_text = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()) {
            isEdit = true;
        }

        // Set the data to the views
        noteTitle.setText(title);
        content.setText(content_text);
        pageTitle.setText(isEdit ? "Edit Note" : "Add Note");

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
        DocumentReference documentReference;
        if(isEdit){
            // If the note is being edited, then update the existing note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else {
            // If the note is being added, then create a new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
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