package layout.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.app.david.mywidget.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.List;

import layout.GlobalClass;
import layout.utils.TraceUtils;

/**
 * Created by TechnoA on 30.03.2017.
 */

public class ShareOnFacebook extends FragmentActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private ShareDialog shareDialog;
    private TextView textResult;

    public void onClickFinish(View v){
        finish();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_facebook);

        textResult = (TextView) findViewById(R.id.textResult);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        List<String> permissionNeeds = Arrays.asList("publish_actions");

        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

                shareMessageToFacebook();
//                textResult.setText("You have shared current quote with friends on Facebook");
            }

            @Override
            public void onCancel()
            {
                TraceUtils.logInfo("ShareOnFacebook onCancel");
//                textResult.setText("You refused to give a quote");
            }

            @Override
            public void onError(FacebookException exception)
            {
                TraceUtils.logInfo("ShareOnFacebook onError");
            }
        });

    }

    public void shareMessageToFacebook() {

        String quote = ((GlobalClass)getApplicationContext()).getMonitorQuotes().getCurrentQuote().getQuote();
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote(quote)
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.app.david.mywidget"))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TraceUtils.logInfo("ShareOnFacebook onResume");
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TraceUtils.logInfo("ShareOnFacebook onPause");
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
        TraceUtils.logInfo("ShareOnFacebook onActivityResult");
    }
}
