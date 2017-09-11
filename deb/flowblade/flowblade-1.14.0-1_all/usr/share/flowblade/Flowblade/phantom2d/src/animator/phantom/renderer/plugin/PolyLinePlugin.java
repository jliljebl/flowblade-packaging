package animator.phantom.renderer.plugin;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.BooleanParam;

public abstract class PolyLinePlugin extends PhantomPlugin
{

	public AnimatedValueVectorParam px = new AnimatedValueVectorParam("px");
	public AnimatedValueVectorParam py = new AnimatedValueVectorParam("py");
	public BooleanParam closed = new BooleanParam(false, "closed");

	public void registerPathParams()
	{
		registerParameter( px );
		registerParameter( py );
		registerParameter( closed );
	}

	/*
	protected GeneralPath getShape( int frame )
	{
		return getShape( (float) frame );
	}
	*/
	protected GeneralPath getShape( float frame )
	{
		GeneralPath mask = new GeneralPath(GeneralPath.WIND_EVEN_ODD, px.size() );
		if( px.size() == 0 ) return mask;
		mask.moveTo( px.elem( 0 ).getValue( frame ), py.elem( 0 ).getValue( frame ) );

		for (int i = 1; i < px.size(); i++)
			mask.lineTo( px.elem( i ).getValue( frame ), py.elem( i ).getValue( frame ) );

		if( closed.get() ) mask.closePath();

		return mask;
	}

	public void addPoint( Point2D.Float point )
	{
		px.get().add( new AnimatedValue( getIOP(), point.x ) );
		py.get().add( new AnimatedValue( getIOP(), point.y ) );
	}
	
	public void insertPoint( Point2D.Float point, int index )
	{
		px.get().insertElementAt( new AnimatedValue( getIOP(), point.x ), index );
		py.get().insertElementAt( new AnimatedValue( getIOP(), point.y ), index );
	}

	public void removePoint( int index )
	{
		px.get().remove( index );
		py.get().remove( index );
	}

	public Point2D.Float getPoint( int i, int frame )
	{
		return new  Point2D.Float( px.elem( i ).getValue( frame ), py.elem( i ).getValue( frame ) );
	}

}//end class