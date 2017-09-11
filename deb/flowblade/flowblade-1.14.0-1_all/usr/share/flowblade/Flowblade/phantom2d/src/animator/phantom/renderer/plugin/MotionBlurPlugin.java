package animator.phantom.renderer.plugin;

import giotto2D.filters.AbstractFilter;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.MotionBlurOp;

public class MotionBlurPlugin extends PhantomPlugin
{
	public AnimatedValue centreX;
	public AnimatedValue centreY;
	public AnimatedValue distance;
	public AnimatedValue angle;
	public AnimatedValue rotation;
	public AnimatedValue zoom;

	public MotionBlurPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "EffectMotionBlur"  );

		centreX = new AnimatedValue(  0.5f, 0, 1 );
		centreY = new AnimatedValue( 0.5f, 0, 1 );
		distance = new AnimatedValue( 10.0f );
		angle = new AnimatedValue( 0.0f );
		rotation = new AnimatedValue( 0.0f );
		zoom = new AnimatedValue( 0.0f );

		registerParameter( centreX );
		registerParameter( centreY );
		registerParameter( distance );
		registerParameter( angle );
		registerParameter( rotation );
		registerParameter( zoom );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor centreXE = new AnimValueNumberEditor( "Centre X", centreX );
		AnimValueNumberEditor centreYE = new AnimValueNumberEditor( "Centre Y", centreY );
		AnimValueNumberEditor distanceE = new AnimValueNumberEditor( "Distance", distance );
		AnimValueNumberEditor angleE = new AnimValueNumberEditor( "Angle", angle );
		AnimValueNumberEditor rotationE = new AnimValueNumberEditor( "Rotation", rotation );
		AnimValueNumberEditor zoomE = new AnimValueNumberEditor( "Zoom", zoom );

		addEditor( centreXE );
		addRowSeparator();
		addEditor(  centreYE );
		addRowSeparator();
		addEditor( distanceE );
		addRowSeparator();
		addEditor( angleE );
		addRowSeparator();
		addEditor( rotationE  );
		addRowSeparator();
		addEditor( zoomE );
	}

	public void doImageRendering( int frame )
	{
		
		MotionBlurOp filter = new MotionBlurOp();
		filter.setCentreX( centreX.getValue( frame ) );
		filter.setCentreY( centreY.getValue( frame ) );
		filter.setDistance( distance.getValue( frame ) );
		filter.setAngle( angle.getValue( frame ) * AbstractFilter.DEGREES_TO_RADIANS  );
		filter.setRotation( rotation.getValue( frame ) );
		filter.setZoom( zoom.getValue( frame ) );

		applyFilter( filter );
	}

}//end class
