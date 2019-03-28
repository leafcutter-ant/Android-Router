package com.joybar.moduleuser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joybar.annotation.router.annotation.RegisterRouter;
import com.joybar.librouter.routercore.InterceptorCallback;
import com.joybar.librouter.routercore.Router;
import com.joybar.librouter.routercore.Rule;
import com.joybar.librouter.guider.routertable.RouterTable$$Moduleshop;
import com.joybar.librouter.routercore.interceptor.TestInterceptor;
import com.joybar.librouter.routerservice.RouterServiceManager;
import com.joybar.librouter.routerservice.exception.RouterServiceException;
import com.joybar.librouter.routerservice.inters.IBaseService;
import com.joybar.librouter.routerservice.inters.IServiceCallBack;
import com.joybar.moduleuser.application.UserApplication;

@RegisterRouter(module = "user", path = "main")
public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private Button btnGotoShop;
    private Button btnGotoShopWithParam;
    private Button btnGotoShopForResult;
    private Button btnGotoShopWithInterceptor;
    private Button btnModuleEventBus;
    private TextView tvDes;
    private Context context;
    private View btnSyncInvokeComponent;
    private View btnAsyncInvokeComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_main);
        context = this;
        initView();
        initListener();
        UserApplication.getInstance().getApplication();

    }

    private void initView() {
        btnGotoShop = findViewById(R.id.btn_simple_navigate);
        btnGotoShopWithParam = findViewById(R.id.btn_with_param);
        btnGotoShopForResult = findViewById(R.id.btn_for_result);
        btnGotoShopWithInterceptor = findViewById(R.id.btn_with_interceptor);
        btnModuleEventBus = findViewById(R.id.btn_module_event_bus);
        tvDes = findViewById(R.id.tv_des);
        btnSyncInvokeComponent = findViewById(R.id.btn_component_invoke_sync);
        btnAsyncInvokeComponent = findViewById(R.id.btn_component_invoke_async);
    }


    private void initListener() {
        btnGotoShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterTable$$Moduleshop
                        .launchMain()
                        .navigate(context);
                // OR
//                Router.create()
//                        .buildRule(new Rule("shop", "main"))
//                        .navigate(context);


            }
        });


        btnGotoShopWithParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterTable$$Moduleshop
                        .launchReceiveParam("obo", 25)
                        .navigate(context);

                // OR
//                final Bundle bundle = new Bundle();
//                bundle.putInt("id", 123);
//                bundle.putString("name", "obo");
//                Router.create()
//                        .buildRule(new Rule("shop", "receive_param"))
//                        .withExtra(bundle)
//                        .navigate(context);
            }
        });

        btnGotoShopForResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RouterTable$$Moduleshop.launchFinishWithResult()
                        .navigate(MainActivity
                        .this, 2);

                // OR
//                Router.create()
//                        .buildRule(new Rule("shop", "finish_with_result"))
//                        .navigate(MainActivity
//                        .this, 2);

            }
        });


        btnGotoShopWithInterceptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Router.create()
                        .buildRule(new Rule("shop", "main"))
                        .addInterceptor(new TestInterceptor()).withInterceptorCallback(new InterceptorCallback() {
                    @Override
                    public void onIntercept(Object result) {
                        Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onContinue() {
                        Toast.makeText(context, "continue", Toast.LENGTH_LONG).show();

                    }
                }).navigate(context);
            }
        });

        btnModuleEventBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterTable$$Moduleshop
                        .launchPostModuleData()
                        .navigate(context);
            }
        });


        btnSyncInvokeComponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IBaseService service = RouterServiceManager.getInstance().getService("DTShopService");
                String result = (String) service.execute("testReturn");

                Toast.makeText(context, "invoke from another component synchronous result is " + result,Toast.LENGTH_LONG).show();
            }
        });

        btnAsyncInvokeComponent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                IBaseService service = RouterServiceManager.getInstance().getService("DTShopService");

                service.executeAsync("testReturnWithCallBack", new IServiceCallBack() {

                    @Override
                    public void onSuccess(Object result) {

                        Toast.makeText(context, "invoke from another component asynchronous result is " + result,Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(RouterServiceException routerServiceException) {

                    }
                });

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String result01 = data.getStringExtra("Result01");
        switch (requestCode) {
            case 2:
                Toast.makeText(this, "onActivityResult:"+result01, Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
