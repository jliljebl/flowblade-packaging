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

import giotto2D.filters.merge.ImageToAlpha;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.blender.Blender;
import animator.phantom.controller.RenderModeController;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.GaussianFilter;

public class PluginMergeIOP extends ImageOperation
{
	public IntegerParam blur  = new IntegerParam( 0 );
	//public boolean useRenderedOpacity = false;

	public PluginMergeIOP( PhantomPlugin plugin )
	{
		this.plugin = plugin;

		opacity = new AnimatedValue( this, 100.0f, 0.0f, 100.0f );
		registerParameter( opacity );
		registerParameter( blur );
		setIOPToHaveSwitches( true );
	}

	public ViewEditorLayer getEditorlayer()
	{
		return plugin.getEditorLayer();
	}

	public ParamEditPanel getEditPanelInstance()
	{
		return plugin.getEditPanel(); 
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		BufferedImage mergeImage = (BufferedImage) sourceImages.elementAt( 1 );

		//--- Check input
		if( renderedImage == null &&  mergeImage == null )
		{
			System.out.println(" renderedImage == null &&  mergeImage == null ");
			return;
		}
		if( renderedImage == null &&  mergeImage != null )
		{
			System.out.println("renderedImage == null &&  mergeImage != null");
			renderedImage = mergeImage;
			return;
		}
		if( renderedImage != null && mergeImage == null )
		{
			System.out.println(" renderedImage != null && mergeImage == null");
			return;
		}
		System.out.println("merge OK");
		//--- ScreenSize is max size for renderedImage ( = destination )
		cropRenderedImageToScreenSize();

		float opacityVal = opacity.getValue( frame );
		int blendMode = getBlendMode();

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
								100.0f );//opacity handled at last line of this method
			g = maskImage.createGraphics();
		}
		else // no motion blur
		{
			maskImage = PluginUtils.createCanvas( renderedImage.getWidth(), renderedImage.getHeight() );
			g = maskImage.createGraphics();
			plugin.renderMask( frame, g, maskImage.getWidth(), maskImage.getHeight()  );
		}

		//--- Blur 
		int blurval = Math.round( blur.get() );
		if( blurval != 0 )
		{
			GaussianFilter gaussianFilter = new GaussianFilter( blurval );
			gaussianFilter.setUseAlpha( false );
			g.drawImage( maskImage, gaussianFilter, 0, 0 );
			g.dispose();//mask rendering done
		}

		ImageToAlpha.filterFromRed( mergeImage, maskImage, new Rectangle( 0, 0, mergeImage.getWidth(), mergeImage.getHeight() ));

		opacityVal = opacityVal / 100.0f;
		//if( useRenderedOpacity )
		//	opacityVal = 1.0f;

		Blender.doAlignedBlend( renderedImage, mergeImage, opacityVal, blendMode, null );
	}

	//--- Overides iop motion blur with global if needed
	private boolean getCurrentMotionBlur()
	{
		boolean motionBlur = getMotionBlur();
		if( !RenderModeController.getGlobalMotionBlur() ) motionBlur = false;
		return motionBlur;
	}
}//end class