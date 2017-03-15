package com.print.demo.firstview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.print.demo.R;
import com.print.demo.secondview.PrintModeActivity;

import java.io.IOException;
import java.util.ArrayList;

import utils.ApplicationContext;
import utils.DeviceControl;

public class ConnectAvtivity extends Activity {

	public int state;
	public Button con;
	public Spinner type;
	public Spinner com;
	public Spinner porttype;
	public Spinner printway;
	private ArrayAdapter<String> mAdapter;
	public LinearLayout show;
	public TextView link;
	public TextView version;
	public Button linktest;
	public ApplicationContext context;
	public boolean mBconnect = false;
	public static ConnectAvtivity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		initUi();
		connect();

	}

	private void initUi() {
		mActivity = this;
		// 多个页面之间数据共享
		context = (ApplicationContext) getApplicationContext();
		context.setObject();

		linktest = (Button) findViewById(R.id.TextView_linktest);
		link = (TextView) findViewById(R.id.TextView_link);
		version = (TextView) findViewById(R.id.text_version);
		version.setText("V " + context.getObject().CON_QueryVersion());
		type = (Spinner) findViewById(R.id.spinner_type);
		porttype = (Spinner) findViewById(R.id.spinner_porttype);
		printway = (Spinner) findViewById(R.id.spinner_printway);
		show = (LinearLayout) findViewById(R.id.linearLayout0);

		ArrayList<String> printName = new ArrayList<String>();
		printName = (ArrayList<String>) context.getObject()
				.CON_GetSupportPrinters();

		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, printName);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(mAdapter);

		String[] printInterface = context.getObject().CON_GetSupportPageMode();
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, printInterface);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		printway.setAdapter(mAdapter);
	}

	private DeviceControl DevCtrl;

	public void connect() {
		// /dev/ttyMT1:115200 //kt45
		// /dev/ttyG1:115200 //tt43

		// 机型判断方法
		modelJudgmen();
		if (mBconnect) {
			context.getObject().CON_CloseDevices(context.getState());

			con.setText(R.string.button_btcon);// "连接"
			mBconnect = false;
		} else {

			System.out.println("----RG---CON_ConnectDevices");

			if (state > 0) {
				Toast.makeText(ConnectAvtivity.this, R.string.mes_consuccess,
						Toast.LENGTH_SHORT).show();

				mBconnect = true;
				Intent intent = new Intent(ConnectAvtivity.this,
						PrintModeActivity.class);
				context.setState(state);
				context.setName("RG-E48");
				context.setPrintway(printway.getSelectedItemPosition());
				startActivity(intent);
			} else {
				Toast.makeText(ConnectAvtivity.this, R.string.mes_confail,
						Toast.LENGTH_SHORT).show();
				mBconnect = false;
			}
		}
	}

	// 机型判断

	private boolean isTT43 = false;

	private void modelJudgmen() {
		// 默认45 45和45Q的打印机串口相同 gpio不同
		state = context.getObject().CON_ConnectDevices("RG-E487",
				"/dev/ttyMT1:115200:1:1", 200);
		Toast.makeText(
				this,
				"" + android.os.Build.MODEL + " release:"
						+ android.os.Build.VERSION.RELEASE, Toast.LENGTH_LONG)
				.show();
		// kt45
		if (android.os.Build.VERSION.RELEASE.equals("4.4.2")) {
			DevCtrl = new DeviceControl(DeviceControl.powerPathKT);
			isTT43 = false;
			try {
				DevCtrl.PowerOnMTDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 45q 设置gpio
		else if (android.os.Build.VERSION.RELEASE.equals("5.1")) {
			DevCtrl = new DeviceControl(DeviceControl.powerPathKT);
			DevCtrl.setGpio(94);
			isTT43 = false;
			try {
				DevCtrl.PowerOnMTDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// // TT43
		else if (android.os.Build.VERSION.RELEASE.equals("4.0.3")) {
			// /dev/ttyG1:115200
			state = context.getObject().CON_ConnectDevices("RG-E487",
					"/dev/ttyG1:115200:1:1", 200);
			DevCtrl = new DeviceControl(DeviceControl.powerPathTT);
			isTT43 = true;
			try {
				DevCtrl.PowerOnDevice();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	// 程序退出时需要关闭电源 省电
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if (isTT43) {
				DevCtrl.PowerOffDevice();
			} else {
				DevCtrl.PowerOffMTDevice();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
