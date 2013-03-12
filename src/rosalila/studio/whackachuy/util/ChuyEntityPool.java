package rosalila.studio.whackachuy.util;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.math.MathUtils;

import rosalila.studio.whackachuy.GameActivity;
import rosalila.studio.whackachuy.WhackAChuyConstants;
import rosalila.studio.whackachuy.entity.ChuyEntity;

public class ChuyEntityPool extends GenericPool<ChuyEntity> implements WhackAChuyConstants{

	private final TiledTextureRegion mChuyTexture;
	private final Scene mScene;
	private final VertexBufferObjectManager mVertexBufferObjectManager;
	
	public ChuyEntityPool(final TiledTextureRegion pTexture,final VertexBufferObjectManager pVertexBufferObject, final Scene pScene){
		if ( pTexture == null)	throw  new IllegalArgumentException("Texture for chuy cannot be null!");
		this.mChuyTexture = pTexture;		
		
		if (pVertexBufferObject == null ) throw new IllegalArgumentException("Vertex Buffered Object cannot be null");
		this.mVertexBufferObjectManager = pVertexBufferObject;
		
		if (pScene == null ) throw new IllegalArgumentException("Scene object cannot be null");
		this.mScene = pScene;
	}
	@Override
	protected ChuyEntity onAllocatePoolItem() {
		ScreenPosition randomCoordinates = this.generateRandomCoordinates();
		ChuyEntity c =new ChuyEntity(randomCoordinates.getCellX(), randomCoordinates.getCelllY(), CELL_WIDTH, CELL_HEIGHT,
				this.mChuyTexture.deepCopy(),this.mVertexBufferObjectManager );
		ChuyEntityPool.this.mScene.getChildByIndex(GameActivity.LAYER_CHUY).attachChild(c);
		ChuyEntityPool.this.mScene.registerTouchArea(c);
		return c;
	}

	@Override
	protected void onHandleRecycleItem(ChuyEntity pChuy){
		pChuy.setIgnoreUpdate(true);
		pChuy.setVisible(false);
		pChuy.clearUpdateHandlers();
	}
	
	@Override
	protected void onHandleObtainItem(ChuyEntity pChuy){
		ScreenPosition randomCoordinates = this.generateRandomCoordinates();
		pChuy.reset();
		pChuy.setIsDead(false);
		pChuy.setCell(randomCoordinates.getCellX(), randomCoordinates.getCelllY());
		
				
		
	}
	
	private ScreenPosition generateRandomCoordinates(){
		int screenSide = MathUtils.random(0, 10000) % 4;
		int chuyCellX = 0;
		int chuyCellY = 0;
		switch (screenSide){
			case 0: { //left side
				chuyCellX = 0;
				chuyCellY = MathUtils.random(1, CELLS_VERTICAL -1 );
			}
			break;
		
			case 1:{ //right side
				chuyCellX = CELLS_HORIZONTAL-1;
				chuyCellY = MathUtils.random(1, CELLS_VERTICAL - 1);
			}
			break;
			
			case 2: { //down side
				chuyCellX = MathUtils.random(1, CELLS_HORIZONTAL-1);
				chuyCellY =0;
			}
			break;
			
			case 3: {//Upside
				chuyCellX = MathUtils.random(1, CELLS_HORIZONTAL-1);
				chuyCellY = CELLS_VERTICAL-1;
			}
		}
		
		return new ScreenPosition(chuyCellX, chuyCellY);
	}
	
	private class ScreenPosition{
		private int mCellX;
		private  int mCellY;
		
		
		public ScreenPosition(int pCellX,int pCellY){
			this.setCellX(pCellX);
			this.setCellY(pCellY);
		}

		private int getCellX() {
			return mCellX;
		}

		private void setCellX(int mCellX) {
			this.mCellX = mCellX;
		}

		private int getCelllY() {
			return mCellY;
		}

		private void setCellY(int mCelllY) {
			this.mCellY = mCelllY;
		}
	}
}
