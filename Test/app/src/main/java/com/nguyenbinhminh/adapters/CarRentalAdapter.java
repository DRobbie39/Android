package com.nguyenbinhminh.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nguyenbinhminh.test.R;
import com.nguyenbinhminh.models.CarRental;
import com.nguyenbinhminh.test.MainActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarRentalAdapter extends BaseAdapter {
    MainActivity context;
    int item_layout;
    List<CarRental> carRentals;
    public CarRentalAdapter(MainActivity context, int item_layout, List<CarRental> carRentals) {
        this.context = context;
        this.item_layout = item_layout;
        this. carRentals =  carRentals;
    }

    @Override
    public int getCount() {
        return  carRentals.size();
    }

    @Override
    public Object getItem(int position) {
        return  carRentals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(item_layout, null);

            //Linking views
            holder.txtCarName = convertView.findViewById(R.id.txtCarName);
            holder.txtRentalPrice = convertView.findViewById(R.id.txtRentalPrice);
            holder.txtRentalLocation= convertView.findViewById(R.id.txtRentalLocation);
            holder.imvPhoto = convertView.findViewById(R.id.imvPhoto);
            holder.txtCarType = convertView.findViewById(R.id.txtCarType);
            holder.imvEdit = convertView.findViewById(R.id.imvEdit);
            holder.imvDelete = convertView.findViewById(R.id.imvDelete);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        //Binding data
        CarRental carRental =  carRentals.get(position);
        holder.txtCarName.setText(carRental.getCarName());

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMANY);
        holder.txtRentalPrice.setText(String.format("Price: %s đồng", numberFormat.format(carRental.getRentalPrice())));
        holder.txtRentalLocation.setText("Location: " + carRental.getRentalLocation());


        byte[] photo = carRental.getCarImv();
        if (photo != null && photo.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            holder.imvPhoto.setImageBitmap(bitmap);
        } else {
            // Set hình ảnh mặc định
            holder.imvPhoto.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.txtCarType.setText("Type: " + carRental.getCarType());
        holder.imvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.openEditDialog(carRental);
            }
        });

        holder.imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.openDeleteConfirmDialog(carRental);
            }
        });
        return convertView;
    }

    public static class ViewHolder{
        TextView txtCarName, txtRentalLocation, txtRentalPrice, txtCarType;
        ImageView imvEdit, imvDelete, imvPhoto;
    }
}
