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

    /* Empty constructor */
    BookModel() {
    }

    /* Class constructor */
    BookModel(String bookTitle, String bookAuthor, String bookPublisher, String bookCover) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookCover = bookCover;
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
