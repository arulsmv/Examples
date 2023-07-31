package com.assignment;
import java.util.Random;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Created by arulsmv on 25/7/23.
 */
// Bench mark testing There are three inner classes of runnable use for adding a record, deleting a record
// and probing(get) a record. Those three can be instanciated into multiple object to perfrom the benchmark test.
public class Main {
    static final int MaxRand = 10000; // Determines the range Max record for this test i have seen is 4000.
    static  SelfDestructingMap<String, String> sDMap = new SelfDestructingMap<String, String>();
    // TODO(arulsmv): Make the following as arg parse or read from env to avoid rebuilding.
    static final long INSERT_FREQ = 40; // 25 QPS per thread.
    static final long PROBE_FREQ = 20; //  50 QPS per thread.
    static final long DELETE_FREQ = 100; // 10 per thread (low because anyway deletes will be performed)


    public static class InsertTask implements Runnable {
        boolean exit = false;
        String keyPrefix; // each instance can have different prefix hence key and value are different or same.
        public static Random numGen = new Random();

        public InsertTask(String prefix) {
            keyPrefix = prefix;
        }

        @Override
        public void run() {
            long waitime = INSERT_FREQ;
            while(!exit) {
                String key = keyPrefix + Integer.toString(numGen.nextInt(MaxRand));
                int timetolive = numGen.nextInt(MaxRand) * 20;
                sDMap.put(key, key+ Integer.toString(timetolive), timetolive);
                try {
                    Thread.sleep(waitime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void stop() {
            exit = true;
        }
    }

    public static class RemoveTask implements Runnable {
        boolean exit = false;
        String keyPrefix;
        public static Random numGen = new Random();

        public RemoveTask(String prefix) {
            keyPrefix = prefix;
        }

        @Override
        public void run() {
            long waitime = DELETE_FREQ;
            while(!exit) {
                String key = keyPrefix + Integer.toString(numGen.nextInt(MaxRand));
                sDMap.remove(key);
                try {
                    Thread.sleep(waitime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void stop() {
            exit = true;
        }
    }

    public static class GetTask implements Runnable {
        boolean exit = false;
        String keyPrefix;
        public static Random numGen = new Random();

        public GetTask(String prefix) {
            keyPrefix = prefix;
        }
        @Override
        public void run() {
            long waitime = PROBE_FREQ;
            while(!exit) {
                String key = keyPrefix + Integer.toString(numGen.nextInt(MaxRand));
                sDMap.get(key);
                try {
                    Thread.sleep(waitime);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        void stop() {
            exit = true;
        }
    }

    // takes argument time for how long the tests to run,
    public static void testTTLMap(int t) {
        InsertTask i1 = new InsertTask("p1"), i2= new InsertTask("p2");
        RemoveTask r1 = new RemoveTask("p1"), r2 = new RemoveTask("p2");
        GetTask g1 = new GetTask("p1"), g2 = new GetTask("p2"), g3 = new GetTask("p1") , g4= new GetTask("p2");

        Thread[] threads = new Thread[8];
        threads[0] = new Thread(i1);
        threads[1] = new Thread(i2);
        threads[2] = new Thread(r1);
        threads[3] = new Thread(r2);
        threads[4] = new Thread(g1);
        threads[5] = new Thread(g2);
        threads[6] = new Thread(g3);
        threads[7] = new Thread(g4);

        for (int i= 0; i<8;i++) {
            threads[i].start();
        }

        long testRunTime = t * 60 * 1000 ;
        try {
            Thread.sleep(testRunTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i1.stop();
        i2.stop();
        r1.stop();
        r2.stop();
        g1.stop();
        g2.stop();
        g3.stop();
        g4.stop();

        sDMap.stop();
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("Bench mark test for time to live hash map.").build()
                .defaultHelp(true)
                .description("Bench mark test for time to live hash map.");
        parser.addArgument("-t", "--time")
                .choices(1, 3, 30, 120).setDefault(3)
                .help("Specify perf test running time");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        testTTLMap(ns.getInt("time"));
        System.out.println("Bye...");
    }

}
