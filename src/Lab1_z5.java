import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

 class CountThree {

    public static int NUM_RUNS = 2;
    /**
     * Promenlivata koja treba da go sodrzi brojot na pojavuvanja na elementot 3
     */
    int count = 0;
    /**
     * TODO: definirajte gi potrebnite elementi za sinhronizacija
     */
    Semaphore semafor = new Semaphore(1);

    public void init() {
    }

    class Counter extends Thread {

        public void count(int[] data) throws InterruptedException {
            // da se implementira
            int brojac = 0;
            for(int i=0; i<data.length; i++){
                if(data[i]==3){
                    brojac++;
                }
                semafor.acquire();
                count += brojac;    //tuka e kriticniot domen
                semafor.release();
            }

        }
        private int[] data;

        public Counter(int[] data) {
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
            CountThree environment = new CountThree();
            environment.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void start() throws Exception {

        init();

        HashSet<Thread> threads = new HashSet<Thread>();
        Scanner s = new Scanner(System.in);
        int total=s.nextInt();

        //se pravat tuka threadsot
        for (int i = 0; i< NUM_RUNS; i++) {
            int[] data = new int[total];
            for (int j = 0; j < total; j++) {
                data[j] = s.nextInt();
            }
            Counter c = new Counter(data);
            threads.add(c);
        }

        //site se startnuat
        for (Thread t : threads) {
            t.start();
        }

        //se blokiraat drugite eden dodeka raboti
        for (Thread t : threads) {
            t.join();
        }
        System.out.println(count);


    }
}