package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.PinchFilter;

public class PinchPlugin extends PhantomPlugin
{
	public AnimatedValue angle;
	public AnimatedValue centreX;
	public AnimatedValue centreY;
	public AnimatedValue radius;
	public AnimatedValue amount;

	public PinchPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Pinch" );

		radius = new AnimatedValue( 100f );
		amount = new AnimatedValue( 0.5f );
		angle = new AnimatedValue( 0 );
		centreX = new AnimatedValue( 0.5f, 0, 1 );
		centreY = new AnimatedValue( 0.5f, 0, 1 );

		registerParameter( angle );
		registerParameter( centreX );
		registerParameter( centreY );
		registerParameter( radius );
		registerParameter( amount );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor amountE = new AnimValueNumberEditor( "Amount", amount );
		AnimValueNumberEditor angleE = new AnimValueNumberEditor( "Angle", angle );
		AnimValueNumberEditor centreXE = new AnimValueNumberEditor( "Centre X", centreX );
		AnimValueNumberEditor centreYE = new AnimValueNumberEditor( "Centre Y", centreY );
		AnimValueNumberEditor radiusE = new AnimValueNumberEditor( "Radius", radius );

		addEditor( angleE );
		addRowSeparator();
		addEditor( centreXE );
		addRowSeparator();
		addEditor( centreYE );
		addRowSeparator();
		addEditor( radiusE );
		addRowSeparator();
		addEditor( amountE );
	}

	public void doImageRendering( int frame )
	{
		PinchFilter f = new PinchFilter();
		f.setRadius( radius.getValue( frame ) );
		f.setAmount( amount.getValue( frame ) );
		f.setAngle( angle.getValue( frame ) );
		f.setCentreX( centreX.getValue( frame ) );
		f.setCentreY( centreY.getValue( frame ) );

		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
