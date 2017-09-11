package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.IntAnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerAnimatedValue;

import com.jhlabs.image.BoxBlurFilter;

public class BoxBlurPlugin extends PhantomPlugin
{
	public IntegerAnimatedValue hRadius;
	public IntegerAnimatedValue vRadius;

	public BoxBlurPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "BoxBlur" );

		hRadius = new IntegerAnimatedValue( 9, 0, 100 );
		vRadius = new IntegerAnimatedValue( 9, 0, 100 );

		registerParameter( hRadius );
		registerParameter( vRadius );
	}

	public void buildEditPanel()
	{
		IntAnimValueNumberEditor hEdit = new IntAnimValueNumberEditor( "Height", hRadius, 2 );
		IntAnimValueNumberEditor vEdit = new IntAnimValueNumberEditor( "Width", vRadius, 2 );

		addEditor( hEdit );
		addRowSeparator();
		addEditor( vEdit );
	}

	public void doImageRendering( int frame )
	{
		BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
		boxBlurFilter.setHRadius( hRadius.getIntValue( frame ) );
		boxBlurFilter.setVRadius( vRadius.getIntValue( frame ) );

		applyFilter( boxBlurFilter );
	}

}//end class
