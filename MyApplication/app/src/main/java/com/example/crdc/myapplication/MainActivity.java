package com.example.crdc.myapplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.DownloadListener;
import android.view.KeyEvent;
import android.content.Intent;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private String errorHtml = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorHtml = "<html><body><h1>网络未连接</h1></body></html>";
        WebView browser = (WebView) findViewById(R.id.Toweb);
        browser.loadUrl("http://172.18.20.47/index");

        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true); //启用javascript
        webSettings.setAppCacheEnabled(true);   //启用appCache
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);      //设置可以访问文件

        //设置可自由缩放网页、JS生效
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //隐藏缩放工具
        webSettings.setDisplayZoomControls(false);

        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
        browser.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
               // Log.d("调试日志",url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
                view.loadData(errorHtml, "text/html", "UTF-8");
            }
        });

        browser.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView browser = (WebView) findViewById(R.id.Toweb);
        if (keyCode == KeyEvent.KEYCODE_BACK && browser.canGoBack()) {
            browser.goBack();
            return true;
        }
        else  if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent i= new Intent(Intent.ACTION_MAIN);   //主启动，不期望接收数据
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //新的activity栈中开启，或者已经存在就调到栈前
            i.addCategory(Intent.CATEGORY_HOME);        //添加种类，为设备首次启动显示的页面
            startActivity(i);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        WebView browser = (WebView) findViewById(R.id.Toweb);

        if (browser != null) {
            browser.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            browser.clearHistory();

            ((ViewGroup) browser.getParent()).removeView(browser);
            browser.destroy();
            browser = null;
        }
        super.onDestroy();
    }
}
