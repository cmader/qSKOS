package at.ac.univie.mminf.qskos4j.util;

import java.util.*;

public class Tuple<T> {

    private List<T> elements;

    public Tuple(List<T> initElements) {
        elements = new ArrayList<>();
        elements.addAll(initElements);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) return false;

        Tuple<?> other = (Tuple<?>) obj;
        if (other.elements.size() != elements.size()) return false;

        for (T element : elements) {
            if (!other.elements.contains(element)) return false;
        }

        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        for (T element : elements) {
            hashCode += element.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        String ret = "(";

        Iterator<T> it = elements.iterator();
        while (it.hasNext()) {
            ret += it.next().toString() + (it.hasNext() ? ", " : "");
        }

        ret += ")";
        return ret;
    }

    public T get(int index) {
        return elements.get(index);
    }

    public T getFirst() {
        return elements.get(0);
    }

    public T getSecond() {
        return elements.get(1);
    }

    public List<T> getElements() {
        return elements;
    }

}