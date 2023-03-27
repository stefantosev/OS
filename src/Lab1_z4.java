//class TwoThreads {
////    public static class Thread1 extends Thread {
////        public void run() {
////            System.out.println("A");
////            System.out.println("B");
////        }
////    }
//
//    //so koristenje na interface, mesto nasleduvanje
//    public static  class ThreadAB implements Runnable{
//        public String t1;
//        public String t2;
//
//        public ThreadAB(String t1, String t2) {
//            this.t1 = t1;
//            this.t2 = t2;
//        }
//
//        @Override
//        public void run() {
//            System.out.println(t1);
//            System.out.println(t2);
//        }
//    }
//
//    public static void main(String[] args) {
////        new Thread1().start();
////        new Thread2().start();
//
//        ThreadAB t1 = new ThreadAB("A", "B");
//        ThreadAB t2 = new ThreadAB("1", "2");
//
//        Thread threadA = new Thread(t1); //koga imame interface na voj nacin
//        Thread threadB = new Thread(t2);
//
//        threadA.start();
//        threadB.start();
//
//    }
//
////    public static class Thread2 extends Thread {
////        public void run() {
////            System.out.println("1");
////            System.out.println("2");
////        }
////    }
//
//}