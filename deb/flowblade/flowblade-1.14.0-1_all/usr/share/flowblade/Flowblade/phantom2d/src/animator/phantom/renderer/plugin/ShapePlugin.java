package animator.phantom.renderer.plugin;

/*
    Copyright Janne Liljeblad

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

import giotto2D.core.GTTObject;
import giotto2D.core.GTTOval;
import giotto2D.core.GTTRectangle;
import giotto2D.core.GTTRoundRectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import animator.phantom.gui.view.editlayer.ShapeEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.FloatParam;
import animator.phantom.renderer.param.IntegerParam;

public class ShapePlugin extends PhantomPlugin
{
	//--- Unscaled width of shape, not user settable.
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;

	public IntegerParam shapeType = new IntegerParam( 0 );

	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;

	public IntegerParam lineWidth = new IntegerParam( 5 );
	public FloatParam roundness = new FloatParam( 10.0f );
	public BooleanParam fillOn = new BooleanParam( true );

	public ShapePlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "Shape" );
		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		red2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		green2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blue2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		red1.setParamName( "Fill Red" );
		green1.setParamName( "Fill Green" );
		blue1.setParamName( "Fill Blue" );
		
		red2.setParamName( "Line Red" );
		green2.setParamName( "Line Green" );
		blue2.setParamName( "Line Blue" );
		
		registerCoords();
		registerParameter( shapeType );
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
		registerParameter( lineWidth );
		registerParameter( roundness );
		registerParameter( fillOn );
	}

	public void buildEditPanel()
	{
		String[] options = { "Rectangle","Oval","Round rextangle" };
		IntegerComboBox shapeSelect = new IntegerComboBox( shapeType,
									"Mask shape",
									 options );
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Fill Color", red1, green1, blue1 );
		AnimColorRGBEditor colorEditor2 = new AnimColorRGBEditor( "Line Color", red2, green2, blue2 );
		IntegerNumberEditor lineEdit = new IntegerNumberEditor( "Line width", lineWidth );
		CheckBoxEditor doFill = new CheckBoxEditor( fillOn, "Fill shape", true );
		FloatNumberEditor roundEdit = new FloatNumberEditor( "Roundness", roundness, 10 );

		addEditor( shapeSelect );
		addRowSeparator();

		addCoordsEditors();

		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( colorEditor2 );
		addRowSeparator();
		addEditor( lineEdit );
		addRowSeparator();
		addEditor( roundEdit );
		addRowSeparator();
		addEditor( doFill );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		AnimatedImageCoordinates coords = getCoords();

		float x = coords.x.getValue( frameTime );
		float y = coords.y.getValue( frameTime );
		float xScale = coords.xScale.getValue( frameTime ) / AnimatedImageCoordinates.xScaleDefault;// normalize
		float yScale = coords.yScale.getValue( frameTime ) / AnimatedImageCoordinates.yScaleDefault;// normalize
		float xAnch = coords.xAnchor.getValue( frameTime );
		float yAnch = coords.yAnchor.getValue( frameTime );
		float rotation = coords.rotation.getValue( frameTime );

		GTTObject shape;
		if( shapeType.get() == 0 ) 
			shape = new GTTRectangle( WIDTH, HEIGHT );
		else if( shapeType.get() == 2 )
			shape =  new GTTRoundRectangle( (float)WIDTH, (float)HEIGHT, roundness.get());
		else 
			shape = new GTTOval( WIDTH, HEIGHT );

		Color color1 = new Color((int)red1.get(frameTime), (int)green1.get(frameTime), (int)blue1.get(frameTime) );
		Color color2 = new Color((int)red2.get(frameTime), (int)green2.get(frameTime), (int)blue2.get(frameTime) );
		
		shape.setScale( xScale, yScale );
		shape.setRotation( rotation );
		shape.setPos( x, y );
		shape.setAnchorPoint( xAnch, yAnch );
		shape.setFillPaint( color1 );
		shape.setFillVisible( fillOn.get() );
		shape.setStrokePaint( color2 );
		shape.setStrokeWidth( lineWidth.get() );

		shape.draw( g, new Rectangle( 0, 0, width,  height ));
	}
	
	public ViewEditorLayer getEditorLayer()
	{
 		return new ShapeEditLayer( this );
	}

}//end class