package com.assignment;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by arulsmv on 25/7/23.
 */
//SelfDestructingMap is a wrapper over Hashmap but the key,value will evaoporate after ttl expires
public class SelfDestructingMap<K, V> implements Runnable, SelfDeletingMap <K, V >  {
    // map is actual map holding the Key and Value
    ConcurrentHashMap<K, ImmutablePair<TTL<K>, V>> map;
    // minHeap used for the priority Queue to remove the TTL expired entries from the map.

    PriorityBlockingQueue<TTL<K>> minHeap;
    // WAIT_TIME is set to 500 ms for the garbage collection to kickstart.
    // This value can be increased or decreased. Increasing will cause more stale entries present in the system,
    // decreasing the value will cause the polling to throttle the cpu. in my opinion 500 ms is a good choice as
    // polling & gc collections can be done quickly than waiting long and cleaning up.
    // more the number of clean up it will take longer, however this should not be a bottle neck.
    static long WAIT_TIME = 500;
    // number of locks.
    static int MAX_SHARD=255;
    public static Logger log = Logger.getLogger(SelfDestructingMap.class.getCanonicalName());

    private Thread gcThread;

    boolean exit=false;

    // The use of PriorityBlockingQueue & ConcurrentHashMap both will use separate locks. for our purpos
    // e we need separate locks.
    // those implementation of locks for synchronization is implemented ith shared Lock
    // Having 256 locks the concurrency is not affected unless the cases where both the key resolves to same lock.
    // Also the Hashing is O(1) & PriorityQueue (insert & delete) are O(log(n)), To hold 1M records a binary tree with
    // depth of 20 should do faster computations. so a single lock will not be held for longer time.
    // Will this cause deadlock. No as there is NO cyclic dependency is introduced. If number of locks and
    // hashing matches tahat of concurrent hashmap, then the lock would be anyway has to be held first here.


    protected static final class shardLock {
        protected int num;
        protected synchronized void synch() {}
        protected int getNum() { return num;}
        public shardLock(int n) {num=n;}
    }
    private final shardLock[] shardLocks;

    public SelfDestructingMap() {
        this.map = new ConcurrentHashMap<K, ImmutablePair<TTL<K>, V>>();
        this.minHeap = new PriorityBlockingQueue<TTL<K>>();
        shardLocks = new shardLock[MAX_SHARD + 1];
        for (int i =0;i<=MAX_SHARD; i++) {
            shardLocks[i] = new shardLock(i);
        }
        // Garbage collction is running when the map is created.
        gcThread = new Thread(this);
        gcThread.start();
        log.log(Level.INFO, "Init complete.");
    }

    // Add and entry to the HashMap.
    public void put(K key, V value, long timeoutMs) {
        // if the time to live is zero or negative, nothing to be done.
        if (timeoutMs <= 0)
            return;
        // get the shard number for the keys lock.
        int num = key.hashCode() & MAX_SHARD;
        long t = System.currentTimeMillis() + timeoutMs;
        TTL ttl = new TTL<K>(t, key);
        ImmutablePair<TTL<K>, V> val = map.get(key);
        // if the key is already present remove it and add the new value.
        // Removing ensures for both the cases where the earlier ttl is after new ttl or before the new ttls.
        // It doesn't store historical data as we are optimizing for the both memory as well as time.

        // Observability ADD Metrics code here to increment the number of puts to HashMap.
        if (val != null) {
            log.log(Level.FINEST, "Reseting the value for key " + key);
            this.remove(val.getKey().key);
        }
        synchronized (shardLocks[num]) {
            map.put(key, new ImmutablePair<TTL<K>, V>(ttl, value));
            minHeap.add(ttl);
        }
        log.log(Level.FINEST, "set the value for key " + key + " with ttl " + t);
    }

    public V get(K key) {
        ImmutablePair<TTL<K>, V>  val = map.get(key);
        // To avoid the race condition as there is a poll time of WAIT_TIME. if the ttl is passed for the key return null.
        // If the query hits exact second then return the value stored at that momemt.

        // Observability ADD Metrics code here to increment the number of get to HashMap.
        if (val != null && val.getKey().timeStamp  > System.currentTimeMillis()) {
            // Observability ADD Metrics code here to increment the number of succesful lookup of HashMap.
            log.log(Level.FINEST, "got the value for key " + key);
            return val.getValue();
        }
        // Observability ADD Metrics code here to increment the number of  non-succesful lookup of HashMap.

        log.log(Level.FINEST, "unable to fetch the value for key " + key);
        return null;
    }

    // Remove specific key This can be called from put, ttl-gc, or from the application.
    public void remove(K key) {
        int num = key.hashCode() & MAX_SHARD;
        log.log(Level.FINEST, "removing the value for key " + key);
        synchronized (shardLocks[num]) {
            ImmutablePair<TTL<K>, V> val= map.get(key);
            if (val == null) {
                return;
            }
            TTL ttl = val.getKey();
            if (ttl == null) {
                return;
            }
            //  Observability ADD Metrics code here to increment the number of keys removed.
            map.remove(key);
            minHeap.remove(ttl);
        }
    }

    // A garbage collectoctor thread that runs every 1/2 (WAIT_TIME) seconds to clean the TTL.
    // Opted for this implimentation as it is easy to mange the CPU as well as memory.
    // Sleep will release the lock and it is a good chooice as this will poll for old values to get cleaned up.
    public void run() {
        while (!exit) {
            long ct = System.currentTimeMillis();
            // peek and check if ttl is expired.
            TTL<K> ttl = minHeap.peek();
            int counter = 0;
            while (ttl != null && ttl.timeStamp < ct ) {
                int num = ttl.key.hashCode() & MAX_SHARD;
                synchronized (shardLocks[num]) {
                    map.remove(ttl.key);
                    minHeap.remove(ttl);
                }
                ttl = minHeap.peek();
                counter++;
            }
            // add the same stat to observability.
            log.log(Level.INFO, "Garbage collector removed, " + counter + " records." + " and in the system " + getStat() + " records are live.");
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
            }
        }
    }

    // To Ensure that the size used by both the hashmap and minheap are same and don't hold exsessive storage.
    public int getStat() {
        if (map.size() != minHeap.size())
            log.log(Level.INFO, "Mismatch in the recoreds");
        return map.size();
    }

    public void stop() {
        log.log(Level.FINEST, "Destroying the Garbage Collection thread");
        exit = true;
    }
}
