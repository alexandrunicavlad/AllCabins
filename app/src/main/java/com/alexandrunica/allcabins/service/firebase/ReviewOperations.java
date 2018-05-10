package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexandrunica.allcabins.cabins.events.OnReviewAddEvent;
import com.alexandrunica.allcabins.cabins.model.ReviewModel;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.otto.Bus;

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
}
