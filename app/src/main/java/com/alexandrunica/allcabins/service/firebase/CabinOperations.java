package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.alexandrunica.allcabins.cabins.events.OnGetCabinEvent;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.ShortCabin;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.explore.event.OnGetFiltredCabinEvent;
import com.alexandrunica.allcabins.favorite.event.OnFavDone;
import com.alexandrunica.allcabins.map.event.OnGetCabinByIdEvent;
import com.alexandrunica.allcabins.map.event.OnGetShortCabinEvent;
import com.alexandrunica.allcabins.profile.ProfileFragment;
import com.alexandrunica.allcabins.profile.event.OnOpenAccount;
import com.alexandrunica.allcabins.profile.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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

    public void getCabinFromId(String id) {
        mRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cabin cabin = dataSnapshot.getValue(Cabin.class);
                if (cabin != null) {
                    cabin.setId(dataSnapshot.getKey());
                    bus.post(new OnFavDone(cabin));
                } else {
                    bus.post(new OnFavDone(null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Debug", "Error retrieving data: " + databaseError.getMessage());
                bus.post(new OnFavDone(null));
            }
        });
    }

    public void getCabins() {
        final ArrayList<ShortCabin> cabins = new ArrayList<>();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShortCabin currentCabin = postSnapshot.getValue(ShortCabin.class);
                    currentCabin.setId(postSnapshot.getKey());
                    cabins.add(currentCabin);
                }
                bus.post(new OnGetShortCabinEvent(cabins));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Debug", "Error retrieving data: " + databaseError.getMessage());
                bus.post(new OnGetShortCabinEvent(cabins));
            }
        });
    }

    public void getCabin(String id) {
        mRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cabin cabin = dataSnapshot.getValue(Cabin.class);
                if (cabin != null) {
                    bus.post(new OnGetCabinByIdEvent(cabin));
                } else {
                    bus.post(new OnGetCabinByIdEvent(null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                bus.post(new OnGetCabinByIdEvent(null));
            }
        });
    }

    public void getFiltredCabin(final String city) {
        final ArrayList<Cabin> cabins = new ArrayList<>();
        //mRef.child("city").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
        mRef.orderByChild("city").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Cabin currentCabin = postSnapshot.getValue(Cabin.class);
                    currentCabin.setId(postSnapshot.getKey());
                    cabins.add(currentCabin);
                }
                bus.post(new OnGetFiltredCabinEvent(cabins, city));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void loadMoreData(int totalItemEachLoad, int currentPage) {
        final ArrayList<Cabin> cabins = new ArrayList<>();
        mRef.orderByChild("name").limitToFirst(totalItemEachLoad * currentPage).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                }
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Cabin currentCabin = data.getValue(Cabin.class);
                    currentCabin.setId(data.getKey());
                    cabins.add(currentCabin);
                }
                bus.post(new OnGetCabinEvent(cabins));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
