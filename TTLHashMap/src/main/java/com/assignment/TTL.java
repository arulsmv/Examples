package com.assignment;

/**
 * Created by arulsmv on 25/7/23.
 */
// A wrapper for heap.
public class TTL<K> implements Comparable<TTL<K>> {
    Long timeStamp;
    K key;

    public TTL(Long timeStamp, K key) {
        this.timeStamp = timeStamp;
        this.key = key;
    }

    public int compareTo(TTL<K> other) {
        return Double.compare(timeStamp , other.timeStamp);
    }
}
