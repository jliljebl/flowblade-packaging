package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.TwirlFilter;

public class TwirlPlugin extends PhantomPlugin
{
	public AnimatedValue centreX;
	public AnimatedValue centreY;
	public AnimatedValue radius;
	public AnimatedValue angle;

	public TwirlPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Twirl" );

		centreX = new AnimatedValue( 0.5f );
		centreY = new AnimatedValue( 0.5f );
		radius = new AnimatedValue( 100.0f );
		angle = new AnimatedValue( 2.0f );

		registerParameter( centreX );
		registerParameter( centreY );
		registerParameter( radius );
		registerParameter( angle );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor centreXE = new AnimValueNumberEditor( "Centre X", centreX );
 		AnimValueNumberEditor centreYE = new AnimValueNumberEditor( "Center Y", centreY );
 		AnimValueNumberEditor radiusE = new AnimValueNumberEditor( "Radius", radius );
 		AnimValueNumberEditor angleE = new AnimValueNumberEditor( "Angle", angle );

		addEditor( centreXE );
		addRowSeparator();
		addEditor( centreYE );
		addRowSeparator();
		addEditor( radiusE );
		addRowSeparator();
		addEditor( angleE );
	}

	public void doImageRendering( int frame )
	{
		TwirlFilter f = new TwirlFilter();
		f.setAngle( angle.getValue( frame ) );
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
