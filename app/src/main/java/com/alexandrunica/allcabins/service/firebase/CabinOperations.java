package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Nica on 4/4/2018.
 */

public class CabinOperations extends FirebaseOperation {

    @Inject
    protected Bus bus;

    public CabinOperations(Context context) {
        super(FirebaseService.TableNames.CABINS_TABLE);
        DaggerDbApplication app = (DaggerDbApplication) context.getApplicationContext();
        app.getAppDbComponent().inject(this);

    }

    public void getCabins() {
        final ArrayList<Cabin> cabins = new ArrayList<>();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Cabin currentCabin = postSnapshot.getValue(Cabin.class);
                    currentCabin.setId(postSnapshot.getKey());
                    cabins.add(currentCabin);
                }
                bus.post(new OnGetCabinEvent(cabins));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Debug", "Error retrieving data: " + databaseError.getMessage());
                bus.post(new OnGetCabinEvent(null));
            }
        });
    }

    public void insertCabin(Cabin cabin) {
        String resortReviewID = mRef.push().getKey();
        mRef.child(resortReviewID).setValue(cabin).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Debug", "Insert successful");
                } else {
                    Log.d("Debug", "Error completing task");
                }
            }
        });
    }
}
