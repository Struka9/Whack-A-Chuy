package rosalila.studio.whackachuy.entity;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
public class ConsoleEntity  extends CellEntity{

	private boolean mIsCaptured;

	public ConsoleEntity(final int pCellX, final int pCellY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pCellX, pCellY, CELL_WIDTH, CELL_HEIGHT, pTextureRegion, pVertexBufferObjectManager);
		this.mIsCaptured = false;
	}
	
	public boolean IsCaptured(){
		return this.mIsCaptured;
	}
	
	public void setIsCaptured(boolean pIsCaptured){
		this.mIsCaptured = pIsCaptured;
	}
}
