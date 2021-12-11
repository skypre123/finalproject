package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.databinding.ActivityContentsBinding;
import com.example.finalproject.databinding.ActivityReviewBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private ActivityReviewBinding binding;
    public List<DiaryModel> array = null;
    public Gson gson = null;
    public String selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gson = new Gson();
        String diaryJson = null;
        try {
            diaryJson = readFile("diary.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        array = gson.fromJson(diaryJson, new TypeToken<List<DiaryModel>>() {}.getType());

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("selectedDate");
        binding.textDiary.setText(selectedDate+" 다이어리");
        binding.buttonBack.setOnClickListener(v -> startDiaryActivity());
        binding.buttonChange.setOnClickListener(v -> startContentsActivity());
        binding.buttonDelete.setOnClickListener(v -> {
            deleteDiary();
            startDiaryActivity();
        });

        for (DiaryModel diaryModel: array) {
            if (diaryModel.getDate().equals(selectedDate)) {
                binding.textTitle.setText(diaryModel.getTitle());
                binding.textContents.setText(diaryModel.getText());
            }
        }
    }

    private void deleteDiary() {
        for (DiaryModel diaryModel: array) {
            if (diaryModel.getDate().equals(selectedDate)) {
                array.remove(diaryModel);
                String jsonString = gson.toJson(array);
                writeFile("diary.json", jsonString);
            }
        }
    }

    private void startContentsActivity() {
        Intent intent = new Intent(this, ContentsActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }

    private void startDiaryActivity() {
        Intent intent = new Intent(this, DiaryActivity.class);
        startActivity(intent);
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private String readFile(String filename) throws FileNotFoundException {
        FileInputStream fis = openFileInput(filename);

        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {

        }
        return stringBuilder.toString().trim();
    }

    private void writeFile(String filename, String data) {
        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

