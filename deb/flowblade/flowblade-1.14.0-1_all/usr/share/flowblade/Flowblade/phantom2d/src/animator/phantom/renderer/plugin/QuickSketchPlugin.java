package animator.phantom.renderer.plugin;

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

import giotto2D.filters.color.Desaturate;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.GaussianFilter;

public class QuickSketchPlugin extends PhantomPlugin
{
	private IntegerParam blur;
	private BooleanParam desaturate;

	public QuickSketchPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "QuickSketch" );

		blur = new IntegerParam( 10, 2, 200 );
		desaturate = new BooleanParam(true);

		registerParameter( blur );
		registerParameter( desaturate );
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor blurEdit = new IntegerValueSliderEditor( "Blur", blur );
		CheckBoxEditor desaturateEdit = new CheckBoxEditor(desaturate, "Desaturate", true); 

		addEditor( blurEdit );
		addRowSeparator();
		addEditor( desaturateEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();
		if( desaturate.get() == true )
			Desaturate.filter( flowImg );

		BufferedImage dodgeLayer = PluginUtils.getImageClone( flowImg );
		GaussianFilter f =  new GaussianFilter();
		f.setRadius( (float) blur.get() );
		Graphics2D gc = dodgeLayer.createGraphics();
		gc.drawImage( dodgeLayer, f, 0, 0);
		gc.dispose();

		PluginUtils.doAlignedBlend( flowImg, dodgeLayer, 1.0f, PluginUtils.COLORDODGE );

		sendFilteredImage( flowImg, frame );
	}

}//end class
