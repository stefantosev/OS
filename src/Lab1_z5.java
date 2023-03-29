//import java.util.HashSet;
//import java.util.Random;
//import java.util.Scanner;
//import java.util.concurrent.Semaphore;
//
// class CountThree {
//
//    public static int NUM_RUNS = 2;
//    /**
//     * Promenlivata koja treba da go sodrzi brojot na pojavuvanja na elementot 3
//     */
//    int count = 0;
//    /**
//     * TODO: definirajte gi potrebnite elementi za sinhronizacija
//     */
//    Semaphore semafor = new Semaphore(1);
//
//    public void init() {
//    }
//
//    class Counter extends Thread {
//
//        public void count(int[] data) throws InterruptedException {
//            // da se implementira
//            int brojac = 0;
//            for(int i=0; i<data.length; i++){
//                if(data[i]==3){
//                    brojac++;
//                }
//                semafor.acquire();
//                count += brojac;    //tuka e kriticniot domen
//                semafor.release();
//            }
//
//        }
//        private int[] data;
//
//        public Counter(int[] data) {
//            this.data = data;
//        }
//
//        @Override
//        public void run() {
//            try {
//                count(data);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            CountThree environment = new CountThree();
//            environment.start();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void start() throws Exception {
//
//        init();
//
//        HashSet<Thread> threads = new HashSet<Thread>();
//        Scanner s = new Scanner(System.in);
//        int total=s.nextInt();
//
//        //se pravat tuka threadsot
//        for (int i = 0; i< NUM_RUNS; i++) {
//            int[] data = new int[total];
//            for (int j = 0; j < total; j++) {
//                data[j] = s.nextInt();
//            }
//            Counter c = new Counter(data);
//            threads.add(c);
//        }
//
//        //site se startnuat
//        for (Thread t : threads) {
//            t.start();
//        }
//
//        //se blokiraat drugite eden dodeka raboti
//        for (Thread t : threads) {
//            t.join();
//        }
//        System.out.println(count);
//
//
//    }
//}

//---------------------------------------------------------------------------

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

 class CountAB {

    public static int NUM_RUNS = 100;
    /**
     * Promenlivi koja treba da go sodrzat brojot na pojavuvanja na karakterite A i B.
     */
     int countA = 0;
     int countB = 0;

    /**
     * Promenliva koja treba da go sodrzi prosecniot brojot na pojavuvanja na karakterite A i B.
     */
    double average = 0.0;
    /**
     * TODO: definirajte gi potrebnite elementi za sinhronizacija
     */
    static Semaphore s1;
    static Semaphore s2;

    public void init() {
        //2 semafora za sekoj posebno
        s1 = new Semaphore(1);
        s2 = new Semaphore(1);

    }

    class CounterA extends Thread {

        public void count(int[] data) throws InterruptedException {
            // da se implementira
            int counter = 0;
            for(int i=0; i<data.length; i++){
                if(data[i] == 'A'){
                    counter++;
                }
            }
            s1.acquire();
            //kriticen region
            countA += counter;
            s1.release();
        }
        private int[] data;

        public CounterA(int[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                count(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CounterB extends Thread {

        public void count(int[] data) throws InterruptedException {
            // da se implementira

            int counter = 0;
            for(int i=0; i<data.length; i++){
                if(data[i] == 'B'){
                    counter++;
                }
            }

            s2.acquire();
            countB += counter; //kriticen region
            s2.release();

        }
        private int[] data;

        public CounterB(int[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                count(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            CountAB environment = new CountAB();
            environment.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void start() throws Exception {

        init();

        HashSet<Thread> threads = new HashSet<Thread>();
        Scanner s = new Scanner(System.in);
        Random r = new Random();
        int total=s.nextInt();

        for (int i = 0; i < NUM_RUNS; i++) {
            int[] data = new int[total];
            for (int j = 0; j < total; j++) {
                data[j] = r.nextInt(10) + 65;
                //prvite 10 bukvi od azbukata za golemite bukvi
            }
            CounterA c = new CounterA(data);
            threads.add(c);
        }

        for (int i = 0; i < NUM_RUNS; i++) {
            int[] data = new int[total];
            for (int j = 0; j < total; j++) {
                data[j] = r.nextInt(10) + 65;
            }
            CounterB c = new CounterB(data);
            threads.add(c);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
        average = (double)(countA + countB) / (NUM_RUNS * total);

        System.out.println(countA);
        System.out.println(countB);
        System.out.printf("%.3f",average);


    }
}
