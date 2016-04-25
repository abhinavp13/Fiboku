package com.pabhinav.fiboku.models;

import android.graphics.Bitmap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author pabhinav
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MessageNames {
    
    private final Bitmap bitmap;
    private final String name;
    private final String phoneNumber;

    private MessageNames(Bitmap bitmap, String phoneNumber, String name){
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.bitmap = bitmap;
    }

    public static class MessageNamesBuilder {

        private Bitmap bitmap;
        private String name;
        private String phoneNumber;
        
        public MessageNamesBuilder addPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public MessageNamesBuilder addName(String name){
            this.name = name;
            return this;
        }

        public MessageNamesBuilder addBitmap(Bitmap bitmap){
            this.bitmap = bitmap;
            return this;
        }

        public MessageNames build(){
            return new MessageNames(bitmap, phoneNumber, name);
        }
    }

}
