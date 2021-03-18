package com.example.equinethermalglove;

import java.io.Serializable;

public class User implements Serializable {

    private String email;

    public User() {
        // Firestore requires this to serialize
    }

    public User(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
