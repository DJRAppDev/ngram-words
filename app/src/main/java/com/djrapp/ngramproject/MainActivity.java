package com.djrapp.ngramproject;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

        song = "";

        tts = new TextToSpeech(MainActivity.this, MainActivity.this);

        generateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song+=lyricList.get((int)(Math.random()*lyricList.size()))+" ";
                song = generateSong(song);
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

    public String generateSong(String song) {
        String result = "";
        result+=song;
        for (int i = 0; i < 10; i++) {
            String last_word = result.split(" ")[result.split(" ").length-1];
            ArrayList<String> nextWords = new ArrayList<>();
            ArrayList<Double> nextProbs = new ArrayList<>();
            double sumWeight = 0;
            for (int j = 0; j < lyricList.size(); j++) {
                if (lyricList.get(j) == null || !lyricList.get(j).contains(" ")) {
                }
                else if (lyricList.get(j).split(" ")[0].equals(last_word)) {
                    nextWords.add(lyricList.get(j));
                    nextProbs.add(Double.valueOf(percentageList.get(j)));
                    sumWeight+=Double.valueOf(percentageList.get(j));
                }
            }
            double randomNumber = Math.random()*sumWeight;
            ArrayList<Double> probabilities = new ArrayList<>();
            for (int k = 0; k < nextProbs.size(); k++) {
                probabilities.add(Math.abs(randomNumber-nextProbs.get(k)));
            }
            int random = 0;
            for (int l = 0; l < probabilities.size(); l++) {
                if (probabilities.get(random) > probabilities.get(l)) {
                    random = l;
                }
            }
            if (nextWords.size() > 0) {
//                result += nextWords.get((int) (Math.random() * nextWords.size())).split(" ",2)[1]+" ";
                result += nextWords.get(random).split(" ", 2)[1] + "\n";
            }
        }
        return result;
    }
}
