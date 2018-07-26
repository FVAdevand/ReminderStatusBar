package com.example.vladimir.reminderstatusbar.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.vladimir.reminderstatusbar.Const;

@Entity(tableName = Const.TABLE_NAME_REMINDERS)
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "image_id")
    private int mImageId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "text")
    private String mText;

    @ColumnInfo(name = "timestamp")
    private long mTimestamp;

    public Reminder(int imageId, @NonNull String title, long timestamp) {
        this(imageId, title, null, timestamp);
    }

    public Reminder(int imageId, @NonNull String title, String text, long timestamp) {
        mImageId = imageId;
        mTitle = title;
        mText = text;
        mTimestamp = timestamp;
    }

    public long getId() {
        return mId;
    }

    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int imageId) {
        mImageId = imageId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }
}
