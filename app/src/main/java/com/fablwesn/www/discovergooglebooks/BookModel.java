package com.fablwesn.www.discovergooglebooks;

/**
 * Model class for a book (search result)
 */
public class BookModel {

    /*
    * Book's title
    * */
    private String bookTitle;
    /*
    * Book's author
    * */
    private String bookAuthor;
    /*
    * Book's publisher
    * */
    private String bookPublisher;
    /*
    * Book's cover uri
    * */
    private String bookCover;

    /* Class constructor */
    public BookModel(String bookTitle, String bookAuthor, String bookPublisher, String bookCover) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookCover = bookCover;
    }

    /*
    * Getter methods
    * */

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public String getBookCover() {
        return bookCover;
    }

    /*
    * toString method
    * */
    @Override
    public String toString() {
        return "BookModel{" +
                "bookTitle='" + bookTitle + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", bookPublisher='" + bookPublisher + '\'' +
                ", bookCover='" + bookCover + '\'' +
                '}';
    }
}
