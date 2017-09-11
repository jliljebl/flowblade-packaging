package animator.phantom.renderer.plugin;

import java.awt.Color;

import animator.phantom.paramedit.AnimColorRGBEditor;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

import com.jhlabs.image.PointillizeFilter;

public class PointillizePlugin extends PhantomPlugin
{
	public AnimatedValue edgeThickness;
	public BooleanParam fadeEdges;
	private AnimatedValue red1;
	private AnimatedValue green1;
	private AnimatedValue blue1;
	public AnimatedValue fuzziness;

	public PointillizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Pointillize" );

		edgeThickness = new AnimatedValue( 0.4f );
		fadeEdges = new BooleanParam( false );
		red1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		green1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		blue1 = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		fuzziness = new AnimatedValue( 0.1f  );

		red1.setParamName( "Edge Red" );
		green1.setParamName( "Edge Green" );
		blue1.setParamName( "Edge Blue" );
		
		registerParameter( edgeThickness );
		registerParameter( fadeEdges );
		registerParameter( red1 );
		registerParameter( green1 );
		registerParameter( blue1 );
		registerParameter( fuzziness );	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor edgeThicknessE = new AnimValueNumberEditor( "Edge thickness", edgeThickness );
		CheckBoxEditor fadeEdgesE = new CheckBoxEditor( fadeEdges, "Fade edges", true );
		AnimColorRGBEditor colorEditor1 = new AnimColorRGBEditor( "Edge color", red1, green1, blue1 );
		AnimValueNumberEditor fuzzinessE = new  AnimValueNumberEditor( "Fuzziness", fuzziness );

		addEditor( edgeThicknessE );
		addRowSeparator();
		addEditor( fadeEdgesE );
		addRowSeparator();
		addEditor( colorEditor1 );
		addRowSeparator();
		addEditor( fuzzinessE );
	}

	public void doImageRendering( int frame )
	{
		PointillizeFilter pointillizeFilter = new PointillizeFilter();
		pointillizeFilter.setEdgeThickness( edgeThickness.getValue( frame ) );
		pointillizeFilter.setFadeEdges( fadeEdges.get() );
		Color color1 = new Color((int)red1.get(frame), (int)green1.get(frame), (int)blue1.get(frame) );
		pointillizeFilter.setEdgeColor( color1.getRGB() );
		pointillizeFilter.setFuzziness( fuzziness.getValue( frame )  );

		applyFilter( pointillizeFilter );
	}

}//end class
