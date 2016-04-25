package com.pabhinav.fiboku.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Describing book result from google book api.
 *
 * @author pabhinav
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchedBook implements Parcelable {

    /** Title of book **/
    private final String title;

    /** Sub-title of book, if exists **/
    private final String subTitle;

    /** List of book authors **/
    private final ArrayList<String> authors;

    /** The publishing press name for this book **/
    private final String publisher;

    /** Date of publishing for this book **/
    private final String publishedDate;

    /** UploadedBook description **/
    private final String description;

    /** This is a 10-digit or 13-digit isbn value for the book **/
    private final String industryIdentifier;

    /** UploadedBook thumbnail link (normal size) **/
    private final String thumbnailLink;

    /** private constructor for this class **/
    private SearchedBook(SearchedBookBuilder searchedBookBuilder){
        title = searchedBookBuilder.title;
        subTitle = searchedBookBuilder.subTitle;
        authors = searchedBookBuilder.authors;
        publishedDate = searchedBookBuilder.publishedDate;
        publisher = searchedBookBuilder.publisher;
        description = searchedBookBuilder.description;
        industryIdentifier = searchedBookBuilder.industryIdentifier;
        thumbnailLink = searchedBookBuilder.thumbnailLink;
    }

    /** Builder class **/
    public static class SearchedBookBuilder{
        
        private String title;
        private String subTitle;
        private ArrayList<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private String industryIdentifier;
        private String thumbnailLink;

        public SearchedBookBuilder addTitle(String title){
            this.title = title;
            return this;
        }
        
        public SearchedBookBuilder addSubtitle(String subTitle){
            this.subTitle = subTitle;
            return this;
        }

        public SearchedBookBuilder addDescription(String description){
            this.description = description;
            return this;
        }

        public SearchedBookBuilder addAuthors(ArrayList<String> authors) {
            this.authors = authors;
            return this;
        }

        public SearchedBookBuilder addPublisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public SearchedBookBuilder addPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public SearchedBookBuilder addIndustryIdentifier(String industryIdentifier) {
            this.industryIdentifier = industryIdentifier;
            return this;
        }

        public SearchedBookBuilder addThumbnailLink(String thumbnailLink) {
            this.thumbnailLink = thumbnailLink;
            return this;
        }

        public SearchedBook build(){
            return new SearchedBook(this);
        }
    }


    /*******************************************************/
    /****** Below methods make this class Parcelable *******/
    /******************************************************/

    protected SearchedBook(Parcel in) {
        title = in.readString();
        subTitle = in.readString();
        if (in.readByte() == 0x01) {
            authors = new ArrayList<String>();
            in.readList(authors, String.class.getClassLoader());
        } else {
            authors = null;
        }
        publisher = in.readString();
        publishedDate = in.readString();
        description = in.readString();
        industryIdentifier = in.readString();
        thumbnailLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subTitle);
        if (authors == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(authors);
        }
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeString(description);
        dest.writeString(industryIdentifier);
        dest.writeString(thumbnailLink);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SearchedBook> CREATOR = new Parcelable.Creator<SearchedBook>() {
        @Override
        public SearchedBook createFromParcel(Parcel in) {
            return new SearchedBook(in);
        }

        @Override
        public SearchedBook[] newArray(int size) {
            return new SearchedBook[size];
        }
    };
}
