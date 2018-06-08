package com.linyuzai.easonrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.linyuzai.easonrecyclerview.index.IndexableWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by YoKey on 16/10/6.
 */
public class IndexBar extends View {
    private int mTotalHeight;
    private float mTextSpace;

    private List<String> mIndexList = new ArrayList<>();
    // 首字母 到 mIndexList 的映射
    private HashMap<String, Integer> mMapping = new HashMap<>();
    private ArrayList<IndexableWrapper> mDatas;

    private int mSelectionPosition;
    private float mIndexHeight;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public static class Config {
        private Drawable background;
        private int textColor;
        private int focusTextColor;
        private float textSize;
        private float textSpace;
        private int width;
        private int paddingTop;

        public Config background(Drawable background) {
            this.background = background;
            return this;
        }

        public Config background(int backgroundColor) {
            this.background = new ColorDrawable(backgroundColor);
            return this;
        }

        public Config textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Config focusTextColor(int textColor) {
            this.focusTextColor = textColor;
            return this;
        }

        public Config textSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        public Config textSpace(float textSpace) {
            this.textSpace = textSpace;
            return this;
        }

        public Config width(int width) {
            this.width = width;
            return this;
        }

        public Config paddingTop(int top) {
            this.paddingTop = top;
            return this;
        }

        public Drawable background() {
            return background;
        }

        public int textColor() {
            return textColor;
        }

        public int focusTextColor() {
            return focusTextColor;
        }

        public float textSize() {
            return textSize;
        }

        public float textSpace() {
            return textSpace;
        }

        public int width() {
            return width;
        }

        public int paddingTop() {
            return paddingTop;
        }
    }

    public IndexBar(Context context) {
        super(context);
    }

    public void init(Config config) {
        init(config.background, config.textColor, config.focusTextColor, config.textSize, config.textSpace);
    }

    void init(Drawable barBg, int barTextColor, int barFocusTextColor, float barTextSize, float textSpace) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(barBg);
        } else {
            setBackgroundDrawable(barBg);
        }

        this.mTextSpace = textSpace;

        mPaint.setColor(barTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(barTextSize);

        mFocusPaint.setTextAlign(Paint.Align.CENTER);
        mFocusPaint.setTextSize(barTextSize + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mFocusPaint.setColor(barFocusTextColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mIndexList.size() > 0) {
            mTotalHeight = (int) (((mIndexList.size() - 1) * mPaint.getTextSize()
                    + mFocusPaint.getTextSize())
                    + (mIndexList.size() + 1) * mTextSpace);
        }

        if (mTotalHeight > height) {
            mTotalHeight = height;
        }

//        // TODO: 16/10/8  Measure AT_MOST
//        if (mode == MeasureSpec.AT_MOST) {
//            int maxWidth = (int) getResources().getDimension(R.dimen.default_indexBar_layout_width);
//            super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
//            return;
//        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIndexList.size() == 0) return;

        mIndexHeight = ((float) getHeight()) / mIndexList.size();

        for (int i = 0; i < mIndexList.size(); i++) {
            if (mSelectionPosition == i) {
                canvas.drawText(mIndexList.get(i), getWidth() / 2, mIndexHeight * 0.85f + mIndexHeight * i, mFocusPaint);
            } else {
                canvas.drawText(mIndexList.get(i), getWidth() / 2, mIndexHeight * 0.85f + mIndexHeight * i, mPaint);
            }
        }
    }

    int getPositionForPointY(float y) {
        if (mIndexList.size() <= 0) return -1;

        int position = (int) (y / mIndexHeight);

        if (position < 0) {
            position = 0;
        } else if (position > mIndexList.size() - 1) {
            position = mIndexList.size() - 1;
        }

        return position;
    }


    int getSelectionPosition() {
        return mSelectionPosition;
    }

    void setSelectionPosition(int position) {
        this.mSelectionPosition = position;
        invalidate();
    }

    int getFirstRecyclerViewPositionBySelection() {
        String index = mIndexList.get(mSelectionPosition);
        if (mMapping.containsKey(index)) {
            return mMapping.get(index);
        }
        return -1;
    }

    List<String> getIndexList() {
        return mIndexList;
    }

    void setDatas(boolean showAllLetter, ArrayList<IndexableWrapper> datas) {
        this.mDatas = datas;
        this.mIndexList.clear();
        this.mMapping.clear();

        ArrayList<String> tempHeaderList = null;
        if (showAllLetter) {
            mIndexList = Arrays.asList(getResources().getStringArray(R.array.indexable_letter));
            mIndexList = new ArrayList<>(mIndexList);
            tempHeaderList = new ArrayList<>();
        }
        for (int i = 0; i < datas.size(); i++) {
            IndexableWrapper wrapper = datas.get(i);
            if (wrapper.getItemType() == IndexableWrapper.TYPE_TITLE || wrapper.getIndexTitle() == null) {
                String index = wrapper.getIndex();
                if (!TextUtils.isEmpty(index)) {
                    if (!showAllLetter) {
                        mIndexList.add(index);
                    } else {
                        if (EasonRecyclerView.INDEX_SIGN.equals(index)) {
                            mIndexList.add(EasonRecyclerView.INDEX_SIGN);
                        } else if (mIndexList.indexOf(index) < 0) {
                            if (wrapper.getHeaderFooterType() == IndexableWrapper.TYPE_HEADER && tempHeaderList.indexOf(index) < 0) {
                                tempHeaderList.add(index);
                            } else if (wrapper.getHeaderFooterType() == IndexableWrapper.TYPE_FOOTER) {
                                mIndexList.add(index);
                            }
                        }
                    }
                    if (!mMapping.containsKey(index)) {
                        mMapping.put(index, i);
                    }
                }
            }
        }
        if (showAllLetter) {
            mIndexList.addAll(0, tempHeaderList);
        }
        requestLayout();
    }

    void setSelection(int firstVisibleItemPosition) {
        if (mDatas == null || mDatas.size() <= firstVisibleItemPosition || firstVisibleItemPosition < 0)
            return;
        IndexableWrapper wrapper = mDatas.get(firstVisibleItemPosition);
        int position = mIndexList.indexOf(wrapper.getIndex());

        if (mSelectionPosition != position && position >= 0) {
            mSelectionPosition = position;
            invalidate();
        }
    }
}
