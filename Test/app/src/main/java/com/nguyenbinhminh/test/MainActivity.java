package com.nguyenbinhminh.test;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nguyenbinhminh.adapters.CarRentalAdapter;
import com.nguyenbinhminh.database.CarRentalDatabase;
import com.nguyenbinhminh.models.CarRental;
import com.nguyenbinhminh.test.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    CarRentalDatabase db;
    CarRentalAdapter adapter;
    ArrayList<CarRental> carRentals;
    ActivityResultLauncher<Intent> launcher;
    ImageView imvPhoto;

    String[] TypeArray = {"4 chỗ", "7 chỗ", "16 chỗ"};

    byte[] convertPhoto() {
        if (imvPhoto != null && imvPhoto.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) imvPhoto.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } else return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            //Gallery
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        imvPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Camera
//            public void onActivityResult(ActivityResult o) {
//                if (o.getResultCode() == RESULT_OK && o.getData() != null) {
//                    Bitmap bitmap = (Bitmap) o.getData().getExtras().get("data");
//                    imvPhoto.setImageBitmap(bitmap);
//                }
//            }
        });


        String[] SearchTypeArray = {"All", "4 chỗ", "7 chỗ", "16 chỗ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SearchTypeArray);
        binding.spnCarType.setAdapter(adapter);

        db = new CarRentalDatabase(this);

        getData();
        addEvents();
    }

    private void addEvents() {
        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_dialog);

                EditText edtName = dialog.findViewById(R.id.edtCarName);
                EditText edtLoc = dialog.findViewById(R.id.edtRentalLocation);
                EditText edtPrice = dialog.findViewById(R.id.edtRentalPrice);
                imvPhoto = dialog.findViewById(R.id.imvPhoto);

                Spinner spnFoodType = dialog.findViewById(R.id.spnCarType);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, TypeArray);
                spnFoodType.setAdapter(adapter);

                Button btnGallery = dialog.findViewById(R.id.btnGallery);
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Open Camera
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //Open gallery
                        launcher.launch(intent);
                    }
                });

                Button btnSave = dialog.findViewById(R.id.btnSave);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = edtName.getText().toString();
                        String loc = edtLoc.getText().toString();
                        String type = spnFoodType.getSelectedItem().toString();
                        if (name.isEmpty() || loc.isEmpty() || edtPrice.getText().toString().isEmpty() || type.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double price;
                        try {
                            price = Double.parseDouble(edtPrice.getText().toString());
                            if (price <= 0) {
                                Toast.makeText(MainActivity.this, "Price must be a positive number.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Invalid price format.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.insertData(name ,price,convertPhoto(),loc,type);
                        getData();
                        dialog.dismiss();
                    }
                });

                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edtSearch = binding.edtSearch.getText().toString().trim();
                String edtFromPrice = binding.edtFormPrice.getText().toString().trim();
                String edtToPrice = binding.edtToPrice.getText().toString().trim();
                String spnCarType = binding.spnCarType.getSelectedItem().toString().trim();


                double edtFrom = -1;
                double edtTo = -1;

                try {
                    if (!edtFromPrice.isEmpty()) {
                        edtFrom = Double.parseDouble(edtFromPrice);
                    }
                    if (!edtToPrice.isEmpty()) {
                        edtTo = Double.parseDouble(edtToPrice);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập giá trị hợp lệ cho giá", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Thực hiện tìm kiếm và xử lý kết quả
                Cursor cursor = db.searchData(edtSearch, edtFrom, edtTo, spnCarType);

                if (cursor != null) {
                    try {
                        if (cursor.getCount() > 0) {
                            carRentals.clear(); // Xóa dữ liệu cũ trong danh sách

                            while (cursor.moveToNext()) {
                                carRentals.add(new CarRental(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),cursor.getBlob(3), cursor.getString(4), cursor.getString(5)));
                            }

                            cursor.close();
                            db.close();
                            adapter.notifyDataSetChanged(); // Cập nhật adapter
                        } else {
                            db.close();
                            carRentals.clear(); // Xóa dữ liệu cũ trong danh sách
                            adapter.notifyDataSetChanged(); // Cập nhật adapter

                            Toast.makeText(getApplicationContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                        }
                    } finally {
                        cursor.close();
                        db.close();
                    }
                }
            }
        });
    }

    private void getData() {
        adapter = new CarRentalAdapter(MainActivity.this, R.layout.item_list, loadDataFromDB());
        binding.lvCarRental.setAdapter(adapter);
    }

    private List<CarRental> loadDataFromDB() {
        carRentals = new ArrayList<>();
        Cursor cursor = db.getData("SELECT * FROM " + CarRentalDatabase.TBL_NAME);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                carRentals.add(new CarRental(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),cursor.getBlob(3), cursor.getString(4), cursor.getString(5)));
            }
            cursor.close();
        }
        return carRentals;
    }

    public void openDeleteConfirmDialog(CarRental carRental) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Xác nhận xóa!");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Bạn có chắc muốn xóa xe " + carRental.getCarName() + " cho thuê?");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.execSql("DELETE FROM " + CarRentalDatabase.TBL_NAME + " WHERE " + CarRentalDatabase.COL_CODE + " = " + carRental.getRentalCode());
                getData();
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void openEditDialog (CarRental carRental) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.edit_dialog);

        TextView txtTilte = dialog.findViewById(R.id.txtTitle);
        txtTilte.setText("Edit rental of " + carRental.getCarName() + " (" + carRental.getRentalCode() +")");

        EditText edtName = dialog.findViewById(R.id.edtCarName);
        edtName.setText(carRental.getCarName());

        EditText edtPrice = dialog.findViewById(R.id.edtRentalPrice);
        edtPrice.setText(String.format("%.0f",carRental.getRentalPrice()));

        imvPhoto = dialog.findViewById(R.id.imvPhoto);
        byte[] photo = carRental.getCarImv();
        if (photo != null && photo.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            imvPhoto.setImageBitmap(bitmap);
        } else {
            // Set hình ảnh mặc định
            imvPhoto.setImageResource(R.drawable.ic_launcher_background);
        }

        EditText edtLoc = dialog.findViewById(R.id.edtRentalLocation);
        edtLoc.setText(carRental.getRentalLocation());

        Spinner spnFoodType = dialog.findViewById(R.id.spnCarType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, TypeArray);
        spnFoodType.setAdapter(adapter);
        for (int i = 0; i < TypeArray.length; i++) {
            if (TypeArray[i].equals(carRental.getCarType())) {
                spnFoodType.setSelection(i);
                break;
            }
        }

        Button btnGallery = dialog.findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Open Camera
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //Open gallery
                launcher.launch(intent);
            }
        });

        Button btnSave = dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Updating data
                String name = edtName.getText().toString();
                String loc = edtLoc.getText().toString();
                String type = spnFoodType.getSelectedItem().toString();
                if (name.isEmpty() || loc.isEmpty() || edtPrice.getText().toString().isEmpty() || type.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(edtPrice.getText().toString());
                    if (price <= 0) {
                        Toast.makeText(MainActivity.this, "Price must be a positive number.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid price format.", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.updateData(carRental.getRentalCode(),name,price,convertPhoto(),loc,type);
                getData();
                dialog.dismiss();
            }
        });

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
}