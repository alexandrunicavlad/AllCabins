package com.alexandrunica.allcabins.notification.event;

import com.alexandrunica.allcabins.cabins.model.BookModel;

public class OnGetBookByIdEvent {

    private BookModel bookModel;

    public OnGetBookByIdEvent(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public BookModel getBookModel() {
        return bookModel;
    }
}
