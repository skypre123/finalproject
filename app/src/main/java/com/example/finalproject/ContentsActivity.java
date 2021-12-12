package com.example.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.finalproject.databinding.ActivityContentsBinding;
import com.example.finalproject.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ContentsActivity extends AppCompatActivity {
    private ActivityContentsBinding binding;
    public String selectedDate;
    public String title;
    public String contents;
    public String evaluation;
    public Bitmap bitmap;
    public String imageString;
    public List<DiaryModel> array = new ArrayList<>();
    public Gson gson = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gson = new Gson();
        String diaryJson = null;
        try {
            diaryJson = FileUtils.readFile(this, "diary.json");
            array = gson.fromJson(diaryJson, new TypeToken<List<DiaryModel>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("selectedDate");
        binding.textDiary.setText(selectedDate+" 다이어리");

        binding.buttonBack.setOnClickListener(v -> onBackPressed());
        binding.buttonPhoto.setOnClickListener(v -> getPhoto());
        binding.buttonSave.setOnClickListener(v -> {
            save();
            startReviewActivity();
        });
    }

    private void startReviewActivity() {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }

    private void save() {
        title = binding.editTitle.getText().toString();
        contents = binding.editContents.getText().toString();
        imageString = getStringFromBitmap(bitmap);
        int id = binding.radioGroup.getCheckedRadioButtonId();
        RadioButton rb = findViewById(id);
        evaluation = rb.getText().toString();


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
            newModel.setTitle(title);
            newModel.setText(contents);
            newModel.setEvaluation(evaluation);
            newModel.setImage(imageString);
            array.add(newModel);

        } else {
            array.get(indexModel).setTitle(title);
            array.get(indexModel).setText(contents);
            array.get(indexModel).setEvaluation(evaluation);
            array.get(indexModel).setImage(imageString);
        }
        String jsonString = gson.toJson(array);
        FileUtils.writeFile(this, "diary.json", jsonString);
    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        resultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            bitmap = loadBitmap(selectedImage);
            binding.image.setImageBitmap(bitmap);
            binding.image.setVisibility(View.VISIBLE);
            binding.buttonPhoto.setVisibility(View.INVISIBLE);
        }
    });

    private Bitmap loadBitmap(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return BitmapFactory.decodeFile(picturePath);
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

}
