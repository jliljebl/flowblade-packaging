package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.PolarFilter;

public class PolarPlugin extends PhantomPlugin
{
	private static String[] convOptions = { "RECT. TO POLAR","POLAR TO RECT.","INVERT IN CIRCLE" };
	public IntegerParam convtype = new IntegerParam( 0 );

	public PolarPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Polar" );
		registerParameter( convtype );
	}

	public void buildEditPanel()
	{
		IntegerComboBox convSelect = new IntegerComboBox( convtype,"Conversion type", convOptions );
		addEditor( convSelect );
	}

	public void doImageRendering( int frame )
	{
		PolarFilter f = new PolarFilter();
		f.setType( convtype.get() );
	
		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
