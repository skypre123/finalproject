package com.example.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.finalproject.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public  String selectedDate = null;
    public String contents = null;
    public List<DiaryModel> array = null;
    public DiaryModel item = null;
    public Gson gson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        binding.calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                binding.textDiary.setVisibility(View.VISIBLE);
                binding.textDiary.setText(String.format("%d / %d / %d", year, month+1, dayOfMonth));
                selectedDate = ""+year+"-"+(month+1)+""+"-"+dayOfMonth+"";
                load();
            }
            });

        binding.buttonMove.setOnClickListener(v -> startContentsActivity());
    }

    private void startContentsActivity() {
        Intent intent = new Intent(this, ContentsActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }

    private void load() {

        int dataCount = 0;
        int indexModel = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getDate().equals(selectedDate)) {
                dataCount++;
                indexModel = i;
            }
        }
        if (dataCount == 0) {
            binding.buttonMove.setVisibility(View.VISIBLE);
            binding.buttonMove.setText("일기 쓰러가기");

        } else {
            this.item = array.get(indexModel);
            binding.textTitle.setText(item.getTitle());
            binding.textEvaluation.setText(item.getEvaluation());
            binding.textTitle.setVisibility(View.VISIBLE);
            binding.textEvaluation.setVisibility(View.VISIBLE);
            binding.buttonMove.setVisibility(View.VISIBLE);
        }
    }


//        binding.buttonDelete.setOnClickListener(v -> {
//            setting2();
//            binding.editContext.setText("");
//            array.remove(item);
//            String jsonString = gson.toJson(array);
//            writeFile("diary.json", jsonString);
//        });

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Toast.makeText(this, "권한이 설정되었습니다", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "권한이 설정되지 않았습니다 권한이 없으므로 앱을 종료합니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    });
}