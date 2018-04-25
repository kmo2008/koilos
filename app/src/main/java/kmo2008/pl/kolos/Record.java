package kmo2008.pl.kolos;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Record {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "password")
    String password;

    @ColumnInfo(name = "title")
    String title;


    public Record() {
    }

    public Record(String password, String title) {
        this.password = password;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title+ ": "+password;
    }
}
