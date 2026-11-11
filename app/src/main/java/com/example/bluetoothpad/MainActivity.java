package com.example.bluetoothpad;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import java.util.*;

public class MainActivity extends Activity {
 PadView pad; BluetoothAdapter adapter; BluetoothHidDevice hid; BluetoothDevice host; boolean registered=false;
 final int[] report={0,0,127,127,127,127,0,0};
 final BluetoothHidDevice.Callback callback=new BluetoothHidDevice.Callback(){
  @Override public void onAppStatusChanged(BluetoothDevice d, boolean plugged){registered=plugged; runOnUiThread(()->pad.invalidate());}
  @Override public void onConnectionStateChanged(BluetoothDevice d,int state){host=d;runOnUiThread(()->pad.invalidate());}
 };
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512); pad=new PadView();setContentView(pad);
  adapter=((BluetoothManager)getSystemService(BLUETOOTH_SERVICE)).getAdapter();
  if(Build.VERSION.SDK_INT>=28 && adapter!=null) adapter.getProfileProxy(this,new BluetoothProfile.ServiceListener(){public void onServiceConnected(int p,BluetoothProfile x){hid=(BluetoothHidDevice)x; register();}public void onServiceDisconnected(int p){hid=null;}},BluetoothProfile.HID_DEVICE);
 }
 void register(){try{BluetoothHidDeviceAppSdpSettings s=new BluetoothHidDeviceAppSdpSettings("Bluetooth Pad","Touch gamepad","Codex",BluetoothHidDevice.SUBCLASS1_GAMEPAD,descriptor());hid.registerApp(s,null,null,getMainExecutor(),callback);}catch(Exception e){}}
 byte[] descriptor(){return new byte[]{(byte)0x05,0x01,(byte)0x09,0x05,(byte)0xa1,0x01,(byte)0x85,0x01,(byte)0x05,0x09,0x19,0x01,0x29,0x10,0x15,0x00,0x25,0x01,(byte)0x95,0x10,0x75,0x01,(byte)0x81,0x02,(byte)0x05,0x01,(byte)0x09,0x39,0x15,0x00,0x25,0x07,0x35,0x00,0x46,0x3b,0x01,0x65,0x14,(byte)0x75,0x04,(byte)0x95,0x01,(byte)0x81,0x42,(byte)0x75,0x04,(byte)0x95,0x01,(byte)0x81,0x03,(byte)0x09,0x30,(byte)0x09,0x31,(byte)0x09,0x33,(byte)0x09,0x34,0x15,(byte)0x81,0x25,0x7f,0x75,0x08,(byte)0x95,0x04,(byte)0x81,0x02,(byte)0xc0};}
 void send(){if(hid!=null&&host!=null)try{byte[] b={(byte)report[0],(byte)(report[0]>>8),(byte)report[1],(byte)report[2],(byte)report[3],(byte)report[4],(byte)report[5],(byte)report[6],(byte)report[7]};hid.sendReport(host,1,b);}catch(Exception ignored){}}
 class PadView extends View{
  Paint p=new Paint(3); HashMap<String,RectF> buttons=new HashMap<>(); HashMap<Integer,String> down=new HashMap<>(); float[] axes={0,0,0,0};
  PadView(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
  void box(Canvas c,String id,float l,float t,float r,float b,String label,int color){RectF q=new RectF(l,t,r,b);buttons.put(id,q);p.setColor(Color.rgb(28,31,39));c.drawRoundRect(q,20,20,p);p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawRoundRect(q,20,20,p);p.setStyle(Paint.Style.FILL);p.setTextAlign(Paint.Align.CENTER);p.setTextSize((b-t)*.38f);c.drawText(label,q.centerX(),q.centerY()+p.getTextSize()*.35f,p);}
  protected void onDraw(Canvas c){float w=getWidth(),h=getHeight(); c.drawColor(Color.rgb(11,12,16));p.setColor(Color.LTGRAY);p.setTextSize(20);p.setTextAlign(Paint.Align.CENTER);String status=registered?(host==null?"Pair from Windows Bluetooth settings":"Bluetooth connected"):"Bluetooth HID starting — Android 9+ required";c.drawText(status,w/2,28,p);
   box(c,"lb",w*.03f,h*.13f,w*.21f,h*.21f,"L1",Color.LTGRAY);box(c,"lt",w*.03f,h*.02f,w*.21f,h*.12f,"L2",Color.LTGRAY);box(c,"rb",w*.79f,h*.13f,w*.97f,h*.21f,"R1",Color.LTGRAY);box(c,"rt",w*.79f,h*.02f,w*.97f,h*.12f,"R2",Color.LTGRAY);
   box(c,"back",w*.23f,h*.06f,w*.31f,h*.13f,"SHARE",Color.LTGRAY);box(c,"start",w*.69f,h*.06f,w*.77f,h*.13f,"OPTIONS",Color.LTGRAY);box(c,"guide",w*.465f,h*.34f,w*.535f,h*.47f,"PS",Color.LTGRAY);
   box(c,"up",w*.10f,h*.29f,w*.17f,h*.42f,"▲",Color.LTGRAY);box(c,"down",w*.10f,h*.47f,w*.17f,h*.60f,"▼",Color.LTGRAY);box(c,"left",w*.055f,h*.36f,w*.12f,h*.49f,"◀",Color.LTGRAY);box(c,"right",w*.15f,h*.36f,w*.215f,h*.49f,"▶",Color.LTGRAY);
   box(c,"y",w*.835f,h*.27f,w*.91f,h*.40f,"△",Color.rgb(63,208,176));box(c,"a",w*.835f,h*.48f,w*.91f,h*.61f,"×",Color.rgb(108,156,255));box(c,"x",w*.77f,h*.36f,w*.845f,h*.49f,"□",Color.rgb(255,139,208));box(c,"b",w*.90f,h*.36f,w*.975f,h*.49f,"○",Color.rgb(255,90,90));
   stick(c,"ls",w*.25f,h*.60f,w*.40f,h*.91f);stick(c,"rs",w*.60f,h*.60f,w*.75f,h*.91f);box(c,"l3",w*.44f,h*.77f,w*.49f,h*.85f,"L3",Color.LTGRAY);box(c,"r3",w*.51f,h*.77f,w*.56f,h*.85f,"R3",Color.LTGRAY);
  }
  void stick(Canvas c,String id,float l,float t,float r,float b){RectF q=new RectF(l,t,r,b);buttons.put(id,q);p.setColor(Color.rgb(20,22,28));c.drawOval(q,p);p.setColor(Color.rgb(85,92,112));float dx=0,dy=0;String k=down.get(-1); if(k!=null&&k.equals(id)){dx=axes[id.equals("ls")?0:2]*q.width()*.3f;dy=axes[id.equals("ls")?1:3]*q.height()*.3f;}c.drawCircle(q.centerX()+dx,q.centerY()+dy,q.width()*.23f,p);}
  public boolean onTouchEvent(android.view.MotionEvent e){int a=e.getActionMasked(),i=e.getActionIndex(),id=e.getPointerId(i);float x=e.getX(i),y=e.getY(i);if(a==0||a==5){for(String k:buttons.keySet())if(buttons.get(k).contains(x,y)){down.put(id,k);if(k.equals("ls")||k.equals("rs"))down.put(-1,k);apply(k,true,x,y);break;}}else if(a==2){for(int n=0;n<e.getPointerCount();n++){String k=down.get(e.getPointerId(n));if(k!=null&&(k.equals("ls")||k.equals("rs")))apply(k,true,e.getX(n),e.getY(n));}}else if(a==1||a==6){String k=down.remove(id);if(k!=null)apply(k,false,x,y);if(k!=null&&(k.equals("ls")||k.equals("rs"))){down.remove(-1);invalidate();}}return true;}
  void apply(String k,boolean on,float x,float y){int bit=Arrays.asList("a","b","x","y","lb","rb","back","start","l3","r3","guide","up","down","left","right").indexOf(k);if(bit>=0){if(on)report[0]|=1<<bit;else report[0]&=~(1<<bit);}if(k.equals("lt"))report[6]=on?255:0;if(k.equals("rt"))report[7]=on?255:0;if(k.equals("ls")||k.equals("rs")){RectF q=buttons.get(k);int j=k.equals("ls")?2:4;report[j]=(int)Math.max(0,Math.min(255,127+(x-q.centerX())/q.width()*510));report[j+1]=(int)Math.max(0,Math.min(255,127+(y-q.centerY())/q.height()*510));}send();invalidate();}
 }
}
