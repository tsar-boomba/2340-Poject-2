package com.example.spotifywrapped.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class User implements Parcelable {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "firstName")
    public String firstName;

    @ColumnInfo(name = "lastName")
    public String lastName;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    @Nullable
    @ColumnInfo(name = "wrappeds")
    public List<String> wrappeds;

    public User() {}

    public User(Parcel parcel) {
        uid = parcel.readInt();
        firstName = parcel.readString();
        lastName = parcel.readString();
        username = parcel.readString();
        password = parcel.readString();
        wrappeds = parcel.createStringArrayList();
    }

    public List<Wrapped> deserializeWrappeds() {
        List<Wrapped> wrappeds;
        if (this.wrappeds != null) {
            wrappeds = this.wrappeds.stream().map((wrappedStr) -> new Gson().fromJson(wrappedStr, Wrapped.class)).collect(Collectors.toList());
        } else {
            wrappeds = new ArrayList<>();
        }

        return wrappeds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeStringList(wrappeds);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", wrappeds=" + wrappeds +
                '}';
    }
}
