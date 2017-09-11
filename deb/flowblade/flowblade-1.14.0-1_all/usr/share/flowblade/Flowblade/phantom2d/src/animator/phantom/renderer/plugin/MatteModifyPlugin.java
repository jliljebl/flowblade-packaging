package animator.phantom.renderer.plugin;

import giotto2D.filters.color.ColorBalance;
import giotto2D.filters.color.RedChannelGammaCurve;
import giotto2D.filters.merge.AlphaToImage;
import giotto2D.filters.merge.ImageToAlpha;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.SingleCurveEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.GaussianFilter;

public class MatteModifyPlugin extends PhantomPlugin
{
	private static final String MATTE_TAB = "Matte";
	private static final String EDGE_TAB = "Edge Hue";
	//--- Matte
	public IntegerParam feather = new IntegerParam(  0, 0, 5);
	public CRCurveParam alphaGamma = new CRCurveParam("alpha");
	public IntegerParam blur = new IntegerParam( 0, 0, 20);

	//--- Edge hue
	public IntegerParam cyRe = new IntegerParam( 0, -100, 100);
	public IntegerParam maGr = new IntegerParam( 0, -100, 100);
	public IntegerParam yeBl = new IntegerParam( 0, -100, 100);

	private GaussianFilter gaussianFilter;
	private RedChannelGammaCurve gammaFilter;
	private ColorBalance colorBalanceFilter;

	public MatteModifyPlugin()
	{
		initPlugin( FILTER, SINGLE_INPUT );
	}

	public void buildDataModel()
	{
		setName( "MatteModify" );

		gaussianFilter = new GaussianFilter( 0 );
		gammaFilter = new RedChannelGammaCurve();
		colorBalanceFilter = new ColorBalance();

		registerParameter( feather );
		registerParameter( alphaGamma );
		registerParameter( blur );

		registerParameter( cyRe );
		registerParameter( maGr );
		registerParameter( yeBl );
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor blurEdit = new IntegerValueSliderEditor( "Alpha blur spread", blur );
		SingleCurveEditor gammaEdit = new SingleCurveEditor( "Key value to Alpha", alphaGamma, 156 );
		IntegerValueSliderEditor featherEdit = new IntegerValueSliderEditor( "Feather", feather );

		IntegerValueSliderEditor cyReEdit = new IntegerValueSliderEditor( "CyanRed", cyRe );
		IntegerValueSliderEditor maGrEdit = new IntegerValueSliderEditor( "MagentaGreen", maGr );
		IntegerValueSliderEditor yeBlEdit = new IntegerValueSliderEditor( "YellowBlue", yeBl );

		//--- tabs
		Vector<String> tabs = new Vector<String>();
		tabs.add( MATTE_TAB );
		tabs.add( "Edge Hue");
		setTabbedPanel( 330, tabs );

		addToTab(MATTE_TAB, blurEdit );
		addToTab(MATTE_TAB, new RowSeparator() );
		addToTab(MATTE_TAB, gammaEdit );
		addToTab(MATTE_TAB, new RowSeparator() );
		addToTab(MATTE_TAB, featherEdit );

		addToTab(EDGE_TAB, cyReEdit );
		addToTab(EDGE_TAB, new RowSeparator() );
		addToTab(EDGE_TAB, maGrEdit );
		addToTab(EDGE_TAB, new RowSeparator() );
		addToTab(EDGE_TAB, yeBlEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();

		//--- get alpha as image
		BufferedImage alphaImg = PluginUtils.createScreenCanvas();
		AlphaToImage.filter( img, alphaImg );
		Graphics2D gc = alphaImg.createGraphics();

		//--- Blur
		if( blur.get() != 0 )
		{
			gaussianFilter.setRadius( blur.get());
			gaussianFilter.setUseAlpha( false );
			gc.drawImage( alphaImg, gaussianFilter, 0, 0);
		}
	
		//--- gamma 
		int[] gammatable = alphaGamma.curve.getCurveCopy( true );
		gammaFilter.setTable( gammatable );
		gammaFilter.filter( alphaImg );

		//--- feather == postblur
		if( feather.get() != 0 )
		{
			gaussianFilter.setRadius( feather.get());
			gaussianFilter.setUseAlpha( false );
			gc.drawImage( alphaImg, gaussianFilter, 0, 0);
		}
		gc.dispose();

		ImageToAlpha.filter( img, alphaImg );

		colorBalanceFilter.setAllTonesValue( ColorBalance.CYAN_RED, cyRe.get(), false );
		colorBalanceFilter.setAllTonesValue( ColorBalance.MAGENTA_GREEN,maGr.get() , false );
		colorBalanceFilter.setAllTonesValue( ColorBalance.YELLOW_BLUE, yeBl.get(), false );
		colorBalanceFilter.createLookupTables();

		colorBalanceFilter.filterSemiTrans( img );

		sendFilteredImage( img, frame );
	}

}//end class
