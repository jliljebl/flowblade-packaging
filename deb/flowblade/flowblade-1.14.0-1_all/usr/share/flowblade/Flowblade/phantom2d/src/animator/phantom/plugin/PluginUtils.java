package animator.phantom.plugin;

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

import giotto2D.filters.merge.AlphaReplace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.util.Vector;

import animator.phantom.blender.Blender;
import animator.phantom.renderer.EditorRendererInterface;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;

/**
* Utility methods used by plugins to create and modify images.
*/
public class PluginUtils
{

	public static final int NORMAL = 0;
	public static final int ADD = 1;
	public static final int LIGHTEN = 2;
	public static final int SCREEN = 3;
	public static final int SUBTRACT = 4;
	public static final int DARKEN = 5;
	public static final int MULTIPLY = 6;
	public static final int OVERLAY = 7;
	public static final int HARDLIGHT = 8;
	public static final int COLORBURN = 9;
	public static final int COLORDODGE = 10;

	public static final int ANIM_COLOR_RED = 0;//indexes in AnimatedValueVectorParam
	public static final int ANIM_COLOR_GREEN = 1;
	public static final int ANIM_COLOR_BLUE = 2;

	/**
	* Returns black image that is the same size as output movie screen. 
	*/
	public static BufferedImage createScreenCanvas()
	{
		return createCanvas( 	new Color( 0, 0, 0, 255 ), 
					EditorRendererInterface.getScreenDimensions().width,
					EditorRendererInterface.getScreenDimensions().height );
	}



	public static BufferedImage createFilterStackableCanvas( PhantomPlugin plugin )
	{
		if( plugin.getIOP().renderingFilterStack() == true )
		{
			Dimension d = plugin.getIOP().getFilterStackDimension();
			return createCanvas( 	new Color( 0, 0, 0, 255 ), 
						d.width,
						d.height );
		}

		return createScreenCanvas();
	}

	/**
	* Returns black image that is of the given size.
	* @param width Width of returned image.
	* @param height Height of retirned image.
	*/
	public static BufferedImage createCanvas( int width, int height )
	{
		return createCanvas( 	new Color( 0, 0, 0, 255 ), 
					width,
					height );
	}
	/**
	* Returns transparent black image that is the same size as output movie screen. 
	*/
	public static BufferedImage createTransparentScreenCanvas()
	{
		return createCanvas(  	new Color( 0, 0, 0, 0 ), 
					EditorRendererInterface.getScreenDimensions().width,
					EditorRendererInterface.getScreenDimensions().height );
	}
	/**
	* Returns transparent black image that is of the given size.
	* @param width Width of returned image.
	* @param height Height of retirned image.
	*/
	public static BufferedImage createTransparentCanvas( int width, int height )
	{
		return createCanvas(  new Color( 0, 0, 0, 0 ), width, height );
	}
	/**
	* Returns colored image that is of the given size.
	* @param c Color of returned image.
	* @param w Width of returned image.
	* @param h Height of retirned image.
	*/
	public static BufferedImage createCanvas( Color c, int w, int h )
	{
		BufferedImage rImg = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		Graphics2D gc = rImg.createGraphics();
		gc.setColor( c );
		gc.fillRect( 0, 0, w, h);
		gc.dispose();
		return rImg;
	}
	/**
	* Returns black image of given size with alpha that is the intersection of source and transparent created image.
	* @param w Width of returned image.
	* @param h Height of retirned image.
	* @param source Alpha source image.
	*/
	public static BufferedImage getAlphaCopy( int w, int h, BufferedImage source )
	{
		BufferedImage alphaCopy = createCanvas( new Color( 0, 0, 0, 0 ), w, h );
		AlphaReplace.filterIntersection( alphaCopy, source );
		return alphaCopy;
	}
	/**
	* Filters image with provided filer.
	* @param fTarget Image to be filtered.
	* @param filter Image filter.
	*/
	public static void filterImage( BufferedImage fTarget, BufferedImageOp filter )
	{
		Graphics2D gc = fTarget.createGraphics();
		gc.drawImage( fTarget, filter, 0, 0 );
		gc.dispose();
	}
	/**
	* Returns screen size of output movie.
	*/
	public static Dimension getScreenSize()
	{
		return EditorRendererInterface.getScreenDimensions();
	}
	/**
	* Clones image.
	* @param source Clone source image.
	*/
	public static BufferedImage getImageClone( BufferedImage src )
	{
		ColorModel cm = src.getColorModel();
		return new BufferedImage( cm, src.copyData( null ), cm.isAlphaPremultiplied(), null);
	}

	/**
	*Blendes source image on destination so that images are top left aligned.
	* @param destination Blend destination image
	* @param source Blend source image
	* @param opacity Opacity in normalized range 0.0 - 1.0
	* @param blendMode Blenmode such as PluginUtils.COLORDODGE.
	*/
	public static void doAlignedBlend( BufferedImage destination, BufferedImage source, float opacity, int blendMode )
	{
		Blender.doAlignedBlend( destination, source, opacity, blendMode, null );
	}

	public static AnimatedValueVectorParam getAnimatedColorParam( PhantomPlugin plugin )
	{
		AnimatedValueVectorParam color = new AnimatedValueVectorParam();
		AnimatedValue red = new AnimatedValue( plugin.getIOP(), 128 );
		AnimatedValue green = new AnimatedValue( plugin.getIOP(), 128 );
		AnimatedValue blue = new AnimatedValue(  plugin.getIOP(), 128 );

		Vector<AnimatedValue> v = new Vector<AnimatedValue> ();
		v.add( red );
		v.add( green );
		v.add( blue );
		color.set( v );
		
		return color;
	}

	public static Color getAnimatedColor( AnimatedValueVectorParam param, int frame )
	{
		AnimatedValue red = param.elem( ANIM_COLOR_RED );
		AnimatedValue green = param.elem( ANIM_COLOR_GREEN );
		AnimatedValue blue = param.elem( ANIM_COLOR_BLUE );
		return new Color( (int) red.get( frame ), (int) green.get( frame ), (int) blue.get( frame ) );
	}

}//end class