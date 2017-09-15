package com.test.cw.textfmwebview;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = (WebView) findViewById(R.id.webView);
        TextView contentView = (TextView) findViewById(R.id.contentView);

        /* An instance of this class will be registered as a JavaScript interface */
        class MyJavaScriptInterface
        {
            private TextView contentView;

            public MyJavaScriptInterface(TextView aContentView)
            {
                contentView = aContentView;
            }

            @SuppressWarnings("unused")
            @android.webkit.JavascriptInterface
            public void processContent(String aContent)
            {
                final String content = aContent;
                contentView.post(new Runnable()
                {
                    public void run()
                    {
                        contentView.setText(content);
                        System.out.println("content = "+ content );

                        // save text in file
                        String dirName = "Download";
                        String fileName = "temp.xml";
                        String dirPath = Environment.getExternalStorageDirectory().toString() +
                                "/" +
                                dirName;
                        File file = new File(dirPath, fileName);

                        try
                        {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            FileOutputStream fOut = new FileOutputStream(file);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            myOutWriter.append(content);
                            myOutWriter.close();

                            fOut.flush();
                            fOut.close();
                        }
                        catch (IOException e)
                        {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }

                    }
                });
            }

            @android.webkit.JavascriptInterface
            public void showToast(String toast) {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
            }
        }

        webView.getSettings().setJavaScriptEnabled(true);
        final MyJavaScriptInterface myInterface = new MyJavaScriptInterface(contentView);
        webView.addJavascriptInterface(myInterface, "INTERFACE");
//        webView.addJavascriptInterface(new MyJavaScriptInterface(contentView), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
            }
        });

//        webView.loadUrl("http://shinyhammer.blogspot.com");
        webView.loadUrl("http://litenoteapp.blogspot.tw/2017/09/xml-link.html");
//        webView.loadUrl("https://drive.google.com/file/d/0B6n7c10rY_1veEJXUFE2NjlnckE/view");
    }
}
