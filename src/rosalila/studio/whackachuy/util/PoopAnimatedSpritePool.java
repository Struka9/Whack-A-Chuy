package rosalila.studio.whackachuy.util;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import rosalila.studio.whackachuy.GameActivity;
import rosalila.studio.whackachuy.WhackAChuyConstants;

public class PoopAnimatedSpritePool extends GenericPool<AnimatedSprite> implements WhackAChuyConstants{
	private final TiledTextureRegion mPoopTexture;
	private final Scene mScene;
	private final VertexBufferObjectManager mVertexBufferObject;
	
	public PoopAnimatedSpritePool(final TiledTextureRegion pTextureRegion, final Scene pScene, 
			final VertexBufferObjectManager pVertexBufferObjectManager){
		if ( pTextureRegion == null)throw new IllegalArgumentException("Poop texture cannot be null");
		this.mPoopTexture = pTextureRegion;
		
		if ( pScene == null)throw new IllegalArgumentException("Scene cannot be null for Poop pool");
		this.mScene = pScene;
		
		if (pVertexBufferObjectManager == null)throw new IllegalArgumentException("Vertex Buffer Object cannot be null for Poop pool");
		this.mVertexBufferObject = pVertexBufferObjectManager;
	}

	@Override
	protected AnimatedSprite onAllocatePoolItem() {
		AnimatedSprite p =new AnimatedSprite(0,0,CELL_HEIGHT,CELL_WIDTH,this.mPoopTexture,this.mVertexBufferObject);
		this.mScene.getChildByIndex(GameActivity.LAYER_CHUY).attachChild(p);
		return p;
	}
	
	@Override
	protected void onHandleObtainItem(AnimatedSprite pAnimatedSprite){
	}
	
	@Override
	protected void onHandleRecycleItem(AnimatedSprite pAnimatedSprite){
		pAnimatedSprite.reset();
		pAnimatedSprite.setVisible(false);
	}
}
