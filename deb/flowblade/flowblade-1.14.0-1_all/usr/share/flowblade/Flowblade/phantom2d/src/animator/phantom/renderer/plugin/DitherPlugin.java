package animator.phantom.renderer.plugin;

import java.util.Vector;

import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.DitherFilter;

public class DitherPlugin extends PhantomPlugin
{
	public IntegerParam levels = new IntegerParam( 6 );
	public IntegerParam matrixtype = new IntegerParam( 0 );

	private static Vector<int[]> matrixes = new Vector <int[]>();
	public static String[] mNames;

	static
	{
		Vector<String> matrixNames = new Vector<String>();
		
		matrixes.add( DitherFilter.ditherMagic2x2Matrix );
		matrixNames.add( "magic2x2" );

		matrixes.add( DitherFilter.ditherMagic4x4Matrix );
		matrixNames.add( "magic4x4" );

		matrixes.add( DitherFilter.ditherOrdered4x4Matrix );
		matrixNames.add( "ordered4x4" );

		matrixes.add( DitherFilter.ditherLines4x4Matrix ); 
		matrixNames.add( "lines4x4" );

		matrixes.add( DitherFilter.dither90Halftone6x6Matrix );
		matrixNames.add( "halftone6x6" );

		matrixes.add( DitherFilter.ditherOrdered6x6Matrix );
		matrixNames.add( "ordered6x6" );

		matrixes.add( DitherFilter.ditherOrdered8x8Matrix );
		matrixNames.add( "ordered8x8" );

		matrixes.add( DitherFilter.ditherCluster3Matrix );
		matrixNames.add( "cluster3" );

		matrixes.add( DitherFilter.ditherCluster4Matrix );
		matrixNames.add( "cluster4" );

		matrixes.add( DitherFilter.ditherCluster8Matrix );
		matrixNames.add( "cluster8" );


		mNames = new String[ matrixNames.size() ];
		for( int i = 0; i <  matrixNames.size(); i++ )
			mNames[ i ] = matrixNames.elementAt( i );
	}

	public DitherPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Dither" );

		registerParameter( levels );
		registerParameter( matrixtype );
	}

	public void buildEditPanel()
	{
		IntegerComboBox matrixSelect = new IntegerComboBox( matrixtype, "Matrix type", mNames );
		IntegerNumberEditor levelsE = new IntegerNumberEditor( "Levels", levels );

		addEditor( matrixSelect );
		addRowSeparator();
		addEditor( levelsE );
	}

	public void doImageRendering( int frame )
	{
		DitherFilter f = new DitherFilter();
		f.setMatrix( matrixes.elementAt( matrixtype.get() ) );
		f.setLevels( levels.get() );

		applyFilter( f );
	}

}//end class
