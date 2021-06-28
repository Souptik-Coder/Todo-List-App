package com.coders.TaskApp.models;

import com.coders.TaskApp.Adapter.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;

public class Header implements RecyclerViewItem {
    public int id;
    public String title;
    public boolean isExpanded;
    private List<RecyclerViewItem> childItems;

    public Header(String title,int id) {
        this.id=id;
        this.title = title+"(0)";
        isExpanded = true;
        childItems = new ArrayList<>();
    }

    @Override
    public int getItemViewType() {
        return 1;
    }

    public List<RecyclerViewItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<RecyclerViewItem> childItems) {
        this.childItems = childItems;
    }

    public void clearChildItems() {
        childItems.clear();
        StringBuilder myName = new StringBuilder(title);
        myName.setCharAt(title.length() - 2, (char) (childItems.size() + '0'));
        title=myName.toString();
    }

    public void addChildItem(RecyclerViewItem item){
        childItems.add(item);
        StringBuilder myName = new StringBuilder(title);
        myName.setCharAt(title.length() - 2, (char) (childItems.size() + '0'));
        title=myName.toString();
    }

    public void removeChildItem(RecyclerViewItem item){
        childItems.remove(item);
        StringBuilder myName = new StringBuilder(title);
        myName.setCharAt(title.length() - 2, (char) (childItems.size() + '0'));
        title=myName.toString();
    }

}
