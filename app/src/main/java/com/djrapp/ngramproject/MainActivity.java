package com.djrapp.ngramproject;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private DatabaseReference database;
    private TextView lyrics;
    private Button generateBT, textToSpeechBT;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance().getReference();
        lyrics = findViewById(R.id.lyrics);
        generateBT = findViewById(R.id.generate);
        textToSpeechBT = findViewById(R.id.toSpeech);

        tts = new TextToSpeech(MainActivity.this, MainActivity.this);
        generateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        textToSpeechBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextToSpeechFunction();
            }
        });

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Snapshot",dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void TextToSpeechFunction(){
        String text = lyrics.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy(){
        tts.shutdown();
        super.onDestroy();
    }

    @Override
    public void onInit(int status){
        if(status == TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.UK);
        }
    }
}
