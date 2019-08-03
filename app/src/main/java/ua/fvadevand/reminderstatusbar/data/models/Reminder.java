package ua.fvadevand.reminderstatusbar.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

import ua.fvadevand.reminderstatusbar.Const;

@Entity(tableName = Const.TABLE_NAME_REMINDERS)
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "icon_res_name")
    private String mIconName;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "text")
    private String mText;

    @ColumnInfo(name = "timestamp")
    private long mTimestamp;

    @Ignore
    public Reminder() {
    }

    public Reminder(String iconName, String title, String text, long timestamp) {
        mIconName = iconName;
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

    public String getIconName() {
        return mIconName;
    }

    public void setIconName(String iconName) {
        mIconName = iconName;
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
        return mId == reminder.mId &&
                mTimestamp == reminder.mTimestamp &&
                Objects.equals(mIconName, reminder.mIconName) &&
                Objects.equals(mTitle, reminder.mTitle) &&
                Objects.equals(mText, reminder.mText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mIconName, mTitle, mText, mTimestamp);
    }
}
