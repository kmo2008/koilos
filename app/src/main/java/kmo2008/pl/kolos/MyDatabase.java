package kmo2008.pl.kolos;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Record.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract RercordDAO rercordDAO();
}
