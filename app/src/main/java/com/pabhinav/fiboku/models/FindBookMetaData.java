package com.pabhinav.fiboku.models;

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
public class FindBookMetaData {

    private final String name;
    private final String phoneNumber;
    private final String bId;

    private FindBookMetaData(String name, String phoneNumber, String bId){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bId = bId;
    }

    public static class FindBookMetaDataBuilder{
        private String name;
        private String phoneNumber;
        private String bId;

        public FindBookMetaDataBuilder addName(String name){
            this.name = name;
            return this;
        }

        public FindBookMetaDataBuilder addPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public FindBookMetaDataBuilder addBId(String bId){
            this.bId = bId;
            return this;
        }

        public FindBookMetaData build(){
            return new FindBookMetaData(name, phoneNumber, bId);
        }
    }
}
