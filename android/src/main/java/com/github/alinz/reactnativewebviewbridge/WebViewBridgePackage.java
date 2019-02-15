package com.github.alinz.reactnativewebviewbridge;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebViewBridgePackage implements ReactPackage {
    private WebViewBridgeManager manager;
    private WebViewBridgeModule module;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        List<NativeModule> modules = new ArrayList<>();
        module = new WebViewBridgeModule(reactApplicationContext);
        module.setPackage(this);
        modules.add(module);
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        manager = new WebViewBridgeManager();
        manager.setPackage(this);
        return Arrays.<ViewManager>asList(manager);
    }


    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Arrays.asList();
    }

     public WebViewBridgeManager getManager(){
        return manager;
    }

    public WebViewBridgeModule getModule(){
        return module;
    }
}

