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

import java.awt.Color;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.BooleanComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

import com.jhlabs.image.CrystallizeFilter;

public class CrystallizePlugin extends PhantomPlugin
{
	public AnimatedValue edgThick;
	public BooleanParam edgFade;
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	public CrystallizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Crystallize" );

		edgThick = new AnimatedValue( 0.4f );
		edgFade = new BooleanParam( false );
		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		red1.setParamName( "Fill Red" );
		green1.setParamName( "Fill Green" );
		blue1.setParamName( "Fill Blue" );

		registerParameter( edgThick );
		registerParameter( edgFade );
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor widthEdit = new AnimValueNumberEditor( "Edge thickness", edgThick );
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Shape Color", red1, green1, blue1 );
 		BooleanComboBox fadeEdit = new BooleanComboBox( edgFade, "Fade edges", "Yes","No", false );

		addEditor( widthEdit );
		addRowSeparator();
		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( fadeEdit );
	}

	public void doImageRendering( int frame )
	{
		CrystallizeFilter f = new CrystallizeFilter();
		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		
		f.setEdgeThickness(edgThick.getValue( frame ) );
		f.setFadeEdges(edgFade.get() );
		f.setEdgeColor(color1.getRGB() );

		applyFilter( f );
	}

}//end class
