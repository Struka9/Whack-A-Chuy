package rosalila.studio.whackachuy.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import rosalila.studio.whackachuy.WhackAChuyConstants;

public abstract class CellEntity extends Sprite implements WhackAChuyConstants, ICellEntity {
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

	public CellEntity(final int pCellX, final int pCellY, final int pWidth, final int pHeight, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pCellX * CELL_WIDTH, pCellY * CELL_HEIGHT, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);

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

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	public boolean isInSameCell(final ICellEntity pCellEntity) {
		return this.mCellX == pCellEntity.getCellX() && this.mCellY == pCellEntity.getCellY();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
