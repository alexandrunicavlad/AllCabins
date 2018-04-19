package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.ProfileFragment;
import com.alexandrunica.allcabins.profile.event.OnOpenAccount;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import javax.inject.Inject;

public class ProfileOperations extends FirebaseOperation {

    @Inject
    protected Bus bus;

    @Inject
    protected DatabaseService databaseService;

    public ProfileOperations(Context context) {
        super(FirebaseService.TableNames.USERS_TABLE);
        DaggerDbApplication app = (DaggerDbApplication) context.getApplicationContext();
        app.getAppDbComponent().inject(this);
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    databaseService.updateOrAdd(user);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getUser(String id) {
        mRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user==null) {
                    bus.post(new OnOpenAccount(ProfileFragment.newInstance(null)));
                } else {
                    databaseService.writeUser(user);
                    bus.post(new OnOpenAccount(ProfileFragment.newInstance(user)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Debug", "Error retrieving data: " + databaseError.getMessage());
                bus.post(new OnOpenAccount(ProfileFragment.newInstance(null)));
            }
        });
    }

    public void insertUser(User user) {
        mRef.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void checkandInsertProfileExists(final User user) {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getId())) {

                } else {
                    insertUser(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addFavorite(String id, String cabinId) {
        mRef.child(id).child("favorites").child(cabinId).setValue(cabinId).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void removeFavorite(String id, String cabinId) {
        mRef.child(id).child("favorites").child(cabinId).removeValue();
    }
}

