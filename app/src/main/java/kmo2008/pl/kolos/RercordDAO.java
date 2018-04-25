package kmo2008.pl.kolos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RercordDAO {

    @Insert
    void addRec(Record record);

    @Query("SELECT * FROM Record")
    List<Record> findAll();
}
