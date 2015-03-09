package com.openpeer.sdk.utils;

import java.util.Collection;

public class CollectionUtils {
    public static void diff(Collection oldCollection, Collection newCollection,
                            Collection added, Collection removed) {
        for(Object oldObject:oldCollection){
            if(!newCollection.contains(oldObject)){
                removed.add(oldObject);
            }
        }
        for(Object newObject:newCollection){
            if(!oldCollection.contains(newObject)){
                added.add(newObject);
            }
        }
    }
}
