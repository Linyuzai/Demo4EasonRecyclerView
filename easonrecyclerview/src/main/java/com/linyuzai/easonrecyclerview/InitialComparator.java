package com.linyuzai.easonrecyclerview;

import java.util.Comparator;

/**
 * Created by YoKey on 16/10/14.
 */
class InitialComparator<T extends Indexable> implements Comparator<IndexWrapper<T>> {
    @Override
    public int compare(IndexWrapper<T> lhs, IndexWrapper<T> rhs) {
        return lhs.getIndex().compareTo(rhs.getIndex());
    }
}
