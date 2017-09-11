package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;

public class AnimatedColorRGBPlugin extends PhantomPlugin
{
	private AnimatedValue red;
	private AnimatedValue green;
	private AnimatedValue blue;
	
	public AnimatedColorRGBPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}
	
	public void buildDataModel()
	{
		setName( "ColorAnimated" );
		red = new AnimatedValue( 120.0f, 0.0f, 255.0f );
		green = new AnimatedValue( 120.0f, 0.0f, 255.0f );
		blue = new AnimatedValue( 120.0f, 0.0f, 255.0f );
		red.setParamName( "Red" );
		green.setParamName( "Green" );
		blue.setParamName( "Blue" );
		
		registerParameter( red );
		registerParameter( green );
		registerParameter( blue );
	}
	
	public void buildEditPanel()
	{
		AnimColorRGBEditor colorEditor = new AnimColorRGBEditor( "Color for Frame", red, green, blue );
		addEditor( colorEditor );
	 }
	
	public void doImageRendering( int frame )
	{
		Color drawC = new Color( (int) red.get( frame ), (int) green.get( frame ), (int) blue.get( frame ));
	
		BufferedImage source = PluginUtils.createScreenCanvas();
		Graphics2D gc = source.createGraphics();
		gc.setColor( drawC );
		gc.fillRect( 0, 0, source.getWidth(), source.getHeight()  );
		gc.dispose();

		sendStaticSource( source, frame );
	}

}//end class
