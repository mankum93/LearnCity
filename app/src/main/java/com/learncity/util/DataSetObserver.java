package com.learncity.util;

import java.util.List;

public interface DataSetObserver<T> {

    public void onChanged(List<T> newData);

    public void onItemRangeChanged(int positionStart, int itemCount);

    public void onItemRangeChanged(int positionStart, int itemCount, Object payload);

    public void onItemRangeInserted(int positionStart, int itemCount);

    public void onItemRangeRemoved(int positionStart, int itemCount);

    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount);
}