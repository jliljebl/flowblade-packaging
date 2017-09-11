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

import giotto2D.filters.noise.ScatterRGB;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

public class ScatterRGBPlugin extends PhantomPlugin
{
	private BooleanParam correlated;
	private BooleanParam independent;
	private BooleanParam animated;
	private AnimatedValue RNoise;
	private ScatterRGB noise;
	
	public ScatterRGBPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "ScatterRGB" );
		noise = new ScatterRGB();
		correlated = new BooleanParam( false );
		independent = new BooleanParam( false );
		animated = new BooleanParam( false );
		RNoise = new AnimatedValue( 20, 0, 100 );

		registerParameter( correlated );
		registerParameter( independent );
		registerParameter( animated );
		registerParameter( RNoise );
	}

	public void buildEditPanel()
	{
		CheckBoxEditor correlatedEdit = new CheckBoxEditor( correlated, "Correlated noise", true );
		CheckBoxEditor independentEdit = new CheckBoxEditor( independent, "Intependent RGB", true );
		CheckBoxEditor animEdit = new CheckBoxEditor( animated, "Animate noise", true );
		AnimValueNumberEditor rEdit = new  AnimValueNumberEditor( "Amount", RNoise );

		addEditor( animEdit );
		addRowSeparator();
		addEditor( correlatedEdit );
		addRowSeparator();
		addEditor( independentEdit );
		addRowSeparator();
		addEditor( rEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImg = getFlowImage();
		BufferedImage filteredImage = PluginUtils.createScreenCanvas();

		noise.setAnimated( animated.get() );
		noise.setCorrelated( correlated.get() );
		noise.setIndependent( independent.get() );
		noise.setNoise( RNoise.get(frame) / 100.0, 0.0, 0.0 );
		noise.filter( flowImg, filteredImage );

		sendFilteredImage( filteredImage, frame );
	}

}//end class
