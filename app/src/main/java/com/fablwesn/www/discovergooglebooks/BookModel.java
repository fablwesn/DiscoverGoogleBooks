package com.fablwesn.www.discovergooglebooks;

/**
 * Model class for a book (search result)
 */
class BookModel {

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
    /*
    * Book's official url to the Google Books Page
    * */
    private String bookUrl;

    /* Empty constructor */
    BookModel() {
    }

    /* Class constructor */
    BookModel(String bookTitle, String bookAuthor, String bookPublisher, String bookCover, String bookUrl) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookCover = bookCover;
        this.bookUrl = bookUrl;
    }

    /*
    * Getter methods
    * */

    // get title string
    public String getBookTitle() {
        return bookTitle;
    }
    // get author string
    public String getBookAuthor() {
        return bookAuthor;
    }
    // get publisher string
    public String getBookPublisher() {
        return bookPublisher;
    }
    // get book cover url as string
    public String getBookCover() {
        return bookCover;
    }
    // get book's official Google Books url as string
    public String getBookUrl() {
        return bookUrl;
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
                ", bookUrl='" + bookUrl + '\'' +
                '}';
    }
}
