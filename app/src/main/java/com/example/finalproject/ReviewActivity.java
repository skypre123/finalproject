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
import com.example.finalproject.utils.FileUtils;
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
            diaryJson = FileUtils.readFile(this, "diary.json");
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
                binding.textEvaluation.setText(diaryModel.getEvaluation());
                String imageString = diaryModel.getImage();
                Bitmap bitmap = FileUtils.getBitmapFromString(imageString);
                binding.image.setImageBitmap(bitmap);
            }
        }
    }

    private void deleteDiary() {
        for (DiaryModel diaryModel: array) {
            if (diaryModel.getDate().equals(selectedDate)) {
                array.remove(diaryModel);
                String jsonString = gson.toJson(array);
                FileUtils.writeFile(this, "diary.json", jsonString);
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

}

