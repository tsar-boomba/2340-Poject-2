package com.example.spotifywrapped;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.spotifywrapped.entities.User;
import com.example.spotifywrapped.entities.UserDao;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
