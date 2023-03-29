import javax.crypto.SealedObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


//------------------PRODUCER CONTROLLER-----------
class ProducerController {

    public static int NUM_RUN = 50;

    //TODO: DA SE DEKLARIRAA SEMAFORI
    static Semaphore accessBuffer; //1 da go pristapi bufferot once at a time
    static Semaphore lock; //go ogranicuva prostapot do numChecks
    static Semaphore canCheck;

    //TODO: DA SE INICIJALIZIRAAT TUKA
    public static void init() {
        accessBuffer = new Semaphore(1);
        lock = new Semaphore(1);
        canCheck = new Semaphore(10); //10 kontroleri istovremeno moze da pravat
    }

    //spodelen resurs
    public static class Buffer {

        public  int numChecks = 0;

        public void produce() {
            System.out.println("Producer is producing...");
        }

        public void check() {
            System.out.println("Controller is checking...");
        }
    }

    public static class Producer extends Thread {
        private final Buffer buffer;

        public Producer(Buffer b) {
            this.buffer = b;
        }

        // TODO: ednostaven producer nema proverki
        public void execute() throws InterruptedException {
            accessBuffer.acquire(); //cekame da vidime dali resursot e sloboden
            this.buffer.produce();
            accessBuffer.release(); //cim ke zavrsi oslobodi go za sleden da pristapi
        }

        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Controller extends Thread {

        private final Buffer buffer;

        public Controller(Buffer buffer) {
            this.buffer = buffer;
        }

        //TODO: da se zamislime kako kontroler za polesno
        public void execute() throws InterruptedException {
            lock.acquire(); //za numChecks
            if(this.buffer.numChecks == 0){ //proverka dali e prv da dobie dozvola
                accessBuffer.acquire();
            }
            this.buffer.numChecks++;
            lock.release();

            canCheck.acquire();//da bideme sigurni da ne e brojot pogolem od 10
            this.buffer.check();//ako e pogolemo od 0 se pristapuva
            lock.acquire(); //za slednata linija da se izvrsi atomicno
            this.buffer.numChecks--; //namaluvame za 1 i pravime lock za da se izvrzi atomicno
            canCheck.release(); //nema vekje procerki (namaluva za 1)
            if(this.buffer.numChecks==0){ //proerka dali e posleden
                accessBuffer.release();  //ako site checks pominale se releasnuva controller
            }
            lock.release();

        }

        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        Producer p = new Producer(buffer);
        List<Controller> controllers = new ArrayList<>();
        init();
        for (int i = 0; i < 10; i++) {
            controllers.add(new Controller(buffer));
        }
        p.start();
        for (int i = 0; i < 10; i++) {
            controllers.get(i).start();
        }
    }
}



//------------------------ Si02 ---------------------
//nema spodeluvanje na resursi
 class SiO2 {

    public static int NUM_RUN = 50;

    //TODO: deklariranje semafori
    static Semaphore si;
    static Semaphore o;
    static Semaphore siHere;
    static Semaphore oHere;
    static Semaphore ready;

    //TODO: inicijalizanje na metodi za sinhronizacija
    public static void init() {
        si = new Semaphore(1); //1 atom za si
        o = new Semaphore(2); //2 atomi za kislorod
        siHere = new Semaphore(0);
        oHere = new Semaphore(0);
        ready = new Semaphore(0);
    }

    public static class Si extends Thread {

        public void bond() {
            System.out.println("Si is bonding now.");
        }

