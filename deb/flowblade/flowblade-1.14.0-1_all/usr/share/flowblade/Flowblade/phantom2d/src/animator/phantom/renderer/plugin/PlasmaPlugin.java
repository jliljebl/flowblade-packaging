package animator.phantom.renderer.plugin;

import giotto2D.filters.color.Desaturate;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.PlasmaFilter;

public class PlasmaPlugin extends PhantomPlugin
{
	public IntegerParam turbulence = new IntegerParam( 10,0,100 );
	public IntegerParam seed = new IntegerParam(42);
	public BooleanParam desaturate = new BooleanParam( false );

	public PlasmaPlugin()
	{
		initPlugin( STATIC_SOURCE );
		makeAvailableInFilterStack( this );
	}

	public void buildDataModel()
	{
		setName( "Plasma" );

		registerParameter( turbulence );
		registerParameter( seed );
		registerParameter( desaturate );
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor turbulenceE = new IntegerValueSliderEditor( "Turbulence" , turbulence );
		IntegerNumberEditor seedE = new IntegerNumberEditor( "Seed", seed );
		CheckBoxEditor desEdit = new CheckBoxEditor( desaturate, "Desaturate", true );

		addEditor( turbulenceE );
		addRowSeparator();
		addEditor( seedE );
		addRowSeparator();
		addEditor( desEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );

		PlasmaFilter filter = new PlasmaFilter();
		filter.setTurbulence( (float)turbulence.get() / 10.0f );
		filter.setSeed( seed.get() );
	
		PluginUtils.filterImage( img, filter );
		if( desaturate.get() )
			Desaturate.filter( img );
		sendStaticSource( img, frame );
	}

}//end class
