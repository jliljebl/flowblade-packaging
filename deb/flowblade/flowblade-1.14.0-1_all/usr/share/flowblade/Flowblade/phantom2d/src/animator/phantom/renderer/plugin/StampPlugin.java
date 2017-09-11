package animator.phantom.renderer.plugin;

import java.awt.Color;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.StampFilter;

public class StampPlugin extends PhantomPlugin
{
	public AnimatedValue threshold;
	public AnimatedValue softness;
   	public AnimatedValue radius;
   
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;

	public StampPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Stamp" );

		threshold = new AnimatedValue( 0.5f );
		softness = new AnimatedValue( 0.5f );
		radius = new AnimatedValue( 5 );
		
		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		red2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		green2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blue2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		
		red1.setParamName( "Light Red" );
		green1.setParamName( "Light Green" );
		blue1.setParamName( "Light Blue" );
		
		red2.setParamName( "Dark Red" );
		green2.setParamName( "Dark Green" );
		blue2.setParamName( "Dark Blue" );
		
		registerParameter( threshold );
		registerParameter( softness );
		registerParameter( radius );

		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor thresEdit = new AnimValueNumberEditor("Threshold" ,threshold );
		AnimValueNumberEditor softnessEdit = new AnimValueNumberEditor("Softness" , softness );
		AnimValueNumberEditor radiusEdit = new AnimValueNumberEditor("Radius" , radius );
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Light Color", red1, green1, blue1 );
		AnimColorRGBEditor colorEditor2 = new AnimColorRGBEditor( "Dark Color", red2, green2, blue2 );

		addEditor( thresEdit );
		addRowSeparator();
		addEditor( softnessEdit);
		addRowSeparator();
		addEditor( radiusEdit );
		addRowSeparator();
		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( colorEditor2 );
	}

	public void doImageRendering( int frame )
	{
		StampFilter stampFilter = new StampFilter();
		stampFilter.setRadius( radius.getValue( frame) );
		stampFilter.setThreshold( threshold.getValue( frame) );
		stampFilter.setSoftness( softness.getValue( frame) );
		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		Color color2 = new Color((int)red2.get(frame), (int)green2.get(frame), (int)blue2.get(frame) );
		stampFilter.setWhite( color1.getRGB() );
		stampFilter.setBlack( color2.getRGB() );

		applyFilter( stampFilter );
	}

}//end class
