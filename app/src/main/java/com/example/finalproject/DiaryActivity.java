package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        gson = new Gson();
        String diaryJson = null;
        try {
            diaryJson = readFromAssets("diary.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        array = gson.fromJson(diaryJson, new TypeToken<List<DiaryModel>>() {}.getType());

        binding.calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                binding.textDiary.setVisibility(View.VISIBLE);
                setting2();
                binding.textDiary.setText(String.format("%d / %d / %d", year, month+1, dayOfMonth));
                selectedDate = ""+year+"-"+(month+1)+""+"-"+dayOfMonth+"";
                binding.editContext.setText("");
                load();
            }
            });

        binding.buttonMove.setOnClickListener(v -> startContentsActivity());

        binding.buttonSave.setOnClickListener(v -> {
            contents = binding.editContext.getText().toString();

            int dataCount = 0;
            int indexModel = 0;
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).getDate().equals(selectedDate)) {
                    dataCount++;
                    indexModel = i;
                }
            }
            if (dataCount == 0) {
                DiaryModel newModel = new DiaryModel();
                newModel.setDate(selectedDate);
                newModel.setText(contents);
                array.add(newModel);

            } else {
                array.get(indexModel).setText(contents);
            }
            String jsonString = gson.toJson(array);
            writeFile("diary.json", jsonString);
            binding.textContext.setText(contents);
            setting1();
        });
    }

    private void startContentsActivity() {
        Intent intent = new Intent(this, ContentsActivity.class);
        startActivity(intent);
    }

    private void writeFile(String filename, String data) {
        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        for (DiaryModel diary : array) {
            if (diary.getDate().equals(selectedDate)) {
                this.item = diary;
                binding.textContext.setText(item.getText());
                setting1();
            }
        }

        binding.buttonChange.setOnClickListener(v -> {
            setting2();
            binding.editContext.setText(item.getText());
        });

        binding.buttonDelete.setOnClickListener(v -> {
            setting2();
            binding.editContext.setText("");
            array.remove(item);
            String jsonString = gson.toJson(array);
            writeFile("diary.json", jsonString);
        });

        if (binding.textContext.getText() == null) {
            binding.textDiary.setVisibility(View.VISIBLE);
            setting2();
        }
    }

    private String readStream(InputStream fis) {
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

    private void setting1() {
        binding.buttonSave.setVisibility(View.INVISIBLE);
        binding.buttonChange.setVisibility(View.VISIBLE);
        binding.buttonDelete.setVisibility(View.VISIBLE);
        binding.editContext.setVisibility(View.INVISIBLE);
        binding.textContext.setVisibility(View.VISIBLE);
    }

    private void setting2() {
        binding.buttonSave.setVisibility(View.VISIBLE);
        binding.buttonChange.setVisibility(View.INVISIBLE);
        binding.buttonDelete.setVisibility(View.INVISIBLE);
        binding.editContext.setVisibility(View.VISIBLE);
        binding.textContext.setVisibility(View.INVISIBLE);
    }

    public String readFromAssets(String name) throws IOException {
        InputStream inputStream = getAssets().open(name);
        return readStream(inputStream);
    }
}