package animator.phantom.renderer.plugin;

import giotto2D.filters.AbstractFilter;
import giotto2D.libcolor.GiottoColorSpace;
import giotto2D.libcolor.GiottoHSLInt;
import giotto2D.libcolor.GiottoRGBInt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.gui.view.ColorPickListener;
import animator.phantom.paramedit.AlphaDisplay;
import animator.phantom.paramedit.BooleanComboBox;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.ParamColorDisplay;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.SingleCurveEditor;
import animator.phantom.plugin.AbstractPluginEditLayer;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.plugin.editlayer.ColorDifferenceEditLayer;

//--- Based on functions on a webpage by Stefan Ihringer
public class ColorDifferenceKeyPlugin extends PhantomPlugin implements ColorPickListener
{
	public ColorParam keyColor = new ColorParam( Color.black );
	private BooleanParam isBlueDiffDirection;
	private IntegerParam spillSuppress;

	private CRCurveParam keyAlphaGamma;
	private CRCurveParam diffAlphaGamma;
	private CRCurveParam combAlphaGamma;

	private ParamColorDisplay keyColorDisp;
	private AlphaDisplay keyDisplay;
	private AlphaDisplay diffDisplay;
	private AlphaDisplay combDisplay;

	private static final String KEY =  "Key";
	private static final String KEY_PART = "Key part.";
	private static final String DIFF_PART = "Diff. part.";
	private static final String COMBINED = "Combined";

