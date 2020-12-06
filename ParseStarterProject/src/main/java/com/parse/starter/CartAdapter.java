package com.parse.starter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartItemViewHolder> {

    List<String> list;
    Context context;
    int quantity;
    static int cost = 0;

    public CartAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        calculateTotalAmount();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.cart_item_view,parent,false);

        return new CartItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartItemViewHolder holder, int position) {

        holder.cartItemName.setText(list.get(position));
        holder.cartItemQuantity.setText(String.valueOf(UserAdapter.cartItem.get(list.get(holder.getAdapterPosition()))));

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    quantity = Integer.parseInt(holder.cartItemQuantity.getText().toString());
                    ++quantity;
                    holder.cartItemQuantity.setText(String.valueOf(quantity));
                    UserAdapter.cartItem.put(list.get(holder.getAdapterPosition()),quantity);
                    calculateTotalAmount();
            }
        });

       holder.subtract.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               quantity = Integer.parseInt(holder.cartItemQuantity.getText().toString());
               --quantity;
               if(quantity == 0) {

                   UserAdapter.cart.remove(list.get(holder.getAdapterPosition()));
                   UserAdapter.cartItem.remove(list.get(holder.getAdapterPosition()));
                   list.remove(holder.getAdapterPosition());
                   notifyItemRemoved(holder.getAdapterPosition());
                   calculateTotalAmount();

               } else {
                   holder.cartItemQuantity.setText(String.valueOf(quantity));
                   UserAdapter.cartItem.put(list.get(holder.getAdapterPosition()),quantity);
                   calculateTotalAmount();
               }
           }
       });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void calculateTotalAmount() {

        cost = 0;

        for(Map.Entry<String, Integer> e : UserAdapter.cartItem.entrySet()) {

            cost = cost + (e.getValue() * UserAdapter.cart.get(e.getKey()));

        }

        CartActivity.totalAmountTextView.setText("Amount: "+cost);
    }

}
