package animator.phantom.renderer;

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

import giotto2D.filters.color.Invert;
import giotto2D.filters.merge.AlphaDifference;
import giotto2D.filters.merge.AlphaExclusion;
import giotto2D.filters.merge.AlphaIntersection;
import giotto2D.filters.merge.AlphaUnion;
import giotto2D.filters.merge.ImageToAlpha;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.blender.Blender;
import animator.phantom.controller.RenderModeController;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.GaussianFilter;

public class PluginMaskIOP extends ImageOperation
{
	public static final int ALPHAOP_MASK = 0;
	public static final int ALPHAOP_UNION = 1;
	public static final int ALPHAOP_INTERSECTION = 2;
	public static final int ALPHAOP_EXCLUSION = 3;
	public static final int ALPHAOP_DIFFERENCE = 4;

	public IntegerParam blur  = new IntegerParam( 0 );
	public IntegerParam maskOp = new IntegerParam( ALPHAOP_MASK );
	public BooleanParam invert = new BooleanParam( false );

	public PluginMaskIOP( PhantomPlugin plugin )
	{
		this.plugin = plugin;
		opacity = new AnimatedValue( this, 100.0f, 0.0f, 100.0f );//super class ImageOperation has this

		registerParameter( opacity );
		registerParameter( blur );
		registerParameter( maskOp );
		registerParameter( invert );
		setAsSource();
		setIOPToHaveSwitches( true );

		makeAvailableForLayerMasks = true;
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		//--- can't combine alphas if no input, hence flag, see below
		boolean wasInput = true;
		if( renderedImage == null )
		{
			renderedImage = PluginUtils.createScreenCanvas();
			wasInput = false;
		}

		//--- Get motion blur
		boolean motionblur = getCurrentMotionBlur();
		
		//--- Get mask
		BufferedImage maskImage = null;
		Graphics2D g = null;
		if( motionblur ) // draw motion blur mask
		{
			maskImage = Blender.doMotionBlurMask(	frame,
								plugin,
								this,
								opacity.getValue( frame ) );
			g = maskImage.createGraphics();
		}
		else // no motion blur
		{
			maskImage = PluginUtils.createCanvas( renderedImage.getWidth(), renderedImage.getHeight() );
			g = maskImage.createGraphics();
			g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity.getValue( frame ) / 100.0f ));
			plugin.renderMask( frame, g, maskImage.getWidth(), maskImage.getHeight()  );
			g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ));
		}

		//--- Blur 
		if( blur.get() != 0 )
		{
			GaussianFilter gaussianFilter = new GaussianFilter( blur.get() );
			gaussianFilter.setUseAlpha( false );
			g.drawImage( maskImage, gaussianFilter, 0, 0 );
		}
		g.dispose();//mask rendering done

		if( invert.get() )
			Invert.filter( maskImage );

		//-- Do user selected mask operation
		int maskOperation = maskOp.get();
		//--- If we don't have input union etc are not meaningful
		if( !wasInput ) maskOperation = ALPHAOP_MASK;

		if( maskOperation == ALPHAOP_MASK )
		{
			ImageToAlpha.filterFull( renderedImage, maskImage );
		}
		else
		{
			ImageToAlpha.filterFull( maskImage, maskImage );

			switch( maskOp.get() )
			{
				case ALPHAOP_UNION:
					AlphaUnion.filter( renderedImage, maskImage );
					break;

				case ALPHAOP_INTERSECTION:
					AlphaIntersection.filter( renderedImage, maskImage );
					break;

				case ALPHAOP_EXCLUSION:
					AlphaExclusion.filter( renderedImage, maskImage );
					break;

				case ALPHAOP_DIFFERENCE:
					AlphaDifference.filter( renderedImage, maskImage );
					break;

				default:
					System.out.println("default hit in MaskIOP.doImageRendering()" );
					break;
			}
		}
	}

	public ParamEditPanel getEditPanelInstance()
	{
		return plugin.getEditPanel(); 
	}

	//--- Overides iop motion blur with global if needed
	//--- Used for view editor rendering
	private boolean getCurrentMotionBlur()
	{
		boolean motionBlur = getMotionBlur();
		if( !RenderModeController.getGlobalMotionBlur() ) motionBlur = false;
		return motionBlur;
	}

	public ViewEditorLayer getEditorlayer()
	{
		return plugin.getEditorLayer();
	}

}//end class
