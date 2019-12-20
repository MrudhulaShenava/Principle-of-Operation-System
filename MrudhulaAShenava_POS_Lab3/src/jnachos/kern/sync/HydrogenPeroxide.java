package jnachos.kern.sync;


import java.io.*;
import jnachos.kern.*;

/**
 *
 */
public class HydrogenPeroxide {

    /** Semaphore H */
    static Semaphore H = new Semaphore("SemH", 0);

    /**    */
    static Semaphore O = new Semaphore("SemO", 0);

    /**    */
    static Semaphore wait = new Semaphore("wait", 0);

    /**    */
    static Semaphore mutex = new Semaphore("MUTEX", 1);

    /**    */
    static Semaphore mutex1 = new Semaphore("MUTEX1", 1);

    /**    */
    static Semaphore mutex2 = new Semaphore("MUTEX2", 1);

    /**    */
    static long count = 0, count1 = 0;

    /**    */
    static int Hcount, Ocount, nH, nO;

    /**    */
    class HAtom implements VoidFunctionPtr {
        int mID;

        /**
         *
         */
        public HAtom(int id) {
            mID = id;
        }

        /**
         * oAtom will call oReady. When this atom is used, do continuous
         * "Yielding" - preserving resource
         */
        public void call(Object pDummy) {
            //if(Hcount >= 2 && Ocount >= 2) {
            mutex.P();
            if (count % 2 == 0) // first H atom
            {
                count++; // increment counter for the first H
                mutex.V(); // Critical section ended
                H.P(); // Waiting for the second H atom
            } else // second H atom
            {
                count++; // increment count for next first H
                mutex.V(); // Critical section ended
                //H.V(); // wake up the first H atom
                O.V(); // wake up O atom
            }

            wait.P(); // wait for water message done

            System.out.println("H atom #" + mID + " used in making peroxide.");
            //}
        }
    }

    /**    */
    class OAtom implements VoidFunctionPtr {
        int mID;

        /**
         * oAtom will call oReady. When this atom is used, do continuous
         * "Yielding" - preserving resource
         */
        public OAtom(int id) {
            mID = id;
        }

        /**
         * oAtom will call oReady. When this atom is used, do continuous
         * "Yielding" - preserving resource
         */
        public void call(Object pDummy) {
            //System.out.println("O-> count1: "+count1+" Hcount: "+Hcount+" Ocount: "+Ocount);
            //if(Hcount >= 2 && Ocount >= 2) {
            //O.P(); // waiting for two H atoms.
            
            mutex2.P();
            if (count1 % 2 == 0) // first O atom
            {
                count1++; // increment counter for the first O
                mutex2.V(); // Critical section ended
                O.P(); // Waiting for the second O atom
            } else // second H atom
            {
                count1++; // increment count for next first O
                mutex2.V(); // Critical section ended
                H.V(); // wake up the first H atom
                O.V(); // wake up O atom
            }
            
            makeHydroPeroxide();
            wait.V();
            wait.V(); // wake up H atoms and they will return to
            wait.V(); // resource pool
            mutex1.P();
            if((Hcount-2) >= 0 && (Ocount-2) >= 0)
            {
                Hcount = Hcount - 2;
                Ocount = Ocount - 2;
            }
            
            System.out.println("Numbers Left: H Atoms: " + Hcount + ", O Atoms: " + Ocount);
            System.out.println("Numbers Used: H Atoms: " + (nH - Hcount) + ", O Atoms: " + (nO - Ocount));
            mutex1.V();
            System.out.println("O atom #" + mID + " used in making peroxide.");
        }
        
        //}
    }

    /**
     * oAtom will call oReady. When this atom is used, do continuous "Yielding"
     * - preserving resource
     */
    public static void makeHydroPeroxide() {
        System.out.println("** Peroxide made! **");
    }

    /**
     * oAtom will call oReady. When this atom is used, do continuous "Yielding"
     * - preserving resource
     */
    public HydrogenPeroxide() {
        runHydroPeroxide();
    }

    /**
     *
     */
    public void runHydroPeroxide() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Number of H atoms ? ");
            nH = (new Integer(reader.readLine())).intValue();
            System.out.println("Number of O atoms ? ");
            nO = (new Integer(reader.readLine())).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Hcount = nH;
        Ocount = nO;

        for (int i = 0; i < nH; i++) {
            HAtom atom = new HAtom(i);
            (new NachosProcess(new String("hAtom" + i))).fork(atom, null);
        }

        for (int j = 0; j < nO; j++) {
            OAtom atom = new OAtom(j);
            (new NachosProcess(new String("oAtom" + j))).fork(atom, null);
        }
    }
}

