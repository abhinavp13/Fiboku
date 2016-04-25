package com.pabhinav.fiboku.booksearch;

import com.pabhinav.fiboku.models.BookImageType;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.util.BookUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Setter;

/**
 * @author pabhinav
 */
public class BookSearchResponseHandler {

    /** ArrayList for all {@code SearchedBook} data items parsed from json string **/
    private ArrayList<SearchedBook> searchedBookArrayList;

    @Setter
    private BookSearchResponseEvents bookSearchResponseEvents;

    /** Handles Book Search Response data **/
    public void handleBookSearchResponse(String responseString){

        JSONArray jsonArray = null;
        try {

            /** Try to get items from Json object **/
            jsonArray = getItemsJSONArrayFromString(responseString);

        }catch (Exception e){

            /** Notify that parsing failed **/
            if(bookSearchResponseEvents != null){
                bookSearchResponseEvents.onBookSearchResponseParsedFailed();
            }
            return;
        }

        if(jsonArray == null || jsonArray.length() == 0){

            /** Notify that empty 'items' body found **/
            if(bookSearchResponseEvents != null){
                bookSearchResponseEvents.onBookSearchResponseItemsBodyEmpty();
            }
            return;
        }

        /** Parse 'items' further and populate arrayList for searched books **/
        /** Call book search response successfully parsed **/
        try {

            /** Try to parse further into 'items' category **/
            parseResponseItems(jsonArray);

            /** Notify successfully parsed **/
            bookSearchResponseEvents.onBookSearchResponseSuccessfullyParsed(searchedBookArrayList);

        }catch (Exception e){

            /** Notify that parsing failed **/
            if(bookSearchResponseEvents != null ){
                bookSearchResponseEvents.onBookSearchResponseParsedFailed();
            }
        }
    }

    /** Helps parse json response, given json array denoting 'items'  **/
    private void parseResponseItems(JSONArray jsonArray) throws Exception{

        /** Loop through every 'items' element and get searched book details **/
        searchedBookArrayList = new ArrayList<>();
        for(int i = 0; i<jsonArray.length(); i++){
            SearchedBook searchedBook = parseSingleItemAndPrepareSearchedBook(jsonArray.getJSONObject(i));
            if(searchedBook != null) {
                searchedBookArrayList.add(searchedBook);
            }
        }
    }

