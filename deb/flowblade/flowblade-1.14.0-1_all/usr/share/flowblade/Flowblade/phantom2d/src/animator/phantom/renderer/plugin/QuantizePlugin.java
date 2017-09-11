package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.QuantizeFilter;

public class QuantizePlugin extends PhantomPlugin
{
	public IntegerParam numColors = new IntegerParam( 128 );
	public BooleanParam dither = new BooleanParam( false );
	public BooleanParam serpentine = new BooleanParam( true );

	public QuantizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Quantize" );

		registerParameter( numColors );
		registerParameter( dither );
		registerParameter( serpentine );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor numColE = new IntegerNumberEditor( "Number of colors", numColors );
		CheckBoxEditor ditherE = new CheckBoxEditor( dither, "Dither", true );
		CheckBoxEditor serpentineE = new CheckBoxEditor( serpentine, "Serpentine", true );

		addEditor( numColE );
		addRowSeparator();
		addEditor( ditherE );
		addRowSeparator();
		addEditor( serpentineE );
	}

	public void doImageRendering( int frame )
	{
		QuantizeFilter f = new QuantizeFilter();
		f.setNumColors( numColors.get() );
		f.setDither( dither.get() );
		f.setSerpentine( serpentine.get() );

		applyFilter( f );
	}

}//end class
