package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.KaleidoscopeFilter;

public class KaleidoscopePlugin extends PhantomPlugin
{
	public AnimatedValue angle;
	public AnimatedValue angle2;
	public AnimatedValue centreX;
	public AnimatedValue centreY;
	public IntegerParam sides;
	public AnimatedValue radius;

	public KaleidoscopePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Kaleidoscope" );

		angle = new AnimatedValue( 0 );
		angle2 = new AnimatedValue( 0 );
		centreX = new AnimatedValue( 0.5f );
		centreY = new AnimatedValue( 0.5f );
		sides = new IntegerParam( 3 );
		radius = new AnimatedValue( 0 );

		registerParameter( sides );
		registerParameter( angle );
		registerParameter( angle2 );
		registerParameter( centreX );
		registerParameter( centreY );
		registerParameter( radius );
	}

	public void buildEditPanel()
	{
 		IntegerNumberEditor sidesE = new IntegerNumberEditor( "Sides", sides );
		AnimValueNumberEditor angleE = new AnimValueNumberEditor( "Angle", angle );
		AnimValueNumberEditor angleE2 = new AnimValueNumberEditor( "Angle 2", angle2 );
		AnimValueNumberEditor centreXE = new AnimValueNumberEditor( "Centre X", centreX );
		AnimValueNumberEditor centreYE = new AnimValueNumberEditor( "Centre Y", centreY );
		AnimValueNumberEditor radiusE = new AnimValueNumberEditor( "Radius", radius );

		addEditor( sidesE );
		addRowSeparator();
		addEditor( angleE );
		addRowSeparator();
		addEditor( angleE2 );
		addRowSeparator();
		addEditor( centreXE );
		addRowSeparator();;
		addEditor( centreYE );
		addRowSeparator();
		addEditor( radiusE );
	}

	public void doImageRendering( int frame )
	{
		KaleidoscopeFilter f = new KaleidoscopeFilter();
		f.setSides( sides.get() );
		f.setAngle( angle.getValue( frame ) );
		f.setAngle2( angle2.getValue( frame ) );
		f.setCentreX( centreX.getValue( frame ) );
		f.setCentreY( centreY.getValue( frame ) );
		f.setRadius( radius.getValue( frame ) );

		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
