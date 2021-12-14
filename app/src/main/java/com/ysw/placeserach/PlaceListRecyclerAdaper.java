package com.ysw.placeserach;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceListRecyclerAdaper extends RecyclerView.Adapter {

    Context context;
    //대량의 데이터 List
    List<Place> places;

    //생성자 - 객체가 new될때 자동으로 실행되는 메소드
    public PlaceListRecyclerAdaper(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View itemView= inflater.inflate(R.layout.recycler_item_list_frgment, parent, false);
        VH vh= new VH(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH vh= (VH)holder;

        Place place= places.get(position);

        vh.tvPlaceName.setText(place.place_name);
        if(place.road_address_name.equals("")) vh.tvAddress.setText(place.address_name);
        else vh.tvAddress.setText(place.road_address_name);
        vh.tvDistance.setText(place.distance+"m");

    }

    @Override
    public int getItemCount() {
        return places.size();
    }


    //현수막 1개 객체(아이템뷰)를 참조하고 있는 클래스
    class VH extends RecyclerView.ViewHolder{

        TextView tvPlaceName;
        TextView tvAddress;
        TextView tvDistance;

        public VH(@NonNull View itemView) {
            super(itemView);

            tvPlaceName= itemView.findViewById(R.id.tv_place_name);
            tvAddress= itemView.findViewById(R.id.tv_address);
            tvDistance= itemView.findViewById(R.id.tv_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int index= getAdapterPosition();
                    Intent intent = new Intent(context, PlaceUrlActivity.class);
                    intent.putExtra("place_url",places.get(index).place_url);
                    context.startActivity(intent);
                }
            });

        }
    }

}
