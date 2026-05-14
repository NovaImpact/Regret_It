package com.example.regret_it;

public class OutQueue {
    int currentIndex = 0;
    int getIndex = 0;
    int objsInQueue = 0;
    Message[] objects = new Message[100];

    public synchronized boolean put(Message obj) {
        if (objsInQueue < 100) {
            objects[currentIndex] = obj;
            currentIndex++;
            objsInQueue++;
            if (currentIndex == 100) currentIndex = 0;
            return true;
        }
        return false;
    }

    public synchronized Message get() {
        if (objsInQueue > 0) {
            Message object = objects[getIndex];
            getIndex++;
            objsInQueue--;
            if (getIndex == 100) getIndex = 0;
            return object;
        }
        return null;
    }
}