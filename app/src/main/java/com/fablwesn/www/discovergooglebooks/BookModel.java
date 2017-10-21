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
    * Book's published year
    * */
    private String bookReleaseYear;
    /*
    * Book's cover uri
    * */
    private String bookCover;
    /*
    * Book's official url to the Google Books Page
    * */
    private String bookUrl;

    /* Class constructor */
    BookModel(String bookTitle, String bookAuthor, String bookPublisher, String bookReleaseYear, String bookCover, String bookUrl) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookReleaseYear = bookReleaseYear;
        this.bookCover = bookCover;
        this.bookUrl = bookUrl;
    }

   /*
    * Getter methods
    * */

    // get title string
    String getBookTitle() {
        return bookTitle;
    }

    // get author string
    String getBookAuthor() {
        return bookAuthor;
    }

    // get publisher string
    String getBookPublisher() {
        return bookPublisher;
    }

    // get release year string
    String getBookReleaseYear() {
        return bookReleaseYear;
    }

    // get book cover url as string
    String getBookCover() {
        return bookCover;
    }

    // get book's official Google Books url as string
    String getBookUrl() {
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
                ", bookReleaseYear='" + bookReleaseYear + '\'' +
                ", bookCover='" + bookCover + '\'' +
                ", bookUrl='" + bookUrl + '\'' +
                '}';
    }
}
