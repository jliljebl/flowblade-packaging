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

import giotto2D.filters.render.SolidNoise;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

public class SolidNoisePlugin extends PhantomPlugin
{
	private BooleanParam turbulent;
	private BooleanParam tilable;
	private IntegerParam detail;
	private IntegerParam xSize;
	private IntegerParam ySize;
 	private IntegerParam seed;
	private BooleanParam animate;
	private IntegerParam changeInterval;

	public SolidNoisePlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "SolidNoise" );

		turbulent = new BooleanParam( false );
		detail = new IntegerParam(1, 1, 4 );
		xSize = new IntegerParam(8, 0, 16 );
		ySize = new IntegerParam(8, 0, 16 );
		seed = new IntegerParam( 12345678 );
		tilable = new BooleanParam( false );
		animate = new BooleanParam( false );
		changeInterval = new IntegerParam( 10 ); 

		registerParameter( turbulent );
		registerParameter( detail );
		registerParameter( xSize );
		registerParameter( ySize );
		registerParameter( seed );
		registerParameter( tilable );
		registerParameter( animate );
		registerParameter( changeInterval );
	}

	public void buildEditPanel()
	{
		CheckBoxEditor turbulentEdit = new CheckBoxEditor( turbulent, "Turbulent", true );
		IntegerValueSliderEditor detailEdit = new IntegerValueSliderEditor( "Detail", detail );
		detailEdit.setSnapToTicks( true );
		IntegerValueSliderEditor xSizeEdit = new IntegerValueSliderEditor( "X size", xSize );
		IntegerValueSliderEditor ySizeEdit = new IntegerValueSliderEditor( "Y size", ySize );
		IntegerNumberEditor seedEdit = new IntegerNumberEditor( "Seed", seed );
		CheckBoxEditor tilableEdit = new CheckBoxEditor( tilable, "Tilable", true );
		IntegerNumberEditor intervalEdit = new IntegerNumberEditor( "Change inteval", changeInterval );
		CheckBoxEditor animateEdit = new CheckBoxEditor( animate, "Animate", true );

		addEditor( turbulentEdit );
		addRowSeparator();
		addEditor( detailEdit );
		addRowSeparator();
		addEditor( xSizeEdit );
		addRowSeparator();
		addEditor( ySizeEdit );
		addRowSeparator();
		addEditor( seedEdit );
		addRowSeparator();
		addEditor( tilableEdit );
		addRowSeparator();
		addEditor( animateEdit );
		addRowSeparator();
		addEditor( intervalEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );
		
		SolidNoise sn = new SolidNoise();
		sn.setTurbulence( turbulent.get() );
		sn.setDetail( detail.get() );
		sn.setXSize( xSize.get() );
		sn.setYSize( ySize.get() );
		if( !animate.get() )
			sn.setSeed( seed.get() );
		else
		{
			int clipFrame = getClipFrame( frame );
			int interval = changeInterval.get();
			if( interval < 1 )
				interval = 1;
			int currentSeed = seed.get() + ( (clipFrame / interval ) * 1 );
			System.out.println( seed.get() );
			sn.setSeed( currentSeed );
			System.out.println( currentSeed );
		}
		sn.setTilable( tilable.get() );
		sn.filter( img );

		sendStaticSource( img, frame );
	}

}//end class
