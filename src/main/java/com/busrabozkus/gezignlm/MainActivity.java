package com.busrabozkus.gezignlm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> isimArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        isimArray=new ArrayList<String>();
        idArray=new ArrayList<Integer>();

        arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,isimArray);
        listView.setAdapter(arrayAdapter);
        arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,isimArray);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //İntent yapmak istediğimizi yapcaz
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("sehirId",idArray.get(position));
                intent.putExtra("info","old");
                startActivity(intent);
            }
        });




        getData();
    }
    //Verileri Çekmek için
    public void getData(){
        try {
            SQLiteDatabase database=this.openOrCreateDatabase("Sehirler",MODE_PRIVATE,null);
            Cursor cursor=database.rawQuery("SELECT * FROM sehirler",null);
            int nameIx=cursor.getColumnIndex("sehiradi");
            int idIx=cursor.getColumnIndex("id");
            while (cursor.moveToNext()){
                isimArray.add(cursor.getString(nameIx));
                idArray.add(cursor.getInt(idIx));


            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }



    }



    public boolean onCreateOptionsMenu(Menu menu) {//Hangi menuyu göstericez
        //Bir xml activity içinde gösterbilmek için Inflater kullanılır
        MenuInflater menuInflater=getMenuInflater();//Menuyu activity bağlayabiliyoruz
        menuInflater.inflate(R.menu.ekle_sehir,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//Herhangi bir item seçildiğinde napılacak
        if (item.getItemId()==R.id.ekle_sehir_item){
            Intent intent=new Intent(MainActivity.this,Main2Activity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}


