package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.SwimFilter;

public class SwimPlugin extends PhantomPlugin
{
	public AnimatedValue angle;
	public AnimatedValue scale;
	public AnimatedValue stretch;
	public AnimatedValue turbulence;
	public AnimatedValue amount;
	public AnimatedValue time;

	public SwimPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName("Swim");

		scale = new AnimatedValue( 32f );
		stretch = new AnimatedValue( 1.0f );
		angle = new AnimatedValue( 0 );
		turbulence = new AnimatedValue( 1.0f );
		amount = new AnimatedValue( 3.0f );
		time = new AnimatedValue( 0.0f );

		registerParameter( scale );
		registerParameter( stretch );
		registerParameter( angle );
		registerParameter( turbulence );
		registerParameter( amount );
		registerParameter( time );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor angleE = new AnimValueNumberEditor( "Angle", angle  );
		AnimValueNumberEditor scaleE = new AnimValueNumberEditor( "Scale", scale  );
		AnimValueNumberEditor stretchE = new AnimValueNumberEditor( "Strech", stretch  );
		AnimValueNumberEditor turbulenceE = new AnimValueNumberEditor( "Turbulence", turbulence  );
		AnimValueNumberEditor amountE = new AnimValueNumberEditor( "Amount", amount  );
		AnimValueNumberEditor timeE = new AnimValueNumberEditor( "Time", time  );

		addEditor( angleE );
		addRowSeparator();
		addEditor( scaleE );
		addRowSeparator();
		addEditor( stretchE );
		addRowSeparator();
		addEditor( turbulenceE );
		addRowSeparator();
		addEditor( amountE );
		addRowSeparator();
		addEditor( timeE );
	}

	public void doImageRendering( int frame )
	{
		SwimFilter f = new SwimFilter();
		f.setScale( scale.getValue( frame ) );
		f.setAmount( amount.getValue( frame ) );
		f.setAngle( angle.getValue( frame ) );
		f.setTurbulence( turbulence.getValue( frame ) );
		f.setAmount( amount.getValue( frame ) );
		f.setTime( time.getValue( frame ) );

		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
