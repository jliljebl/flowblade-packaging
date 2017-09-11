package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.RippleFilter;

public class RipplePlugin extends PhantomPlugin
{
	public AnimatedValue xAmplitude;
	public AnimatedValue yAmplitude;
	public AnimatedValue xWavelength;
	public AnimatedValue yWavelength;
	public IntegerParam waveType;

	private static String[] rOptions = { "SINE","SAWTOOTH","TRIANGLE","NOISE" };

	public RipplePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Ripple" );

		xAmplitude  = new AnimatedValue( 5.0f );
		yAmplitude  = new AnimatedValue( 5.0f );
		xWavelength  = new AnimatedValue( 16.0f );
		yWavelength  = new AnimatedValue( 16.0f );
		waveType = new IntegerParam( 0 );

		registerParameter( xAmplitude );
		registerParameter( yAmplitude );
		registerParameter( xWavelength );
		registerParameter( yWavelength );
		registerParameter( waveType );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor xAmplitudeE = new AnimValueNumberEditor( "X Amplitude", xAmplitude );
 		AnimValueNumberEditor yAmplitudeE = new AnimValueNumberEditor( "Y Amplitude", yAmplitude );
 		AnimValueNumberEditor xWavelengthE = new AnimValueNumberEditor( "X Wavelength", xWavelength );
 		AnimValueNumberEditor yWavelengthE = new AnimValueNumberEditor( "Y Wavelength", xWavelength );
		IntegerComboBox wTypeE = new IntegerComboBox( waveType,"Wave type", rOptions );

		addEditor( xAmplitudeE );
		addRowSeparator();
		addEditor( yAmplitudeE );
		addRowSeparator();
		addEditor( xWavelengthE );
		addRowSeparator();
		addEditor( yWavelengthE );
		addRowSeparator();
		addEditor( wTypeE );
	}

	public void doImageRendering( int frame )
	{
		RippleFilter f = new RippleFilter();
		f.setXAmplitude( xAmplitude.getValue( frame ) );
		f.setXWavelength( xWavelength.getValue( frame ));
		f.setYAmplitude( yAmplitude.getValue( frame ));
		f.setYWavelength( yWavelength.getValue( frame ));
		f.setWaveType( waveType.get() );

		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
