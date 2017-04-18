package com.print.demo.firstview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.goonear.crop.CropImage;

import java.io.ByteArrayOutputStream;

/**
 * Created by 张明_ on 2017/4/17.
 */

public class BaseActivity extends Activity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F4||keyCode == KeyEvent.KEYCODE_F5) {
            GetandSaveCurrentImage();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage() {
        //1.构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //2.获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
        Intent intent = new Intent(this, CropImage.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();
        intent.putExtra("bitmap", bitmapByte);
        startActivity(intent);
    }
}
