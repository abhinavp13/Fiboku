package com.pabhinav.fiboku.booksearch;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pabhinav
 */
public class BookSearchUrl {

    @Getter @Setter
    protected String googleBookSearchApiUrl;

    public BookSearchUrl(BookSearchUrlBuilder bookSearchUrlBuilder){
        this.setGoogleBookSearchApiUrl(bookSearchUrlBuilder.googleBookSearchApiUrl);
    }

    /** Url Builder for google book api **/
    public static class BookSearchUrlBuilder {

        private String googleBookSearchApiUrl = "https://www.googleapis.com/books/v1/volumes?";
        private boolean searchQueryAdded = false;

        public BookSearchUrlBuilder addSearchQuery(String query){
            query = query.replace(" ","%20");
            googleBookSearchApiUrl = googleBookSearchApiUrl + "q=" + query;
            searchQueryAdded = true;
            return this;
        }

        public BookSearchUrlBuilder addStartIndex(int startIndex){
            googleBookSearchApiUrl = googleBookSearchApiUrl + "&startIndex=" + startIndex;
            return this;
        }

        public BookSearchUrlBuilder addMaxResults(int maxResults){
            googleBookSearchApiUrl = googleBookSearchApiUrl + "&maxResults=" + maxResults;
            return this;
        }

        public BookSearchUrl build(){

            /** Searched query is not present **/
            if(!searchQueryAdded){
                throw new UnsupportedOperationException("You forgot to put searched query while building url");
            } else {
                return new BookSearchUrl(this);
            }
        }
    }
}
