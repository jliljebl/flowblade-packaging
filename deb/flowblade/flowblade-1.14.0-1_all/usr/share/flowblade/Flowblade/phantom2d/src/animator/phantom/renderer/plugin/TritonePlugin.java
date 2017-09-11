package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.ArrayColormap;

public class TritonePlugin extends PhantomPlugin
{
	public ColorParam lightColor = new ColorParam( new Color( 255, 255, 255 ) );
	public ColorParam darkColor = new ColorParam( new Color( 0,0,0 ) );
	public ColorParam middleColor = new ColorParam( new Color( 128,128,128 ) );

	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;

	private AnimatedValue red2;
	private AnimatedValue green2;
	private AnimatedValue blue2;
	
	private AnimatedValue red3;
	private AnimatedValue green3;
	private AnimatedValue blue3;
	
	public IntegerParam middleValue = new IntegerParam( 128 );

	private int[] rbgLook = new int[256];

	public static final int alpha_mask = 0x00ffffff;
	public static final int alpha = 24;
	public static final int red = 16;
	public static final int green = 8;
	public static final int MAX_RGB = 3 * 255;

	public TritonePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "TriTone" );

		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		red2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		green2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		blue2 = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		
		red3 = new AnimatedValue( 128.0f, 0.0f, 255.0f );
		green3 = new AnimatedValue( 128.0f, 0.0f, 255.0f );
		blue3 = new AnimatedValue( 128.0f, 0.0f, 255.0f );

		red1.setParamName( "Light Red" );
		green1.setParamName( "Light Green" );
		blue1.setParamName( "Light Blue" );
		
		red2.setParamName( "Dark Red" );
		green2.setParamName( "Dark Green" );
		blue2.setParamName( "Dark Blue" );
		
		red3.setParamName( "Mid Red" );
		green3.setParamName( "Mid Green" );
		blue3.setParamName( "Mid Blue" );
		

		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( red2 );
		registerParameter( green2 );
		registerParameter( blue2 );
		registerParameter( red3 );
		registerParameter( green3 );
		registerParameter( blue3 );
		registerParameter( middleValue );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor mVal = new IntegerNumberEditor( "Middle threshold", middleValue);
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Light Color", red1, green1, blue1 );
		AnimColorRGBEditor colorEditor2 = new AnimColorRGBEditor( "Dark Color", red2, green2, blue2 );
		AnimColorRGBEditor colorEditor3 = new AnimColorRGBEditor( "Mid Color", red3, green3, blue3 );

		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( mVal );
		addRowSeparator();
		addEditor( colorEditor3 );
		addRowSeparator();
		addEditor( colorEditor2 );
	}

	public void doImageRendering( int frame )
	{
		//--- Crate lookup for color replacement	
		ArrayColormap cmap = new ArrayColormap();
		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		Color color2 = new Color((int)red2.get(frame), (int)green2.get(frame), (int)blue2.get(frame) );
		Color color3 = new Color((int)red3.get(frame), (int)green3.get(frame), (int)blue3.get(frame) );

		cmap.setColor( 0, color2.getRGB() );
		cmap.setColor( 255, color1.getRGB() );
		cmap.setColorInterpolated(middleValue.get(), 0, 255, color3.getRGB() );

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

	}

	public static int[] getBank( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();
		DataBufferInt dbuf = (DataBufferInt) imgRaster.getDataBuffer();
		return dbuf.getData( 0 );
	}

}//end class
