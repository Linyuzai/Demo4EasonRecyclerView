package com.linyuzai.easonrecyclerview.custom;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.linyuzai.easonrecyclerview.index.IndexableWrapper;
import com.linyuzai.easonrecyclerview.rx.AdditionalDataObservable;
import com.linyuzai.easonrecyclerview.rx.AdditionalDataObserver;
import com.linyuzai.easonrecyclerview.rx.IndexBarDataObservable;
import com.linyuzai.easonrecyclerview.rx.IndexBarDataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YoKey on 16/10/16.
 */

public abstract class AbstractAdditionalAdapter<T> {
    private final AdditionalDataObservable mDataSetObservable = new AdditionalDataObservable();
    private final IndexBarDataObservable mIndexBarDataSetObservable = new IndexBarDataObservable();

    ArrayList<IndexableWrapper<T>> mIndexableWrapperList = new ArrayList<>();
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
    public AbstractAdditionalAdapter(String index, String indexTitle, List<T> datas) {
        this.mIndex = index;
        this.mIndexTitle = indexTitle;

        if (indexTitle != null) {
            IndexableWrapper<T> wrapper = wrapEntity();
            wrapper.setItemType(IndexableWrapper.TYPE_TITLE);
        }
        for (int i = 0; i < datas.size(); i++) {
            IndexableWrapper<T> wrapper = wrapEntity();
            wrapper.setData(datas.get(i));
        }
    }

    private IndexableWrapper<T> wrapEntity() {
        IndexableWrapper<T> wrapper = new IndexableWrapper<>();
        wrapper.setIndex(mIndex);
        wrapper.setIndexTitle(mIndexTitle);
        wrapper.setHeaderFooterType(getHeaderFooterType());
        mIndexableWrapperList.add(wrapper);
        return wrapper;
    }

    private IndexableWrapper<T> wrapEntity(int pos) {
        IndexableWrapper<T> wrapper = new IndexableWrapper<>();
        wrapper.setIndex(mIndex);
        wrapper.setIndexTitle(mIndexTitle);
        wrapper.setHeaderFooterType(getHeaderFooterType());
        mIndexableWrapperList.add(pos, wrapper);
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
        int size = mIndexableWrapperList.size();

        IndexableWrapper<T> wrapper = wrapEntity();
        wrapper.setItemType(getItemViewType());
        wrapper.setData(data);

        if (size > 0) {
            mDataSetObservable.notifyAdd(getHeaderFooterType() == IndexableWrapper.TYPE_HEADER, mIndexableWrapperList.get(size - 1), wrapper);
            mIndexBarDataSetObservable.notifyChanged();
        }
    }

    public void removeData(T data) {
        for (IndexableWrapper wrapper : mIndexableWrapperList) {
            if (wrapper.getData() == data) {
                mIndexableWrapperList.remove(wrapper);
                mDataSetObservable.notifyRemove(getHeaderFooterType() == IndexableWrapper.TYPE_HEADER, wrapper);
                mIndexBarDataSetObservable.notifyChanged();
                return;
            }
        }
    }

    int getHeaderFooterType() {
        return IndexableWrapper.TYPE_HEADER;
    }

    public void addData(int position, T data) {
        int size = mIndexableWrapperList.size();
        if (position >= size) {
            return;
        }

        IndexableWrapper<T> wrapper = wrapEntity(position + 1);
        wrapper.setItemType(getItemViewType());
        wrapper.setData(data);

        if (size > 0) {
            mDataSetObservable.notifyAdd(getHeaderFooterType() == IndexableWrapper.TYPE_HEADER, mIndexableWrapperList.get(position), wrapper);
            mIndexBarDataSetObservable.notifyChanged();
        }
    }

    public void addDatas(List<T> datas) {
        for (int i = 0; i < datas.size(); i++) {
            addData(datas.get(i));
        }
    }

    public void addDatas(int position, List<T> datas) {
        int size = mIndexableWrapperList.size();
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

    public OnItemClickListener<T> getOnItemClickListener() {
        return mListener;
    }


    public OnItemLongClickListener getOnItemLongClickListener() {
        return mLongListener;
    }

    public ArrayList<IndexableWrapper<T>> getDatas() {
        for (IndexableWrapper<T> wrapper : mIndexableWrapperList) {
            if (wrapper.getItemType() == IndexableWrapper.TYPE_CONTENT) {
                wrapper.setItemType(getItemViewType());
            }
        }
        return mIndexableWrapperList;
    }

    public void registerDataSetObserver(AdditionalDataObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(AdditionalDataObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void registerIndexBarDataSetObserver(IndexBarDataObserver observer) {
        mIndexBarDataSetObservable.registerObserver(observer);
    }

    public void unregisterIndexBarDataSetObserver(IndexBarDataObserver observer) {
        mIndexBarDataSetObservable.unregisterObserver(observer);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View v, int currentPosition, T entity);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View v, int currentPosition, T entity);
    }
}
