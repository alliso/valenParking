package com.upv.dadm.valenparking.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upv.dadm.valenparking.Fragments.FavouriteFragment;
import com.upv.dadm.valenparking.Parkings;
import com.upv.dadm.valenparking.R;

import java.util.List;

public class fauvoriteAdapter extends RecyclerView.Adapter<fauvoriteAdapter.ViewHolder> {


    private List<Parkings> data;
    private Parkings lastClickedParking;
    private Context context;
    private int layout;
    boolean aux;
    private OnFavouriteLongClickListener clickLongListener;
    private OnFavouriteShortClickListener clickShortListener;
    private OnShortClickGoToMapListener clickMapListener;

    public fauvoriteAdapter(Context context, int resource, List<Parkings> data, OnFavouriteLongClickListener LongClicklistener, OnFavouriteShortClickListener shortClickListener, OnShortClickGoToMapListener clickMapListener) {
        super();
        this.data = data;
        this.context = context;
        this.layout = resource;
        this.clickLongListener = LongClicklistener;
        this.clickShortListener = shortClickListener;
        this.clickMapListener = clickMapListener;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list, parent, false);
        fauvoriteAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Parkings parkings = data.get(position);
        aux = false;
        holder.tv_list_name.setText((data.get(position).getParkingName()));
        holder.tv_list_free.setText((String.valueOf(data.get(position).getCalle())));
        holder.v.setBackgroundColor(parkings.isSelected() ? context.getResources().getColor(R.color.favouriteSelected) : context.getResources().getColor(R.color.colorBgApp));

        for(Parkings p : data){
            if(p.isSelected()) {
                aux = true;
            }
        }



            holder.lytParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(!aux) {
                        final Parkings parking = data.get(holder.getAdapterPosition());
                        parking.setSelected(!parking.isSelected());
                        holder.lytParent.setBackgroundColor(parking.isSelected() ? context.getResources().getColor(R.color.favouriteSelected) : context.getResources().getColor(R.color.colorBgApp));
                        clickLongListener.onFavouriteLongClick();
                        notifyItemChanged(holder.getAdapterPosition());

                    }
                    return true;
                }
            });


            holder.lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(aux) {
                        final Parkings parking = data.get(holder.getAdapterPosition());
                        parking.setSelected(!parking.isSelected());
                        holder.lytParent.setBackgroundColor(parking.isSelected() ? context.getResources().getColor(R.color.favouriteSelected) : context.getResources().getColor(R.color.colorBgApp));
                        clickShortListener.onFavouriteShortClick();
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });

            holder.lytParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!aux) {
                        final Parkings parking = data.get(holder.getAdapterPosition());

                        setLastClickedParking(parking);


                        clickMapListener.onShortClickGoToMap();
                    }
                }
            });



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_list_name;
        TextView tv_list_free;
        public View lytParent;
        View v;

        public ViewHolder(View view) {
            super(view);
            tv_list_name = (TextView) view.findViewById(R.id.quotation_list_name);
            tv_list_free = (TextView) view.findViewById(R.id.quotation_list_calle);
            lytParent = (View) view.findViewById(R.id.parking_view);
            v = view;
        }
    }

    public interface OnFavouriteLongClickListener {
        void onFavouriteLongClick();
    }

    public interface OnFavouriteShortClickListener{
        void onFavouriteShortClick();
    }

    public interface OnShortClickGoToMapListener{
        void onShortClickGoToMap();
    }

    public Parkings getLastClickedParking() {
        return lastClickedParking;
    }

    public void setLastClickedParking(Parkings lastClickedParking) {
        this.lastClickedParking = lastClickedParking;
    }
}
