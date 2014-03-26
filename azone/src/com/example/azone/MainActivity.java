package com.example.azone;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.mobile.idp.sdk.AccessTokenHandler;
import org.wso2.mobile.idp.sdk.CallBack;
import org.wso2.mobile.idp.sdk.ClientCredentials;
import org.wso2.mobile.idp.sdk.RefreshTokenHandler;
import org.wso2.mobile.idp.sdk.ServerUtilities;
import org.wso2.mobile.idp.sdk.TokenEndPoints;
import org.wso2.mobile.idp.sdk.Tokens;
import org.wso2.mobile.idp.sdk.WebViewBridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends Activity implements CallBack{
	Button button;
	Context context;
	Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()){
			case R.id.action_settings :
				Log.v("Menu Clicked", "Menu Setting Clicked");
				ClientCredentials.getInstance(OauthCostants.CLIENT_ID,OauthCostants.CLIENT_SECRET,OauthCostants.REDIRECT_URL);
				WebViewBridge w = new WebViewBridge();
				startActivityForResult(w.getLoginIntenttte(), 0);
				break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data != null){
			String code = data.getStringExtra("code");
			String authorizeURL = data.getStringExtra("authorize_url");
			String accessTokenURL = data.getStringExtra("access_token_url");
			Log.v("Test","test");
			Log.v("Test Data",code);
			TokenEndPoints tokenEndPoints = TokenEndPoints.getInstance();
			tokenEndPoints.setAccessTokenURL(accessTokenURL);
			tokenEndPoints.setAuthorizeURL(authorizeURL);
			super.onActivityResult(requestCode, resultCode, data);
			try{
				AccessTokenHandler accessTokenHandler = new AccessTokenHandler(getApplicationContext(),this);
				accessTokenHandler.obtainAccessToken(code);
			}catch(Exception e){
				Log.d("ERROR",e.toString());
			}
		}
	}
	public void requestNewAccessToken(){
		try{
			RefreshTokenHandler refreshTokenHandler = new RefreshTokenHandler(getApplicationContext(), this);
			refreshTokenHandler.obtainNewAccessToken();
		}catch(Exception e){
			Log.d("ERROR",e.toString());
		}
	}
	
	@Override
	public void receiveAccessToken(String response, String status, String message) {
		if("200".equals(status)){
			Log.v("Access Token",response);
    		Tokens tokens = Tokens.getTokensInstance();
    		Log.v("Refresh Token",tokens.getRefreshToken());
    		Log.v("Id_token",tokens.getIdToken().substring(27));
    		try {
    			JSONObject mainObject = new JSONObject(tokens.getIdToken().substring(27));
    			String subject = mainObject.getString("sub");
    			int index = subject.indexOf('@');
    			if(index>0){
    				subject = subject.substring(0, index);
    			}
    			Log.v("Subject",subject);
    			MenuItem item = menu.findItem(R.id.action_settings);
    			//item.setVisible(false);
    			item.setTitle("Welcome : "+subject);
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
	}

	@Override
	public void receiveNewAccessToken(String response, String status, String message){
		// TODO Auto-generated method stub
		if("200".equals(status)){
			Log.v("Access Token",response);
		}		
	}

	void setSSL(){
		InputStream inputStream = context.getResources().openRawResource(R.raw.truststore);
		ServerUtilities.enableSSL(inputStream,OauthCostants.TRUSTSTORE_PASSWORD);

	}

}
