package com.example.equinethermalglove;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * class that holds information about the database and authentication used for the project
 */
public class dbManager {

    // variables
    static FirebaseAuth auth = FirebaseAuth.getInstance();
    static FirebaseFirestore dB = FirebaseFirestore.getInstance();

    // save data (not used)
    public void saveUser(User user) {
        dB.collection("users")
                .document(auth.getCurrentUser().getUid())
                .set(user);
    }

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

//    public static DocumentReference getCurrentUser() {
//        return dB.collection("users")
//                .document(auth.getCurrentUser().getUid());
//    }
//
//    public Task<Void> updateCurrentUser(User user) {
//        return dB.collection("users")
//                .document(auth.getCurrentUser().getUid())
//                .set(user);
//    }
//
//    public DocumentReference getUser(String uid) {
//        return dB.collection("users")
//                .document(uid);
//    }

    // TODO: learn what this does for comments
    public Task<Void> updateDeviceToken(String token) {
        return dB.collection("users")
                .document(auth.getCurrentUser().getUid())
                .update("deviceToken", token);
    }
}
