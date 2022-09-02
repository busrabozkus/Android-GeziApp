package com.busrabozkus.gezignlm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {
    Bitmap secilenResim;
    ImageView imageView;
    EditText sehirAdiText,tarihText;
    Button button;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView=findViewById(R.id.imageView);
        sehirAdiText=findViewById(R.id.sehirAdiText);
        tarihText=findViewById(R.id.tarihText);
        button=findViewById(R.id.button);
        database=this.openOrCreateDatabase("Sehirler",MODE_PRIVATE,null);


        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if (info.matches("new")){//Yeni bişey eklıyo
            sehirAdiText.setText("");
            tarihText.setText("");
            button.setVisibility(View.VISIBLE);
            Bitmap secilmisResim= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.foto);
            imageView.setImageBitmap(secilmisResim);



        }
        else{
            int sehirId=intent.getIntExtra("sehirId",1);
            button.setVisibility(View.INVISIBLE);
            try {
                Cursor cursor=database.rawQuery("SELECT * FROM sehirler WHERE id=?",new String[] {String.valueOf(sehirId)});
                int sehirAdıIx=cursor.getColumnIndex("sehiradi");
                int yılIx=cursor.getColumnIndex("yil");
                int ResımIx=cursor.getColumnIndex("resim");
                while (cursor.moveToNext()){
                    sehirAdiText.setText(cursor.getString(sehirAdıIx));
                    tarihText.setText(cursor.getString(yılIx));

                    byte[] bytes= cursor.getBlob(ResımIx);
                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
                cursor.close();
            }catch (Exception e){

            }



        }
        }




    public void resimSec(View view ){
        //Kullanıcıya resim izni için sorucaz
        //İzin verilemezse
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }
        //İzin verilirse
        else{
            //Galeriyi açma
            Intent İntenttoGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//Dosyanın nerde kayıtlı old anlıycaz
            startActivityForResult(İntenttoGalery,2);//Sonucun verileceği metot
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent İntenttoGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//Dosyanın nerde kayıtlı old anlıycaz
                startActivityForResult(İntenttoGalery,2);//Sonucun verileceği metot
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==2 && resultCode==RESULT_OK && data!=null){
            Uri imageData=data.getData();
            try {

                if (Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source=ImageDecoder.createSource(this.getContentResolver(),imageData);
                    secilenResim=ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(secilenResim);
                }
                else {
                    secilenResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(secilenResim);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void kaydet(View view){
        String sehirAdi=sehirAdiText.getText().toString();
        String tarih=tarihText.getText().toString();
        Bitmap kucukResim=resmiKucult(secilenResim,300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        kucukResim.compress(Bitmap.CompressFormat.PNG,50,outputStream);//veriyi çevirirken kulllanılan özellikleri compress belirler
        byte[] byteArray=outputStream.toByteArray();
        //ByteArrayoutputstream ile görseli veriye çevidik
        try {
            database=this.openOrCreateDatabase("Sehirler",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS sehirler(id INTEGER PRİMARY KEY,sehiradi VARCHAR,yil VARCHAR,resim BLOB)");

            String sqlString="INSERT INTO sehirler(sehiradi,yil,resim) VALUES (?,?,?)";
            //sql de çalıştırılacak duruma getiriyoruz
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,sehirAdi);
            sqLiteStatement.bindString(2,tarih);
            sqLiteStatement.bindBlob(3,byteArray);
            sqLiteStatement.execute();



        }
        catch (Exception e){

        }
        //finish();//Aktiveteyi tamamen bitirir
       Intent intent=new Intent(Main2Activity.this,Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       startActivity(intent);

    }
    public Bitmap resmiKucult(Bitmap image,int maximumsize){
        //En fazla maksimum size'ı alacak
        int width=image.getWidth();
        int height=image.getHeight();
        float bitmapOran= (float)width/(float)height;
        if (bitmapOran>1){//Yatay Resimse
            width=maximumsize;
            height=(int)(width/bitmapOran);
        }
        else {//Dikey Resimse
            height=maximumsize;
            width=(int)(height*bitmapOran);
        }
        return  Bitmap.createScaledBitmap(image,width,height,true);
    }
}







