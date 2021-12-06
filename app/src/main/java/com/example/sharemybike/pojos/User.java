package com.example.sharemybike.pojos;

import com.google.firebase.auth.FirebaseAuth;

public class User {

    private String name;
    private String email;
    private FirebaseAuth mAuth;

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public void setAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    private static User mInstance;

    //nobody can instantiate
    private User() {

    }

    public static User getInstance() {
        if (mInstance == null)
            mInstance = new User();
        return mInstance;
    }
}
