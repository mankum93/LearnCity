package com.learncity.util;

public interface DataSetObserver {

    public void onChanged();

    public void onItemRangeChanged(int positionStart, int itemCount);

    public void onItemRangeChanged(int positionStart, int itemCount, Object payload);

    public void onItemRangeInserted(int positionStart, int itemCount);

    public void onItemRangeRemoved(int positionStart, int itemCount);

    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount);
}