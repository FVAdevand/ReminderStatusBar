package ua.fvadevand.reminderstatusbar.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import ua.fvadevand.reminderstatusbar.Const;

import java.util.Objects;

@Entity(tableName = Const.TABLE_NAME_REMINDERS)
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "icon_id")
    private int mIconId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "text")
    private String mText;

    @ColumnInfo(name = "timestamp")
    private long mTimestamp;

    @Ignore
    public Reminder(int iconId, @NonNull String title, long timestamp) {
        this(iconId, title, null, timestamp);
    }

    public Reminder(int iconId, @NonNull String title, String text, long timestamp) {
        mIconId = iconId;
        mTitle = title;
        mText = text;
        mTimestamp = timestamp;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return mIconId == reminder.mIconId &&
                mTimestamp == reminder.mTimestamp &&
                Objects.equals(mTitle, reminder.mTitle) &&
                Objects.equals(mText, reminder.mText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mIconId, mTitle, mText, mTimestamp);
    }
}
