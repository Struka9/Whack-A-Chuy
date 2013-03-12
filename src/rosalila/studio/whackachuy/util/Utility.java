package rosalila.studio.whackachuy.util;

import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

public class Utility {
	public static TiledTextureRegion createTiledTextureRegionFromSvgAsset(TextureManager pTextureManager,
			final Context pContext, final int onePictWidth, final int onePictHeight,final String... paths){
			       BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(
                    pTextureManager, onePictWidth * paths.length, onePictHeight,
                    TextureOptions.BILINEAR);

		 			ITextureRegion[] iTextureRegions = new ITextureRegion[paths.length];

				     for (int i = 0; i < paths.length; i++)
				     {
				             iTextureRegions[i] = SVGBitmapTextureAtlasTextureRegionFactory
				                             .createFromAsset(mBitmapTextureAtlas, pContext, paths[i],
		                                             onePictWidth, onePictHeight,onePictWidth*i,0);
				     }
				     mBitmapTextureAtlas.load();
				     TiledTextureRegion result = new TiledTextureRegion(mBitmapTextureAtlas,iTextureRegions);

				     return result;	
	}
	 public static TiledTextureRegion createTiledTextureRegionFromAsset(
             TextureManager pTextureManager, final Context pContext,
             final int onePictWidth, final int onePichHeight,
             final String... paths){
		 			BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(
                     pTextureManager, onePictWidth * paths.length, onePichHeight,
                     TextureOptions.BILINEAR);

		 			ITextureRegion[] iTextureRegions = new ITextureRegion[paths.length];

				     for (int i = 0; i < paths.length; i++)
				     {
				             iTextureRegions[i] = BitmapTextureAtlasTextureRegionFactory
				                             .createFromAsset(mBitmapTextureAtlas, pContext, paths[i],
				                                             onePictWidth * i, 0);
				     }

				     TiledTextureRegion result = new TiledTextureRegion(mBitmapTextureAtlas,iTextureRegions);
				     mBitmapTextureAtlas.load();
				     return result;
	 }
	 
	 
}
