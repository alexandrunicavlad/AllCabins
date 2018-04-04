package com.alexandrunica.allcabins.service.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Nica on 4/4/2018.
 */

public class FirebaseOperation {


    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef;

    public FirebaseOperation(String tableName) {
        mRef = mDatabase.getReference(tableName);
    }
}