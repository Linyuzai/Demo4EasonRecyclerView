package com.linyuzai.easonrecyclerview;

import java.util.Comparator;

/**
 * Created by YoKey on 16/10/14.
 */
class InitialComparator<T extends Indexable> implements Comparator<IndexableWrapper<T>> {
    @Override
    public int compare(IndexableWrapper<T> lhs, IndexableWrapper<T> rhs) {
        return lhs.getIndex().compareTo(rhs.getIndex());
    }
}
