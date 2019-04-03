package com.print.demo.printview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.goonear.crop.CropImage;
import com.print.demo.firstview.BaseActivity;
import com.speedata.print.R;

import java.io.ByteArrayOutputStream;

import utils.ApplicationContext;
import utils.DisplayUtil;

public class GraphicFirstActivity extends BaseActivity {
    public AutoCompleteTextView languageText;
    public Button printLan, button_screen_print;
    // 高宽设置
    public EditText wight;
    public EditText hight;
    public EditText X0;
    public EditText Y0;
    public ApplicationContext context;

    private RelativeLayout mRelativeLayout;

    // 对齐方式
    public RadioGroup align;
    public RadioButton left;
    public RadioButton center;
    public RadioButton right;

    private Layout.Alignment alignType;


    // 文字设置
    public CheckBox dthick;
    private EditText mSize;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mRelativeLayout = findViewById(R.id.rl_first);

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


        // 对齐方式
        align = (RadioGroup) findViewById(R.id.radioGroup1);
        left = (RadioButton) findViewById(R.id.radio0);
        center = (RadioButton) findViewById(R.id.radio1);
        right = (RadioButton) findViewById(R.id.radio2);


        // 文字设置
        dthick = (CheckBox) findViewById(R.id.checkBox03);
        mSize = findViewById(R.id.editText_textsize);

        //计算长度
        alignType = Layout.Alignment.ALIGN_LEFT;

        //设置监听
        align.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("NewApi")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                alignType = Layout.Alignment.ALIGN_LEFT;
                if (center.isChecked()) {
                    alignType = Layout.Alignment.ALIGN_CENTER;
                }
                if (right.isChecked()) {
                    alignType = Layout.Alignment.ALIGN_RIGHT;
                }

            }
        });

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

                Bitmap bitmap = text2BitmapForUnit(GraphicFirstActivity.this, languageText.getText().toString());

                //打印图片
                context.getObject().
                        CON_PageStart(context.getState(), true, Integer.parseInt(wight.getText().toString()), Integer.parseInt(hight.getText().toString()));
                context.getObject().
                        ASCII_CtrlReset(context.getState());
                context.getObject().
                        DRAW_SetFillMode(false);
                context.getObject().
                        DRAW_SetLineWidth(4);
                context.getObject().
                        DRAW_PrintPicture(context.getState(), bitmap, 0, 0, 0, 0);
                context.getObject().
                        CON_PageEnd(context.getState(), context.getPrintway());

                //空行
                 context.getObject().ASCII_PrintBuffer(context.getState(), new byte[]{0x1B, 0x66, 1,
                        (byte) 4}, 4);

            }
        });
    }



    private TextPaint mTextPaint;
    /**
     * 在填写信息的时候写单位，文字转bitmap绘制出来
     *
     * @param text
     * @return
     */
    @SuppressLint("NewApi")
    public Bitmap text2BitmapForUnit(Context context, String text) {

        String wighttext = wight.getText().toString();
        String highttext = hight.getText().toString();

        mTextPaint = new TextPaint();
        //设置抗锯齿
        mTextPaint.setAntiAlias(true);

        //设置字体加粗
        if (dthick.isChecked()) {
            mTextPaint.setFakeBoldText(true);
        } else {
            mTextPaint.setFakeBoldText(false);
        }

        //设置字体颜色
        mTextPaint.setColor(Color.parseColor("#000000"));

        int size = Integer.parseInt(mSize.getText().toString());

        mTextPaint.setTextSize(DisplayUtil.sp2px(size, (Activity) context));


        StaticLayout staticLayout = new StaticLayout(text, mTextPaint, Integer.parseInt(wighttext),
                alignType, 1.0f, 0.0f, true);
        Bitmap bitmap = Bitmap.createBitmap(Integer.parseInt(wighttext), Integer.parseInt(highttext), Bitmap.Config.ARGB_8888);

        //填充颜色
        bitmap.eraseColor(Color.parseColor("#FFFFFF"));

        Canvas canvas = new Canvas(bitmap);
        staticLayout.draw(canvas);
        return bitmap;
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