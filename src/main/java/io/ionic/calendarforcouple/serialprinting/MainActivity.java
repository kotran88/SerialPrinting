package io.ionic.calendarforcouple.serialprinting;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private UsbDeviceConnection connection;
    EditText scl;
    EditText sda;
    private IntentFilter filter;
    public Context mContext;
    UsbManager manager;
    StringBuilder stringb=new StringBuilder();
    UsbDevice device;
    UsbSerialDevice serialPort;
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("dialog","action:"+action);
            Toast.makeText(getApplicationContext(),action.toLowerCase(),Toast.LENGTH_SHORT).show();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                Toast.makeText(getApplicationContext(),"usb DETACHED",Toast.LENGTH_SHORT).show();
                Log.e("dialog", "usb ACTION_USB_DEVICE_DETACHED!!!");
//                stopService(new Intent(MainActivity.this,BackService.class));
                unregisterReceiver(mUsbReceiver);
                finishAndRemoveTask();
                System.exit(0);
            }


            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                Toast.makeText(getApplicationContext(),"usb attached"+mPhysicaloid.open(),Toast.LENGTH_SHORT).show();
                checkPermission();
                Log.e("dialog", "usb  attached!!!");
                UsbManager manager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
                    manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                }
                Log.e("dialog", "device lis tshowwww");
                Toast.makeText(getApplicationContext(),"attached device isssss : ",Toast.LENGTH_SHORT).show();


                boolean granted =
                        intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = manager.openDevice(device);
                    UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    Toast.makeText(getApplicationContext(),serialPort.getDeviceId()+"s!",Toast.LENGTH_SHORT).show();
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
//                            serialPort.read(mCallback); //
                            Toast.makeText(getApplicationContext(),"opened!!!!",Toast.LENGTH_SHORT).show();

                            serialPort.write(("rrr").getBytes());
 serialPort.read(mCallback); //

                        } else {
                            Toast.makeText(getApplicationContext(),"not opened!",Toast.LENGTH_SHORT).show();
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }

//                startService(new Intent(MainActivity.this,
//                        BackService.class));
            }
            if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {

//                Toast.makeText(getApplicationContext(),"usb acc attached"+mPhysicaloid.open(),Toast.LENGTH_SHORT).show();
//                checkPermission();
                Log.e("dialog", "usb  attached2222!!!");
//                startService(new Intent(MainActivity.this,
//                        BackService.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this.getApplicationContext();
        checkPermission();

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);

        registerReceiver(mUsbReceiver, filter);



        Button refresh = findViewById(R.id.refresh);

         scl=findViewById(R.id.scl);
         sda=findViewById(R.id.sda);
        Button refresh2 = findViewById(R.id.refresh2);
        refresh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dialog","scl and sda is : "+scl.getText()+"///"+sda.getText());
                if (serialPort.open()) { //Set Serial Connection Parameters.

                    serialPort.setBaudRate(9600);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback); //

                    serialPort.write((scl.getText().toString()+"c"+sda.getText().toString()+"p").getBytes());


                } else {
                    Toast.makeText(getApplicationContext(),"not opened!",Toast.LENGTH_SHORT).show();
                    Log.e("dialog", "PORT NOT OPEN22");
                }

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsbManager manager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
                    manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                }
                Log.e("dialog", "device lis tshowwww");
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Log.e("dialog", deviceList.toString()+"device lis tshowwww");

                if (!deviceList.isEmpty()) {
                    boolean keep = true;
                    for (Map.Entry entry : deviceList.entrySet()) {
                        device = (UsbDevice) entry.getValue();
                        int deviceVID = device.getVendorId();
                        Log.e("dialog","vid is : "+deviceVID);

                    }
                }




                manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                Log.e("dialog", "device lis tshowwww");

                Intent intent = getIntent();
                if(device!=null){
                    connection = manager.openDevice(device);
                    Log.e("dialog","device is : "+device.toString());
                    Toast.makeText(getApplicationContext(),device.toString(),Toast.LENGTH_SHORT).show();
                    Log.e("dialog","getDeviceId is : "+device.getDeviceId());
                    Log.e("dialog","getProductId is : "+device.getProductId());
                     serialPort = UsbSerialDevice.createUsbSerialDevice(UsbSerialDevice.CP210x, device, connection,0);
//        UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        Toast.makeText(getApplicationContext(),"opened!!!",Toast.LENGTH_SHORT).show();
                        Log.e("dialog","serialport : "+serialPort.toString());

                        serialPort.setBaudRate(9600);
                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

                        serialPort.write("data".getBytes());

                        serialPort.read(mCallback); //


                    } else {
                        Log.e("dialog", "PORT IS NULL");
                    }
                    Log.d("dialog", "PERM NOT GRANTED");


                }else{
                    Toast.makeText(getApplicationContext(),"device null!",Toast.LENGTH_SHORT).show();
                    Log.e("dialog", "PORT NOT OPEN");

                }

            }
        });



//        Toast.makeText(getApplicationContext(),"device isssss : "+device,Toast.LENGTH_SHORT).show();
    }
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {

        @Override
        public void onReceivedData(final byte[] c)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 이 곳에 UI작업을 한다
                    Toast.makeText(getApplicationContext(),"rrr",Toast.LENGTH_LONG).show();
                    Log.e("dialog","onreceived : ");
                    String value = new String(c);
                    byte[] bytes = c;

                    String s = Base64.getEncoder().encodeToString(bytes);
                    Log.e("dialog",s.toLowerCase());
                    Log.e("dialog",value);
                    stringb.append(value);
                    Toast.makeText(getApplicationContext(),""+stringb.toString(),Toast.LENGTH_SHORT).show();
                    Log.e("dialog","stringbuiilder value : "+stringb.toString());
//                                Toast.makeText(getApplicationContext(),"saving..!!!",Toast.LENGTH_SHORT).show();
                }
            });

            // Code here :)
        }

    };
    void checkPermission(){
        String[] permissions = {
                Manifest.permission.SYSTEM_ALERT_WINDOW


        };
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        Log.i("dialog","Permision Length : "+permissions.toString()+"///"+permissions.length);
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck = mContext.checkSelfPermission(permissions[i]);
            }

            Log.i("dialog","permission checking:"+permissionCheck+"////"+permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                Log.i("dialog", "denied" + permissions[i]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    mContext.requestPermissions(permissions,202);
                }
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                    Log.i("dialog", "shouldshow" + permissions[i]);
//
//                } else {
//                    Log.i("dialog", "requesting" + permissions[i]);
//                    ActivityCompat.requestPermissions(this, permissions, i);
////
//                }
                break;
            } else {
                Log.i("dialog", "granted" + permissions[i]);

            }

        }
    }
}
