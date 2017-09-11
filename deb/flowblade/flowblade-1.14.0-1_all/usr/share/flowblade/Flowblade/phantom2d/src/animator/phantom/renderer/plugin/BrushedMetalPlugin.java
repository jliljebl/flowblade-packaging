package animator.phantom.renderer.plugin;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.FloatParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.BrushedMetalFilter;

public class BrushedMetalPlugin extends PhantomPlugin
{
	public IntegerParam radius = new IntegerParam( 10 );
	public FloatParam amount = new FloatParam( 0.1f );
	public ColorParam c = new ColorParam( 0xff888888, true );
	public FloatParam shine = new FloatParam( 0.1f );
    	public BooleanParam monochrome = new BooleanParam( true );

	public BrushedMetalPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "BrushedMetal" );

		registerParameter( radius );
		registerParameter( amount );
		registerParameter( c );
		registerParameter( shine );
		registerParameter( monochrome );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor radiusE = new IntegerNumberEditor( "Radius", radius  );
		FloatNumberEditor amountE = new FloatNumberEditor( "Amount", amount  );
		ParamColorSelect cE = new ParamColorSelect( c, "Color" );
		FloatNumberEditor shineE = new FloatNumberEditor( "Shine", shine );
		CheckBoxEditor monocE = new CheckBoxEditor( monochrome, "Monochrome", true );

		addEditor( radiusE );
		addRowSeparator();
		addEditor( amountE );
		addRowSeparator();
		addEditor( cE );
		addRowSeparator();
		addEditor( shineE );
		addRowSeparator();
		addEditor( monocE );
	}

	public void doImageRendering( int frame )
	{
		BrushedMetalFilter bmFilt = new BrushedMetalFilter();
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		bmFilt.setRadius( radius.get() );
		bmFilt.setAmount( amount.get() );
		bmFilt.setColor( c.get().getRGB() );
		bmFilt.setMonochrome( monochrome.get() );
		bmFilt.setShine( shine.get() );
	
		PluginUtils.filterImage( img, bmFilt );

		sendStaticSource( img, frame );
	}

}//end class
