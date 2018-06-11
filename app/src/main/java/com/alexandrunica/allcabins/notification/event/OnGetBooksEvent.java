package com.alexandrunica.allcabins.notification.event;

import com.alexandrunica.allcabins.cabins.model.BookModel;

import java.util.List;

public class OnGetBooksEvent {

    private List<BookModel> bookList;
    private String filter;

    public OnGetBooksEvent(List<BookModel> bookList, String filter) {
        this.bookList = bookList;
        this.filter = filter;
    }

    public List<BookModel> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookModel> bookList) {
        this.bookList = bookList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
