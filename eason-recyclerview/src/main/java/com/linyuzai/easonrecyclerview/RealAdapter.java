package com.linyuzai.easonrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.linyuzai.easonrecyclerview.custom.AbstractAdditionalAdapter;
import com.linyuzai.easonrecyclerview.custom.IndexableSuffixAdapter;
import com.linyuzai.easonrecyclerview.custom.IndexablePrefixAdapter;
import com.linyuzai.easonrecyclerview.index.Indexable;
import com.linyuzai.easonrecyclerview.index.IndexableWrapper;

import java.util.ArrayList;

/**
 * Created by YoKey on 16/10/6.
 */
@SuppressWarnings("unchecked")
class RealAdapter<T extends Indexable> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<IndexableWrapper<T>> mDatasList = new ArrayList<>();
    private ArrayList<IndexableWrapper<T>> mDatas;
    private ArrayList<IndexableWrapper<T>> mHeaderDatasList = new ArrayList<>();
    private ArrayList<IndexableWrapper<T>> mFooterDatasList = new ArrayList<>();
    private IndexableAdapter<T> mAdapter;

    private SparseArray<IndexablePrefixAdapter> mHeaderAdapterMap = new SparseArray<>();
    private SparseArray<IndexableSuffixAdapter> mFooterAdapterMap = new SparseArray<>();

    private IndexableAdapter.OnItemTitleClickListener mTitleClickListener;
    private IndexableAdapter.OnItemContentClickListener<T> mContentClickListener;
    private IndexableAdapter.OnItemTitleLongClickListener mTitleLongClickListener;
    private IndexableAdapter.OnItemContentLongClickListener<T> mContentLongClickListener;

    void setIndexableAdapter(IndexableAdapter<T> adapter) {
        this.mAdapter = adapter;
    }

    void addIndexableHeaderAdapter(IndexablePrefixAdapter adapter) {
        mHeaderDatasList.addAll(0, adapter.getDatas());
        mDatasList.addAll(0, adapter.getDatas());
        mHeaderAdapterMap.put(adapter.getItemViewType(), adapter);
        notifyDataSetChanged();
    }

    void removeIndexableHeaderAdapter(IndexablePrefixAdapter adapter) {
        mHeaderDatasList.removeAll(adapter.getDatas());
        if (mDatasList.size() > 0) {
            mDatasList.removeAll(adapter.getDatas());
        }
        mHeaderAdapterMap.remove(adapter.getItemViewType());
        notifyDataSetChanged();
    }

    void addIndexableFooterAdapter(IndexableSuffixAdapter adapter) {
        mFooterDatasList.addAll(adapter.getDatas());
        mDatasList.addAll(adapter.getDatas());
        mFooterAdapterMap.put(adapter.getItemViewType(), adapter);
        notifyDataSetChanged();
    }

    void removeIndexableFooterAdapter(IndexableSuffixAdapter adapter) {
        mFooterDatasList.removeAll(adapter.getDatas());
        if (mDatasList.size() > 0) {
            mDatasList.removeAll(adapter.getDatas());
        }
        mFooterAdapterMap.remove(adapter.getItemViewType());
        notifyDataSetChanged();
    }

    void setDatas(ArrayList<IndexableWrapper<T>> datas) {
        if (mDatas != null && mDatasList.size() > mHeaderDatasList.size() + mFooterDatasList.size()) {
            mDatasList.removeAll(mDatas);
        }

        this.mDatas = datas;

        mDatasList.addAll(mHeaderDatasList.size(), datas);
        notifyDataSetChanged();
    }

    ArrayList<IndexableWrapper<T>> getItems() {
        return mDatasList;
    }

    @Override
    public int getItemViewType(int position) {
        return mDatasList.get(position).getItemType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final RecyclerView.ViewHolder holder;

        if (viewType == IndexableWrapper.TYPE_TITLE) {
            holder = mAdapter.onCreateTitleViewHolder(parent);
        } else if (viewType == IndexableWrapper.TYPE_CONTENT) {
            holder = mAdapter.onCreateContentViewHolder(parent);
        } else {
            AbstractAdditionalAdapter adapter;
            if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                adapter = mHeaderAdapterMap.get(viewType);
            } else {
                adapter = mFooterAdapterMap.get(viewType);
            }
            holder = adapter.onCreateContentViewHolder(parent);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                IndexableWrapper<T> wrapper = mDatasList.get(position);
                if (viewType == IndexableWrapper.TYPE_TITLE) {
                    if (mTitleClickListener != null) {
                        mTitleClickListener.onItemClick(v, position, wrapper.getIndexTitle());
                    }
                } else if (viewType == IndexableWrapper.TYPE_CONTENT) {
                    if (mContentClickListener != null) {
                        mContentClickListener.onItemClick(v, wrapper.getOriginalPosition(), position, wrapper.getData());
                    }
                } else {
                    AbstractAdditionalAdapter adapter;
                    if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                        adapter = mHeaderAdapterMap.get(viewType);
                    } else {
                        adapter = mFooterAdapterMap.get(viewType);
                    }

                    if (adapter != null) {
                        AbstractAdditionalAdapter.OnItemClickListener listener = adapter.getOnItemClickListener();
                        if (listener != null) {
                            listener.onItemClick(v, position, wrapper.getData());
                        }
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                IndexableWrapper<T> wrapper = mDatasList.get(position);
                if (viewType == IndexableWrapper.TYPE_TITLE) {
                    if (mTitleLongClickListener != null) {
                        return mTitleLongClickListener.onItemLongClick(v, position, wrapper.getIndexTitle());
                    } else {
                        return true;
                    }
                } else if (viewType == IndexableWrapper.TYPE_CONTENT) {
                    if (mContentLongClickListener != null) {
                        return mContentLongClickListener.onItemLongClick(v, wrapper.getOriginalPosition(), position, wrapper.getData());
                    } else {
                        return true;
                    }
                } else {
                    AbstractAdditionalAdapter adapter;
                    if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                        adapter = mHeaderAdapterMap.get(viewType);
                    } else {
                        adapter = mFooterAdapterMap.get(viewType);
                    }

                    if (adapter != null) {
                        AbstractAdditionalAdapter.OnItemLongClickListener listener = adapter.getOnItemLongClickListener();
                        if (listener != null) {
                            return listener.onItemLongClick(v, position, wrapper.getData());
                        }
                    }
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IndexableWrapper<T> item = mDatasList.get(position);

        int viewType = getItemViewType(position);
        if (viewType == IndexableWrapper.TYPE_TITLE) {
            if (View.INVISIBLE == holder.itemView.getVisibility()) {
                holder.itemView.setVisibility(View.VISIBLE);
            }
            mAdapter.onBindTitleViewHolder(holder, item.getIndexTitle());
        } else if (viewType == IndexableWrapper.TYPE_CONTENT) {
            mAdapter.onBindContentViewHolder(holder, item.getData());
        } else {
            AbstractAdditionalAdapter adapter;
            if (mHeaderAdapterMap.indexOfKey(viewType) >= 0) {
                adapter = mHeaderAdapterMap.get(viewType);
            } else {
                adapter = mFooterAdapterMap.get(viewType);
            }
            adapter.onBindContentViewHolder(holder, item.getData());
        }
    }

    @Override
    public int getItemCount() {
        return mDatasList.size();
    }

    void setOnItemTitleClickListener(IndexableAdapter.OnItemTitleClickListener listener) {
        this.mTitleClickListener = listener;
    }

    void setOnItemContentClickListener(IndexableAdapter.OnItemContentClickListener<T> listener) {
        this.mContentClickListener = listener;
    }

    void setOnItemTitleLongClickListener(IndexableAdapter.OnItemTitleLongClickListener listener) {
        this.mTitleLongClickListener = listener;
    }

    void setOnItemContentLongClickListener(IndexableAdapter.OnItemContentLongClickListener<T> listener) {
        this.mContentLongClickListener = listener;
    }

    void addHeaderFooterData(boolean header, IndexableWrapper preData, IndexableWrapper data) {
        processAddHeaderFooterData(header ? mHeaderDatasList : mFooterDatasList, preData, data);
    }

    private void processAddHeaderFooterData(ArrayList<IndexableWrapper<T>> list, IndexableWrapper preData, IndexableWrapper data) {
        for (int i = 0; i < list.size(); i++) {
            IndexableWrapper wrapper = list.get(i);
            if (wrapper == preData) {
                int index = i + 1;
                list.add(index, data);
                if (list == mFooterDatasList) {
                    index += mDatasList.size() - mFooterDatasList.size() + 1;
                }
                mDatasList.add(index, data);
                notifyItemInserted(i + 1);
                return;
            }
        }
    }

    void removeHeaderFooterData(boolean header, IndexableWrapper data) {
        processremoveHeaderFooterData(header ? mHeaderDatasList : mFooterDatasList, data);
    }

    private void processremoveHeaderFooterData(ArrayList<IndexableWrapper<T>> list, IndexableWrapper data) {
        for (int i = 0; i < list.size(); i++) {
            IndexableWrapper wrapper = list.get(i);
            if (wrapper == data) {
                list.remove(data);
                mDatasList.remove(data);
                notifyItemRemoved(i);
                return;
            }
        }
    }
}
