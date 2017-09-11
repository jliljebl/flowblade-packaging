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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.blender.Blender;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

public class PluginFullScreenMovingSourceIOP extends RenderModeIOP
{
	public BooleanParam useOverRule = new BooleanParam( false );

	public PluginFullScreenMovingSourceIOP( PhantomPlugin plugin )
	{
		this.plugin = plugin;

		opacity = new AnimatedValue( this, 100.0f, 0.0f, 100.0f );//ImageOperation has this parameter
		doInputMaskCombineBlend = true;

		registerParameter( opacity );
		registerParameter( useOverRule );

		setAsSource();
		setIOPToHaveSwitches( false );
	}

	public ViewEditorLayer getEditorlayer()
	{
		return plugin.getEditorLayer();
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		//--- if leaf, create rendered image
		if( renderedImage == null )
		{
			if( backgroundType.get() == BLACK_BACKGROUND )
				renderedImage = PluginUtils.createScreenCanvas();
			else
				renderedImage = PluginUtils.createTransparentScreenCanvas();
		}

		boolean useOver = useOverRule.get();
		// Transparent always needs over rule.
		if( backgroundType.get() == TRANSPARENT_BACKGROUND )
			useOver = true;

		if ( getCurrentMotionBlur() )
		{
			Blender.doFullScreenMotionBlurBlend( 	
							frame,
							renderedImage,
							plugin,//blender does callback to do image rendering
							this,
							opacity.getValue( frame ),
							getBlendMode(),
							null,
							useOver );
		}
		else //no motion blur
		{
			BufferedImage source = PluginUtils.createTransparentScreenCanvas();
			Graphics2D g = source.createGraphics();
			plugin.renderFullScreenMovingSource( (float) frame, g, source.getWidth(), source.getHeight() );
			source = applyFilterStack( frame, this, source );
			Blender.doAlignedBlend( renderedImage, source, opacity.getValue( frame ) / 100.0f , getBlendMode(), null, useOver );
		}
	}

	public ParamEditPanel getEditPanelInstance()
	{
		return plugin.getEditPanel(); 
	}


}//end class
