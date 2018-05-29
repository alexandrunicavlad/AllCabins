package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexandrunica.allcabins.cabins.events.OnReviewAddEvent;
import com.alexandrunica.allcabins.cabins.events.OnReviewFailEvent;
import com.alexandrunica.allcabins.cabins.model.ReviewModel;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.explore.event.OnGetFiltredCabinEvent;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class ReviewOperations extends FirebaseOperation {

    @Inject
    protected Bus bus;

    @Inject
    protected DatabaseService databaseService;

    public ReviewOperations(Context context) {
        super(FirebaseService.TableNames.CABINS_REVIEWS_TABLE);
        DaggerDbApplication app = (DaggerDbApplication) context.getApplicationContext();
        app.getAppDbComponent().inject(this);

    }

    public void insertReview(final ReviewModel reviewModel) {
        final String id = mRef.push().getKey();
        mRef.child(id).setValue(reviewModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    bus.post(new OnReviewAddEvent(id, reviewModel));
                } else {
                    bus.post(new OnReviewAddEvent(id, null));
                }
            }
        });
    }

    public void getReviewFromName(final ReviewModel reviewModel) {
        final ArrayList<ReviewModel> reviewModels = new ArrayList<>();
        //mRef.child("city").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
        mRef.orderByChild("from").equalTo(reviewModel.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ReviewModel currentReview = postSnapshot.getValue(ReviewModel.class);
                    if(currentReview.getCabin().equals(reviewModel.getCabin())) {
                        Calendar cal = Calendar.getInstance();
                        Calendar cal1 = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            cal.setTime(sdf.parse(currentReview.getDate()));
                            long diff = cal1.getTimeInMillis() - cal.getTimeInMillis();
                            long seconds = diff / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;
                            if (days < 30) {
                                reviewModels.add(reviewModel);
                                break;
                            }
                        } catch (ParseException e) {
                            reviewModels.add(reviewModel);
                        }

                    }
                }
                if (reviewModels.size() > 0) {
                    bus.post(new OnReviewFailEvent(false));
                } else {
                    insertReview(reviewModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
