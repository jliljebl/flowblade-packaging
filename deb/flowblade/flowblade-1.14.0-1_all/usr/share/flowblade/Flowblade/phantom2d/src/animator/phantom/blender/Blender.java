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

import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.controller.RenderModeController;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.ImageOperation;

public class Blender
{
	private static Vector<SynchronizedImageBlender> blenders = null;
	private static int lastGivenBlender = 0;

	private static int passes = 7;
	public static int[] selectablePasses = { 3, 5, 7, 9 };//used by gui
	public static String[] selectablePassesOpts = { "3","5","7","9" };

	private static int shutterAngle = 150;
	public static int minShutterAngle = 10;//used by gui
	public static int maxShutterAngle = 180;//used by gui

	//------------------------------------------------------------ blender handling
	//--- Called after project changed.
	public static void initBlenders()
	{
		blenders = new Vector<SynchronizedImageBlender>();
		for( int i = 0; i < RenderModeController.getBlendersCount(); i++ )
			blenders.add( new SynchronizedImageBlender() );
	}

	private static SynchronizedImageBlender getBlender()
	{
		if( blenders == null ) initBlenders();
		for( int i = 0; i < blenders.size(); i++ )
		{
			if( blenders.elementAt( i ).isOccupied() == false )
			{
				lastGivenBlender = i;
				System.out.println("given blender:" + i );
				return blenders.elementAt( i );
			}
		}

		lastGivenBlender++; //race between these 2-3 lines ? if you null pointer for blender then fix
		if( lastGivenBlender == blenders.size() )
			lastGivenBlender = 0;
		//System.out.println("given blender:" + lastGivenBlender );
		return blenders.elementAt( lastGivenBlender );
	}

	//------------------------------------------------------------- global render params
	//--- Get and set for motion blur  passes.
	public static void setPasses( int passes_ )
	{ 
		passes = passes_; 
		SynchronizedImageBlender.setPasses( passes );
		
	}
	public static int getPasses(){ return passes; }
	//--- Get and set for shutter angle.
	public static void setShutterAngle( int shutterAngle_ )
	{
		if( shutterAngle_ < minShutterAngle ) shutterAngle = minShutterAngle;
		else if( shutterAngle_ > maxShutterAngle ) shutterAngle = maxShutterAngle;
		else shutterAngle = shutterAngle_;
		SynchronizedImageBlender.setShutterAngle( shutterAngle );
	}
	public static int getShutterAngle(){ return shutterAngle; }

	//------------------------------------------------------------------------------ BLEND FUNCTIONS
	//--- Blends two stationary topleft aligned images.
	//--- NOTE: OPACITY NEEDS TO BE NORMALIZED BEFORE CALLING THIS
	public static void doAlignedBlend( BufferedImage destination, BufferedImage source, float opacity, int blendMode, BufferedImage sourceMask )
	{
		getBlender().doAlignedBlend( destination, source, opacity, blendMode, sourceMask );
	}
	//--- Blends two stationary topleft aligned images.
	//--- NOTE: OPACITY NEEDS TO BE NORMALIZED BEFORE CALLING THIS
	public static void doAlignedBlend( BufferedImage destination, BufferedImage source, float opacity, int blendMode, BufferedImage sourceMask, boolean useOverRule )
	{
		getBlender().doAlignedBlend( destination, source, opacity, blendMode, sourceMask, useOverRule );
	}
	//--- Blends two images.
	public static void doBlend( 	int frame,
					BufferedImage destination,
					BufferedImage source,
					ImageOperation sourceIOP,
					float opacity,
					int blendMode,
					int interpolation,
					BufferedImage sourceMask,
					boolean useOverCompositionRule )
	{
		getBlender().doBlend( 	frame, destination, source, sourceIOP, opacity, blendMode, interpolation, sourceMask, useOverCompositionRule );
	}
	//--- Blends two images using coordinates values.
	public static void doCoordsBlend( 	int frame,
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
		getBlender().doCoordsBlend( frame, destination, source, x, y, xScale, yScale, xAnch, yAnch, rotation, opacity, blendMode, interpolation, sourceMask, useOverCompositionRule );
	}
	//--- Blends two images with motion blur applied to source.
	public static void doMotionBlurBlend( 	int frame,
						BufferedImage destination,
						BufferedImage source,
						ImageOperation sourceIOP,
						float opacity,
						int blendMode,
						int interpolation, 
						BufferedImage sourceMask,
						boolean useOverCompositionRule )
	{

		getBlender().doMotionBlurBlend( frame, destination, source, sourceIOP, opacity, blendMode, interpolation, sourceMask, useOverCompositionRule );
	}

	//--- Blends SVGImage on destination image with motion blur applied to source.
	public static void doFullScreenMotionBlurBlend( int frame,
							BufferedImage destination,
							PhantomPlugin plugin,
							ImageOperation sourceIOP,
							float opacity,
							int blendMode,
							BufferedImage sourceMask,
							boolean useOverCompositionRule )
	{
		getBlender().doFullScreenMotionBlurBlend( frame, destination, plugin, sourceIOP, opacity, blendMode, sourceMask, useOverCompositionRule );
	}

	//--- Draws a series of white images on top of each other to create motion blurred mask image.
	public static BufferedImage doMotionBlurMask( 	int frame,
							PhantomPlugin plugin,
							ImageOperation maskIOP,
							float opacity)
	{
		return getBlender().doMotionBlurMask( frame, plugin, maskIOP, opacity );
	}


	//--- draw 1 pix wide transparent edge using blender
	public static void cutEdges( BufferedImage img )
	{
		ImageBlender.cutAlphaEdges( img );
	}

	public static String[] getBlendModes(){ return ImageBlender.blendModes; }

}//end class
