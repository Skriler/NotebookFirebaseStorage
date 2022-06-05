package edu.itstep.notebookfirebasestorage.services;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.itstep.notebookfirebasestorage.entities.Note;

public class FirebaseService {
    public static final String DATABASE_NAME = "Notebook db";

    private final DatabaseReference dbRef;
    private final ArrayList<Note> notes;

    public FirebaseService() {
        FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        dbRef = firebaseDB.getReference(Note.class.getSimpleName());
        firebaseDB.getReference("dbName").setValue(DATABASE_NAME);

        notes = new ArrayList<>();

        setDBValueListener();
    }

    private void setDBValueListener() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readNoteList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(error.getMessage(), error.getDetails());
            }
        });
    }

    private void readNoteList(DataSnapshot snapshot) {
        notes.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Note note = getNodeFromSnapshot(dataSnapshot);
            notes.add(note);
        }
    }

    private Note getNodeFromSnapshot(DataSnapshot snapshot) {
        String title = snapshot.child("title").getValue(String.class);
        String theme = snapshot.child("theme").getValue(String.class);
        String description = snapshot.child("description").getValue(String.class);
        return new Note(title, theme, description);
    }

    public void saveNote(Note note) {
        dbRef.push().setValue(note);
    }

    public void saveNoteList(ArrayList<Note> notes) {
        dbRef.setValue(notes);
    }
}
