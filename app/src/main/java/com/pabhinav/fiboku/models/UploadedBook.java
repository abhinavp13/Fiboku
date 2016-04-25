package com.pabhinav.fiboku.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Describing book uploaded by a user.
 *
 * @author pabhinav
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(exclude = {"id"})
public class UploadedBook implements Parcelable {

    /** id of book in database **/
    private int id;

    /** {@code SearchedBook} model for basic book details **/
    private final SearchedBook searchedBook;

    /** Enum describing used book condition **/
    private final BookCondition condition;

    /** Used UploadedBook condition description by user **/
    private final String conditionDescription;

    /** UploadedBook upload date and time **/
    private final String uploadTimestamp;

    /** UploadedBook image **/
    private byte[] bookImage;
    
    /** private constructor **/
    private UploadedBook(UploadedBookBuilder uploadedBookBuilder){
        searchedBook = uploadedBookBuilder.searchedBook;
        condition = uploadedBookBuilder.condition;
        conditionDescription = uploadedBookBuilder.conditionDescription;
        uploadTimestamp = uploadedBookBuilder.uploadTimestamp;
        bookImage = uploadedBookBuilder.bookImage;
    }

    /** Builder Class **/
    public static class UploadedBookBuilder {
        
        private SearchedBook searchedBook;
        private BookCondition condition;
        private String conditionDescription;
        private String uploadTimestamp;
        private byte[] bookImage;

        public UploadedBookBuilder addSearchedBook(SearchedBook searchedBook){
            this.searchedBook = searchedBook;
            return this;
        }

        public UploadedBookBuilder addCondition(BookCondition condition) {
            this.condition = condition;
            return this;
        }

        public UploadedBookBuilder addConditionDescription(String conditionDescription) {
            this.conditionDescription = conditionDescription;
            return this;
        }

        public UploadedBookBuilder addUploadTimestamp(String uploadTimestamp) {
            this.uploadTimestamp = uploadTimestamp;
            return this;
        }

        public UploadedBookBuilder addBookImage(byte[] bookImage){
            this.bookImage = bookImage;
            return this;
        }

        public UploadedBook build(){
            return new UploadedBook(this);
        }
    }

    /*******************************************************/
    /****** Below methods make this class Parcelable *******/
    /******************************************************/

    protected UploadedBook(Parcel in) {
        id = in.readInt();
        searchedBook = (SearchedBook) in.readValue(SearchedBook.class.getClassLoader());
        condition = (BookCondition) in.readValue(BookCondition.class.getClassLoader());
        conditionDescription = in.readString();
        uploadTimestamp = in.readString();
        bookImage = new byte[in.readInt()];
        in.readByteArray(bookImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeValue(searchedBook);
        dest.writeValue(condition);
        dest.writeString(conditionDescription);
        dest.writeString(uploadTimestamp);
        dest.writeInt(bookImage.length);
        dest.writeByteArray(bookImage);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UploadedBook> CREATOR = new Parcelable.Creator<UploadedBook>() {
        @Override
        public UploadedBook createFromParcel(Parcel in) {
            return new UploadedBook(in);
        }

        @Override
        public UploadedBook[] newArray(int size) {
            return new UploadedBook[size];
        }
    };
}
