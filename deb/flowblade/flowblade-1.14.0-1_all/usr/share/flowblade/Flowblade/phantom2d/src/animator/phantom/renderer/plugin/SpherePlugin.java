package animator.phantom.renderer.plugin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.SphereFilter;

public class SpherePlugin extends PhantomPlugin
{
	public AnimatedValue centreX;
	public AnimatedValue centreY;
	public AnimatedValue refractionIndex;
	public AnimatedValue radius;

	public SpherePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Sphere" );

		centreX = new AnimatedValue( 0.5f );
		centreY = new AnimatedValue( 0.5f );
		refractionIndex = new AnimatedValue( 1.5f );
		radius =  new AnimatedValue( 100 );

		registerParameter( centreX );
		registerParameter( centreY );
		registerParameter( refractionIndex );
		registerParameter( radius );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor centreXE = new AnimValueNumberEditor( "Centre X", centreX );
		AnimValueNumberEditor centreYE = new AnimValueNumberEditor( "Centre Y", centreY );
		AnimValueNumberEditor refractionIndexE = new AnimValueNumberEditor( "RefractionIndex", refractionIndex );
		AnimValueNumberEditor radiusE = new AnimValueNumberEditor( "Radius", radius );

		addEditor( centreXE );
		addRowSeparator();
		addEditor( centreYE );
		addRowSeparator();
		addEditor( refractionIndexE );
		addRowSeparator();
		addEditor( radiusE );
	}

	public void doImageRendering( int frame )
	{
		SphereFilter f = new SphereFilter();
		f.setRefractionIndex( refractionIndex.getValue( frame ) );
		f.setRadius( radius.getValue( frame ) );
		f.setCentreX( centreX.getValue( frame ) );
		f.setCentreY( centreY.getValue( frame ) );

		BufferedImage img = getFlowImage();
		Graphics2D gc = img.createGraphics();
		gc.drawImage( img, f, 0, 0);
		gc.dispose();

		sendFilteredImage( img, frame );
	}

}//end class
