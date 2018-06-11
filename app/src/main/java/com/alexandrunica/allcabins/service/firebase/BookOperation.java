package com.alexandrunica.allcabins.service.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alexandrunica.allcabins.cabins.model.BookModel;
import com.alexandrunica.allcabins.cabins.model.ReviewModel;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.map.event.OnGetCabinByIdEvent;
import com.alexandrunica.allcabins.notification.event.OnGetBookByIdEvent;
import com.alexandrunica.allcabins.notification.event.OnGetBooksEvent;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import javax.inject.Inject;

public class BookOperation extends FirebaseOperation {

    @Inject
    protected Bus bus;

    @Inject
    protected DatabaseService databaseService;

    public BookOperation(Context context) {
        super(FirebaseService.TableNames.CABINS_BOOK);
        DaggerDbApplication app = (DaggerDbApplication) context.getApplicationContext();
        app.getAppDbComponent().inject(this);

    }

    public void insertBook(final BookModel bookModel) {
        final String id = mRef.push().getKey();
        mRef.child(id).setValue(bookModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //bus.post(new OnReviewAddEvent(id, reviewModel));
                } else {
                    //bus.post(new OnReviewAddEvent(id, null));
                }
            }
        });
    }

    public void updateBook(final BookModel bookModel, String id) {
        mRef.child(id).setValue(bookModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //bus.post(new OnReviewAddEvent(id, reviewModel));
                } else {
                    //bus.post(new OnReviewAddEvent(id, null));
                }
            }
        });
    }

    public void getBook(final String id) {
        mRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BookModel book = dataSnapshot.getValue(BookModel.class);
                if (book != null) {
                    bus.post(new OnGetBookByIdEvent(book));
                } else {
                    bus.post(new OnGetBookByIdEvent(null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                bus.post(new OnGetBookByIdEvent(null));
            }
        });
    }

    public void getAllBook(final String uid, final String filter) {
        final ArrayList<BookModel> books = new ArrayList<>();
        mRef.orderByChild(filter).equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BookModel bookModel = postSnapshot.getValue(BookModel.class);
                    bookModel.setId(postSnapshot.getKey());
                    books.add(bookModel);
                }
                bus.post(new OnGetBooksEvent(books, filter));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

