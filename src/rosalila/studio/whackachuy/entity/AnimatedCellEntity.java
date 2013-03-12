package rosalila.studio.whackachuy.entity;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import rosalila.studio.whackachuy.WhackAChuyConstants;

public abstract class AnimatedCellEntity extends AnimatedSprite implements WhackAChuyConstants, ICellEntity {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected int mCellX;
	protected int mCellY;

	// ===========================================================
	// Constructors
	// ===========================================================

	public AnimatedCellEntity(final int pCellX, final int pCellY, final int pWidth, final int pHeight, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pCellX * CELL_WIDTH, pCellY * CELL_HEIGHT, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager);
		this.mCellX = pCellX;
		this.mCellY = pCellY;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getCellX() {
		return this.mCellX;
	}

	public int getCellY() {
		return this.mCellY;
	}

	public void setCell(final ICellEntity pCellEntity) {
		this.setCell(pCellEntity.getCellX(), pCellEntity.getCellY());
	}

	public void setCell(final int pCellX, final int pCellY) {
		this.mCellX = pCellX;
		this.mCellY = pCellY;
		this.setPosition(this.mCellX * CELL_WIDTH, this.mCellY * CELL_HEIGHT);
	}

	public boolean isInSameCell(final ICellEntity pCellEntity) {
		return this.mCellX == pCellEntity.getCellX() && this.mCellY == pCellEntity.getCellY();
	}
}
