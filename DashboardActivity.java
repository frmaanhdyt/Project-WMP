package com.example.project_wmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;




public class DashboardActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private SharedPreferences sharedPreferences;
    private EditText taskTitleEditText, taskDescriptionEditText;
    private TextView taskDeadlineTextView;
    private String selectedDeadline;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView taskImageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        taskDeadlineTextView = findViewById(R.id.taskDeadlineTextView);
        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button viewTasksButton = findViewById(R.id.viewTasksButton);

        taskImageView = findViewById(R.id.taskImageView);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);

        taskDeadlineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                startActivity(new Intent(DashboardActivity.this, Login_page.class));
                finish();
            }
        });

        viewTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTaskList();
            }
        });

        Button captureImageButton = findViewById(R.id.captureImageButton);
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }
    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                Log.d("DashboardActivity", "Camera intent is being launched");
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Log.d("DashboardActivity", "No camera app found");
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            taskImageView.setImageBitmap(imageBitmap);
            taskImageView.setVisibility(View.VISIBLE); // Show the image view
        }
    }


    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(DashboardActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth++;
                    selectedDeadline = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                    taskDeadlineTextView.setText(selectedDeadline);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void addTask() {
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(selectedDeadline)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(title, description, selectedDeadline, imageUri != null ? imageUri.toString() : null);
        taskList.add(newTask);
        taskAdapter.notifyItemInserted(taskList.size() - 1);

        taskTitleEditText.setText("");
        taskDescriptionEditText.setText("");
        taskDeadlineTextView.setText("Task Deadline");
        selectedDeadline = null;
        taskImageView.setVisibility(View.GONE);
    }


    private void goToTaskList() {
        Intent intent = new Intent(DashboardActivity.this, TaskListActivity.class);
        intent.putExtra("taskList", taskList);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(DashboardActivity.this, Login_page.class));
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
