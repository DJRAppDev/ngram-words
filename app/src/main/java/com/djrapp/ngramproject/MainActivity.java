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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private DatabaseReference database;
    private TextView lyrics;
    private Button generateBT, textToSpeechBT;
    private TextToSpeech tts;
    private static ArrayList<String> lyricList, frequencyList, percentageList;
    private String song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance().getReference();
        lyrics = findViewById(R.id.lyrics);
        generateBT = findViewById(R.id.generate);
        textToSpeechBT = findViewById(R.id.toSpeech);

        lyricList = new ArrayList<>();
        frequencyList = new ArrayList<>();
        percentageList = new ArrayList<>();

        song = new String();

        tts = new TextToSpeech(MainActivity.this, MainActivity.this);

        generateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyrics.setText(song);
            }
        });
        textToSpeechBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextToSpeechFunction();
            }
        });
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot tempSnapShot = dataSnapshot.child("0").child("data");
                for(DataSnapshot snapshots : tempSnapShot.getChildren()){
                    String lyricLine = (String) snapshots.child("Field1").getValue();
                    String frequency = snapshots.child("Field3").getValue() + "";
                    String percentage = snapshots.child("Field5").getValue() + "";

                    lyricList.add(lyricLine);
                    frequencyList.add(frequency);
                    percentageList.add(percentage);
                }
                //Run methods after the for loop
                song+=lyricList.get((int)(Math.random()*lyricList.size()))+" ";
                //TODO: Fix the generateSong method so this can be uncommented.
                //song = generateSong(song,lyricList,percentageList);
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

    public void printArrayList(ArrayList<String> arrList){
        for(int i = 0; i < arrList.size(); i++){
            Log.d("Abc", arrList.get(i));
        }
    }

    //TODO: Optimize method because Android can't handle it
    public String generateSong(String song, ArrayList<String> lyricList, ArrayList<String> percentageList) {
        String result = "";
        result+=song;
        for (int i = 0; i < 1; i++) {
            String last_word = result.split(" ")[result.split(" ").length-1];
            ArrayList<String> nextWords = new ArrayList<String>();
            ArrayList<Double> nextProbs = new ArrayList<Double>();
            double sumWeight = 0;
            for (int j = 0; j < /*lyricList.size()*/ 1; j++) {
                if (lyricList.get(j).split(" ")[0].equals(last_word)) {
                    nextWords.add(lyricList.get(j));
                    nextProbs.add(Double.parseDouble(percentageList.get(j)));
                    sumWeight+=Double.parseDouble(percentageList.get(j));
                }
            }
            double randomNumber = Math.random()*sumWeight;
            ArrayList<Double> probabilities = new ArrayList<>();
            for (int k = 0; k < nextProbs.size(); k++) {
                probabilities.add(Math.abs(randomNumber-nextProbs.get(k)));
            }
            int random = 0;
            for (int l = 0; l < probabilities.size(); l++) {
                if (probabilities.get(random) > probabilities.get(i)) {
                    random = l;
                }
            }
            result+=nextWords.get(random);
        }
        return result;
    }

}
