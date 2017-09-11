package animator.phantom.renderer.imagemerge;

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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.blender.Blender;
import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.view.editlayer.MergeEditLayer;
import animator.phantom.paramedit.imagemerge.BasicTwoMergeEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.RenderModeIOP;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

public class BasicTwoMergeIOP extends RenderModeIOP
{
	private Dimension mergeSize = null;
	public BooleanParam useOverRule = new BooleanParam( false );

	public BasicTwoMergeIOP()
	{
		name = "Merge";

		opacity = new AnimatedValue( this, 100.0f, 0.0f, 100.0f );
		registerCoords( new AnimatedImageCoordinates( this ) );
		registerParameter( opacity );
		registerParameter( useOverRule );
		setIOPToHaveSwitches();
		setMaskInput( false );
		setCenterable();
	}

	public ParamEditPanel getEditPanelInstance()
	{
		return new BasicTwoMergeEditPanel( this );
	}
	
	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		BufferedImage mergeImage = (BufferedImage) sourceImages.elementAt( 1 );

		handleMergeSize( mergeImage );

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
		
		//--- Cuts alha edges if requested
		handleSmoothEdges( mergeImage );

		float opacityval = opacity.getValue( frame );

		//--- Get render params
		boolean motionBlur = getCurrentMotionBlur();
		int blendMode = getBlendMode();
		int interpolation = getCurrentInterpolation();

		//--- Do blend
		if( motionBlur )
			Blender.doMotionBlurBlend( frame, renderedImage, mergeImage, this, opacityval, blendMode, interpolation, null, useOverRule.get() );
		else
			Blender.doBlend( frame, renderedImage, mergeImage, this, opacityval, blendMode, interpolation, null, useOverRule.get() );
	}

	//--- This is called always when rendered.
	//--- It updates the MergeEditLayer for size of image being merged
	//--- if mergesize has changed.
	//--- This relies on the fact that full render is called always after flow changes
	//--- CHECK IF WORKS WHEN view ediitor view mode is not Flow View
	private void handleMergeSize( BufferedImage mImg )
	{
		if( mImg == null )
		{
			if( mergeSize != null )
			{
				mergeSize = null;
				GUIComponents.viewEditor.removeLayer( this );
			}
			return;
		}

		Dimension mSize = new Dimension( mImg.getWidth(), mImg.getHeight() );
		if( mergeSize == null ||
			( mSize.width != mergeSize.width || mSize.height != mergeSize.height ))
		{
			mergeSize = mSize;
			GUIComponents.viewEditor.replaceEditlayer( 
				new MergeEditLayer( this ) );
			GUIComponents.viewEditor.updateLayerSelector();
		}
	}

	public Rectangle getImageSize()
	{
		if( mergeSize == null )
			return null;
		else
			return new Rectangle( mergeSize.width, mergeSize.height);
	}

}//end class

