package animator.phantom.renderer.plugin;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;

public class ShearPlugin extends PhantomPlugin
{
	public AnimatedValue skewX;
	public AnimatedValue skewY;

	private static final double P_MULT = Math.PI / 180.0;

	public ShearPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Shear" );

		skewX = new AnimatedValue( 0.0f, -90.0f, 90.0f );
		skewY = new AnimatedValue( 0.0f, -90.0f, 90.0f );

		registerParameter( skewX );
		registerParameter( skewY );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor xSkewE = new AnimValueNumberEditor( "Skew X", skewX );
		AnimValueNumberEditor ySkewE = new AnimValueNumberEditor( "Skew Y", skewY );

		addEditor( xSkewE );
		addRowSeparator();
		addEditor( ySkewE );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();
		BufferedImage canvas = PluginUtils.createTransparentCanvas( img.getWidth(), img.getHeight() );

		AffineTransform transform = getTransform( frame );
		AffineTransformOp tOp = new AffineTransformOp( transform, AffineTransformOp.TYPE_BILINEAR );
		tOp.filter( img, canvas  );
		sendFilteredImage( canvas, frame );
	}

	private AffineTransform getTransform( int frame )
	{
		AffineTransform transform = new AffineTransform();
		double sx = ((double) skewX.get( frame )) * P_MULT;
		double sy = ((double) skewY.get( frame )) * P_MULT;
		transform.shear(Math.tan( sx ), Math.tan( sy ));
		return transform;
	}

}//end class
