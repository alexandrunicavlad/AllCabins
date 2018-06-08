package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;

import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import static com.alexandrunica.allcabins.service.firebase.FirebaseService.TableNames.CABINS_BOOK;
import static com.alexandrunica.allcabins.service.firebase.FirebaseService.TableNames.CABINS_REVIEWS_TABLE;
import static com.alexandrunica.allcabins.service.firebase.FirebaseService.TableNames.CABINS_TABLE;
import static com.alexandrunica.allcabins.service.firebase.FirebaseService.TableNames.USERS_TABLE;

/**
 * Created by Nica on 4/4/2018.
 */

public class FirebaseService {

    @Inject
    FirebaseAuth firebaseAuth;
    @Inject
    Context context;
    @Inject
    Bus bus;

    public static class TableNames {
        public static final String USERS_TABLE = "Users";
        public static final String CABINS_TABLE = "Cabins";
        public static final String CABINS_REVIEWS_TABLE = "CabinsReviews";
        public static final String CABINS_PHOTO_TABLE = "CabinsPhoto";
        public static final String CABINS_BOOK = "Books";
    }

    public FirebaseService(DaggerDbApplication app) {
        app.getAppDbComponent().inject(this);
    }

    public static FirebaseOperation getFirebaseOperation(String tableName, Context context) {
        switch (tableName) {
            case CABINS_TABLE:
                return new CabinOperations(context);

            case USERS_TABLE:
                return new ProfileOperations(context);

            case CABINS_REVIEWS_TABLE:
                return new ReviewOperations(context);

            case CABINS_BOOK:
                return new BookOperation(context);
            default:
                return null;
        }

    }

}


