package animator.phantom.blender;

/*
    Copyright Janne Liljeblad

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

import giotto2D.blending.ImageBlender;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.controller.ProjectController;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.AnimatedValue;

public class SynchronizedImageBlender
{
	private ImageBlender blender = null;
	private boolean occupied = false;

	private static int passes = 7;
	private static int shutterAngle = 150;

	public static void setPasses( int passes_ ){ passes = passes_; }
	public static void setShutterAngle( int shutterAngle_ ){ shutterAngle = shutterAngle_; }

	public SynchronizedImageBlender()
	{
		blender = new ImageBlender( ProjectController.getScreenSize() );
	}

	private ImageBlender getBlender()
	{
		return blender;
	}

	//-- Used to deside which blender to give to reduce waiting on synchronized for multiple blenders
	public boolean isOccupied(){ return occupied; }
	private void setOccupied(){ occupied = true; }
	private void setNotOccupied(){ occupied = false; }

	public synchronized void doAlignedBlend( BufferedImage destination, BufferedImage source, float opacity, int blendMode, BufferedImage sourceMask )
	{
		setOccupied();

		getBlender().blendImages( 	destination,
					source,
					sourceMask,
					opacity,
					blendMode,
					false );
		setNotOccupied();
	}

	public synchronized void doAlignedBlend( BufferedImage destination, BufferedImage source, float opacity, int blendMode, BufferedImage sourceMask, boolean useOverRule )
	{
		setOccupied();

		getBlender().blendImages( 	destination,
					source,
					sourceMask,
					opacity,
					blendMode,
					useOverRule );
		setNotOccupied();
	}
	
	public synchronized void doBlend( 	int frame,
						BufferedImage destination,
						BufferedImage source,
						ImageOperation sourceIOP,
						float opacity,
						int blendMode,
						int interpolation,
						BufferedImage sourceMask,
						boolean useOverCompositionRule )
	{
		setOccupied();

		AnimatedImageCoordinates coords = sourceIOP.getCoords();

		float x = coords.x.getValue( frame );
		float y = coords.y.getValue( frame );
		float xScale = coords.xScale.getValue( frame ) / AnimatedImageCoordinates.xScaleDefault;// normalize
		float yScale = coords.yScale.getValue( frame ) / AnimatedImageCoordinates.yScaleDefault;// normalize
		float xAnch = coords.xAnchor.getValue( frame );
		float yAnch = coords.yAnchor.getValue( frame );
		float rotation = coords.rotation.getValue( frame );
		opacity = opacity / 100.0f;// normalize, opacity value range 0 - 100

		getBlender().blendImages(	destination,
						source,
						x,
						y,
						blendMode,
						xScale,
						yScale,
						xAnch,
						yAnch,
						rotation,
						opacity,
						interpolation,
						sourceMask,
						useOverCompositionRule );
		setNotOccupied();
	}

	public synchronized void doCoordsBlend( 	int frame,
							BufferedImage destination,
							BufferedImage source,
							float x,
							float y,
							float xScale,
							float yScale,
							float xAnch,
							float yAnch,
							float rotation,
							float opacity,
							int blendMode,
							int interpolation,
							BufferedImage sourceMask,
							boolean useOverCompositionRule )
	{
		setOccupied();
		opacity = opacity / 100.0f;// normalize, opacity value range 0 - 100

		getBlender().blendImages(	destination,
						source,
						x,
						y,
						blendMode,
						xScale,
						yScale,
						xAnch,
						yAnch,
						rotation,
						opacity,
						interpolation,
						sourceMask,
						useOverCompositionRule );
		setNotOccupied();
	}

	public synchronized void doMotionBlurBlend( 	int frame,
							BufferedImage destination,
							BufferedImage source,
							ImageOperation sourceIOP,
							float opacity,
							int blendMode,
							int interpolation, 
							BufferedImage sourceMask,
							boolean useOverCompositionRule )
	{
		setOccupied();

		//--- Get 2D position and scale of image.
		AnimatedImageCoordinates coords = sourceIOP.getCoords();

		float[] times = createFrameTimeArray( frame, sourceIOP );

		float[] x = getValueArray( coords.x, times );
		float[] y = getValueArray( coords.y, times );
		float[] xScale = getValueArray( coords.xScale, times );
		divideArrayValues( xScale, AnimatedImageCoordinates.xScaleDefault );// normalize
		float[] yScale = getValueArray( coords.yScale, times );
		divideArrayValues( yScale, AnimatedImageCoordinates.xScaleDefault );// normalize
		float[] xAnch = getValueArray( coords.xAnchor, times );
		float[] yAnch = getValueArray( coords.yAnchor, times );
		float[] rotation = getValueArray( coords.rotation, times );
		opacity = opacity / 100.0f;// normalize, opacity value range 0 - 100

		boolean hasCutEdges = false;
		if( sourceIOP.switches != null && sourceIOP.switches.fineEdges == true )
			hasCutEdges = true;

		//--- blend
		getBlender().moveblendImages( 	destination,
						source,
						x,// in pix
						y,// in pix
						blendMode,// modes defined in this class
						xScale,// 0 - 1
						yScale,// 0 - 1
						xAnch,// in pix
						yAnch,// in pix
						rotation,// in degrees
						opacity,// 0 - 1
						interpolation,
						hasCutEdges,//if edges are cut step mask needs edges cut too  
						sourceMask,
						useOverCompositionRule );// tells if edges are cut
		setNotOccupied();
	}

	public synchronized void doFullScreenMotionBlurBlend( int frame,
							BufferedImage destination,
							PhantomPlugin plugin,
							ImageOperation sourceIOP,
							float opacity,
							int blendMode,
							BufferedImage sourceMask,
							boolean useOverCompositionRule )
	{
		setOccupied();

		//--- Get step image times.
		float[] times = createFrameTimeArray( frame, sourceIOP );
		//--- opacity for single renderpass if fraction of desired opacity.
		opacity = opacity / 100.0f;// normalize
		float passOpacity = opacity / times.length;
		//--- Init blender for fullscreen move blend.
		getBlender().initFullScreenMotionBlur();

		//--- Draw step images.
		for( int i = 0; i < times.length; i++ )
		{
			float frameTime = times[ i ];
			
			BufferedImage stepImage = PluginUtils.createTransparentScreenCanvas();
			Graphics2D g = stepImage.createGraphics();
			plugin.renderFullScreenMovingSource( frameTime, g, stepImage.getWidth(), stepImage.getHeight() );
			getBlender().drawFullScreenStepImage( stepImage, passOpacity );
		}
		//--- Do final blend.
		getBlender().doFullScreenMotionBlur( destination, sourceMask, blendMode, opacity, useOverCompositionRule );

		setNotOccupied();
	}

	//--- Draws a series of white images on top of each other to create motion blurred mask image.
	public synchronized BufferedImage doMotionBlurMask( 	int frame,
							PhantomPlugin plugin,
							ImageOperation maskIOP,
							float opacity)
	{
		setOccupied();

		//--- Get step image times.
		float[] times = createFrameTimeArray( frame, maskIOP );
		//--- opacity for single renderpass if fraction of desired opacity.
		opacity = opacity / 100.0f;// normalize
		float passOpacity = opacity / times.length;
		//--- Init blender for fullscreen move blend.
		getBlender().initFullScreenMotionBlur();

		//--- Draw step images.
		for( int i = 0; i < times.length; i++ )
		{
			float frameTime = times[ i ];
			
			BufferedImage stepImage = PluginUtils.createScreenCanvas();
			plugin.renderMask( frameTime, stepImage.createGraphics(), stepImage.getWidth(), stepImage.getHeight() );
			getBlender().drawFullScreenStepImage( stepImage, passOpacity );
		}

		BufferedImage img = getBlender().getCombinedDraw();

		setNotOccupied();

		//--- Returns combined draw to be used as BW mask image.
		return img;

	}

	//--- Makes sure that all times are in clip frame > 0 area.
	private static float[] createFrameTimeArray( int frame, ImageOperation iop )
	{
		Vector<Float> times = new Vector<Float>();

		float frameFract = (float) shutterAngle / 360.0f;
		float startTime = (float) frame - ( frameFract / 2.0f );
		float timeStep = frameFract / (float)( passes - 1 );

		for( int i = 0; i < passes; i++ )
		{
			float curTime = startTime + i * timeStep;

			int floorFrame = (new Double( Math.floor( (double) curTime ) )).intValue();
			int clipFrame = iop.getClipFrame( floorFrame );
			if( clipFrame < 0 ) continue;

			times.add( new Float( curTime ));
		}
	
		float[] rArray = new float[ times.size() ];
		for( int i = 0; i < times.size(); i++ )
		{
			rArray[ i ] = times.elementAt( i ).floatValue();
		}

		return rArray;
	}

	//--- Creates value array of AnimatedValues values for different times using passes and shutterAngle.
	private static float[] getValueArray( AnimatedValue val, float[] times  )
	{
		float[] rArray = new float[ times.length ];

		for( int i = 0; i < times.length; i++ )
			rArray[ i ] = val.getValue( times[ i ] );
		return rArray;
	}
	//--- For normalizing.
	private static void divideArrayValues( float[] array, float divider )
	{
		for( int i = 0; i < array.length; i++ )
			array[ i ] = array[ i ] / divider;
	}

}//end class
