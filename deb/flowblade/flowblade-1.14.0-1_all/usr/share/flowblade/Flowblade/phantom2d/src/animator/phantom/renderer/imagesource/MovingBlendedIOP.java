package animator.phantom.renderer.imagesource;

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

import giotto2D.filters.merge.DataCopy;
import giotto2D.filters.transform.FlipFilter;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import animator.phantom.blender.Blender;
import animator.phantom.paramedit.FlipSelect;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.RenderModeIOP;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

//--- An abstract base class for iops that blend moving bitmap images
public abstract class MovingBlendedIOP extends RenderModeIOP
{
	public IntegerParam flipTrans = new IntegerParam( 0 );
	public BooleanParam asCanvas = new BooleanParam( false );
	public BooleanParam useOverRule = new BooleanParam( false );

	protected void registerMovingBlendParams()
	{
		opacity = new AnimatedValue( this, 100.0f, 0.0f, 100.0f );
		doInputMaskCombineBlend = true;

		registerParameter( opacity );
		registerParameter( asCanvas );
		registerParameter( flipTrans );
		registerParameter( useOverRule );
	}

	//--- Blends images using params registered in this and super class
	public void renderMovingBlendedImage( int frame, BufferedImage img )
	{
		if( img == null )
		{
			System.out.println( getName() + " was not rendered." );
			return;
		}

		//--- If there are filters in stack, copys img, apllies filters and returns ref to copy.
		img = applyFilterStack( frame, this, img );

		//--- Canvas is canvas, Mask input discarded.
		if( asCanvas.get() )
		{
			renderedImage = PluginUtils.createCanvas( img.getWidth(), img.getHeight() );
			DataCopy.copy( img, renderedImage ); 
			return;
		}

		//--- If image received was null, create new destination image.
		if( renderedImage == null )
			renderedImage = PluginUtils.createScreenCanvas();

		//--- Do flip
		if( flipTrans.get() != FlipSelect.NONE )
		{
			if( flipTrans.get() == FlipSelect.HORIZONTAL ) img = FlipFilter.filter( img, false, true );
			if( flipTrans.get() == FlipSelect.VERTICAL ) img = FlipFilter.filter( img, true, false );
		}

		//--- Cuts alpha edges if needed
		handleSmoothEdges( img );

		float opacityval = opacity.getValue( frame );

		//--- Get basic render params.
		boolean motionBlur = getCurrentMotionBlur();
		int blendMode = getBlendMode(); 
		int interpolation = getCurrentInterpolation();

		//--- ScreenSize is max size for renderedImage ( = destination )
		cropRenderedImageToScreenSize();

		//--- Non-null mask must be made same size as blend destination ( = renderedImage ) 
		BufferedImage mask = null;

		//--- Do blend
		if( motionBlur )
			Blender.doMotionBlurBlend( frame, renderedImage, img, this, opacityval, blendMode, interpolation, mask, useOverRule.get());
		else
			Blender.doBlend( frame, renderedImage, img, this, opacityval, blendMode, interpolation, mask, useOverRule.get() );
	}

	//--- Used by view editor layers
	public Rectangle getImageSize() 
	{
		int w = getFileSource().getImageWidth();
		int h = getFileSource().getImageHeight();
		return new Rectangle( w, h );
	}

	// calling this method on object makes this object useless as coords lose to this, and
	// filter stack is just moved
	public void cloneValuesToReplacement( MovingBlendedIOP mIop )
	{
		mIop.flipTrans.set( flipTrans.get() );
		mIop.asCanvas.set( asCanvas.get() );
		mIop.useOverRule.set( useOverRule.get() );

		mIop.registerCoords( getCoords() );
		mIop.loadClipValues( getMaxLength(), getBeginFrame(), getClipStartFrame(), getClipEndFrame() );
		mIop.setFilterStack( getFilterStack() );

		mIop.blendMode.set( blendMode.get() );
		mIop.setOnOffState( isOn() );
		mIop.backgroundType.set( backgroundType.get() );

		mIop.setLooping( getLooping() );
		mIop.setParentMover( parentMoverType, parentNodeID, null );//parent iop set later

		mIop.setLocked( getLocked()); 
	}

}//end class
