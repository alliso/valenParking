package com.upv.dadm.valenparking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class fauvoriteAdapter extends RecyclerView.Adapter<fauvoriteAdapter.ViewHolder> {

    private List<Parkings> data;
    private Context context;
    private int layout;

    public fauvoriteAdapter(Context context, int resource, List<Parkings> data) {
        this.data= data;
        this.context = context;
        this.layout = resource;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list, parent, false);
        fauvoriteAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tv_list_name.setText((data.get(position).getParkingName()));
        holder.tv_list_free.setText((String.valueOf(data.get(position).getFree())));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_list_name;
        TextView tv_list_free;
        View v;

        public ViewHolder(View view) {
            super(view);
            tv_list_name = (TextView) view.findViewById(R.id.quotation_list_name);
            tv_list_free = (TextView) view.findViewById(R.id.quotation_list_plazas_libres);
            v = view;
        }
    }

}
