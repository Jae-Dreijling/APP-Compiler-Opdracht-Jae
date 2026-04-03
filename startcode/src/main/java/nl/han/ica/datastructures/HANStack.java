package nl.han.ica.datastructures;

import java.util.ArrayList;

public class HANStack<T> implements IHANStack<T> {

    private ArrayList<T> data;

    public HANStack() {
        data = new ArrayList<>();
    }

    @Override
    public void push(T value) {
        data.add(value);
    }

    @Override
    public T pop() {
        if (data.isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return data.remove(data.size() - 1);
    }

    @Override
    public T peek() {
        if (data.isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return data.get(data.size() - 1);
    }
}