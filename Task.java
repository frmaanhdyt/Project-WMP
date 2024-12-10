package com.example.project_wmp;

import java.io.Serializable;

public class Task implements Serializable {
    private String title;
    private String description;
    private String deadline;
    private String imagePath;

    public Task(String title, String description, String deadline, String imagePath) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getImagePath() {
        return imagePath;
    }
}
