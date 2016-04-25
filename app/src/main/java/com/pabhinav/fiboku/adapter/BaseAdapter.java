package com.pabhinav.fiboku.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pabhinav
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.DataObjectHolder>  {

    private static RecyclerViewAdapterEvents recyclerViewAdapterEvents;
    private int childLayoutResId;
    @Getter @Setter
    private ArrayList<T> mDataset;
    @Getter
    private Context context;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;

    /** Reflects addition of a footer view **/
    @Getter
    private static boolean footerAdded;
    private int footerLayoutResId;

    public BaseAdapter(int childLayoutResId, ArrayList<T> mDataset, Context context, boolean footerAdded, int footerLayoutResId){
        this.childLayoutResId = childLayoutResId;
        this.mDataset = mDataset;
        this.context = context;
        BaseAdapter.footerAdded = footerAdded;
        this.footerLayoutResId = footerLayoutResId;
    }

    /** Static view holder for Adapter **/
    public static abstract class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** Constructor with root view **/
        public DataObjectHolder(View itemView) {
            super(itemView);
            initialize(itemView);
            if(itemView != null) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if(recyclerViewAdapterEvents != null){
                recyclerViewAdapterEvents.onItemClick(getPosition(), v);
            }
        }

        /** Needs to be implemented by implementing class for initializing view elements in each item's view **/
        public abstract void initialize(View rootView);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1 && isFooterAdded()) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){
            return onCreateFooterInitializeDataObjectHolder(LayoutInflater.from(parent.getContext()).inflate(footerLayoutResId,parent, false));
        } else {
            return onCreateInitializeDataObjectHolder(LayoutInflater.from(parent.getContext()).inflate(childLayoutResId, parent, false));
        }
    }

    /** Initialize here with the implementation for DataObjectHolder class. **/
    public abstract DataObjectHolder onCreateInitializeDataObjectHolder(View v);

    /** Initialize footer here with the implementation for DataObjectHolder class. **/
    public abstract DataObjectHolder onCreateFooterInitializeDataObjectHolder(View v);

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position){

        if(holder.getItemViewType() == TYPE_FOOTER){
            onBindFooterItemViewHolder(holder);
        } else {
            onBindItemViewHolder(holder, position, mDataset.get(position));
        }
    }

    /** on Bind View holder for sub-class **/
    public abstract void onBindItemViewHolder(DataObjectHolder holder, int position, T dataObj);

    /** on Bind View holder for last item, called when there is a footer view added to it **/
    public abstract void onBindFooterItemViewHolder(DataObjectHolder holder);

    @Override
    public int getItemCount() {
        if(!isFooterAdded()) {
            return (mDataset != null) ? mDataset.size() : 0;
        } else {
            return (mDataset != null) ? mDataset.size() + 1 : 1;
        }
    }

    /** Add single item at the end **/
    public void addItem(T dataObj) {
        if(mDataset != null) {
            mDataset.add(dataObj);
            notifyDataSetChanged();
        }
    }

    /** Add Multiple items at the end **/
    public void addMultipleItems(ArrayList<T> objectArrayList){
        if(mDataset != null) {
            mDataset.addAll(objectArrayList);
            notifyDataSetChanged();
        }
    }

    /** Delete Single Item **/
    public void deleteItem(T dataObj) {
        if(mDataset != null) {
            mDataset.remove(mDataset.indexOf(dataObj));
            notifyDataSetChanged();
        }
    }

    /** Deletes all items **/
    public void deleteAllItems(){
        if(mDataset != null){
            mDataset = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    public T getItemAtPosition(int position){
        if(mDataset != null){
            return mDataset.get(position);
        }
        return null;
    }

    public void changeDataCompletely(ArrayList<T> tArrayList){
        mDataset.clear();
        mDataset = new ArrayList<>(tArrayList);
        notifyDataSetChanged();
    }

    public void setRecyclerViewAdapterEvents(RecyclerViewAdapterEvents recyclerViewAdapterEvents) {
        BaseAdapter.recyclerViewAdapterEvents = recyclerViewAdapterEvents;
    }

    /** Callback events **/
    public interface RecyclerViewAdapterEvents{
        void onItemClick(int position, View v);
    }
}
