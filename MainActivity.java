package com.cheng.wuziqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    WuziqiPanel wuziView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wuziView = (WuziqiPanel) findViewById(R.id.wuzi);
    }
    /*、
    *选项菜单*
    *Activity的onMenuItemSelected 方法中判断如果是选项菜单就走onMenuItemSelected ，
    * 如果是上下文菜单就走onContextItemSelected
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();

        if(id==R.id.action_setting) {
            wuziView.start();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