    /** Fetches items array present in json string **/
    private JSONArray getItemsJSONArrayFromString(String jsonString) throws Exception{

        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.has(Constants.BOOK_API_RESPONSE_ITEMS)) {
            return (JSONArray) jsonObject.get(Constants.BOOK_API_RESPONSE_ITEMS);
        } else {
            return null;
        }
    }

    /**
     *  This method helps parse the JSON object returned by
     *  google books api.
     *
     *  Required Sub-part of response type from google book api :
     *
     *  'kind' : 'book#volume' (ignore book shelves)
     *  "volumeInfo": {
     *      "title": string,
     *      "subtitle": string,
     *      "authors": [ list of strings ],
     *      "publisher": string,
     *      "publishedDate": string,
     *      "description": string,
     *      "industryIdentifiers": [{
     *          "type": string,
     *          "identifier": string
     *      }
     *      "imageLinks": {
     *          "smallThumbnail": string,
     *          "thumbnail": string,
     *          "small": string,
     *          "medium": string,
     *          "large": string,
     *          "extraLarge": string
     *      },
     *  }
     *
     */
    public SearchedBook parseSingleItemAndPrepareSearchedBook(JSONObject jsonObject) throws Exception {

        /** Does not have a 'kind' tag, data could be anything, return null **/
        String kindForResult;
        if(jsonObject.has(Constants.BOOK_API_RESPONSE_KIND)) {
            kindForResult = jsonObject.getString(Constants.BOOK_API_RESPONSE_KIND);
        } else {
            return null;
        }

        /** Not a book volume **/
        if(!kindForResult.equals("books#volume")){
            return null;
        }

        /** Volume info from 'items' **/
        JSONObject volumeInfo;
        if(jsonObject.has(Constants.BOOK_API_RESPONSE_VOLUME_INFO)) {
            volumeInfo = (JSONObject) jsonObject.getJSONObject(Constants.BOOK_API_RESPONSE_VOLUME_INFO);
        } else {
            return null;
        }

        /** Title **/
        String title;
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_TITLE)) {
            title = volumeInfo.getString(Constants.BOOK_API_RESPONSE_TITLE);
        } else {
            return null;
        }

        /** Subtitle **/
        String subtitle = "";
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_SUBTITLE)) {
            subtitle = volumeInfo.getString(Constants.BOOK_API_RESPONSE_SUBTITLE);
        }

        /** Authors **/
        JSONArray authors;
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_AUTHORS)) {
            authors = volumeInfo.getJSONArray(Constants.BOOK_API_RESPONSE_AUTHORS);
        } else {
            return null;
        }
        ArrayList<String> authorsList = new ArrayList<>();
        for(int j = 0; j< authors.length(); j++){
            String author = authors.getString(j);
            authorsList.add(author);
        }

        /** Publisher **/
        String publisher = "";
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_PUBLISHER)) {
            publisher = volumeInfo.getString(Constants.BOOK_API_RESPONSE_PUBLISHER);
        }

        /** PublishedDate **/
        String publishedDate = "";
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_PUBLISHED_DATE)) {
            publishedDate = volumeInfo.getString(Constants.BOOK_API_RESPONSE_PUBLISHED_DATE);
        }

        /** Description **/
        String description = "";
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_DESCRIPTION)) {
            description = volumeInfo.getString(Constants.BOOK_API_RESPONSE_DESCRIPTION);
        }

        /** ISBN **/
        String industryIdentifier = "";
        JSONArray identifiersJSONArray = null;
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER)) {
            identifiersJSONArray = volumeInfo.getJSONArray(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER);
        }
        for(int j = 0; identifiersJSONArray!=null && j < identifiersJSONArray.length(); j++){
            JSONObject identifierJsonObject = identifiersJSONArray.getJSONObject(j);

            String type = "";
            if(identifierJsonObject.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE)) {
                type = identifierJsonObject.getString(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE);
            }
            if(type.equals(Constants.BOOK_API_RESPONSE_ISBN_13)){
                if(identifierJsonObject.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER)) {
                    industryIdentifier = identifierJsonObject.getString(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER);
                }
            }
        }

        /** ImageLink **/
        JSONObject imageLinksJSONObject;
        if(volumeInfo.has(Constants.BOOK_API_RESPONSE_IMAGELINKS)) {
            imageLinksJSONObject = (JSONObject) volumeInfo.get(Constants.BOOK_API_RESPONSE_IMAGELINKS);
        } else {
            imageLinksJSONObject = new JSONObject();
        }
        HashMap<BookImageType, String> bookImageTypeStringHashMap = new HashMap<>();
        if(imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL)) {
            bookImageTypeStringHashMap.put(BookImageType.SMALL_THUMBNAIL,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL));
        }
        if(imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL)) {
            bookImageTypeStringHashMap.put(BookImageType.THUMBNAIL,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL));
        }
        if(imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALL)) {
            bookImageTypeStringHashMap.put(BookImageType.SMALL,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALL));
        }
        if(imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_MEDIUM)) {
            bookImageTypeStringHashMap.put(BookImageType.MEDIUM,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_MEDIUM));
        }
        if(imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_LARGE)) {
            bookImageTypeStringHashMap.put(BookImageType.LARGE,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_LARGE));
        }
        if(imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_XLARGE)) {
            bookImageTypeStringHashMap.put(BookImageType.XLARGE,
                    imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_XLARGE));
        }

        String thumbnail = BookUtil.preferredImageLink(bookImageTypeStringHashMap);

        /** return the whole make over for searched book **/
        return new SearchedBook.SearchedBookBuilder()
                                            .addTitle(title)
                                            .addSubtitle(subtitle)
                                            .addDescription(description)
                                            .addAuthors(authorsList)
                                            .addIndustryIdentifier(industryIdentifier)
                                            .addPublishedDate(publishedDate)
                                            .addPublisher(publisher)
                                            .addThumbnailLink(thumbnail)
                                            .build();
    }

    /** Callback events for each response parsing mechanism timeline **/
    public interface BookSearchResponseEvents{
        void onBookSearchResponseSuccessfullyParsed(ArrayList<SearchedBook> searchedBookArrayList);
        void onBookSearchResponseParsedFailed();
        void onBookSearchResponseItemsBodyEmpty();
    }

}
