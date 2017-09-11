package giotto2D.blending;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import giotto2D.blending.mbmodes.AddBlendMB;
import giotto2D.blending.mbmodes.ColorBurnMB;
import giotto2D.blending.mbmodes.ColorDodgeBlendMB;
import giotto2D.blending.mbmodes.DarkenBlendMB;
import giotto2D.blending.mbmodes.HardlightBlendMB;
import giotto2D.blending.mbmodes.LightenBlendMB;
import giotto2D.blending.mbmodes.MultiplyBlendMB;
import giotto2D.blending.mbmodes.NormalBlendMB;
import giotto2D.blending.mbmodes.OverlayBlendMB;
import giotto2D.blending.mbmodes.ScreenBlendMB;
import giotto2D.blending.mbmodes.SubtractBlendMB;
import giotto2D.blending.modes.AbstractBlender;
import giotto2D.core.GeometricFunctions;
import giotto2D.filters.color.ColorFill;
import giotto2D.filters.merge.AlphaIntersection;
import giotto2D.filters.merge.AlphaReplace;
import giotto2D.filters.merge.AlphaToImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class ImageBlender extends AbstractImageBlender
{
	//--- Render quality
	public static final int BICUBIC_RENDER = AffineTransformOp.TYPE_BICUBIC;//3
	public static final int BILINEAR_RENDER = AffineTransformOp.TYPE_BILINEAR;//2
	public static final int NEAREST_NEIGHBOR_RENDER = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;//1

	//--- String reprsentations of blend modes for combo box selecting.
	public static String[] blendModes = {	"Normal",
						"Add",
						"Lighten",
						"Screen",
						"Subtract",
						"Darken",
						"Multiply",
						"Overlay",
						"Hard light",
						"Color Burn",
						"Color Dodge" };
	//--- Screensize this blender works on.
	private Dimension screenSize;

	//--- BW image used to mask off pixels when doing blends
	private BufferedImage blendMask;

	//--- Translated source images are drawn on this before doing in motion bulr version step blend.
	private BufferedImage drawImg;

	//--- Used to create motion blurs.
	private BufferedImage combinedDraw;

	//--- Used to create motion blurs.
	private BufferedImage blurMask;

	//-------------------------------------------------- CONSTRUCTOR
	public ImageBlender( Dimension screenSize )
	{
		this.screenSize = screenSize;
		
		System.out.println("->INIT BLENDER<-");
		Runtime r = Runtime.getRuntime();

		long freeMem = r.freeMemory();

		//--- create help images
		blendMask = new BufferedImage( 	screenSize.width,
						screenSize.height,
						BufferedImage.TYPE_INT_ARGB );

		drawImg = new BufferedImage( 	screenSize.width,
						screenSize.height,
						BufferedImage.TYPE_INT_ARGB );

		combinedDraw = new BufferedImage(	screenSize.width,
						screenSize.height,
						BufferedImage.TYPE_INT_ARGB );

		blurMask = new BufferedImage(	screenSize.width,
						screenSize.height,
						BufferedImage.TYPE_INT_ARGB );
		
		long freeMemLater = r.freeMemory();
		System.out.println("->BLENDER MEMORY CONSUMPTION > " + (freeMem - freeMemLater) +"<-" );
	}

	//--- Blends images and does motion blur source image.
	public void moveblendImages( 	BufferedImage destinationImage,
					BufferedImage sourceImage,
					float[] x,// in pix
					float[] y,// in pix
					int mode,// modes defined in this class
					float[] scaleX,// 0 - 1
					float[] scaleY,// 0 - 1
					float[] anchorX,// in pix
					float[] anchorY,// in pix
					float[] rotation,// in degrees
					float opacity,// 0 - 1
					int interPolation,// quality choises definend in this class.
					boolean hasCutEdges,// if image has 1 pix wide alpha edge
					BufferedImage destinationMask,
					boolean useOverCompositionRule )
	{
		//--- number of times image is rendered for move blur
		int imgCount = x.length;

		//--- Create maskImg. Its image of source image's alpha channel.
		BufferedImage maskImg = new BufferedImage(	sourceImage.getWidth(),
								sourceImage.getHeight(),
								BufferedImage.TYPE_INT_ARGB );
 		AlphaToImage.filter( sourceImage, maskImg );

		//--- Create linedmaskImg. Its white squre the size of source image
		//--- with 1 pix black edges.
		//--- This is used when a square image with no alpha edges is rendered 
		//--- Katso voidaanko poistaa ja lisätä mustat viivat maskImgeen
		BufferedImage linedMaskImg = null;
		if( hasCutEdges )
		{
			linedMaskImg = new BufferedImage(	sourceImage.getWidth(),
								sourceImage.getHeight(),
								BufferedImage.TYPE_INT_ARGB );
			ColorFill.whiteFill( linedMaskImg );
			Graphics2D lgmi = linedMaskImg.createGraphics();
			lgmi.setColor( Color.black );
			lgmi.drawRect( 0,0,sourceImage.getWidth() - 1, sourceImage.getHeight() - 1 );
			lgmi.dispose();
		}

		//--- Values of animatable parameters for single render pass.
		float tx;
		float ty;
		float tscaleX;
		float tscaleY;
		float tanchorX;
		float tanchorY;
		float trotation;
		
		//--- opacity for single renderpass if a fraction of desired opacity.
		//--- float passOpacity = opacity / imgCount;
		int[] passOps = getPassOpacities( opacity, imgCount );
	
		//--- Combined area of all erase passes in full pixels (int)
		Rectangle combinedIntErase = null;

		//--- Structure of all erase passes in subpixel (float)
		Rectangle2D.Float[] eraseRects = new Rectangle2D.Float[ imgCount ];

		//--- Get combined area of all erase passes.
		for( int i = 0; i < imgCount; i++ )
		{
			tx = x[ i ];
			ty = y[ i ];
			tscaleX = scaleX[ i ];
			tscaleY = scaleY[ i ];
			tanchorX = anchorX[ i ];
			tanchorY = anchorY[ i ];
			trotation = rotation[ i ];
		
			//--- Init combined rect on first pass.
			if( i == 0 ) combinedIntErase = new Rectangle( (int)tx, (int)ty, 1, 1 );

			Rectangle2D.Float eraseRect = getEraseRectagle( sourceImage, tx, ty, tscaleX, tscaleY, tanchorX, tanchorY, trotation );
			eraseRects[ i ] = eraseRect;
			
			SwingUtilities.computeUnion( (int)eraseRect.x, (int)eraseRect.y, (int)eraseRect.width, (int)eraseRect.height, combinedIntErase );
		}

		//--- Combined erase rect in float,
		Rectangle2D.Float combinedErase = 
			new Rectangle2D.Float( (float) combinedIntErase.x,
						 (float) combinedIntErase.y,
						 (float) combinedIntErase.width,
						 (float) combinedIntErase.height );


		//--- Erase blurmask. This is combined image of masks.
		//--- It is used to do final blend.
		//--- This is a optimization.
		//--- We could just erase it all, but if we are adding a small image to large this should be faster.
		Graphics2D gm = blurMask.createGraphics();
		gm.setColor( Color.black );
		gm.fill( combinedErase );
		gm.dispose();

		//--- Erase comginedDraw. This is the combined image of step pictures.
		//--- It is used as source in final blend.
		//--- This is a optimization.
		//--- We could just erase it all, but if we are adding a small image to large this should be faster.
		Graphics2D  gb = combinedDraw.createGraphics();
		gb.setColor( Color.black );
		gb.fill( combinedErase );
		gb.dispose();

		//--- Do render passes to create motion blur.
		for( int i = 0; i < imgCount; i++ )
		{
			//--- Erase/draw rect for step
			Rectangle2D.Float eraseRect = eraseRects[ i ];

			//--- Erase blend mask. This is per step.
			Graphics2D gm2 = blendMask.createGraphics();
			gm2.setColor( Color.black );
			gm2.fill( eraseRect );
			gm2.dispose();

			//--- Erase draw image. This is per step.
			Graphics2D gd = drawImg.createGraphics();
			gd.setColor( Color.black );
			gd.fill( eraseRect );
			gd.dispose();

			//--- Get transform for step
			tx = x[ i ];
			ty = y[ i ];
			tscaleX = scaleX[ i ];
			tscaleY = scaleY[ i ];
			tanchorX = anchorX[ i ];
			tanchorY = anchorY[ i ];
			trotation = rotation[ i ];

			//--- AffineTransformOp does not take 0 scales smiling and - scales dont render we have flip for it
			if( tscaleX <= 0 ) tscaleX = 0.0001f;
			if( tscaleY <= 0 ) tscaleY = 0.0001f;

			//--- Create AffineTransformOp filter to render transformed source on drawImage help image.
			AffineTransform transform = getTransform( tx, ty, tscaleX, tscaleY, tanchorX, tanchorY, trotation );
			AffineTransformOp tRenderer = new AffineTransformOp( transform, interPolation );
			
			//--- Draw transformed maskImage image on blend mask.
			tRenderer.filter( maskImg, blendMask );

			//--- Draw transformed source step image on drawImg
			tRenderer.filter( sourceImage, drawImg );

			//--- Do step blend for source image into blur help
			AbstractBlender blender = getBlender( MOVE_BLUR_ADD );
			Rectangle blendArea = getBlendArea( eraseRect, destinationImage );

			int stepOpacity = passOps[ i ];

			blender.blendImages(	combinedDraw,
						drawImg,
						blendMask,
						stepOpacity,
						blendArea );

			//--- Make blend mask smaller for step if fine edges
			//--- or we will get lighter edges.
			if( hasCutEdges ) tRenderer.filter( linedMaskImg, blendMask );

			//--- Do step blend for blurMask. This is its own mask.
			blender.blendImages(	blurMask,
						blendMask,
						blendMask,
						stepOpacity,
						blendArea );
		}

		//--- Get special motion blur blend.
		AbstractBlender blender = getMBBlender( mode );
		Rectangle combineBlendArea = getBlendArea( combinedErase, destinationImage );

		//--- Calculate int opacity 0 - 255 from normalized( 0 - 1) opacity. 
		//--- Blenders use int opacities.
		//--- This is the total opacity of image.
		int intOpacity = Math.round( opacity * 255 );

		//--- Apply destination mask, if provided
		if( destinationMask != null ) 
			AlphaIntersection.filterToImage( blurMask, destinationMask );

		//--- Do final blend.
		blender.blendImages(	destinationImage,
					combinedDraw,
					blurMask,
					intOpacity,
					combineBlendArea );

		//--- Do alpha combine. If not OVER do nothing
		blender.doAlphaCombine( useOverCompositionRule,
					true,
					destinationImage,
					combinedDraw,
					blurMask,
					intOpacity,
					combineBlendArea );
	}

	//--- Blend images, no motion blur.
	public void blendImages( 	BufferedImage destinationImage,
					BufferedImage sourceImage,
					float x,// in pix
					float y,// in pix
					int mode,// modes defined in this class
					float scaleX,// 0 - 1
					float scaleY,// 0 - 1
					float anchorX,// in pix
					float anchorY,// in pix
					float rotation,// in degrees
					float opacity,// 0 - 1
					int interPolation,
					BufferedImage destinationMask,
					boolean useOverCompositionRule )
	{
		//--- AffineTransformOp does not take 0 scales smiling
		if( scaleX == 0 ) 
			scaleX = 0.0001f;
		if( scaleY == 0 ) 
			scaleY = 0.0001f;

		AffineTransform transform = getTransform( x, y, scaleX, scaleY, anchorX, anchorY, rotation );

		//--- Create AffineTransformOp filter to render transformed source on drawImage help image.
		AffineTransformOp tRenderer = new AffineTransformOp( transform, interPolation );

		//--- Get area to be erased.
		Rectangle2D.Float eraseRect = getEraseRectagle( sourceImage, x, y, scaleX, scaleY, anchorX, anchorY, rotation );

		//--- Draw source image on drawImage help canvas.
		//--- Get graphics.
		Graphics2D gd = drawImg.createGraphics();
		//--- Fill erased area with black.
		gd.setColor( Color.black );
		gd.fill( eraseRect );
		gd.dispose();

		//--- Draw treansformed source image on help canvas
		//--- NOTE: This also draws alpha on destination image.
		tRenderer.filter( sourceImage, drawImg );

		//--- Create mask for doing blend.
		//--- Fill bounding box with black.
		Graphics2D gm = blendMask.createGraphics();
		gm.setColor( Color.black );
		gm.fill( eraseRect );//--- Fills alpha too
		gm.dispose();
		//--- Draw white hole in blend mask.
		//--- Create bw image from source alpha used to make hole.
		BufferedImage maskImg = new BufferedImage(	sourceImage.getWidth(),
								sourceImage.getHeight(),
								BufferedImage.TYPE_INT_ARGB );
		Graphics2D gmi = maskImg.createGraphics();
		//--- maskImg does not need to be image sources alpha
		//--- because blender uses sources alpha when rendering
		ColorFill.whiteFill( maskImg );
		gmi.dispose();

		//--- draw translated white image on blend mask.
		tRenderer.filter( maskImg, blendMask );

		//--- Apply source mask, if provided
		if( destinationMask != null ) 
			AlphaReplace.filter( drawImg, destinationMask );

		//--- BLEND
		//--- Get blender for mode.
		AbstractBlender blender = getBlender( mode );

		//--- Get area for blend.
		Rectangle blendArea = getBlendArea( eraseRect, destinationImage );

		//--- Calculate int opacity 0 - 255 from float opacity 0 - 1
		int intOpacity = Math.round( opacity * 255 );

		//--- Do blend.
		blender.blendImages(	destinationImage,
					drawImg,
					blendMask,
					intOpacity,
					blendArea );

		//--- Do alpha comine. If not OVER do nothing
		blender.doAlphaCombine( useOverCompositionRule,
					false,
					destinationImage,
					drawImg,
					blendMask,
					intOpacity,
					blendArea );
	}

	//--- Blends images, with top left corners aligned.
	public void blendImages( 	BufferedImage destinationImage,
					BufferedImage sourceImage,
					BufferedImage sourceMask,
					float opacity,
					int mode,
					boolean useOverCompositionRule )
	{

		//--- Apply source mask, if provided
		if( sourceMask != null ) 
			AlphaReplace.filter( sourceImage, sourceMask );
	
		//--- Calculate int opacity 0 - 255 from float opacity 0 - 1
		int intOpacity = Math.round( opacity * 255 );

		Rectangle blendArea = new Rectangle( 0, 0, sourceImage.getWidth(), sourceImage.getHeight());
		blendArea = SwingUtilities.computeIntersection( 0,
								0,
								destinationImage.getWidth(),
								destinationImage.getHeight(),
								blendArea );

		//--- Paint blend mask white in blend area, black else where
		Graphics2D gm = blendMask.createGraphics();

		//--- Paint black if not full screen blend.
		gm.setColor( Color.black );
		if( blendArea.width + 2 < blendMask.getWidth() || blendArea.height + 2 < blendMask.getHeight() )// +2 because using method designed for different use, hack 
			gm.fillRect( 0,0, blendMask.getWidth(), blendMask.getHeight() );

		//--- Paint blend area white
		gm.setColor( Color.white );
		gm.fillRect( 0,0, blendArea.width + 2,  blendArea.height +2  );// +2 because using method designed for different use, hack
		gm.dispose();

		//--- Get blender
		AbstractBlender blender = getBlender( mode );

		//--- Do blend.
		blender.blendImages(	destinationImage,
					sourceImage,
					blendMask,
					intOpacity,
					blendArea );

		//--- Do alpha comine. Is noop if not OVER
		blender.doAlphaCombine( useOverCompositionRule,
					false,
					destinationImage,
					sourceImage,
					blendMask,
					intOpacity,
					blendArea );
	}

	//--- Used to blend continuously rasterized svg images.
	//--- Fully erases (paints with black) blurMask and combinedDraw helper images
	//--- FSMB means FullScreenMotionBlur
	public void initFullScreenMotionBlur()
	{
		Rectangle fsrect = new Rectangle( 0, 0, screenSize.width, screenSize.height );
		//--- Create blur mask. This is combined image of masks.
		//--- It is used to do final blend.
		Graphics2D gm = blurMask.createGraphics();
		gm.setColor( Color.black );
		gm.fill( fsrect );
		gm.dispose();
		//--- Create blur help. This is the combined image of sub pictures.
		//--- It is used as source in final blend.
		Graphics2D  gb = combinedDraw.createGraphics();
		gb.setColor( Color.black );
		gb.fill( fsrect );
		gb.dispose();
	}
	//--- Used to blend continuously rasterized svg images.
	public void drawFullScreenStepImage( BufferedImage stepImg, float fstepOpacity )
	{
		//--- draw blend mask. This is per step.
		AlphaToImage.filter( stepImg,  blendMask);
		//--- Do step blend for source image into blur help
		AbstractBlender blender = getBlender( MOVE_BLUR_ADD );
		Rectangle blendArea = new Rectangle( 0, 0, stepImg.getWidth(), stepImg.getHeight() );

		//--- Calculate int opacity 0 - 255 from normalized( 0 - 1) opacity.
		int stepOpacity = Math.round( fstepOpacity * 255 );

		blender.blendImages(	combinedDraw,
					stepImg,
					blendMask,
					stepOpacity,
					blendArea );

		//--- Do step blend for blurMask. This blendmask is its own mask.
		blender.blendImages(	blurMask,
					blendMask,
					blendMask,
					stepOpacity,
					blendArea );
	}

	public BufferedImage getCombinedDraw(){ return combinedDraw; }

	//--- Used to blend continuously rasterized svg images.
	public void doFullScreenMotionBlur( BufferedImage destination, BufferedImage destinationMask, int mode, float opacity, boolean useOverCompositionRule )
	{
		//--- Get special motion blur blend.
		AbstractBlender blender = getMBBlender( mode );
		
		//--- Calculate int opacity 0 - 255 from normalized( 0 - 1) opacity.
		int intOpacity = Math.round( opacity * 255 );

		//--- Apply destinationMask, if provided
		if( destinationMask != null ) AlphaIntersection.filterToImage( blurMask, destinationMask );

		//--- Do final blend.
		Rectangle combineBlendArea = new Rectangle( 0, 0, screenSize.width, screenSize.height );
		blender.blendImages(	destination,
					combinedDraw,
					blurMask,
					intOpacity,
					combineBlendArea );

		//--- Do alpha comine. Is noop if not OVER
		blender.doAlphaCombine( useOverCompositionRule,
					true,
					destination,
					combinedDraw,
					blurMask,
					intOpacity,
					combineBlendArea );
	}


	//--- Returns transform object for animation coordinates.
	//--- Anim coords are given to image, transformation apllies to coordinate space transformations.
	private AffineTransform getTransform( 	float x,// in pix
						float y,// in pix
						float scaleX,// 0 - 1
						float scaleY,// 0 - 1
						float anchorX,// in pix
						float anchorY,// in pix
						float rotation )// rotation in degrees
	{
		//--- Calculate translation.
		//--- Get scaled anchor offsets.
		anchorX = scaleX * anchorX;
		anchorY = scaleY * anchorY;

		//--- Get offset to topleft corner of scaled and rotated image from anchor point.
		Point2D.Float topLeftOffset = new Point2D.Float( -anchorX, -anchorY );
		topLeftOffset = GeometricFunctions.rotatePointAroundOrigo( rotation, topLeftOffset );

		//--- Get translation to topleft point = scaled and rotated topleft point  + x and y translation.
		float tx = x + topLeftOffset.x;
		float ty = y + topLeftOffset.y;

		//--- Create AffineTransform object for translation of source image.
		AffineTransform transform = new AffineTransform();
		transform.translate((double) tx, (double) ty);
 		transform.rotate( Math.toRadians( (double) rotation ) );
		transform.scale( (double) scaleX, (double) scaleY );

		return transform;
	}

	//--- Returns erase rectangle for TRANSFORMED sourceImage.
	private Rectangle2D.Float getEraseRectagle(	BufferedImage sourceImage,
						 	float x,// in pix
							float y,// in pix
							float scaleX,// 0 - 1
							float scaleY,// 0 - 1
							float anchorX,// in pix
							float anchorY,// in pix
							float rotation )// in degrees
	{
		//--- Scaled anchor values.
		anchorX = scaleX * anchorX;
		anchorY = scaleY * anchorY;

		if( sourceImage == null )
		{
			Throwable t = new Throwable( "ImageBlender.getEraseRectagle(): sourceImage == null");
			t.printStackTrace();
		}

		//--- Get bounding box position and size for scaled ans roatated source image.
		//--- Get size of scaled source image.
		float sourceWidth = sourceImage.getWidth() * scaleX;
		float sourceHeight = sourceImage.getHeight() * scaleY;
		Rectangle2D.Float scaledImgSize = 
			new Rectangle2D.Float( 0,0,sourceWidth, sourceHeight );
		//--- Get offset from anchorpoint to bounding box topleftcorner
		//--- of scaled and rotated source image.
		Point2D.Float boundingOffset = GeometricFunctions.getBoundingOffset(	scaledImgSize,
											anchorX,
											anchorY,
											rotation );
		//--- Get bounding box size for rotated image.
		Rectangle2D.Float boundingSize = GeometricFunctions.getBoundingSize(	sourceWidth,
											sourceHeight,
											rotation );
		//--- Get translation to topleft point.of fill area
		float BxTranslation = x - boundingOffset.x - 2;// -2 because erase area is made 2 pixels bigger then calculated.
		float ByTranslation = y - boundingOffset.y - 2;// -2 because erase area is made 2 pixels bigger then calculated.

		//--- Erase areas need to be bit bigger to avoid spill. add 4 to width for extra coveridge.
		Rectangle2D.Float eraseSize = new Rectangle2D.Float( BxTranslation, ByTranslation, boundingSize.width + 4, boundingSize.height + 4 );

		return eraseSize;
	}

	//--- Returns area in pi coords on whitch blend is performed
	private Rectangle getBlendArea( Rectangle2D.Float eraseRect, BufferedImage destinationImage )
	{
		if( destinationImage == null )
		{
			Throwable t = new Throwable( "ImageBlender.getBlendArea(): destinationImage == null");
			t.printStackTrace();
			return null;
		}

		//--- Erase rect was made bigger 
		int bx = new Float( eraseRect.x ).intValue() + 1;
		int by = new Float( eraseRect.y ).intValue() + 1;
		int bwidth = new Float( eraseRect.width ).intValue() - 2;
		int bheight = new Float( eraseRect.height ).intValue() - 2;
		Rectangle blendArea = new Rectangle( bx, by, bwidth, bheight);

				
		blendArea = SwingUtilities.computeIntersection( 0,
								0,
								destinationImage.getWidth(),
								destinationImage.getHeight(),
								blendArea );

		return blendArea;
	}

	//--- Return blender object for given mode
	private AbstractBlender getMBBlender( int mode )
	{
		switch( mode )
		{
			case NORMAL:
				return new NormalBlendMB();

			case LIGHTEN:
				return new LightenBlendMB();
			
			case DARKEN:
				return new DarkenBlendMB();

			case ADD:
				return new AddBlendMB();

			case SCREEN:
				return new ScreenBlendMB();

			case MULTIPLY:
				return new MultiplyBlendMB();

			case SUBTRACT:
				return new SubtractBlendMB();

			case OVERLAY:
				return new OverlayBlendMB();

			case HARDLIGHT:
				return new HardlightBlendMB();

			case COLORBURN:
				return new ColorBurnMB();

			case COLORDODGE:
				return new ColorDodgeBlendMB();

			default:
				System.out.println("ImageBlender.getmBlender() default hit");
				return new NormalBlendMB();
		}
	}

	public static void cutAlphaEdges( BufferedImage img )
	{
		WritableRaster rast = img.getRaster();
		copyRowClearAlpha( 0, 0, rast );
		copyRowClearAlpha( rast.getHeight() - 1, rast.getHeight() - 1, rast );
		copyColumnClearAlpha( 0, 0, rast );
		copyColumnClearAlpha( rast.getWidth() - 1, rast.getWidth() - 1, rast );
	}

	public static void copyRowClearAlpha( int srow, int drow, WritableRaster raster )
	{
		int[] pixel = new int[ 4 ];

		for( int col = 0; col < raster.getWidth(); col++ )
		{
			raster.getPixel( col, srow, pixel );
			pixel[ 3 ] = 0;
			raster.setPixel( col, drow, pixel );
		}
	}

	public static void copyColumnClearAlpha( int scol, int dcol, WritableRaster raster )
	{
		int[] pixel = new int[ 4 ];

		for( int row = 0; row < raster.getHeight(); row++ )
		{
			raster.getPixel( scol, row, pixel );
			pixel[ 3 ] = 0;
			raster.setPixel( dcol, row, pixel );
		}
	}
	
	private int[] getPassOpacities( float opacity, int imgCount )
	{
		float passOpacity = opacity / imgCount;
		int intOpacity = Math.round( opacity * 255 );
		int stepOpacity = Math.round( passOpacity * 255 );
		int[] ops = new int[ imgCount ];
		for( int i = 0; i < ops.length; i++ ) ops[ i ] = stepOpacity;
		int combop = 0;
		for( int i = 0; i < ops.length; i++ ) combop += ops[ i ];
		int sindex = 0;
		while( combop < intOpacity )
		{
			ops[ sindex ] += 1;
			sindex++;
			if( sindex == imgCount ) sindex = 0;
			combop = 0;
			for( int i = 0; i < ops.length; i++ ) combop += ops[ i ];
		}
		return ops;
	}

	public static void debugSave( BufferedImage image, String name )
	{
		try
		{
			File f = new File( "/home/janne/" + name + ".png" );
			ImageIO.write( image, "png", f );
		}
		catch( IOException e )
		{
			System.out.println( "ImagewWrite failed in PNG writer" );
		}
	}

}//end class
