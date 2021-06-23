package com.coders.TaskApp.models;

import com.coders.TaskApp.Adapter.RecyclerViewItem;

public class Header implements RecyclerViewItem {
    public Header(String title) {
        this.title = title;
    }

    public String title;
    @Override
    public int getItemViewType() {
        return 1;
    }
}
