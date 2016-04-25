package com.pabhinav.fiboku.util;

import com.pabhinav.fiboku.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author pabhinav
 */
public class MessageUtil {

    public static void sortMessages(ArrayList<Message> messageArrayList){

        Collections.sort(messageArrayList, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return -1*lhs.getTimeForMessage().compareTo(rhs.getTimeForMessage());
            }
        });
    }
}
