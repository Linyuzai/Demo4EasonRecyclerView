package com.linyuzai.easonrecyclerview.index;

/**
 * Created by YoKey on 16/10/9.
 */
public interface Indexable {

    String getFieldIndexBy();

    void setFieldIndexBy(String indexField);

    void setFieldPinyinIndexBy(String pinyin);
}
