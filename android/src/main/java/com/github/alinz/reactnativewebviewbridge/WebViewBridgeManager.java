package com.github.alinz.reactnativewebviewbridge;

import android.webkit.WebView;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.webview.ReactWebViewManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import java.net.MalformedURLException;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import java.net.URL;
import java.net.URLDecoder;
import android.os.Environment;
import android.webkit.URLUtil;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class WebViewBridgeManager extends ReactWebViewManager {
    private static final String REACT_CLASS = "RCTWebViewBridge";

    public static final int COMMAND_SEND_TO_BRIDGE = 101;
    private Activity mActivity = null;
    private WebViewBridgePackage aPackage;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public
    @Nullable
    Map<String, Integer> getCommandsMap() {
        Map<String, Integer> commandsMap = super.getCommandsMap();

        commandsMap.put("sendToBridge", COMMAND_SEND_TO_BRIDGE);

        return commandsMap;
    }

    @Override
    protected WebView createViewInstance(ThemedReactContext reactContext) {
        WebView root = super.createViewInstance(reactContext);
        final WebViewBridgeModule module = this.aPackage.getModule();
        root.setWebChromeClient(new WebChromeClient(){

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                module.setUploadMessage(uploadMsg);
                module.openFileChooserView();
            }

            public boolean onJsConfirm (WebView view, String url, String message, JsResult result){
                // return true;
                return super.onJsConfirm(view, url, message, result);
            }

            public boolean onJsPrompt (WebView view, String url, String message, String defaultValue, JsPromptResult result){
                // return true;
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,JsResult result) {                
                // return true;
                return super.onJsAlert(view, url, message, result);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                module.setUploadMessage(uploadMsg);
                module.openFileChooserView();
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                module.setUploadMessage(uploadMsg);
                module.openFileChooserView();
            }

            // For Android > 5.0
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                Log.d("customwebview", "onShowFileChooser");

                module.setmUploadCallbackAboveL(filePathCallback);
                if (module.grantFileChooserPermissions()) {
                    module.openFileChooserView();
                } else {
                    Toast.makeText(module.getActivity().getApplicationContext(), "Cannot upload files as permission was denied. Please provide permission to access storage, in order to upload files.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        root.addJavascriptInterface(new JavascriptBridge(root), "WebViewBridge");
        return root;
    }

    @Override
    public void receiveCommand(WebView root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);

        switch (commandId) {
            case COMMAND_SEND_TO_BRIDGE:
                sendToBridge(root, args.getString(0));
                break;
            default:
                //do nothing!!!!
        }
    }

    private void sendToBridge(WebView root, String message) {
        String script = "WebViewBridge.onMessage('" + message + "');";
        WebViewBridgeManager.evaluateJavascript(root, script);
    }

    static private void evaluateJavascript(WebView root, String javascript) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            root.evaluateJavascript(javascript, null);
        } else {
            root.loadUrl("javascript:" + javascript);
        }
    }

    @ReactProp(name = "allowFileAccessFromFileURLs")
    public void setAllowFileAccessFromFileURLs(WebView root, boolean allows) {
        root.getSettings().setAllowFileAccessFromFileURLs(allows);
    }

    @ReactProp(name = "allowUniversalAccessFromFileURLs")
    public void setAllowUniversalAccessFromFileURLs(WebView root, boolean allows) {
        root.getSettings().setAllowUniversalAccessFromFileURLs(allows);
    }

    public void setPackage(WebViewBridgePackage aPackage){
        this.aPackage = aPackage;
    }

    public WebViewBridgePackage getPackage(){
        return this.aPackage;
    }
}
