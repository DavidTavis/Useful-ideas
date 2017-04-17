package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.example.david.mywidgetnewattempt.R;
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

/**
 * Created by TechnoA on 30.03.2017.
 */

public class ShareOnFacebook extends FragmentActivity {

    private static final String LOG_TAG = "MyLogWidget";
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private ShareDialog shareDialog;

    public void onClickFinish(View v){
        finish();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_facebook);
        Log.d(LOG_TAG,"ShareOnFacebook onCreate");
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
                Log.d(LOG_TAG," onSuccess");
                shareMessageToFacebook();
                Log.d(LOG_TAG,"shareMessageToFacebook onSuccess");
            }

            @Override
            public void onCancel()
            {
                Log.d(LOG_TAG,"ShareOnFacebook onCancel");
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception)
            {
                Log.d(LOG_TAG,"ShareOnFacebook onError");
                System.out.println("onError");
            }
        });

    }

    public void shareMessageToFacebook() {
        QuotesRepository quotesRepository = getQuotesRepository(getApplicationContext());
        String quote = quotesRepository.getMonitorQuotes().getCurrentQuote();

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Your Rules")
                    .setImageUrl(Uri.parse("http://ipic.su/img/img7/fs/S70331-030256_1.1490977198.jpg"))
                    .setContentDescription(quote)
                    .setContentUrl(Uri.parse("https://drive.google.com/open?id=0B-MrVh-HoJ8qUVZZUjkzOUlxLVk"))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    public QuotesRepository getQuotesRepository(Context context){
        final GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
        QuotesRepository quotesRepository = globalVariable.getQuotesRepository();
        if(quotesRepository == null){
            globalVariable.setQuotesRepository(new QuotesRepository(context));
            quotesRepository = globalVariable.getQuotesRepository();
        }
        return quotesRepository;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG," onResume");
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG," onPause");
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
        Log.d(LOG_TAG," onActivityResult");
    }
}
