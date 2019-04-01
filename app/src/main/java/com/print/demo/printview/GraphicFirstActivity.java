package com.print.demo.printview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goonear.crop.CropImage;

import com.print.demo.firstview.BaseActivity;
import com.speedata.print.R;

import java.io.ByteArrayOutputStream;

import utils.ApplicationContext;

public class GraphicFirstActivity extends BaseActivity {
	public AutoCompleteTextView languageText;
	public Button printLan, button_screen_print;
	// 高宽设置
	public EditText wight;
	public EditText hight;
	public EditText X0;
	public EditText Y0;
	public ApplicationContext context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);

		wight = (EditText) findViewById(R.id.editText_lanwide);
		hight = (EditText) findViewById(R.id.editText_lanhight);
		button_screen_print = (Button) findViewById(R.id.button_screen_print);
		button_screen_print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GetandSaveCurrentImage();
			}
		});
		printLan = (Button) findViewById(R.id.button_printlan);
		context = (ApplicationContext) getApplicationContext();
		languageText = (AutoCompleteTextView) findViewById(R.id.autoCompleteText_lan);
		printLan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//控制输入,确保不为空
				String wighttext = wight.getText().toString();
				String highttext = hight.getText().toString();

				if (("".equalsIgnoreCase(wighttext)) || ("".equalsIgnoreCase(highttext))) {

					Toast.makeText(GraphicFirstActivity.this,
							R.string.null_wight_hight, Toast.LENGTH_SHORT).show();
					return;
				}


				context.getObject().CON_PageStart(context.getState(), true,
						Integer.parseInt(wight.getText().toString()),
						Integer.parseInt(hight.getText().toString()));
				context.getObject().ASCII_CtrlReset(context.getState());
				context.getObject().DRAW_SetFillMode(false);

				// 对多语言数据进行处理一行一行处理
				int y = 25;
				int size = 8;
				String str[] = languageText.getText().toString().split("\n");
				for (int i = 0; i < str.length; i++) {
					y += 25;

					context.getObject().DRAW_PrintText(context.getState(), 20,
							y, str[i], 20);
					size += 2;
				}
				context.getObject().CON_PageEnd(context.getState(),
						context.getPrintway());

				//空行
				context.getObject().ASCII_PrintBuffer(context.getState(), new byte[]{0x1B, 0x66, 1,
						(byte) 4}, 4);
			}
		});
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
		Intent intent = new Intent(GraphicFirstActivity.this, CropImage.class);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] bitmapByte = baos.toByteArray();
		intent.putExtra("bitmap", bitmapByte);
		startActivity(intent);
	}
}