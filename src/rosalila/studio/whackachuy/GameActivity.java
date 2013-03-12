package rosalila.studio.whackachuy;

import java.io.IOException;
import java.util.LinkedList;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import rosalila.studio.whackachuy.entity.ChuyDeathAnimationListener;
import rosalila.studio.whackachuy.entity.ChuyEntity;
import rosalila.studio.whackachuy.entity.ChuyTouchListener;
import rosalila.studio.whackachuy.entity.ConsoleEntity;
import rosalila.studio.whackachuy.facebook.BaseRequestListener;
import rosalila.studio.whackachuy.facebook.PostToFacebookButton;
import rosalila.studio.whackachuy.facebook.SessionEvents;
import rosalila.studio.whackachuy.facebook.SessionEvents.AuthListener;
import rosalila.studio.whackachuy.facebook.SessionEvents.LogoutListener;
import rosalila.studio.whackachuy.facebook.SessionStore;
import rosalila.studio.whackachuy.util.ChuyEntityPool;
import rosalila.studio.whackachuy.util.PoopAnimatedSpritePool;
import rosalila.studio.whackachuy.util.Utility;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class GameActivity extends BaseGameActivity implements WhackAChuyConstants,IOnSceneTouchListener {

	//Facebook fields
	private boolean mDisableTouch = false;
	private final  static String APP_ID = "482666878429702";
	private final String[] mFacebookPermissions = {"publish_stream"};
	private Facebook mFacebook = new Facebook(GameActivity.APP_ID);
	private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(GameActivity.this.mFacebook);
	private ButtonSprite mFacebookSessionButton;
	private BitmapTextureAtlas mFacebookSessionTextureAtlas;
	private ITextureRegion mFacebookSessionTextureRegion;
	
	//Layers of the game
	public static final int LAYER_COUNT = 4;
	public static final int LAYER_BACKGROUND = 0;
	public static final int LAYER_CHUY = LAYER_BACKGROUND + 1;
	public static final int LAYER_CONSOLES = LAYER_CHUY + 1;
	public static final int LAYER_TEXT = LAYER_CONSOLES +1;
	
	//All the scenes the game needs
	private Scene mSplashScene;
	private Scene mTitleScene;
	private Scene mGameScene;
	
	private Camera mCamera;
	
	//Background for the splash screen
	private BuildableBitmapTextureAtlas mSplashBackgroundTextureAtlas;
	private ITextureRegion mSplashBackgroundTextureRegion;
	
	//Background for the game scene
	private BuildableBitmapTextureAtlas mGameBackgroundTextureAtlas;
	private ITextureRegion mGameBackgroundTextureRegion;
	
	//Background for the title scene
	private BuildableBitmapTextureAtlas mTitleBackgroundTextureAtlas;
	private ITextureRegion mTitleBackgroundTextureRegion;
	
	//Texture for Chuy and Consoles
	private BuildableBitmapTextureAtlas mConsoleTextureAtlas;
	private ITextureRegion mWiiTextureRegion;
	private ITextureRegion mXboxTextureRegion;
	private ITextureRegion mPlayStationTextureRegion;
	private ITextureRegion mChuyTiledTextureRegion;
	private ITextureRegion mPoopTiledTextureRegion;
	
	//Font and Text
	private Font mFont;
	private Text mScoreText;
	private String mScoreString;
	private Text mHighScoreText;
	private String mHighScoreString;
	
	//Chuys and Consoles on the screen
	private LinkedList<ConsoleEntity> mConsolesOnScreen;	
	private LinkedList<ChuyEntity> mChuysOnScreen ;	

	//Pool to obtain chuys and poop
	private ChuyEntityPool mChuyEntityPool;
	private PoopAnimatedSpritePool mPoopPool;
	
	//Game logic members
	private boolean mGameFinished;
	private boolean mGameRunning;
	private int mHighScore;
	private int mChuyHits;
	private int mGameScore ;
	private int mGameLevel;
	private int mMaxNumberOfChuys;
	private float mChuyMovementDelay = 0.3f;
	private float mChuySpawnDelay = 1.5f;
	private static String PREFS_NAME = "Whack-A-Chuy";
	private static String PREFS_HIGHSCORE="highscore";
	
	//onGameOver Entities
	private Text	mGameOverText;
	private BuildableBitmapTextureAtlas mButtonTextureAtlas;
	private ITextureRegion mFacebookLogoTexture;
	private ITextureRegion mQuitImageTexture;
	private PostToFacebookButton mPostToFacebookButton;
	private ButtonSprite mQuitButton;
	
	//Sounds
	private Music mBackgroundMusic;
	private Sound mPoopSound;
	
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT );

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, 
				new FillResolutionPolicy( ), this.mCamera);
		
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		return engineOptions;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);	    
		this.mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
	        super.onResume();
	        if(this.mEngine != null){
	        	if (!this.mEngine.isRunning())this.mEngine.start();
	        }              
	}
	 
	@Override
	protected void onPause() {
	        super.onPause();
	        if(this.mEngine != null ){
	        	if ( this.mEngine.isRunning())this.mEngine.stop();
	        }
	}
	
	@Override
	protected void onSetContentView(){
		final FrameLayout frameLayout = new FrameLayout(this);
        final FrameLayout.LayoutParams frameLayoutLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                             FrameLayout.LayoutParams.FILL_PARENT);
 
        final AdView adView = new AdView(this, AdSize.BANNER, "a14fdaed341e595");

        adView.refreshDrawableState();
        adView.setVisibility(AdView.VISIBLE);
        final FrameLayout.LayoutParams adViewLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                             FrameLayout.LayoutParams.WRAP_CONTENT,
                                             Gravity.CENTER_HORIZONTAL|Gravity.TOP);
       
 
        AdRequest adRequest = new AdRequest();
        adView.loadAd(adRequest); 
        this.mRenderSurfaceView = new RenderSurfaceView(this);
        mRenderSurfaceView.setRenderer(mEngine,GameActivity.this);
 
        final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
                new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());

        frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
        frameLayout.addView(adView, adViewLayoutParams);
        this.setContentView(frameLayout, frameLayoutLayoutParams);
	}
	
	private void setConsoleToRandomCell(ConsoleEntity pConsole){
		pConsole.setCell(MathUtils.random(4, CELLS_HORIZONTAL-4), MathUtils.random(4, CELLS_VERTICAL-4));
	}
		
	private void onGameOver(){
		this.mGameRunning = false;
	    this.mGameFinished = true;
	    this.mDisableTouch = true;
			//Got a new Highscore so we display some text 
			GameActivity.this.mGameOverText = new Text(0, 0, this.mFont, getString(R.string.game_over)  , new TextOptions(HorizontalAlign.CENTER ), 
					this.getVertexBufferObjectManager());
			GameActivity.this.mGameOverText.setPosition(5,(GameActivity.CELL_HEIGHT)*(GameActivity.CELLS_VERTICAL/3) );
			GameActivity.this.mGameOverText.setScale(0.0f);
			GameActivity.this.mGameOverText.registerEntityModifier(new ScaleModifier(2, 0.0f, 1.0f));
			GameActivity.this.mGameScene.getChildByIndex(GameActivity.LAYER_TEXT).attachChild(GameActivity.this.mGameOverText);
						
			GameActivity.this.mQuitButton = new ButtonSprite((CELLS_HORIZONTAL-5)*CELL_WIDTH,(CELLS_VERTICAL-3)*CELL_HEIGHT,
		    		  GameActivity.this.mQuitImageTexture,this.getVertexBufferObjectManager());
		      GameActivity.this.mGameScene.getChildByIndex(LAYER_TEXT).attachChild(GameActivity.this.mQuitButton);
		      
		      GameActivity.this.mQuitButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
						float pTouchAreaLocalY) {
					GameActivity.this.mGameScene = null;
					GameActivity.this.mEngine.setScene(GameActivity.this.mTitleScene);
				}
			});
		      GameActivity.this.mGameScene.registerTouchArea(GameActivity.this.mQuitButton);
		if ( this.mGameScore > this.mHighScore ){	
			//Save the new Highscore locally
			SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(GameActivity.PREFS_HIGHSCORE, this.mGameScore);
			editor.commit();
			
			//Display the post to facebook button
			String facebookMessage = String.format(getString(R.string.facebook_status), GameActivity.this.mGameScore);
			GameActivity.this.mPostToFacebookButton = new PostToFacebookButton(this.mQuitButton.getX() - ( CELL_WIDTH*2+10), this.mQuitButton.getY(),
					this.mFacebookLogoTexture,this.getVertexBufferObjectManager(),facebookMessage, getString(R.string.facebook_name), this.mFacebook);
			
			this.mGameScene.getChildByIndex(GameActivity.LAYER_TEXT).attachChild(GameActivity.this.mPostToFacebookButton);	
			this.mGameScene.registerTouchArea(GameActivity.this.mPostToFacebookButton);	
		}
		this.mGameScene.registerUpdateHandler(new TimerHandler(3.0f,true,new ITimerCallback() {
			
			public void onTimePassed(TimerHandler pTimerHandler) {
				GameActivity.this.mGameScene.unregisterUpdateHandler(pTimerHandler);
				GameActivity.this.mDisableTouch = false;
			}
		} ));
		
      
	}
	
	private void handleChuySpawn(){
		if ( GameActivity.this.mGameRunning ){
			while( GameActivity.this.mChuysOnScreen.size()  < GameActivity.this.mMaxNumberOfChuys ){			
				spawnChuy();
			}
		}
	}
	
	private void spawnChuy(){
		
		final ChuyEntity chuy = GameActivity.this.mChuyEntityPool.obtainPoolItem();
		
		chuy.setOnTouchListener(new ChuyTouchListener() {
			
			@Override
			public void onTouch(final ChuyEntity pChuy) {		
			    //Update the score text
				if( GameActivity.this.mGameRunning){
					GameActivity.this.mChuyHits++;
					GameActivity.this.mGameScore+= 1 * GameActivity.this.mConsolesOnScreen.size(); //TODO
				    GameActivity.this.mScoreText.setText(GameActivity.this.mScoreString + GameActivity.this.mGameScore );	
					GameActivity.this.mGameLevel = GameActivity.this.mGameScore/10;
					GameActivity.this.mMaxNumberOfChuys = 5 + mGameLevel*2;
					GameActivity.this.mChuySpawnDelay = 1.5f - mGameLevel*0.1f <= 0? 0.1f: 1.5f - mGameLevel*0.1f;
				}
			}
		});
		
		chuy.setDeathAnimationListener(new ChuyDeathAnimationListener() {
			
			@Override
			public void onAnimationFinish(ChuyEntity pChuy) {
				GameActivity.this.recycleChuy(pChuy);					
			}
		});

		chuy.registerUpdateHandler(new TimerHandler(GameActivity.this.mChuyMovementDelay, true, new ITimerCallback(){
	
				public void onTimePassed(TimerHandler pTimerHandler) {
					if ( GameActivity.this.mGameRunning && !chuy.isDead()){
						if ( chuy.hasCapturedConsole() ){
							chuy.runAwayWithConsole();
							GameActivity.this.handleRunningChuyNewPosition(chuy);
						}else{
							ConsoleEntity nearestConsole = null;
							double distanceToNearestConsole = Double.MAX_VALUE; //Really large distance
							
							for (final ConsoleEntity console : GameActivity.this.mConsolesOnScreen) {
								double distanceToConsole = Math.sqrt(Math.pow( (chuy.getCellX()-console.getCellX()), 2) + 
										Math.pow( (chuy.getCellY()-console.getCellY()), 2));
								if ( distanceToConsole < distanceToNearestConsole){
									distanceToNearestConsole = distanceToConsole;
									nearestConsole = console;
								}
							}				
							if ( nearestConsole != null)chuy.moveToConsole(nearestConsole);			
						}					
					}
					}
				}));
		GameActivity.this.mChuysOnScreen.add(chuy);	
	}
	
	private void handleRunningChuyNewPosition(final ChuyEntity pRunningChuy){
		if (pRunningChuy.getCellX() < 0 || pRunningChuy.getCellX() >=CELLS_HORIZONTAL 
				|| pRunningChuy.getCellY() < 0 || pRunningChuy.getCellY() >= CELLS_VERTICAL){
			
			//Detach and remove the console
			if (pRunningChuy.hasCapturedConsole()){				
				GameActivity.this.mConsolesOnScreen.remove(pRunningChuy.getCapturedConsole());
				GameActivity.this.mEngine.runOnUpdateThread(new Runnable() {	
					public void run() {
						GameActivity.this.mGameScene.getChildByIndex(GameActivity.LAYER_CONSOLES).detachChild(pRunningChuy.getCapturedConsole());		
					}
				});
				if ( GameActivity.this.mConsolesOnScreen.size() == 0 )GameActivity.this.onGameOver();
			}	
			GameActivity.this.recycleChuy(pRunningChuy);
		}
	}
	
	private void recycleChuy(ChuyEntity pRunningChuy){
		if (pRunningChuy.hasCapturedConsole())pRunningChuy.releaseConsole();
		GameActivity.this.mChuysOnScreen.remove(pRunningChuy);
		GameActivity.this.mChuyEntityPool.recyclePoolItem(pRunningChuy);
	}

	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if ( this.mDisableTouch )return false;
		if(!this.mGameRunning && this.mGameFinished ){
			//GameOver so recreate the scene
			GameActivity.this.createGameScene();
		}
		if ( pSceneTouchEvent.isActionDown()){
			  final AnimatedSprite poopSprite = GameActivity.this.mPoopPool.obtainPoolItem();
			  poopSprite.setVisible(true);
        	  poopSprite.animate(200, false,new IAnimationListener() {
				
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
						int pInitialLoopCount) {
					GameActivity.this.mPoopSound.play();
					
				}
				
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
						int pRemainingLoopCount, int pInitialLoopCount) {
					
				}
				
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
						int pOldFrameIndex, int pNewFrameIndex) {							
				}
				
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					//GameActivity.this.mPoopSound.stop();
					GameActivity.this.mPoopPool.recyclePoolItem(poopSprite);							
				}
			});
        	   poopSprite.setPosition(pSceneTouchEvent.getX()-poopSprite.getWidth()/2,pSceneTouchEvent.getY()-poopSprite.getHeight()/2);
		}
		return false;
	}

	//Splash Screen resources
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)throws Exception {	
		/* Load the font we are going to use. */
		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 
				512, 512, TextureOptions.BILINEAR, this.getAssets(), "Plok.ttf", 30, true, Color.BLACK);
		this.mFont.load();
		
		SVGBitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mSplashBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH,CAMERA_HEIGHT,TextureOptions.BILINEAR);
		this.mSplashBackgroundTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mSplashBackgroundTextureAtlas,this, "rosalila_logo.svg", CAMERA_WIDTH, CAMERA_HEIGHT);
		try {
			this.mSplashBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mSplashBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		pOnCreateResourcesCallback.onCreateResourcesFinished();		
	}

	//Splash Screen Scene
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)throws Exception {
		GameActivity.this.mSplashScene = new Scene();
		//Attach the background
		Sprite mSplashBackground = new Sprite(0,0,GameActivity.this.mSplashBackgroundTextureRegion,getVertexBufferObjectManager());
		GameActivity.this.mSplashScene.attachChild(new Entity());
		GameActivity.this.mSplashScene.setBackgroundEnabled(false);
		GameActivity.this.mSplashScene.getChildByIndex(LAYER_BACKGROUND).attachChild(mSplashBackground);
		pOnCreateSceneCallback.onCreateSceneFinished(GameActivity.this.mSplashScene);		
	}

	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
	    mEngine.registerUpdateHandler(new TimerHandler(0.01f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                    mEngine.unregisterUpdateHandler(pTimerHandler);
                    createGameResources();
                    createScenes();                  
                    try
                    {
                            Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                            e.printStackTrace();
                    }            
                   mEngine.setScene(GameActivity.this.mTitleScene);
            }
         }));
		pOnPopulateSceneCallback.onPopulateSceneFinished();	
	}
	
	private void createGameResources(){
		SVGBitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");	
					
		/* Load all the textures this game needs. */		
		this.mGameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH,CAMERA_HEIGHT,TextureOptions.BILINEAR);
		this.mGameBackgroundTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mGameBackgroundTextureAtlas,this, "bg_game.svg", CAMERA_WIDTH, CAMERA_HEIGHT);
		try {
			this.mGameBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mGameBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

		this.mConsoleTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(),1024,1024,TextureOptions.BILINEAR);
		this.mWiiTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mConsoleTextureAtlas, this, "console_wii.svg",CELL_WIDTH,CELL_HEIGHT);
		this.mXboxTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mConsoleTextureAtlas, this, "console_xbox.svg",CELL_WIDTH,CELL_HEIGHT);
		this.mPlayStationTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mConsoleTextureAtlas, this, "console_ps.svg",CELL_WIDTH,CELL_HEIGHT);
		try {
			mConsoleTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			mConsoleTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		//Create the tiled textureRegion for chuy 
		mChuyTiledTextureRegion = Utility.createTiledTextureRegionFromSvgAsset(this.getTextureManager(), this, CELL_WIDTH, CELL_HEIGHT,
				"chuy/fall1.svg",
				"chuy/fall2.svg",
				"chuy/fall3.svg",
				"chuy/fall4.svg",
				"chuy/fall5.svg",
				"chuy/fall6.svg",
				"chuy/walk_down1.svg",
				"chuy/walk_down2.svg",
				"chuy/walk_down3.svg",
				"chuy/walk_left1.svg",
				"chuy/walk_left2.svg",
				"chuy/walk_left3.svg",
				"chuy/walk_right1.svg",
				"chuy/walk_right2.svg",
				"chuy/walk_right3.svg",
				"chuy/walk_up1.svg",
				"chuy/walk_up2.svg",
				"chuy/walk_up3.svg");
	
		//Load the texture for poop
		this.mPoopTiledTextureRegion = Utility.createTiledTextureRegionFromSvgAsset(this.getTextureManager(), this, CELL_WIDTH, CELL_HEIGHT, 
				"poop1.svg",
				"poop2.svg",
				"poop3.svg",
				"poop4.svg",
				"poop5.svg");
		
		
		//TODO: Optimize size of the atlas
		//Load the texture for the sprite buttons
		this.mButtonTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 1024, 1024);
		this.mFacebookLogoTexture =  SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mButtonTextureAtlas, this, "facebook_logo.svg",CELL_WIDTH*2,CELL_HEIGHT*2);
		this.mQuitImageTexture = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mButtonTextureAtlas, this, "quit_game.svg",CELL_WIDTH*2,CELL_HEIGHT*2);
		try {
			this.mButtonTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mButtonTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		//Load the music the game needs
		MusicFactory.setAssetBasePath("mfx/");
		try{
			this.mBackgroundMusic = MusicFactory.createMusicFromAsset(this.getMusicManager(), this, "bg_music.ogg");
			this.mBackgroundMusic.setLooping(true);
		}catch(final IOException pException){
			Debug.e(pException.getMessage());
		}		
		//Load the sounds the game needs
		SoundFactory.setAssetBasePath("mfx/");
		try{
			this.mPoopSound = SoundFactory.createSoundFromAsset(this.getSoundManager(), this, "splat1.mp3");
		}catch(final IOException pException){
			Debug.e(pException.getMessage());
		}
	}

	private void createScenes(){
		this.mEngine.registerUpdateHandler(new FPSLogger());
		createTitleScene();
		createGameScene();
	}
	
	public void createTitleScreenResources() {
		SVGBitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mTitleBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(),1024,1024,TextureOptions.BILINEAR);
		this.mTitleBackgroundTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
				createFromAsset(this.mTitleBackgroundTextureAtlas,this,"title_bg.svg",CAMERA_WIDTH,CAMERA_HEIGHT);
		try {
			this.mTitleBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.mTitleBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

	    SessionStore.restore(GameActivity.this.mFacebook,GameActivity.this);
	    SessionListener s = new SessionListener();
	    SessionEvents.addAuthListener(s);
	    SessionEvents.addLogoutListener(s);

		GameActivity.this.mFacebookSessionTextureAtlas = new BitmapTextureAtlas(GameActivity.this.getTextureManager(),70,70,TextureOptions.BILINEAR);
	    GameActivity.this.mFacebookSessionTextureRegion = SVGBitmapTextureAtlasTextureRegionFactory.
					createFromAsset(GameActivity.this.mFacebookSessionTextureAtlas, GameActivity.this, this.mFacebook.isSessionValid() ?"facebook_logout.svg": "facebook_login.svg",70, 70,0,0);				
	    GameActivity.this.mFacebookSessionTextureAtlas.load();
	}
	
	public void createTitleScene(){
		this.createTitleScreenResources();
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();		
		this.mTitleScene = new Scene();
		this.mTitleScene.setBackgroundEnabled(false);
		this.mTitleScene.attachChild(new Sprite(0,0,this.mTitleBackgroundTextureRegion,vertexBufferObjectManager));
		
		//Register an update handler so the scene wait a little bit before accepting touch
		this.mTitleScene.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback(){

			public void onTimePassed(TimerHandler pTimerHandler) {
				GameActivity.this.mTitleScene.unregisterUpdateHandler(pTimerHandler);
				//For now it doesn't have buttons, so we set a touch listener directly to the scene
				GameActivity.this.mTitleScene.setOnSceneTouchListener(new IOnSceneTouchListener(){

					public boolean onSceneTouchEvent(Scene pScene,TouchEvent pSceneTouchEvent) {
						if ( pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN){
							GameActivity.this.createGameScene();
						}
						return false;
					}
					
				});
				
			    			    		    
			    GameActivity.this.mFacebookSessionButton = new ButtonSprite(25,500,GameActivity.this.mFacebookSessionTextureRegion,
			    		vertexBufferObjectManager);	
			    GameActivity.this.mFacebookSessionButton.setOnClickListener(new OnClickListener(){
							public void onClick(ButtonSprite pButtonSprite,
									float pTouchAreaLocalX, float pTouchAreaLocalY) {
								Log.d("Facebook", "Click on the button");
					            if (GameActivity.this.mFacebook.isSessionValid()) {
					            	Log.d("Facebook", "Session is valid");
					                GameActivity.this.mAsyncRunner.logout(GameActivity.this, new LogoutRequestListener());
					            } else {
					            	Log.d("Facebook", "Session is invalid");
					            	GameActivity.this.mFacebook.authorize(GameActivity.this, GameActivity.this.mFacebookPermissions,
					                              new LoginDialogListener());
					            }			
							}
							
				});
				GameActivity.this.mTitleScene.attachChild(GameActivity.this.mFacebookSessionButton);
				GameActivity.this.mTitleScene.registerTouchArea(GameActivity.this.mFacebookSessionButton);
			}
			
		}));	
	}

	public void createGameScene(){
	    this.initGameFields();
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		this.mGameScene = new Scene();
		for(int i = 0; i < LAYER_COUNT; i++) {
			this.mGameScene.attachChild(new Entity());
		}
		// No background color needed as we have a fullscreen background sprite. 
		this.mGameScene.setBackgroundEnabled(false);
		this.mGameScene.getChildByIndex(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mGameBackgroundTextureRegion, vertexBufferObjectManager));
				
		// The ScoreText showing how many points the player has scored. 
		mScoreString = getString(R.string.score);
		this.mScoreText = new Text(0, (CELLS_VERTICAL-2)*CELL_HEIGHT, this.mFont, mScoreString  + String.valueOf(0) , 
				(mScoreString + "XXXX").length(), vertexBufferObjectManager);
		//this.mScoreText.setPosition(0, 0);
		//this.mScoreText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		//this.mScoreText.setAlpha(0.5f);
		this.mGameScene.getChildByIndex(LAYER_TEXT).attachChild(this.mScoreText);
		
		this.mGameScene.registerUpdateHandler(new TimerHandler(GameActivity.this.mChuySpawnDelay, true, new ITimerCallback(){
			public void onTimePassed(TimerHandler pTimerHandler) {
				handleChuySpawn();				
			}
			
		} ));
		
		//The HighscoreText showing the highest number of chuys killed ever
		SharedPreferences settings = getSharedPreferences(GameActivity.PREFS_NAME, 0);
		this.mHighScore = settings.getInt(GameActivity.PREFS_HIGHSCORE, 0);
		this.mHighScoreString = getString(R.string.highscore);
		this.mHighScoreText = new Text(0, 0, this.mFont, this.mHighScoreString  + String.valueOf(this.mHighScore) , 
				(this.mHighScoreString + this.mHighScore).length(),this.getVertexBufferObjectManager());
		this.mHighScoreText.setPosition(0, this.mScoreText.getY() + this.mScoreText.getHeight());
		this.mHighScoreText.setScale(0.7f);
		//this.mHighScoreText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mHighScoreText.setAlpha(0.5f);
		this.mGameScene.getChildByIndex(LAYER_TEXT).attachChild(this.mHighScoreText);
		
        // The swiping-text. 
		final Text noSwipingText = new Text(0, 0, this.mFont, getString(R.string.no_swiping), 
				new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		noSwipingText.setPosition((CAMERA_WIDTH - noSwipingText.getWidth()) * 0.5f, (CAMERA_HEIGHT - noSwipingText.getHeight()) * 0.5f);
		noSwipingText.setScale(0.0f);
		noSwipingText.registerEntityModifier(new ScaleModifier(2, 0.0f, 1.4f));
		this.mGameScene.getChildByIndex(GameActivity.LAYER_TEXT).attachChild(noSwipingText);

		// The handler that removes the swiping-text and starts the game. 
		this.mGameScene.registerUpdateHandler(new TimerHandler(3.0f, new ITimerCallback() {

			public void onTimePassed(final TimerHandler pTimerHandler) {
				GameActivity.this.mGameScene.unregisterUpdateHandler(pTimerHandler);
				GameActivity.this.mGameScene.getChildByIndex(GameActivity.LAYER_TEXT).detachChild(noSwipingText);
				GameActivity.this.mGameRunning = true;
			}
		}));
        	
    	//Allocate the pool to obtains Chuys
		GameActivity.this.mChuyEntityPool = new ChuyEntityPool((TiledTextureRegion) GameActivity.this.mChuyTiledTextureRegion,
				vertexBufferObjectManager,GameActivity.this.mGameScene);
		
		GameActivity.this.mPoopPool = new PoopAnimatedSpritePool((TiledTextureRegion) GameActivity.this.mPoopTiledTextureRegion, GameActivity.this.mGameScene, 
				vertexBufferObjectManager);
		this.mBackgroundMusic.setVolume(0.5f);
        this.mBackgroundMusic.play();		        
        this.spawnConsoles();
        mEngine.setScene(this.mGameScene);
					
		this.mGameScene.setOnSceneTouchListener(this);
		this.mGameScene.setTouchAreaBindingOnActionDownEnabled(true);
	}
	
    private void initGameFields(){
    	this.mGameFinished = false;
    	this.mGameRunning = false;
    	this.mGameScore = 0;
    	this.mChuyHits = 0;
    	this.mMaxNumberOfChuys = 5;
    	this.mChuySpawnDelay = 1.5f;
    	this.mGameLevel = 0;
    	this.mChuysOnScreen = new LinkedList<ChuyEntity>();   	    	
     }
    
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ){
    		GameActivity.this.finish();
    		return true;
    	} 	
    	return super.onKeyDown(keyCode, event);
    }
        
    private void spawnConsoles(){
    	VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
    	//Spawn Consoles
    			ConsoleEntity wiiConsole;
    			ConsoleEntity xboxConsole;
    			ConsoleEntity playStationConsole;
    			wiiConsole = new ConsoleEntity(0, 0, this.mWiiTextureRegion,vertexBufferObjectManager);
    			setConsoleToRandomCell(wiiConsole);
    			this.mGameScene.getChildByIndex(LAYER_CONSOLES).attachChild(wiiConsole);
    			
    			xboxConsole = new ConsoleEntity(0,0,this.mXboxTextureRegion,vertexBufferObjectManager);
    			setConsoleToRandomCell(xboxConsole);
    			this.mGameScene.getChildByIndex(LAYER_CONSOLES).attachChild(xboxConsole);
    			
    			playStationConsole = new ConsoleEntity(0, 0, this.mPlayStationTextureRegion, vertexBufferObjectManager);
    			setConsoleToRandomCell(playStationConsole);
    			this.mGameScene.getChildByIndex(LAYER_CONSOLES).attachChild(playStationConsole);	
    			
    			this.mConsolesOnScreen = new LinkedList<ConsoleEntity>();
    			this.mConsolesOnScreen.add(wiiConsole);
    			this.mConsolesOnScreen.add(playStationConsole);
    			this.mConsolesOnScreen.add(xboxConsole);
    }
    
    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();          
        }

        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }
    
    private class LogoutRequestListener extends BaseRequestListener {
        public void onComplete(String response, final Object state) {
            // callback should be run in the original thread, 
            // not the background thread
            GameActivity.this.mEngine.runOnUpdateThread(new Runnable() {
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }
    
    private class SessionListener implements AuthListener, LogoutListener {
        
        public void onAuthSucceed() {
            SessionStore.save(GameActivity.this.mFacebook, GameActivity.this);
            Toast.makeText(GameActivity.this, R.string.facebook_login_succeed, Toast.LENGTH_LONG).show();
            GameActivity.this.mFacebookSessionTextureAtlas.clearTextureAtlasSources();
            SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(GameActivity.this.mFacebookSessionTextureAtlas, GameActivity.this, "facebook_logout.svg",70,70,0,0);
        }

        public void onAuthFail(String error) {
        	Log.d("Facebook", error);
        	Toast.makeText(GameActivity.this, R.string.facebook_login_error, Toast.LENGTH_LONG).show();
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish() {
            SessionStore.clear(GameActivity.this);
            GameActivity.this.runOnUiThread(new Runnable(){

				public void run() {
					GameActivity.this.mFacebookSessionTextureAtlas.clearTextureAtlasSources();
		            SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(GameActivity.this.mFacebookSessionTextureAtlas, GameActivity.this, "facebook_login.svg",70,70,0,0);
					Toast.makeText(GameActivity.this, R.string.facebook_logout_succeed, Toast.LENGTH_LONG).show();					
				}
            	
            });
            
        }
    }
}
