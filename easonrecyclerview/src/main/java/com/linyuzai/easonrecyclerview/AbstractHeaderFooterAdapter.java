package com.linyuzai.easonrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.linyuzai.easonrecyclerview.rx.HeaderFooterDataObservable;
import com.linyuzai.easonrecyclerview.rx.HeaderFooterDataObserver;
import com.linyuzai.easonrecyclerview.rx.IndexBarDataObservable;
import com.linyuzai.easonrecyclerview.rx.IndexBarDataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/16.
 */

abstract class AbstractHeaderFooterAdapter<T> {
    private final HeaderFooterDataObservable mDataSetObservable = new HeaderFooterDataObservable();
    private final IndexBarDataObservable mIndexBarDataSetObservable = new IndexBarDataObservable();

    ArrayList<IndexWrapper<T>> mIndexWrapperList = new ArrayList<>();
    protected OnItemClickListener<T> mListener;
    protected OnItemLongClickListener<T> mLongListener;

    private String mIndex, mIndexTitle;

    /**
     * 不想显示哪个就传null
     *
     * @param index      IndexBar的字母索引
     * @param indexTitle IndexTitle
     * @param datas      数据源
     */
    public AbstractHeaderFooterAdapter(String index, String indexTitle, List<T> datas) {
        this.mIndex = index;
        this.mIndexTitle = indexTitle;

        if (indexTitle != null) {
            IndexWrapper<T> wrapper = wrapEntity();
            wrapper.setItemType(IndexWrapper.TYPE_TITLE);
        }
        for (int i = 0; i < datas.size(); i++) {
            IndexWrapper<T> wrapper = wrapEntity();
            wrapper.setData(datas.get(i));
        }
    }

    private IndexWrapper<T> wrapEntity() {
        IndexWrapper<T> wrapper = new IndexWrapper<>();
        wrapper.setIndex(mIndex);
        wrapper.setIndexTitle(mIndexTitle);
        wrapper.setHeaderFooterType(getHeaderFooterType());
        mIndexWrapperList.add(wrapper);
        return wrapper;
    }

    private IndexWrapper<T> wrapEntity(int pos) {
        IndexWrapper<T> wrapper = new IndexWrapper<>();
        wrapper.setIndex(mIndex);
        wrapper.setIndexTitle(mIndexTitle);
        wrapper.setHeaderFooterType(getHeaderFooterType());
        mIndexWrapperList.add(pos, wrapper);
        return wrapper;
    }

    public abstract int getItemViewType();

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, T entity);

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    public void addData(T data) {
        int size = mIndexWrapperList.size();

        IndexWrapper<T> wrapper = wrapEntity();
        wrapper.setItemType(getItemViewType());
        wrapper.setData(data);

        if (size > 0) {
            mDataSetObservable.notifyAdd(getHeaderFooterType() == IndexWrapper.TYPE_HEADER, mIndexWrapperList.get(size - 1), wrapper);
            mIndexBarDataSetObservable.notifyChanged();
        }
    }

    public void removeData(T data) {
        for (IndexWrapper wrapper : mIndexWrapperList) {
            if (wrapper.getData() == data) {
                mIndexWrapperList.remove(wrapper);
                mDataSetObservable.notifyRemove(getHeaderFooterType() == IndexWrapper.TYPE_HEADER, wrapper);
                mIndexBarDataSetObservable.notifyChanged();
                return;
            }
        }
    }

    int getHeaderFooterType() {
        return IndexWrapper.TYPE_HEADER;
    }

    public void addData(int position, T data) {
        int size = mIndexWrapperList.size();
        if (position >= size) {
            return;
        }

        IndexWrapper<T> wrapper = wrapEntity(position + 1);
        wrapper.setItemType(getItemViewType());
        wrapper.setData(data);

        if (size > 0) {
            mDataSetObservable.notifyAdd(getHeaderFooterType() == IndexWrapper.TYPE_HEADER, mIndexWrapperList.get(position), wrapper);
            mIndexBarDataSetObservable.notifyChanged();
        }
    }

    public void addDatas(List<T> datas) {
        for (int i = 0; i < datas.size(); i++) {
            addData(datas.get(i));
        }
    }

    public void addDatas(int position, List<T> datas) {
        int size = mIndexWrapperList.size();
        if (position >= size) {
            return;
        }

        for (int i = datas.size() - 1; i >= 0; i--) {
            addData(position, datas.get(i));
        }
    }

//    public void removeAll(List<T> datas) {
//        // TODO: 16/10/27
//    }

    OnItemClickListener<T> getOnItemClickListener() {
        return mListener;
    }


    OnItemLongClickListener getOnItemLongClickListener() {
        return mLongListener;
    }

    ArrayList<IndexWrapper<T>> getDatas() {
        for (IndexWrapper<T> wrapper : mIndexWrapperList) {
            if (wrapper.getItemType() == IndexWrapper.TYPE_CONTENT) {
                wrapper.setItemType(getItemViewType());
            }
        }
        return mIndexWrapperList;
    }

    void registerDataSetObserver(HeaderFooterDataObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(HeaderFooterDataObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    void registerIndexBarDataSetObserver(IndexBarDataObserver observer) {
        mIndexBarDataSetObservable.registerObserver(observer);
    }

    void unregisterIndexBarDataSetObserver(IndexBarDataObserver observer) {
        mIndexBarDataSetObservable.unregisterObserver(observer);
    }

    interface OnItemClickListener<T> {
        void onItemClick(View v, int currentPosition, T entity);
    }

    interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View v, int currentPosition, T entity);
    }
}
