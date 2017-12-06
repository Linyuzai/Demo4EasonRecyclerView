package com.linyuzai.easonrecyclerview.custom;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.linyuzai.easonrecyclerview.index.Indexable;
import com.linyuzai.easonrecyclerview.IndexableAdapter;
import com.linyuzai.easonrecyclerview.index.IndexableWrapper;

import java.util.List;

/**
 * 该HeaderAdapter 接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
 * Created by YoKey on 16/10/14.
 */
public class SimpleSuffixAdapter<T extends Indexable> extends IndexableSuffixAdapter<T> {
    private IndexableAdapter<T> mAdapter;

    public SimpleSuffixAdapter(IndexableAdapter<T> adapter, String index, String indexTitle, List<T> datas) {
        super(index, indexTitle, datas);
        this.mAdapter = adapter;
    }

    @Override
    public int getItemViewType() {
        return IndexableWrapper.TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        return mAdapter.onCreateContentViewHolder(parent);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity) {
        mAdapter.onBindContentViewHolder(holder, entity);
    }
}
