package rosalila.studio.whackachuy.facebook;

import java.io.IOException;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.json.JSONException;
import org.json.JSONStringer;

import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;


public class PostToFacebookButton extends ButtonSprite {

	private String mFacebookStatus;
	private String mName;
	private final Facebook mFacebook;
	private AsyncFacebookRunner mAsyncFacebookRunner;
	
	public PostToFacebookButton(float pX, float pY,
			ITextureRegion pNormalTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, String pStatus, String pName, Facebook pFacebook) {
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager);
		this.mFacebook = pFacebook;
		this.setOnClickListener(new PostToFacebookButtonClickListener());
		this.mFacebookStatus = pStatus;
		this.mName = pName;
	}
	
	private class PostToFacebookButtonClickListener implements OnClickListener{

		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
	         try {
	                String response = PostToFacebookButton.this.mFacebook.request("me");
	                Bundle parameters = new Bundle();
	                parameters.putString("picture", "http://i.imgur.com/aux06.png");
	                parameters.putString("name", PostToFacebookButton.this.mName);
	                parameters.putString("message", PostToFacebookButton.this.mFacebookStatus);
	                parameters.putString("link", "https://play.google.com/store/apps/details?id=rosalila.studio.whackachuy");
	                       
	                response = PostToFacebookButton.this.mFacebook.request("me/feed", parameters, 
	                        "POST");
	                Log.d("Tests", "got response: " + response);
	                if (response == null || response.equals("") || 
	                        response.equals("false")) {
	                   Log.v("Error", "Blank response");
	                }
	         } catch(IOException e){
	        	 e.printStackTrace();
	         }
		}
		
	}
}


