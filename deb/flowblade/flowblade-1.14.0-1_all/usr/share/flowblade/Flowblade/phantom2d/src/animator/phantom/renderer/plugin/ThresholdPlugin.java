package animator.phantom.renderer.plugin;

import giotto2D.filters.color.Threshold;

import java.awt.Color;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

public class ThresholdPlugin extends PhantomPlugin
{
	private AnimatedValue redLight;
	private AnimatedValue greenLight;
	private AnimatedValue blueLight;

	private AnimatedValue redDark;
	private AnimatedValue greenDark;
	private AnimatedValue blueDark;
	
	public AnimatedValue toneLimit;

	public ThresholdPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Threshold" );

		toneLimit = new AnimatedValue( 128, 0, 255 );
		
		redLight = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		greenLight = new AnimatedValue( 120.0f, 0.0f, 255.0f );
		blueLight = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		redDark = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		greenDark = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blueDark = new AnimatedValue( 0.0f, 0.0f, 255.0f );

		redLight.setParamName( "Red Light" );
		greenLight.setParamName( "Green Light" );
		blueLight.setParamName( "Blue Light" );
		
		redDark.setParamName( "Red Dark" );
		greenDark.setParamName( "Green Dark" );
		blueDark.setParamName( "Blue Dark" );

		registerParameter( redLight );
		registerParameter( greenLight );
		registerParameter( blueLight );
		registerParameter( redDark );
		registerParameter( greenDark );
		registerParameter( blueDark );
		registerParameter( toneLimit );

	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor limitEditor = new AnimValueSliderEditor(  "Threshold", toneLimit);
		AnimColorRGBEditor lightEditor = new AnimColorRGBEditor( "Light Color", redLight, greenLight, blueLight );
		AnimColorRGBEditor darkEditor  = new AnimColorRGBEditor( "Dark Color", redDark, greenDark, blueDark );

		addEditor( limitEditor );
		addRowSeparator();
		addEditor( lightEditor );
		addRowSeparator();
		addEditor( darkEditor );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img  = getFlowImage();
		Color lightColor = new Color((int)redLight.get(frame), (int)greenLight.get(frame), (int)blueLight.get(frame) );
		Color darkColor = new Color((int)redDark.get(frame), (int)greenDark.get(frame), (int)blueDark.get(frame) );
		Threshold.filter( img, (int) toneLimit.get(frame), darkColor, lightColor );

		sendFilteredImage( img, frame );
	}

}//end class
