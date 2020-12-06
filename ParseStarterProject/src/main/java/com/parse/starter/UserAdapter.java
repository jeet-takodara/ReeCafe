package com.parse.starter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserItemViewHolder> {

    List<UserItemData> list;
    Context context;
    static Map<String,Integer> cart;
    static Map<String,Integer> cartItem;

    public UserAdapter(List<UserItemData> list, Context context) {
        this.list = list;
        this.context = context;
        cart = new HashMap<>();
        cartItem = new HashMap<>();
    }

    @NonNull
    @Override
    public UserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_menu_user,parent,false);

        UserItemViewHolder holder = new UserItemViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserItemViewHolder holder, int position) {
        holder.itemName.setText(list.get(position).name.toUpperCase());
        holder.itemPrice.setText(MessageFormat.format("{0}/-", list.get(position).price));
        Glide.with(context).load(list.get(position).imageUrl).into(holder.itemImage);

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.isPressed()) {
                    if (cart.containsKey(list.get(holder.getAdapterPosition()).name))
                        Toast.makeText(context, "Item already in cart!", Toast.LENGTH_SHORT).show();
                    else {
                        cart.put(list.get(holder.getAdapterPosition()).name, list.get(holder.getAdapterPosition()).price);
                        cartItem.put(list.get(holder.getAdapterPosition()).name,1);
                        Toast.makeText(context, list.get(holder.getAdapterPosition()).name.toUpperCase()+" added to cart!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
