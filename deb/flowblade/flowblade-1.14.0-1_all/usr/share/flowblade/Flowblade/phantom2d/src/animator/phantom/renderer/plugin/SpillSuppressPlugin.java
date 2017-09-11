package animator.phantom.renderer.plugin;

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

public class SpillSuppressPlugin extends PhantomPlugin
{
	private IntegerParam spillSuppress;

	public SpillSuppressPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "SpillSupression"  );

		spillSuppress = new IntegerParam( 0 );
		registerParameter( spillSuppress );
	}

	public void buildEditPanel()
	{
		String[] options = { "green lim. blue", "green lim. red", "green lim. blue/red","blue lim. green", "blue lim. red/green" }; 
		IntegerComboBox spillSelect = new IntegerComboBox( spillSuppress, "Spill supression", options );

		addEditor( spillSelect );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();

		if( spillSuppress.get() == 0 ) greenSupress1( img );
		if( spillSuppress.get() == 1 ) greenSupress2( img );
		if( spillSuppress.get() == 2 ) greenSupress3( img );
		if( spillSuppress.get() == 3 ) blueSupress1( img );
		if( spillSuppress.get() == 4 ) blueSupress2( img );

		sendFilteredImage( img, frame );
	}


	public static void greenSupress1( BufferedImage image )
	{
		int a, r, g, b;
		int[] pixels = AbstractFilter.getBank( image );

		for( int i = 0; i < pixels.length; i++ )
		{
			a = ( pixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			r = ( pixels[ i ] >> AbstractFilter.red ) & 0xff;
			g = ( pixels[ i ] >> AbstractFilter.green ) & 0xff;
			b = pixels[ i ] & 0xff;

			if( g > b ) g = b;

			pixels[ i ] = a << AbstractFilter.alpha | r << AbstractFilter.red | g << AbstractFilter.green | b;
		}
	}

	public static void greenSupress2( BufferedImage image )
	{
		int a, r, g, b;
		int[] pixels = AbstractFilter.getBank( image );

		for( int i = 0; i < pixels.length; i++ )
		{
			a = ( pixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			r = ( pixels[ i ] >> AbstractFilter.red ) & 0xff;
			g = ( pixels[ i ] >> AbstractFilter.green ) & 0xff;
			b = pixels[ i ] & 0xff;

			if( g > r ) g = r;

			pixels[ i ] = a << AbstractFilter.alpha | r << AbstractFilter.red | g << AbstractFilter.green | b;
		}
	}

	public static void greenSupress3( BufferedImage image )
	{
		int a, r, g, b;
		int[] pixels = AbstractFilter.getBank( image );

		for( int i = 0; i < pixels.length; i++ )
		{
			a = ( pixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			r = ( pixels[ i ] >> AbstractFilter.red ) & 0xff;
			g = ( pixels[ i ] >> AbstractFilter.green ) & 0xff;
			b = pixels[ i ] & 0xff;

			if( g > ((r + b)/2) ) g = ((r + b)/2);

			pixels[ i ] = a << AbstractFilter.alpha | r << AbstractFilter.red | g << AbstractFilter.green | b;
		}
	}

	public static void blueSupress1( BufferedImage image )
	{
		int a, r, g, b;
		int[] pixels = AbstractFilter.getBank( image );

		for( int i = 0; i < pixels.length; i++ )
		{
			a = ( pixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			r = ( pixels[ i ] >> AbstractFilter.red ) & 0xff;
			g = ( pixels[ i ] >> AbstractFilter.green ) & 0xff;
			b = pixels[ i ] & 0xff;

			if( b > g ) b = g;

			pixels[ i ] = a << AbstractFilter.alpha | r << AbstractFilter.red | g << AbstractFilter.green | b;
		}
	}

	public static void blueSupress2( BufferedImage image )
	{
		int a, r, g, b;
		int[] pixels = AbstractFilter.getBank( image );

		for( int i = 0; i < pixels.length; i++ )
		{
			a = ( pixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			r = ( pixels[ i ] >> AbstractFilter.red ) & 0xff;
			g = ( pixels[ i ] >> AbstractFilter.green ) & 0xff;
			b = pixels[ i ] & 0xff;

			if( b > ((r + g)/2) ) b = ((r + g)/2);

			pixels[ i ] = a << AbstractFilter.alpha | r << AbstractFilter.red | g << AbstractFilter.green | b;
		}
	}

}//end class
