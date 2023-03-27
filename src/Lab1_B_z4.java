
//class ThreadClassNumbers implements Runnable {
//
//    @Override
//    public void run() {
//        for(int i = 0; i<10;i++) System.out.println(i);
//    }
//}


//class ThreadClassLetters implements Runnable {
//
//    @Override
//    public void run() {
//        for(int i = 0; i<10;i++) System.out.println((char)(i + 65));
//    }
//}

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

class ThreadClassLettersNumbers<E> implements Runnable {

   List<E> podatoci;

    public ThreadClassLettersNumbers(List<E> podatoci) {
        this.podatoci = podatoci;
    }

    @Override
    public void run() {
        //for(int i = 0; i<podatoci.size();i++) System.out.println((char)(i + 65));
        for(int i = 0; i<podatoci.size();i++){
            System.out.println(podatoci.get(i));
        }
    }
}



class TwoThreads {

    public static void main(String[] args) throws InterruptedException {
//        ThreadClassLetters letters = new ThreadClassLetters();
//        ThreadClassNumbers numbers = new ThreadClassNumbers();
//        letters.start();
//        letters.join();
//        numbers.start();
//        numbers.join();

        List<Integer> numbers = new ArrayList<>();
        List<Character> letters = new ArrayList<>();

        //ja polnime nizata za broevi
        for(int i=1; i<11; i++){
            numbers.add(i);
        }

        //ja polnime nizata za bukvi
        for(int i=0; i<10; i++){
            letters.add((char) (i + 65));
        }

        //pravime instanci od klasite i prakjame listata kako konstruktor
        ThreadClassLettersNumbers<Integer> num = new ThreadClassLettersNumbers<Integer>(numbers);
        ThreadClassLettersNumbers<Character> lett = new ThreadClassLettersNumbers<Character>(letters);

        Thread t1 = new Thread(num);
        Thread t2 = new Thread(lett);

        t1.start();
        t1.join();

        t2.start();
        t2.join();
    }

}