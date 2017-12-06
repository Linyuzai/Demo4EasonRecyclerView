package com.linyuzai.easonrecyclerview.index;

import android.support.annotation.NonNull;

import com.linyuzai.easonrecyclerview.util.PinyinUtil;

import java.util.Comparator;

/**
 * Created by YoKey on 16/10/7.
 */
public class PinyinComparator<T extends Indexable> implements Comparator<IndexableWrapper<T>> {

    @Override
    public int compare(IndexableWrapper<T> lhs, IndexableWrapper<T> rhs) {
        String lhsIndexName = lhs.getIndexByField();
        String rhsIndexName = rhs.getIndexByField();

        if (lhsIndexName == null) {
            lhsIndexName = "";
        }
        if (rhsIndexName == null) {
            rhsIndexName = "";
        }
        return compareIndexName(lhsIndexName.trim(), rhsIndexName.trim());
    }

    private int compareIndexName(String lhs, String rhs) {
        int index = 0;

        String lhsWord = getWord(lhs, index);
        String rhsWord = getWord(rhs, index);
        while (lhsWord.equals(rhsWord) && !lhsWord.equals("")) {
            index++;
            lhsWord = getWord(lhs, index);
            rhsWord = getWord(rhs, index);
        }
        return lhsWord.compareTo(rhsWord);
    }

    @NonNull
    private String getWord(String indexName, int index) {
        if (indexName.length() < (index + 1)) return "";
        String firstWord;
        if (PinyinUtil.matchingPolyphone(indexName)) {
            firstWord = PinyinUtil.getPingYin(PinyinUtil.getPolyphoneRealHanzi(indexName).substring(index, index + 1));
        } else {
            firstWord = PinyinUtil.getPingYin(indexName.substring(index, index + 1));
        }
        return firstWord;
    }
}