	public ColorDifferenceKeyPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "ColorDifferenceKey" );

		keyColor = new ColorParam( Color.black );
		isBlueDiffDirection = new BooleanParam( true );
		spillSuppress = new IntegerParam( 0 );

		keyAlphaGamma = new CRCurveParam("alphakey");
		diffAlphaGamma = new CRCurveParam("alphadiff");
		combAlphaGamma = new CRCurveParam("alphacomb");

		registerParameter( keyColor );
		registerParameter( isBlueDiffDirection );
		registerParameter( spillSuppress );
		registerParameter( keyAlphaGamma );
		registerParameter( diffAlphaGamma );
		registerParameter( combAlphaGamma );
	}

	public void buildEditPanel()
	{
		keyColorDisp = new ParamColorDisplay( keyColor, "Key Color", "keycolor" );
		BooleanComboBox diffDirEdit = new BooleanComboBox(	isBlueDiffDirection, 
									"Diff. hue better for", 
									"blue screen",
									"green screen",
									 true );
		String[] options = { "None", "green lim. blue", "green lim. red", "green lim. blue/red","blue lim. green", "blue lim. red/green" }; 
		IntegerComboBox spillSelect = new IntegerComboBox( spillSuppress, "Spill supression", options );
		keyDisplay = new AlphaDisplay( "Key Partial Matte" );
		diffDisplay = new AlphaDisplay( "Difference Partial Matte" );
		combDisplay = new AlphaDisplay( "Combined Matte" );

		SingleCurveEditor keyGammaEdit = new SingleCurveEditor( "Key Partial Gamma", keyAlphaGamma, 120, 135 );
		SingleCurveEditor diffGammaEdit = new SingleCurveEditor( "Key Partial Gamma", diffAlphaGamma, 120, 135 );
		SingleCurveEditor combineGammaEdit = new SingleCurveEditor( "Key Partial Gamma", combAlphaGamma, 120, 135 );

		Vector<String> paneNames = new Vector<String>();
		paneNames.add( KEY );
		paneNames.add( KEY_PART );
		paneNames.add( DIFF_PART );
		paneNames.add( COMBINED );
		
		setTabbedPanel( 350, paneNames );

		addToTab(KEY, keyColorDisp );
		addToTab(KEY, new RowSeparator() );
		addToTab(KEY, diffDirEdit );
		addToTab(KEY, new RowSeparator() );
		addToTab(KEY, spillSelect );
		addToTab(KEY, new RowSeparator() );

		addToTab(KEY_PART, keyDisplay );
		addToTab(KEY_PART, new RowSeparator() );
		addToTab(KEY_PART, keyGammaEdit );

		addToTab(DIFF_PART, diffDisplay );
		addToTab(DIFF_PART, new RowSeparator() );
		addToTab(DIFF_PART, diffGammaEdit );

		addToTab(COMBINED, combDisplay );
		addToTab(COMBINED, new RowSeparator() );
		addToTab(COMBINED, combineGammaEdit );
	}

	public void doImageRendering( int frame )
	{
		//--- Get key color and diff hues
		Color keyC = keyColor.get();
		GiottoRGBInt keyRGB = new GiottoRGBInt( keyC );
		GiottoHSLInt keyINT = new GiottoHSLInt();
		GiottoColorSpace.rgb_to_hsl_int( keyRGB, keyINT );

		double keyHue = keyINT.h / 255.0;//--- GiottoHSL: H [0, 255], L [0, 255], S [0, 255].
		double diffHue = keyHue - (1.0 / 6.0);
		if( diffHue < 0.0 )
			diffHue = 1.0 - diffHue;

		if( !isBlueDiffDirection.get() )
		{
			diffHue = keyHue + (1.0 / 6.0);
			if( diffHue > 1.0 )
				diffHue = diffHue - 1.0;
		}

		//--- Create key and diff mattes into alpha channels
		BufferedImage keyImg = getFlowImage();
		BufferedImage diffImg = PluginUtils.getImageClone( keyImg );

		//--- Create and display key partial
		createHueMatte( keyHue, true, keyImg );
		alphaGamma( keyAlphaGamma.curve.getCurveCopy( true ), keyImg );
		keyDisplay.displayAlpha( keyImg );

		//--- Create and display diff partial
		createHueMatte( diffHue, false, diffImg );
		alphaGamma( diffAlphaGamma.curve.getCurveCopy( true ), diffImg );
		diffDisplay.displayAlpha( diffImg );

		//--- Create and display combined
		screenCombine( keyImg, diffImg );
		alphaGamma( combAlphaGamma.curve.getCurveCopy( true ), keyImg );
		combDisplay.displayAlpha( keyImg );

		//... Spill supression
		if( spillSuppress.get() == 1 ) SpillSuppressPlugin.greenSupress1( keyImg );
		if( spillSuppress.get() == 2 ) SpillSuppressPlugin.greenSupress2( keyImg );
		if( spillSuppress.get() == 3 ) SpillSuppressPlugin.greenSupress3( keyImg );
		if( spillSuppress.get() == 4 ) SpillSuppressPlugin.blueSupress1( keyImg );
		if( spillSuppress.get() == 5 ) SpillSuppressPlugin.blueSupress2( keyImg );

		sendFilteredImage( keyImg, frame );
	}

	private void createHueMatte( double hue, boolean isKeyMatte, BufferedImage image )
	{

		int[] pixels = AbstractFilter.getBank( image );
		int pix;
		GiottoRGBInt pixRGB = new GiottoRGBInt();
		GiottoHSLInt pixHSL = new GiottoHSLInt();
		double tmphue, pixHue, deltaHue, alpha, saturation, luminance;

		for( int i = 0; i < pixels.length; i++ )
		{
			//--- Get pixel in HSL 
			pix = pixels[ i ];
			pixRGB.r = ( pix >> AbstractFilter.red ) & 0xff;
			pixRGB.g = ( pix >> AbstractFilter.green ) & 0xff;
			pixRGB.b = pix & 0xff;
			GiottoColorSpace.rgb_to_hsl_int( pixRGB, pixHSL );

			//--- get hue delta 
			pixHue = (pixHSL.h / 255.0);
			tmphue = pixHue - hue;
			if( tmphue > 0.5 )
			{
				deltaHue = hue - pixHue + 1.0;
			}
			else
			{
				if( tmphue < -0.5 )
					deltaHue = pixHue + 1.0 - hue;
				else
					deltaHue = Math.abs( tmphue ); 
			}

			//--- Get alpha
			alpha = 0.0;
			if( deltaHue  > 1.0 / 6.0 )
				alpha = (deltaHue - (1.0 / 6.0 )) * 6.0;
			if( deltaHue > 2.0 / 6.0 )
				alpha = 1.0; 

			//--- Correct for saturation
			saturation = pixHSL.s / 255.0;
			alpha = (saturation * alpha) + (1.0 - saturation) * 0.5;

			//--- Correct for luminance
			//--- Key matte and difference matte have different luminance functions
			luminance = pixHSL.l / 255.0;
			if( isKeyMatte )
			{
				if( luminance < 0.5 )
					alpha = 1.0 - 2.0 * ( 1 - alpha ) * luminance;
				else
					alpha = 2.0 * alpha * ( 1.0 - luminance );
			}
			else
			{

				if( luminance < 0.5 )
					alpha = 2.0 * alpha * luminance;
				else
					 alpha = 1.0 - 2.0 * ( 1.0 - alpha) * (1.0 - luminance );

			}

			pixels[ i ] = ((int) Math.round( alpha * 255 ) << AbstractFilter.alpha) | ( pix & AbstractFilter.alpha_mask);
		}
	}

	private void screenCombine(	BufferedImage destination,
					BufferedImage source )
	{

		int[] dPixels = AbstractFilter.getBank( destination );
		int[] sPixels = AbstractFilter.getBank( source );
		int da, sa;

		//f(a,b) = 1 - (1-a) * (1-b)
		for( int i = 0; i < dPixels.length; i++ )
		{
			da = ( dPixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			sa = ( sPixels[ i ] >> AbstractFilter.alpha ) & 0xff;
			da = ( 255 - ((255 - da) * (255 - sa)) / 255 );

			dPixels[ i ] = ( da << AbstractFilter.alpha ) | ( dPixels[ i ] & AbstractFilter.alpha_mask);
		}
	}

	public void alphaGamma( int[] look, BufferedImage image )
	{
		int a;
		int[] pixels = AbstractFilter.getBank( image );

		for( int i = 0; i < pixels.length; i++ )
		{
			a = look[ ( pixels[ i ] >> AbstractFilter.alpha ) & 0xff ];
			pixels[ i ] = ( a << AbstractFilter.alpha ) | ( pixels[ i ] & AbstractFilter.alpha_mask );
		}
	}

	public void colorPicked( Color c )
	{
		keyColor.set( c );
		keyColorDisp.frameChanged();
	}

	public AbstractPluginEditLayer getPluginEditLayer()
	{
		return new ColorDifferenceEditLayer( this, this );
	}

}//end class

//g>(b+r)/2?g=(b+r)/2 
//g > r ? g = r
//g > b ? g = b
//b > g ? b = g
//matte = b - max( g, r )