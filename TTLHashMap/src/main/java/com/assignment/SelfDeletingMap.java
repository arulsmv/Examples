package com.assignment;

/**
 * Created by arulsmv on 25/7/23. A copy Given in the assignment.
 */

public interface SelfDeletingMap <K,V> {
    //copyPaste
    // If there is no entry with the key in the map, add the key/value pair as a new entry.
    // If there is an existing entry with the key, the current entry will be replaced with the new key/value pair.
    // If the newly added entry is not removed after timeoutMs since it's added to the map, remove it.
    void put(K key, V value, long timeoutMs);

    // Get the value associated with the key if present; otherwise, return null.
    V get(K key);

    // Remove the entry associated with key, if any.
    void remove(K key);
}
