package com.example.contacthandbook.fragment.classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.contacthandbook.R;

public class ClassDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String className = intent.getStringExtra("className");
        setTitle("Class:" + className);
        setContentView(R.layout.activity_class_detail);
    }
}