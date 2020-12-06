package com.parse.starter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    List<ItemData> list;
    List<Boolean> status;
    ParseObject item,oldPrice;
    PopupMenu menu;
    Context context;
    boolean focusable=true;
    EditText updatePrice;
    Button updateP,closeButtonP;
    LayoutInflater inflater;
    View popupView;
    PopupWindow popupWindow;
    String name;

    public MyAdapter(List<ItemData> list, List<Boolean> status, Context context) {
        this.list = list;
        this.status = status;
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.item_menu_admin,viewGroup,false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.itemName.setText(list.get(i).itemName);
        itemViewHolder.switchItem.setChecked(status.get(i));
        itemViewHolder.switchItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ItemDetails");
                    query.whereEqualTo("name", list.get(itemViewHolder.getAdapterPosition()).itemName);
                    try {
                        item = query.getFirst();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isChecked) {
                        item.put("status", true);
                    } else {
                        item.put("status", false);
                    }
                    try {
                        item.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        itemViewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu = new PopupMenu(context ,itemViewHolder.menuButton);
                menu.inflate(R.menu.admin_popup_menu);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.modifyPrice) {
                            setpopupWindowPrice();
                            updatePrice = popupView.findViewById(R.id.updatePrice);
                            closeButtonP = popupView.findViewById(R.id.closePriceButton);
                            updateP = popupView.findViewById(R.id.updatePriceButton);
                            name = list.get(itemViewHolder.getAdapterPosition()).itemName;

                            try {

                                ParseQuery<ParseObject> price = ParseQuery.getQuery("ItemDetails");
                                price.whereEqualTo("code",ParseUser.getCurrentUser().getString("Code"));
                                price.whereEqualTo("name",name);

                                oldPrice = price.getFirst();

                                updatePrice.setText(String.valueOf(oldPrice.getInt("price")));

                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                            closeButtonP.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    popupWindow.dismiss();
                                }
                            });

                            updateP.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (updatePrice.getText().toString().contentEquals("")) {
                                        updatePrice.setError("Price Required");
                                    } else {
                                        queryUpdate();
                                    }
                                }
                            });
                        }
                        return true;
                    }
                });
                menu.show();
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


    private void setpopupWindowPrice(){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_modify_price,null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }
        popupWindow.showAtLocation(AdminActivity.adminLayout, Gravity.CENTER,0,0);
    }

    public void queryUpdate() {

        oldPrice.put("price",Integer.valueOf(updatePrice.getText().toString()));

        try {

            oldPrice.save();
            popupWindow.dismiss();
            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}