package com.tradeapp.ui.product;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.tradeapp.R;
import com.tradeapp.model.Product;
import com.tradeapp.network.ApiClient;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 401;
    private Uri selectedImage;
    private ImageView preview;
    private EditText name, category, price, unit, stock, moq;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        preview=findViewById(R.id.uploadImagePreview);
        name=findViewById(R.id.productNameInput); category=findViewById(R.id.categoryInput);
        price=findViewById(R.id.priceInput); unit=findViewById(R.id.unitInput);
        stock=findViewById(R.id.stockInput); moq=findViewById(R.id.moqInput);

        findViewById(R.id.chooseImageButton).setOnClickListener(v -> chooseImage());
        findViewById(R.id.saveProductButton).setOnClickListener(v -> createProduct());
    }

    private void chooseImage() {
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("image/*");
        startActivityForResult(i,PICK_IMAGE);
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            selectedImage=data.getData();
            try { getContentResolver().takePersistableUriPermission(selectedImage,
                    data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored) {}
            Glide.with(this).load(selectedImage).centerCrop().into(preview);
        }
    }

    private void createProduct() {
        try {
            if(name.getText().toString().trim().isEmpty() || price.getText().toString().trim().isEmpty()
                    || unit.getText().toString().trim().isEmpty()) {
                Toast.makeText(this,"Name, price and unit are required",Toast.LENGTH_SHORT).show(); return;
            }
            Product p=new Product();
            p.setName(name.getText().toString().trim());
            p.setCategory(category.getText().toString().trim());
            p.setUnitPrice(new BigDecimal(price.getText().toString().trim()));
            p.setUnit(unit.getText().toString().trim());
            p.setStockQty(Integer.parseInt(stock.getText().toString().trim()));
            p.setMinOrderQuantity(Integer.parseInt(moq.getText().toString().trim()));

            ApiClient.getApi(this).createProduct(p).enqueue(new Callback<>() {
                @Override public void onResponse(Call<Product> call,Response<Product> r) {
                    if(!r.isSuccessful() || r.body()==null) { Toast.makeText(AddProductActivity.this,"Could not publish product",Toast.LENGTH_SHORT).show(); return; }
                    if(selectedImage==null) { Toast.makeText(AddProductActivity.this,"Product published",Toast.LENGTH_SHORT).show(); finish(); return; }
                    uploadImage(r.body().getId());
                }
                @Override public void onFailure(Call<Product> call,Throwable t) { Toast.makeText(AddProductActivity.this,"Network error: "+t.getMessage(),Toast.LENGTH_SHORT).show(); }
            });
        } catch(Exception e) { Toast.makeText(this,"Check the product details",Toast.LENGTH_SHORT).show(); }
    }

    private void uploadImage(Long productId) {
        try {
            String mime=getContentResolver().getType(selectedImage);
            if(mime==null) mime="image/jpeg";
            String filename=getFileName(selectedImage); if(filename==null) filename="product.jpg";
            InputStream in=getContentResolver().openInputStream(selectedImage);
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            byte[] buffer=new byte[8192]; int n;
            while((n=in.read(buffer))!=-1) out.write(buffer,0,n);
            in.close();
            byte[] bytes=out.toByteArray();
            RequestBody body=RequestBody.create(MediaType.parse(mime),bytes);
            MultipartBody.Part part=MultipartBody.Part.createFormData("image",filename,body);
            ApiClient.getApi(this).uploadProductImage(productId,part).enqueue(new Callback<>() {
                @Override public void onResponse(Call<Product> c,Response<Product> r) { Toast.makeText(AddProductActivity.this,r.isSuccessful()?"Product published":"Product created, but image upload failed",Toast.LENGTH_LONG).show(); finish(); }
                @Override public void onFailure(Call<Product> c,Throwable t) { Toast.makeText(AddProductActivity.this,"Product created, but image upload failed",Toast.LENGTH_LONG).show(); finish(); }
            });
        } catch(Exception e) { Toast.makeText(this,"Product created, but image could not be uploaded",Toast.LENGTH_LONG).show(); finish(); }
    }

    private String getFileName(Uri uri) {
        Cursor c=getContentResolver().query(uri,null,null,null,null);
        if(c!=null) try { int i=c.getColumnIndex(OpenableColumns.DISPLAY_NAME); if(c.moveToFirst() && i>=0) return c.getString(i); } finally { c.close(); }
        return null;
    }
}
