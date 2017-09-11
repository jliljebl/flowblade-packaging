package animator.phantom.renderer.plugin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import animator.phantom.gui.view.editlayer.PolyCurveEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

public class PolyCurveShapePlugin extends PolyCurvePlugin
{
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;
	
	public IntegerParam lineWidth = new IntegerParam( 5 );
	public BooleanParam fillOn = new BooleanParam( true );

	public PolyCurveShapePlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "CurveShape" );

		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		red2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		green2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blue2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );

		red1.setParamName( "Red Fill" );
		green1.setParamName( "Green Fill" );
		blue1.setParamName( "Blue Fill" );
		
		red2.setParamName( "Red Stroke" );
		green2.setParamName( "Green Stroke" );
		blue2.setParamName( "Blue Stroke" );

		registerPathParams();
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
		registerParameter( lineWidth );
		registerParameter( fillOn );
	}

	public void buildEditPanel()
	{
		AnimColorRGBEditor fillEditor = new AnimColorRGBEditor( "Fill Color", red1, green1, blue1 );
		AnimColorRGBEditor strokeEditor = new AnimColorRGBEditor(  "Stroke Color", red2, green2, blue2 );
		IntegerNumberEditor lineEdit = new IntegerNumberEditor( "Line width", lineWidth );
		CheckBoxEditor doFill = new CheckBoxEditor( fillOn, "Fill shape", true );

		addEditor( fillEditor );
		addRowSeparator();
		addEditor( strokeEditor );
		addRowSeparator();
		addEditor( lineEdit );
		addRowSeparator();
		addEditor( doFill );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		GeneralPath shape = getShape( frameTime );

		Color fill = new Color((int)red1.get(frameTime), (int)green1.get(frameTime), (int)blue1.get(frameTime) );
		Color stroke = new Color((int)red2.get(frameTime), (int)green2.get(frameTime), (int)blue2.get(frameTime) );

		g.setColor( fill );
		if( fillOn.get() ) 
			g.fill( shape );
		g.setStroke( new BasicStroke( lineWidth.get() ) );
		g.setColor( stroke );
		g.draw( shape );
	}

	public ViewEditorLayer getEditorLayer()
	{
 		return new PolyCurveEditLayer( this );
	}

}//end class
