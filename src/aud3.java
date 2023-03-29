import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//*************** SINHRONIZACIJA NA THREADS ****************

//metod 1: synchronization (monitor)
//class SyncLibrary {
//    List<String> knigi = new ArrayList<>();
//    int kapacitet;
//
//    public SyncLibrary(int kapacitet) {
//        this.kapacitet = kapacitet;
//    }
//
//    //vrakjame kniga vo bibliotekata
//    //monitorot kaj se naogjame ke se naogja edinstveno samo vo ovaa funkcija
//    //pristap eden po eden thread
//    public synchronized void returnBook(String kniga) throws InterruptedException {
////        if(knigi.size() != kapacitet){
////            knigi.add(kniga);
////        }
//
//        while(knigi.size() == kapacitet){
//            wait();
//        }
//
//        knigi.add(kniga);
//        notifyAll();  //gi budime site threads sho spijat
//    }
//
//    //clenot pozajmuva kniga
//    public String borrowBook() throws InterruptedException {
//        String kniga = "";
//        //ako ima edna kniga barem se zima prvata
////        if(knigi.size() != 0){
////            kniga = knigi.get(0);
////        }
//
//        //ke ceka se dodeka ne doe nova kniga
//        while(knigi.size() == 0){
//            wait();
//        }
//        kniga = knigi.remove(0);
//        notifyAll(); //gi targetira threadsot so cekaat
//        return kniga;
//    }
//}
//
////---------------------------------------------------------
//
////metod 2: binaren semafor (Mutex) - katanec otkluci i zakluci
////isto e i so locks
//class MutexLibrary{
//    List<String> knigi = new ArrayList<>();
//    int kapacitet;
//
//    public static Lock lock = new ReentrantLock();
//
//
//    public MutexLibrary(int kapacitet) {
//        this.kapacitet = kapacitet;
//    }
//
//    //vrakjame kniga vo bibliotekata
//    public void returnBook(String kniga) throws InterruptedException {
////        if(knigi.size() != kapacitet){
////            knigi.add(kniga);
////        }
//
//        //beskonecen uslov oti ke ima problem ke se zakluci inaku
//        while (true){
//            lock.lock(); //zaklcugi go kriticniot domen
//
//            if(knigi.size() < kapacitet){
//                knigi.add(kniga);
//                lock.unlock(); //koga se dodava knigata se unlock
//                break;
//            }
//
//            lock.unlock();
//        }
//
//    }
//
//    //clenot pozajmuva kniga
//    public String borrowBook() throws InterruptedException {
//        String kniga = "";
//        //ako ima edna kniga barem se zima prvata
////        if(knigi.size() != 0){
////            kniga = knigi.get(0);
////        }
//
//        while(true){
//            lock.lock();
//
//            if(knigi.size() > 0){
//                kniga = knigi.remove(0);
//                lock.unlock();
//                break;
//            }
//
//            lock.unlock(); //ako ne e ispolnet uslovot predaj go na drug klucot
//        }
//        return kniga;
//    }
//}

//------------------------------------------------------------------------

//metod 3: semafori - povekje klucevi
class SemaphoreLibrary{
    List<String> knigi = new ArrayList<>();
    int kapacitet;

    //ni pomaga za kriticniot domen i ke bide kako mutex
    Semaphore coordinator = new Semaphore(1);

    //slednive dva se za funkciite posebno
    Semaphore s1 = new Semaphore(10);
    Semaphore s2 = new Semaphore(10);


    public SemaphoreLibrary(int kapacitet) {
        this.kapacitet = kapacitet;
    }

    //vrakjame kniga vo bibliotekata
    public void returnBook(String kniga) throws InterruptedException {
        s1.acquire(); //dava kluc za site clenovi, n1, n2... , n10

        coordinator.acquire(); //zaklucuvame kriticen domen

        //ne dodvava knigi dodeka e polno
        while(knigi.size() == kapacitet){
            coordinator.release(); //nemoze da dodae i za dzabe ke bide otvoren
            Thread.sleep(1000); //cekame 1s moze ke dojde nekoj da go zeme klucot
            coordinator.acquire(); //povtorno probaj da pristapis ako ima vneseno kniga
        }

        knigi.add(kniga);

        coordinator.release(); //oslobodi kriticen domen

        s1.release();  //signalizira deka sleden moze da pristapi
    }

    //clenot pozajmuva kniga
    public String borrowBook() throws InterruptedException {
        String kniga = "";

        s2.acquire(); //davame lock na clenovite

        coordinator.acquire();

        //istata postapka i logika kako i vo gornata funkcija
        while(knigi.size() == 0) {
            coordinator.release();
            Thread.sleep(1000); //spie 1s dodeka ne dojde nov clen i ne pozajmi kniga
            coordinator.acquire();
        }
        kniga = knigi.remove(0);

        s2.release(); //signalizirame deka zavrsil pa za sleden da pristapi

        return kniga;
    }
}

class LibraryDemo{
    public static void main(String[] args) throws InterruptedException {

        List<Member> members = new ArrayList<>();
        SemaphoreLibrary library = new SemaphoreLibrary(10);

        for(int i=0; i<10; i++){
            Member member = new Member("M"+i, library);
            members.add(member); //se dodavaat site novi clenovi
        }

        for(Member  member : members){
            member.start();
        }
        for(Member member : members){
            member.join(2000);
        }
        System.out.println("Uspesno bese !");
    }
}

//membersot ni se kako thread za pristap
class Member extends Thread{
    private String ime;
    private SemaphoreLibrary biblioteka;

    public Member(String ime, SemaphoreLibrary biblioteka){
        this.ime = ime;
        this.biblioteka = biblioteka;
    }

    @Override
    public void run(){

        //printanje za return book
        for(int i=0; i<3; i++){
            System.out.println("Member " +i+ "returns book");
            try{
                biblioteka.returnBook("Book " +i);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        //printanje za borrow book
        for(int i=0; i<5; i++){
            System.out.println("Member " +i+ "borrows book");
            try{
                biblioteka.borrowBook();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}