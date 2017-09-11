package animator.phantom.renderer.plugin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import animator.phantom.gui.view.editlayer.PolyLineEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

public class PolyLineShapePlugin extends PolyLinePlugin
{
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;

	public IntegerParam lineWidth = new IntegerParam( 5 );
	public IntegerParam cap = new IntegerParam( 0 );
	public IntegerParam join = new IntegerParam( 0 );
	public IntegerParam miterLimit = new IntegerParam( 10, 1, 180 );
	public BooleanParam fillOn = new BooleanParam( true );

	public static final String[] CAP_OPTS = { "Butt","Round","Square" };
	public static final String[] JOIN_OPTS = { "Bevel","Miter","Round" };

	public PolyLineShapePlugin()
	{
		initPlugin( FULL_SCREEN_MOVING_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "LineShape" );

		registerPathParams();
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
		
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
		registerParameter( lineWidth );
		registerParameter( cap );
		registerParameter( join );
		registerParameter( miterLimit );
		registerParameter( fillOn );
	}

	public void buildEditPanel()
	{
		AnimColorRGBEditor fillEditor = new AnimColorRGBEditor( "Fill Color", red1, green1, blue1 );
		AnimColorRGBEditor strokeEditor = new AnimColorRGBEditor(  "Stroke Color", red2, green2, blue2 );
		IntegerComboBox joinSelect = new IntegerComboBox( join, "Join", JOIN_OPTS );
		IntegerComboBox capSelect = new IntegerComboBox( cap, "Caps", CAP_OPTS );
		IntegerNumberEditor lineEdit = new IntegerNumberEditor( "Line width", lineWidth );
		IntegerNumberEditor miterLimitE = new IntegerNumberEditor( "Miter Limit", miterLimit );
		CheckBoxEditor doFill = new CheckBoxEditor(fillOn, "Fill shape", true );

		addEditor( fillEditor );
		addRowSeparator();
		addEditor( strokeEditor );
		addRowSeparator();
		addEditor( lineEdit );
		addRowSeparator();
		addEditor( joinSelect );
		addRowSeparator();
		addEditor( capSelect );
		addRowSeparator();
		addEditor( miterLimitE );
		addRowSeparator();
		addEditor( doFill );
	}

	public void renderFullScreenMovingSource( float frameTime, Graphics2D g, int width, int height )
	{
		GeneralPath shape = getShape( frameTime );
		
		Color fill = new Color((int)red1.get(frameTime), (int)green1.get(frameTime), (int)blue1.get(frameTime) );
		Color stroke = new Color((int)red2.get(frameTime), (int)green2.get(frameTime), (int)blue2.get(frameTime) );
		
		g.setColor( fill );
		if( fillOn.get() ) g.fill( shape );
		g.setStroke( getStroke() );
		g.setColor( stroke );
		g.draw( shape );
	}

	private BasicStroke getStroke()
	{
		float width = (float) lineWidth.get();

		int capV;
		if( cap.get() == 0 ) capV = BasicStroke.CAP_BUTT;
		else if( cap.get() == 1 ) capV =  BasicStroke.CAP_ROUND;
		else  capV =  BasicStroke.CAP_SQUARE;

		int joinV;
		if( join.get() == 0 ) joinV = BasicStroke.JOIN_BEVEL;
		else if( join.get() == 1 ) joinV = BasicStroke.JOIN_MITER;
		else joinV = BasicStroke.JOIN_ROUND;

		float miterlimit = 10.0f;
		return new BasicStroke( width, capV, joinV, miterlimit );
	}

	public ViewEditorLayer getEditorLayer()
	{
 		return new PolyLineEditLayer( this );
	}

}//end class
