package com.example.regret_it;

public class ChannelQueue {
    int currentIndex = 0;
    int getIndex = 0;
    int objsInQueue = 0;
    Channel[] objects = new Channel[100];

    public synchronized boolean put(Channel obj) {
        if (objsInQueue < 100) {
            objects[currentIndex] = obj;
            currentIndex++;
            objsInQueue++;
            if (currentIndex == 100) currentIndex = 0;
            return true;
        }
        return false;
    }

    public synchronized Channel get() {
        if (objsInQueue > 0) {
            Channel object = objects[getIndex];
            getIndex++;
            objsInQueue--;
            if (getIndex == 100) getIndex = 0;
            return object;
        }
        return null;
    }
}