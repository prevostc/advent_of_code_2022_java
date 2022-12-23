package com.prevostc.utils;

import java.util.List;

public class LoopingList<T> {
    private final T[] array;
    private int index = 0;

    public LoopingList(T[] array) {
        this.array = array;
    }

    public LoopingList(List<T> list) {
        this.array = (T[]) list.toArray();
    }

    public T next() {
        T item = array[index];
        index = (index + 1) % array.length;
        return item;
    }

    public T previous() {
        index = (index - 1) % array.length;
        if (index < 0) {
            index = array.length + index;
        }
        return array[index];
    }

    public T current() {
        return array[index];
    }

    public int size() {
        return array.length;
    }

    public int currentIndex() {
        return index;
    }

    public void reset() {
        index = 0;
    }

    public void reset(int index) {
        this.index = index;
    }

    public void reset(T item) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(item)) {
                index = i;
                return;
            }
        }
        throw new IllegalArgumentException("Item not found in the list");
    }

    public T[] toArray() {
        return array;
    }
}
