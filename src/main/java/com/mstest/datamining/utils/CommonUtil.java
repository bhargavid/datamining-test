package com.mstest.datamining.utils;

import java.util.Collections;

/**
 * Created by bloganathan on 9/21/14.
 */
public class CommonUtil {
    //to avoid for each failing from null list
    public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
        return iterable == null ? Collections.<T>emptyList() : iterable;
    }
}
