package com.example.equinethermalglove;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * class that holds information about the database and authentication used for the project
 */
public class dbManager {

    // variables
    static FirebaseAuth auth = FirebaseAuth.getInstance();
    static FirebaseFirestore dB = FirebaseFirestore.getInstance();

    /**
     * get the database reference
     * @return
     *      database reference
     */
    public static FirebaseFirestore getdB() {
        return dB;
    }

    /**
     * get the authentication reference for the user
     * @return
     *      user authentication data
     */
    public static FirebaseAuth getAuth() {
        return auth;
    }
}
