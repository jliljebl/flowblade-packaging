package animator.phantom.renderer.plugin;

/*
    Copyright Janne Liljeblad.

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

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.WoodFilter;

public class WoodPlugin extends PhantomPlugin
{
	private AnimatedValue fibres;
	private AnimatedValue angle;
	private AnimatedValue stretch;
	private AnimatedValue scale;
	private AnimatedValue turbulence;
	private AnimatedValue gain;

	public WoodPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "Wood" );

		fibres = new AnimatedValue( 0, 0, 100 );
		angle = new AnimatedValue( 0, 0, 90 );
		stretch = new AnimatedValue( 10, 1, 100 );
		scale = new AnimatedValue( 200, 1, 1000 );
		turbulence = new AnimatedValue( 0, 0, 200 );
		gain = new AnimatedValue( 8, 1, 10 );

		registerParameter( fibres );
		registerParameter( angle );
		registerParameter( stretch );
		registerParameter( scale );
		registerParameter( turbulence );
		registerParameter( gain );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor fEdit = new  AnimValueSliderEditor( "Fibres", fibres );
		AnimValueSliderEditor aEdit = new  AnimValueSliderEditor( "Angle", angle );
		AnimValueSliderEditor sEdit = new  AnimValueSliderEditor( "Stretch", stretch );
		AnimValueSliderEditor scEdit = new  AnimValueSliderEditor( "Scale", scale );
		AnimValueSliderEditor tEdit = new  AnimValueSliderEditor( "Turbulence", turbulence );
		AnimValueSliderEditor gEdit = new  AnimValueSliderEditor( "Gain", gain );

		addEditor( fEdit );
		addRowSeparator();
		addEditor( aEdit );
		addRowSeparator();
		addEditor( sEdit );
		addRowSeparator();
		addEditor( scEdit );
		addRowSeparator();
		addEditor( tEdit );
		addRowSeparator();
		addEditor( gEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		WoodFilter f = new WoodFilter();
		f.setFibres( fibres.get( frame ) / 20.0f );
		f.setAngle( angle.get( frame) );
		f.setStretch( stretch.get( frame ) );
		f.setScale( scale.get( frame ) );
		f.setTurbulence( turbulence.get( frame ) / 100.0f );
		f.setGain( gain.get( frame ) / 10.0f );
		PluginUtils.filterImage( img, f );

		sendStaticSource( img, frame );
	}

}//end class

