package com.example.doge.test;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.test.R;


public class MainActivity extends AppCompatActivity {



    public TextView textView,takeshot;
    private EditText ip_text,webip_text;
    private Button ip_submit;
    private ImageView arm_left,arm_right,arm_up,arm_down;
    private WebView webView;
    private String url = "http://192.168.43.181:8080/?action=stream";
    private String ip = "192.168.43.181";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    public void onStart() {
        super.onStart();

        final TextView mLogLeft = (TextView)findViewById(R.id.log_left);
        final TextView stop = (TextView)findViewById(R.id.stop);
        final TextView tv_catch = (TextView)findViewById(R.id.tv_catch);
        final TextView tv_release = (TextView) findViewById(R.id.tv_release);
        TextView takeshot = (TextView) findViewById(R.id.takeshot);
        arm_left = (ImageView)findViewById(R.id.arm_left);
        arm_right = (ImageView) findViewById(R.id.arm_right);
        arm_up = (ImageView)  findViewById(R.id.arm_up);
        arm_down = (ImageView)  findViewById(R.id.arm_dwon);
        RockerView rockerViewLeft = (com.example.doge.test.RockerView)findViewById(R.id.rockerView_left);
        super.onStart();
        webView = (WebView)this.findViewById(R.id.video_car);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        //webView.setInitialScale(50);   //100代表不缩放
        webView.loadUrl(url );
        ip_text = (EditText)findViewById(R.id.edt_ip);
        webip_text = (EditText)findViewById(R.id.edt_webip);
        ip_submit = (Button)findViewById(R.id.btn_ip);
        ip_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip_text.getText().toString()!="")
                {
                    url= ip_text.getText().toString();
                    webView.loadUrl(url );
                }
                if (webip_text.getText().toString()!="")
                {
                    ip = webip_text.getText().toString();
                }
                Log.e("小车ip",url);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                view.loadUrl("file:///android_asset/error.html");
            }
        });

        takeshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketUtil s = new SocketUtil(3333);
                s.send("拍摄");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("停止");
            }
        });
        tv_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("松开");
            }
        });
        tv_catch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("抓取");
            }
        });
        arm_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("left");
            }
        });
        arm_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("right");
            }
        });
        arm_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("up");
            }
        });
        arm_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketUtil s = new SocketUtil(3333);
                s.send("down");
            }
        });
        if (rockerViewLeft != null) {
            rockerViewLeft.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
            rockerViewLeft.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
                @Override
                public void onStart() {
                    mLogLeft.setText(null);
                }

                @Override
                public void direction(RockerView.Direction direction) {
                    mLogLeft.setText( getDirection(direction));
                    SocketUtil s = new SocketUtil(ip,3333);
                    s.send(getDirection(direction));
                }

                @Override
                public void onFinish() {
                    mLogLeft.setText(null);
                }
            });
        }
    }

    private String getDirection(RockerView.Direction direction) {
        String message = null;
        switch (direction) {
            case DIRECTION_LEFT:
                message = "左";
                break;
            case DIRECTION_RIGHT:
                message = "右";
                break;
            case DIRECTION_UP:
                message = "上";
                break;
            case DIRECTION_DOWN:
                message = "下";
                break;
            case DIRECTION_UP_LEFT:
                message = "左上";
                break;
            case DIRECTION_UP_RIGHT:
                message = "右上";
                break;
            case DIRECTION_DOWN_LEFT:
                message = "左下";
                break;
            case DIRECTION_DOWN_RIGHT:
                message = "右下";
                break;
            default:
                break;
        }
        return message;
    }





}