        //TODO:
        public void execute() throws InterruptedException {
          si.acquire();
          siHere.release(2); //prakja poraka kon dvata atomi kislorod deka e tuka
          oHere.acquire(2); //ja primile porakata od si i
          ready.release(2); //si prakja poraka kon kislorodite deka e spremen
          bond();
          si.release(); //sme zavrsile so rabotata na silicium
        }

        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class O extends Thread {

        public void bond() {
            System.out.println("O is bonding now.");
        }

        //TODO: posebno za ednata instanca za kislorod
        public void execute() throws InterruptedException {
            o.acquire(); //cekame edna instanca da se izvrsi
            siHere.acquire();
            oHere.release();
            ready.acquire();
            bond();  //spojuvanje za SiO2
            o.release();  //zavrsil edniot moze sega slednata instanca na kislorodt da prodolzi
        }

        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



//--------------------- Sinhronizacija na toalet --------------------

class FinkiToilet {
    public static class Toilet {

        public void vlezi() {
            System.out.println("Vleguva...");
        }

        public void izlezi() {
            System.out.println("Izleguva...");
        }
    }

    //TODO: deklaracija na semafori i brojaci
    static Semaphore toiletSemafor;
    static Semaphore mLock;
    static Semaphore zLock;
    static  int numM;
    static int numZ;

    //TODO: inicijaliziranje semafori i brojaci
    public static void init() {
        toiletSemafor = new Semaphore(1); //one at a time za koj da pristapuva
        mLock = new Semaphore(1);
        zLock = new Semaphore(1);
        numM = 0;
        numZ = 0;
    }

    public static class Man extends Thread {

        private Toilet toilet;

        public Man(Toilet toilet) {
            this.toilet = toilet;
        }

        //TODO: implement enter and exit methods
        public void enter() throws InterruptedException {
            mLock.acquire();
            if(numM==0){
                toiletSemafor.acquire();
            }
            numM++;
            this.toilet.vlezi(); //ne se atomicni, ako e maz mora samo 1 da vleze a ne povekje
            mLock.release();
        }

        public void exit() throws InterruptedException {
            mLock.acquire();
            this.toilet.izlezi(); //ne e atomicna
            numM--; //izleguva mazot od toaletot
            if(numM==0){
                toiletSemafor.release(); //osloboduvame za ako nema vekje mazi da mozat zenite da pristapat
            }
            mLock.release();
        }

        @Override
        public void run() {
            super.run();
        }
    }

    public static class Woman extends Thread {

        private Toilet toilet;

        public Woman(Toilet toilet) {
            this.toilet = toilet;
        }

        //TODO: implement enter and exit methods
        public void enter() throws InterruptedException {
            zLock.acquire();
            if(numZ==0){  //ako si prv na queue da vlezes
                toiletSemafor.acquire();
            }
            numZ++; //vlegla edna zena
            this.toilet.vlezi(); //ne se atomicni, ako e zena(thread) mora samo 1 da vleze a ne povekje
            zLock.release();
        }

        public void exit() throws InterruptedException {
            zLock.acquire();
            this.toilet.izlezi();
            numZ--;  //izlegla zenata se namaluva brojot od queue-to
            if(numZ==0){
                toiletSemafor.release(); //da moze da se oslobi da pristapat mazite
            }
            zLock.release();
        }

        @Override
        public void run() {
            super.run();
        }
    }
}



//------------------- UPISI NA FINKI ---------------
 class FinkiEnrolment {

    //TODO deklaracija na semofori
    static Semaphore slobodnoUpisnoMesto;
    static Semaphore enter;
    static Semaphore here;
    static Semaphore done;


    //TODO incijaliziranje semadori
    public static void init() {
        slobodnoUpisnoMesto = new Semaphore(4);
        enter = new Semaphore(0);
        here = new Semaphore(0);
        done = new Semaphore(0);
    }

    public static class Member extends Thread {

        //TODO implement method execute
        public void execute() throws InterruptedException {
            slobodnoUpisnoMesto.acquire();
            int brStudenti = 10; //kolku studenti treba da zapise

            //komunikacija na 2 threads  se dodeka ne zavrsi so 1- studenti
            while(brStudenti>0){
                enter.release();
                here.acquire();
                enrol();
                done.release();
                brStudenti--; //namaluvame br na studenti
            }
            slobodnoUpisnoMesto.release(); //cim gi zapisla osloboduva
        }

        public void enrol() {
            System.out.println("Enrol student...");
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Student extends Thread {

        //TODO implement method execute
        public void execute() throws InterruptedException {
            enter.acquire();
            giveDocuments();
            here.release();
            done.acquire();

        }

        public void giveDocuments() {
            System.out.println("Giving documents...");
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


