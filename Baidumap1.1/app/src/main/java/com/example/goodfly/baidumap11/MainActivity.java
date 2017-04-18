package com.example.goodfly.baidumap11;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;


public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap bdMap;
    private Button locateBtn;
    private BitmapDescriptor currentMarker = null;
    private LocationClient locClient;
    public double latitude, longitude;
    private boolean isFirstLoc = true;
    private Handler handler;
    private final static String authBaseArr[] =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
    private final static String authComArr[] = {Manifest.permission.READ_PHONE_STATE};
    private final static int authBaseRequestCode = 1;
    private final static int authComRequestCode = 2;
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    String authinfo = null;
    //算路
    private BNRoutePlanNode.CoordinateType mCoordinateType = null;
    //算路成功
    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    //两种locationmode
    private MyLocationConfiguration.LocationMode currentMode;
    //sb官方sdk
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init();
        locateBtn = (Button) findViewById(R.id.locate_btn);
        mMapView = (MapView) findViewById(R.id.bmapview);
        bdMap = mMapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        Log.i("BMap", "Started");
        LatLng point1 = bdMap.getMapStatus().target;
        System.out.println("kkkk" + point1);
        System.out.println("jjjjjjjj" + latitude + longitude);
        final LatLng pointtt = new LatLng(39.963175, 116.400244);
        OverlayOptions option = new MarkerOptions().icon(bitmap).position(point1);
        bdMap.addOverlay(option);
//        MarkerOptions markerOptions;
//        markerOptions = new MarkerOptions().icon(bitmap).position(point1);
//        bdMap.addOverlay(markerOptions);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String[] strings = msg.obj.toString().split(" ");
                locateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                LatLng L = new LatLng(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]));
                MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(L);
                bdMap.addOverlay(markerOptions);
            }
        };

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("BD09LL");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        //option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            LatLng po = new LatLng(location.getLatitude(), location.getLongitude());
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                Log.i("BaiduLocationApiDem", sb.toString());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
//                        msg.arg1 = location.getLatitude();
//                        msg.arg2 = (int) location.getLongitude();
//                        double[] array = new double[]{location.getLatitude(), location.getLongitude()};
//                        msg.obj = array;
//                        msg.arg1 = (int) location.getLatitude();
                    msg.obj = location.getLatitude() + " " + location.getLongitude();
                    handler.sendMessage(msg);
                }
            }).start();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    //设置appid
    private void setAppid() {
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9481658");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr){
            if (pm.checkPermission(auth,this.getPackageName())!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    //初始化导航
    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, authinfo, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    public void initSuccess() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                    }

                    public void initStart() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    public void initFailed() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }
                }, null,ttsHandler,ttsPlayStateListener /*mTTSCallback*/);
    }
    //内部tts状态回传handler
    private Handler ttsHandler = new Handler(){
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type){
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG:{
//                    Toast.makeText(this,"tts Start",Toast.LENGTH_LONG).show();
                    System.out.println("tts Start!");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG:{
                    System.out.println("tts End!");
                    break;
                }
                default:break;
            }
        }
    };
    //内部tts播报状态回掉接口
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener =
            new BaiduNaviManager.TTSPlayStateListener() {
                @Override
                public void playStart() {
                    Log.i("ttsListener","tts listener start!");
                }

                @Override
                public void playEnd() {
                    Log.i("ttsListener","tts listener end!");
                }
            };
    //算路
    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType){
        mCoordinateType = coType;
        if (!hasInitSuccess){
            Toast.makeText(MainActivity.this,"没有初始化",Toast.LENGTH_LONG).show();
            Log.i("routeplan","没有初始化");
        }
        //权限申请
        if (Build.VERSION.SDK_INT>=23){
            //保证导航功能完全
            if (!hasRequestComAuth){
                hasRequestComAuth = true;
                this.requestPermissions(authComArr,authComRequestCode);
                return;
            }else{
                Toast.makeText(MainActivity.this,"没有权限",Toast.LENGTH_LONG).show();
                Log.i("routeplan","没有权限");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
//        locClient.stop();
        // 回收bitmip资源
        //bitmap.recycle();
    }

}