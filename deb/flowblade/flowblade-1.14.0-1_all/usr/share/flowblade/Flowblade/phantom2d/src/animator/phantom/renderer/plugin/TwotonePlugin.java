package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.ArrayColormap;

public class TwotonePlugin extends PhantomPlugin
{
	private AnimatedValue redLight;
	private AnimatedValue greenLight;
	private AnimatedValue blueLight;

	private AnimatedValue redDark;
	private AnimatedValue greenDark;
	private AnimatedValue blueDark;
	
	private int[] rbgLook = new int[256];

	public static final int alpha_mask = 0x00ffffff;
	public static final int alpha = 24;
	public static final int red = 16;
	public static final int green = 8;
	public static final int MAX_RGB = 3 * 255;

	public TwotonePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "TwoTone" );
		
		redLight = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		greenLight = new AnimatedValue( 120.0f, 0.0f, 255.0f );
		blueLight = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		redDark = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		greenDark = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blueDark = new AnimatedValue( 0.0f, 0.0f, 255.0f );

		redLight.setParamName( "Red Light" );
		greenLight.setParamName( "Green Light" );
		blueLight.setParamName( "Blue Light" );
		
		redDark.setParamName( "Red Dark" );
		greenDark.setParamName( "Green Dark" );
		blueDark.setParamName( "Blue Dark" );
		
		registerParameter( redLight );
		registerParameter( greenLight );
		registerParameter( blueLight );
		registerParameter( redDark );
		registerParameter( greenDark );
		registerParameter( blueDark );
	}

	public void buildEditPanel()
	{
		AnimColorRGBEditor lightEditor = new AnimColorRGBEditor( "Light Color" ,  redLight, greenLight, blueLight );
		AnimColorRGBEditor  darkEditor  = new AnimColorRGBEditor(  "Dark Color", redDark, greenDark, blueDark );
		  
		addEditor( lightEditor );
		addRowSeparator();
		addEditor( darkEditor );
	}

	public void doImageRendering( int frame )
	{
		//--- Crate lookup for color replacement	
		ArrayColormap cmap = new ArrayColormap();
		Color lightColor = new Color((int)redLight.get(frame), (int)greenLight.get(frame), (int)blueLight.get(frame) );
		Color darkColor = new Color((int)redDark.get(frame), (int)greenDark.get(frame), (int)blueDark.get(frame) );

		cmap.setColorRange( 0, 255, darkColor.getRGB(), lightColor.getRGB() );

		for( int j = 0; j < 256; j++ )
			rbgLook[ j ] = cmap.getColor( (float) j / 255.0f ) & alpha_mask;

		//--- Do color replacement.
		BufferedImage source  = getFlowImage();
		int[] pix = getBank( source );
		int rbg;
		int lumaValue;
		int a;
		int r;
		int g;
		int b;

		for( int i = 0; i < pix.length; i++ )
		{
			a = ( pix[ i ] >> alpha ) & 0xff;
			r = ( pix[ i ] >> red ) & 0xff;
			g = ( pix[ i ] >> green ) & 0xff;
			b = ( pix[ i ] & 0xff );

			lumaValue = (( r + g + b ) * 255 ) / MAX_RGB;
			rbg = rbgLook[ lumaValue ];

			pix[ i ] =  a << alpha | rbg;
		}

		sendFilteredImage( source, frame );

	}//end filter

	public static int[] getBank( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();
		DataBufferInt dbuf = (DataBufferInt) imgRaster.getDataBuffer();
		return dbuf.getData( 0 );
	}

}//end class
