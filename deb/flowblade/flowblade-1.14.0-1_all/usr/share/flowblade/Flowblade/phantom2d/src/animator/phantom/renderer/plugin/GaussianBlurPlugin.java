package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.GaussianFilter;

public class GaussianBlurPlugin extends PhantomPlugin
{
	public AnimatedValue radius;
	public IntegerParam alphaMode;//remove

	public GaussianBlurPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Gaussian Blur" );

		radius = new AnimatedValue( 9, 1, 200 );
		alphaMode = new IntegerParam( 0 );

		registerParameter( radius );
		registerParameter( alphaMode );//remove
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor radiusEdit = new AnimValueNumberEditor( "Radius", radius );
		addEditor( radiusEdit );
	}

	public void doImageRendering( int frame )
	{
		//--- RGB blur
		GaussianFilter f =  new GaussianFilter();
		f.setRadius( radius.getValue( frame ));
		f.setUseAlpha( true );

		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
