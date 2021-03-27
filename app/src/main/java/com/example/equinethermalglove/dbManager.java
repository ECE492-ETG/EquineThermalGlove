package com.example.equinethermalglove;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//TODO: add functionality to connect to and store data in the database
public class dbManager {

    Horse horse;

    static FirebaseAuth auth = FirebaseAuth.getInstance();
    static FirebaseFirestore dB = FirebaseFirestore.getInstance();

    public void saveUser(User user) {
        dB.collection("users")
                .document(auth.getCurrentUser().getUid())
                .set(user);
    }

    public static DocumentReference getCurrentUser() {
        return dB.collection("users")
                .document(auth.getCurrentUser().getUid());
    }

    public Task<Void> updateCurrentUser(User user) {
        return dB.collection("users")
                .document(auth.getCurrentUser().getUid())
                .set(user);
    }

    public DocumentReference getUser(String uid) {
        return dB.collection("users")
                .document(uid);
    }

    public Task<Void> updateDeviceToken(String token) {
        return dB.collection("users")
                .document(auth.getCurrentUser().getUid())
                .update("deviceToken", token);
    }

    public void connectToDb() {

    }
    
    public void addHorseData(Horse h) {

    }

    public void deleteHorse(Horse h) {

    }

    public void updateHorse(Horse h) {

    }
}
