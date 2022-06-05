package edu.itstep.notebookfirebasestorage.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.itstep.notebookfirebasestorage.R;
import edu.itstep.notebookfirebasestorage.entities.Note;
import edu.itstep.notebookfirebasestorage.fragments.NoteAddFragment;
import edu.itstep.notebookfirebasestorage.fragments.NoteDetailsFragment;
import edu.itstep.notebookfirebasestorage.fragments.NoteListFragment;
import edu.itstep.notebookfirebasestorage.services.FirebaseService;
import edu.itstep.notebookfirebasestorage.services.NotebookService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String THEME_LIST_TAG = "themes";
    public static final String NOTE_NAME_LIST_TAG = "notes";
    public static final String NOTE_TAG = "note";

    private FirebaseService firebaseService;
    private ArrayList<Note> notes;
    private ArrayList<String> themes;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvDB = findViewById(R.id.tvDB);
        tvDB.setText(FirebaseService.DATABASE_NAME);

        firebaseService = new FirebaseService();
        themes = NotebookService.getThemes();

        if (notes == null) {
            notes = NotebookService.getNoteList();
            firebaseService.saveNoteList(notes);
        }

        setFragmentResultListeners();
        showNoteListFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showNoteList:
                showNoteListFragment();
                break;
            case R.id.addNote:
                showNoteAddFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnClose) {
            showNoteListFragment();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener(
                "inputNodeRequest",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        String title = result.getString("title");
                        String theme = result.getString("theme");
                        String description = result.getString("description");
                        Note newNode = new Note(title, theme, description);
                        firebaseService.saveNote(newNode);
                        notes.add(newNode);
                        showNoteListFragment();
                    }
                }
        );

        getSupportFragmentManager().setFragmentResultListener(
                "inputNoteNameRequest",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        String noteName = result.getString("noteName");
                        showNoteDetailsFragment(noteName);
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showNoteListFragment() {
        Bundle args = new Bundle();
        args.putSerializable(NOTE_NAME_LIST_TAG, NotebookService.getNoteNameList(notes));
        showCustomFragment(new NoteListFragment(), args);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showNoteDetailsFragment(String noteName) {
        Bundle args = new Bundle();
        args.putSerializable(NOTE_TAG, NotebookService.getNoteByName(notes, noteName));
        showCustomFragment(new NoteDetailsFragment(), args);
    }

    private void showNoteAddFragment() {
        Bundle args = new Bundle();
        args.putSerializable(THEME_LIST_TAG, themes);
        showCustomFragment(new NoteAddFragment(), args);
    }

    private void showCustomFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frgContainerView, fragment)
                .commit();
    }
}