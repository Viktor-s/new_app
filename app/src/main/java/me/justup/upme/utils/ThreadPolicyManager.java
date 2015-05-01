package me.justup.upme.utils;

import android.os.StrictMode;

public class ThreadPolicyManager {

    // ========== Singleton ==========

    private static ThreadPolicyManager sInstance;
    public static ThreadPolicyManager getInstance(){
        if(sInstance == null){
            sInstance = new ThreadPolicyManager();
        }

        return sInstance;
    }

    private ThreadPolicyManager(){}

    // ========== Class ==========

    private StrictMode.ThreadPolicy mPreviousThreadPolicy;

    /**
     * Prevent exceptions from doing disk and network operations in a service
     */
    private void setPermissiveThreadPolicy() {
        mPreviousThreadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(mPreviousThreadPolicy).permitNetwork().permitDiskReads().permitDiskWrites().build());
    }

    /**
     * Reset thread policy to previously known state for consistency
     */
    private void resetThreadPolicy() {
        if (mPreviousThreadPolicy != null) {
            StrictMode.setThreadPolicy(mPreviousThreadPolicy);
        }
    }

    /**
     * This will temporarily set the thread policy to permissive,
     * execute the unit of work, and then reset the thread policy
     */
    public void executePermissiveUnit(PermissiveUnit unit){
        setPermissiveThreadPolicy();
        unit.executeUnitOfWork();
        resetThreadPolicy();
    }

    // ========= PermissiveUnit ========

    /**
     * This is a functor to allow a unit of work to be executed
     * permissively
     */
    public static abstract class PermissiveUnit{
        public abstract void executeUnitOfWork();
    }

}
