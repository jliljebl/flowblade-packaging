package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.GreenKey;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.FloatParam;

public class GreenKeyPlugin extends PhantomPlugin
{
	private FloatParam k0;
	private FloatParam k1;
	private FloatParam k2;

	public GreenKeyPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "GreenKey" );

		k0 = new FloatParam( 1.0f, 0.8f, 1.2f );
		k1 = new FloatParam( 1.0f, 0.8f, 1.2f );
		k2 = new FloatParam( 1.0f, 0.8f, 1.2f );

		registerParameter( k0 );
		registerParameter( k1 );
		registerParameter( k2 );
	}

	public void buildEditPanel()
	{
		FloatNumberEditor kedit1 = new FloatNumberEditor( "Blue mult.", k0 );
		FloatNumberEditor kedit2 = new FloatNumberEditor( "Green mult.", k1 );
		FloatNumberEditor kedit3 = new FloatNumberEditor( "Level", k2 );

		addEditor( kedit1 );
		addRowSeparator();
		addEditor( kedit2 );
		addRowSeparator();
		addEditor( kedit3  );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();
		GreenKey.filter( img, k0.get(), k1.get(), k2.get());

		sendFilteredImage( img, frame );
	}

}//end class
