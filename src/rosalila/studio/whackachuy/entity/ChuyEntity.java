package rosalila.studio.whackachuy.entity;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ChuyEntity extends AnimatedCellEntity{

	public static final int DIRECTION_UNDEFINED = -1;
	public static final int DIRECTION_RIGHT = 0;
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_UP = 2;
	public static final int DIRECTION_DOWN = 3;
	private int mDirectionEscape = DIRECTION_UNDEFINED;
	
	private ChuyTouchListener mChuyTouchListener = null;
	private ChuyDeathAnimationListener mChuyDeathAnimationListener = null;
	private ConsoleEntity mCapturedConsole = null;
	private boolean mDead;
	
	public ChuyEntity(int pCellX, int pCellY, int pWidth, int pHeight,
			TiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pCellX, pCellY, pWidth, pHeight, pTiledTextureRegion,
				pVertexBufferObjectManager);
	}
	
	public void moveToConsole(ConsoleEntity pConsole){
		if ( this.getX() > pConsole.getX() ){ //Moving left
			this.animate(new  long[]{300,300,300},9,11,true);
			//this.setCell(this.getCellX()-1, this.getCellY());
			this.setPosition(this.getX()-35, this.getY());
		}
		else if ( this.getX() < pConsole.getX() ){ //Moving right
			this.animate(new  long[]{300,300,300},12,14,true);
			this.setPosition(this.getX()+35, this.getY());
		}
		else 	if ( this.getY() > pConsole.getY() )	{ //Walk down
				this.animate(new  long[]{300,300,300},15,17,true);
				this.setPosition(this.getX(), this.getY()-35);
			}else if ( this.getY() < pConsole.getY() ){ //Walk up
				this.animate(new  long[]{300,300,300},6,8,true);
				this.setPosition(this.getX(), this.getY()+35);
			}
		
		//if ( this.isInSameCell(pConsole) && !pConsole.IsCaptured() )this.captureConsole(pConsole);
		if ( this.getX() == pConsole.getX() && this.getY() == pConsole.getY() && !pConsole.IsCaptured())this.captureConsole(pConsole);
	}
	
	public void captureConsole(ConsoleEntity pConsole){
		this.mCapturedConsole = pConsole;
		if (pConsole != null)
			pConsole.setIsCaptured(true);
	}
	
	public boolean hasCapturedConsole(){
		return this.mCapturedConsole != null;
	}
	
	public ConsoleEntity getCapturedConsole(){
		return this.mCapturedConsole;
	}
	
	public void releaseConsole()
	{
		this.mCapturedConsole.setIsCaptured(false);
		//TODO
		this.mDirectionEscape = ChuyEntity.DIRECTION_UNDEFINED;
		this.mCapturedConsole = null;
	}
	
	public void runAwayWithConsole(){
		if ( this.mCapturedConsole == null)return;
		if ( this.mDirectionEscape == DIRECTION_UNDEFINED ){
		    int cellsToLowestXLimit = Math.abs(this.getCellX()-0);
		    int cellsToHighestXLimit = Math.abs(this.getCellX()-CELLS_HORIZONTAL);
		    int cellsToLowestYLimit = Math.abs(this.getCellY()-0);
		    int cellsToHighestYLimit = Math.abs(this.getCellY()-CELLS_VERTICAL);
		
		    int shortestDistance = Integer.MAX_VALUE;
		    if ( cellsToLowestXLimit < shortestDistance )		this.mDirectionEscape = DIRECTION_LEFT;
		    if ( cellsToHighestXLimit < shortestDistance )	this.mDirectionEscape = DIRECTION_RIGHT;
		    if ( cellsToLowestYLimit < shortestDistance )		this.mDirectionEscape = DIRECTION_DOWN;
		    if ( cellsToHighestYLimit < shortestDistance )		this.mDirectionEscape = DIRECTION_UP;
		}
		switch (this.mDirectionEscape){
		    case DIRECTION_LEFT:
		    	this.animate(new  long[]{300,300,300},9,11,true);
		    	//this.setCell(this.getCellX()-1, this.getCellY());
		    	this.setX(this.getX()-35);
		    	this.mCapturedConsole.setPosition(this);
		    	break;
		    	
		    case DIRECTION_RIGHT:
				this.animate(new  long[]{300,300,300},12,14,true);
				//this.setCell(this.getCellX()+1,this.getCellY());
				this.setX(this.getX()+35);
				this.mCapturedConsole.setPosition(this);
			     break;
			     
		    case DIRECTION_UP:
				this.animate(new  long[]{300,300,300},6,8,true);
				this.setY(this.getY()+35);
				this.mCapturedConsole.setPosition(this);
				break;
				
		    case DIRECTION_DOWN:
		    	this.animate(new  long[]{300,300,300},15,17,true);
		    	this.setY(this.getY()-35);
				this.mCapturedConsole.setPosition(this);
		    	break;
		    	
		}
					
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, 
			final float pTouchAreaLocalY) {

		if ( this.isDead() )return true; //Ignore the touch
		
		switch(pSceneTouchEvent.getAction()){
			case TouchEvent.ACTION_DOWN:
				//this.setIgnoreUpdate(true);
				this.animate(new long[]{150,150,150,150,150,150},0,5,false);		
				if ( this.hasCapturedConsole() )this.releaseConsole();
				
				this.setIsDead(true);
				
				if ( this.mChuyTouchListener != null ) this.mChuyTouchListener.onTouch(this);		
				break;
				
			default:
				break;
		}
		return true;
	}
	
	@Override
	public void onManagedUpdate(float pSecondsElapsed){
		super.onManagedUpdate(pSecondsElapsed);
		
		if ( this.mDead && !this.isAnimationRunning() ){
			if (this.mChuyDeathAnimationListener != null )this.mChuyDeathAnimationListener.onAnimationFinish(this);
		}
	}
	
	public void setIsDead(boolean pBool){
		this.mDead = pBool;
	}
	
	public void setOnTouchListener(ChuyTouchListener pListener){
		this.mChuyTouchListener = pListener;
	}
	
	public void setDeathAnimationListener(ChuyDeathAnimationListener pListener){
		this.mChuyDeathAnimationListener = pListener;
	}
	
	public boolean isDead(){
		return this.mDead;
	}
}
