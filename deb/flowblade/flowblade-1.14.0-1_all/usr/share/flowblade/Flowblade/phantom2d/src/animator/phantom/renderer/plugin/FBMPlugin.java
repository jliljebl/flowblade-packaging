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
import java.util.Vector;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.FBMFilter;

public class FBMPlugin extends PhantomPlugin
{
	private AnimatedValue angle;
	private AnimatedValue stretch;
	private AnimatedValue scale;
	private AnimatedValue amount;
	private AnimatedValue gain;
	private AnimatedValue bias;
	private AnimatedValue H;
	private AnimatedValue octaves;
	private AnimatedValue lacunarity;
	private IntegerParam type;

	private static final String FBM = "Pattern";
	private static final String LIGHTNESS = "Lightness";
	private static final String FUNCTION = "Function";

	public FBMPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "FractalBrownian" );

		angle = new AnimatedValue( 0, 0, 360 );
		stretch = new AnimatedValue( 50, 5, 200 );
		scale = new AnimatedValue( 50, 2, 500 );
		amount = new AnimatedValue( 100, 0, 200 );
		gain = new AnimatedValue( 20, 1, 99 );
		bias = new AnimatedValue( 50, 0, 100 );
		H = new AnimatedValue( 25, 0, 200 );
		octaves = new AnimatedValue( 45, 0, 100 );
		lacunarity = new AnimatedValue( 200, 0 , 400 );
		type = new IntegerParam( FBMFilter.NOISE );

		registerParameter( angle );
		registerParameter( stretch );
		registerParameter( scale );
		registerParameter( amount );
		registerParameter( gain );
		registerParameter( bias );
		registerParameter( H );
		registerParameter( octaves );
		registerParameter( lacunarity );
		registerParameter( type );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor aEdit = new  AnimValueSliderEditor( "Angle", angle );
		AnimValueSliderEditor sEdit = new  AnimValueSliderEditor( "Stretch", stretch );
		AnimValueSliderEditor scEdit = new  AnimValueSliderEditor( "Scale", scale );
		AnimValueSliderEditor tEdit = new  AnimValueSliderEditor( "Amount", amount );
		AnimValueSliderEditor gEdit = new  AnimValueSliderEditor( "Gain", gain );
		AnimValueSliderEditor bEdit = new  AnimValueSliderEditor( "Bias", bias );
		AnimValueSliderEditor oEdit = new  AnimValueSliderEditor( "Octaves", octaves );
		AnimValueSliderEditor lEdit = new  AnimValueSliderEditor( "Lacunarity", lacunarity );
		AnimValueSliderEditor HEdit = new  AnimValueSliderEditor( "H", H );
		String[] options = {"Noise", "Ridged Noise", "Distorted Noise", "Convolution Noise", "Cellular" };
		IntegerComboBox typeEdit = new IntegerComboBox( type, "Pattern", options );

		Vector<String> paneNames = new Vector<String>();
		paneNames.add( FBM );
		paneNames.add( LIGHTNESS );
		paneNames.add( FUNCTION );

		setTabbedPanel( 200, paneNames );

		addToTab(FBM, aEdit );
		addToTab(FBM, new RowSeparator() );
		addToTab(FBM, sEdit );
		addToTab(FBM, new RowSeparator() );
		addToTab(FBM, scEdit );

		addToTab(LIGHTNESS, tEdit );
		addToTab(LIGHTNESS, new RowSeparator() );
		addToTab(LIGHTNESS, gEdit );
		addToTab(LIGHTNESS, new RowSeparator() );
		addToTab(LIGHTNESS, bEdit );

		addToTab(FUNCTION, typeEdit );
		addToTab(FUNCTION, new RowSeparator() );
		addToTab(FUNCTION, oEdit );
		addToTab(FUNCTION, new RowSeparator() );
		addToTab(FUNCTION, lEdit );
		addToTab(FUNCTION, new RowSeparator() );
		addToTab(FUNCTION, HEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		FBMFilter f = new FBMFilter();
		f.setAngle( (float) Math.toRadians( (double) angle.get( frame ) ));
		f.setStretch( stretch.get( frame ) / 50.0f );
		f.setScale( scale.get( frame ) );
		f.setAmount( amount.get( frame ) / 100.0f );
		f.setGain( gain.get( frame ) / 100.0f );
		f.setBias( bias.get( frame ) / 100.0f );
		f.setOctaves( octaves.get( frame ) / 10.0f );
		f.setH( H.get( frame ) / 50.0f );
		f.setLacunarity( lacunarity.get( frame ) / 10.0f );
		f.setBasisType( type.get() );
		PluginUtils.filterImage( img, f );

		sendStaticSource( img, frame );
	}

}//end class

