package animator.phantom.renderer.plugin;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import animator.phantom.gui.view.SVec;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.param.BooleanParam;

public abstract class PolyCurvePlugin extends PhantomPlugin
{
	//--- Points are attributes of segments but all points are attributes of two segments.
	public AnimatedValueVectorParam px = new AnimatedValueVectorParam("px");
	public AnimatedValueVectorParam py = new AnimatedValueVectorParam("py");

	//--- Unlike points, control points are only part of one segment.
	public AnimatedValueVectorParam cpx = new AnimatedValueVectorParam("cpx");
	public AnimatedValueVectorParam cpy = new AnimatedValueVectorParam("cpy");

	public BooleanParam closed = new BooleanParam( false, "closed" );

	protected static final float DEFAULT_TENSION = 0.3f;

	protected void registerPathParams()
	{
		registerParameter( px );
		registerParameter( py );
		registerParameter( cpx );
		registerParameter( cpy );
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

		int lastIndex = px.size();
		//--- When shape not closed, we cannot create seq from last point to first.
		if( !closed.get() ) lastIndex = lastIndex - 1;
	
		for (int i = 0; i < lastIndex; i++)
			addSeq( mask, i,  frame );

		if( closed.get() ) mask.closePath();

		return mask;
	}

	private void addSeq( GeneralPath mask, int index, float frame )
	{
		int lastIndex = index + 1;
		if( index == px.size() - 1 ) lastIndex = 0;

		Point2D.Float cp1 = getControlPoint( index, true, frame );
		Point2D.Float cp2 = getControlPoint( index, false, frame );

		mask.curveTo(	cp1.x,
				cp1.y,
				cp2.x,
				cp2.y,
				px.elem( lastIndex ).getValue( frame ),
				py.elem( lastIndex ).getValue( frame ) );
	}

	public void addPoint( Point2D.Float point )
	{
		//--- point
		px.get().add( new AnimatedValue( getIOP(), point.x ) );
		py.get().add( new AnimatedValue( getIOP(), point.y ) );
		//--- control point 1
		cpx.get().add( new AnimatedValue( getIOP(), point.x ) );
		cpy.get().add( new AnimatedValue( getIOP(), point.y ) );
		//--- control point 2
		cpx.get().add( new AnimatedValue( getIOP(), point.x) );
		cpy.get().add( new AnimatedValue( getIOP(), point.y) );
	}
	
	public void insertPoint( Point2D.Float point, int index )
	{
		//--- point
		px.get().insertElementAt( new AnimatedValue( getIOP(), point.x ), index );
		py.get().insertElementAt( new AnimatedValue( getIOP(), point.y ), index );
		//--- control point 1
		cpx.get().insertElementAt( new AnimatedValue( getIOP(), 0), index * 2 - 1 );
		cpy.get().insertElementAt( new AnimatedValue( getIOP(), 0), index * 2 - 1 );
		//--- control point 2
		cpx.get().insertElementAt( new AnimatedValue( getIOP(), 0), index * 2 - 1 );
		cpy.get().insertElementAt( new AnimatedValue( getIOP(), 0), index * 2 - 1 );

		setInsertControlPointPlaces( index );
	}

	public void removePoint( int index )
	{
		//--- point
		px.get().remove( index );
		py.get().remove( index );
		//--- control point 1
		cpx.get().remove( index * 2 );
		cpy.get().remove( index * 2 );
		//--- control point 2
		cpx.get().remove( index * 2 );
		cpy.get().remove( index * 2 );
	}

	public void initControlPoints()
	{
		for( int i = 0; i < cpx.size(); i++ )
		{
			cpx.elem( i ).clearKeyframes();
			cpy.elem( i ).clearKeyframes();
		}
		for( int i = 0; i < px.size(); i++ )
			setDefaultControlPointPlaces( i );
	}

	private void setDefaultControlPointPlaces( int segIndex )
	{
		int lastIndex = segIndex + 1;
		if( segIndex == px.size() - 1 ) lastIndex = 0;
		Point2D.Float start = new Point2D.Float( px.elem( segIndex ).getValue( 0 ),
							py.elem( segIndex ).getValue( 0 ) );
		Point2D.Float end = new Point2D.Float( px.elem( lastIndex ).getValue( 0 ),
							py.elem( lastIndex ).getValue( 0 ) );
		SVec forward = new SVec( start, end );
		SVec back = new SVec( end, start );
		SVec cp1 = forward.getMultipliedSVec( DEFAULT_TENSION );
		SVec cp2 = back.getMultipliedSVec( DEFAULT_TENSION );
		//--- cp 1
		cpx.elem( segIndex * 2 ).setValue( 0, cp1.getEndPos().x );
		cpy.elem( segIndex * 2 ).setValue( 0, cp1.getEndPos().y );
		//--- cp 2
		cpx.elem( segIndex * 2 + 1 ).setValue( 0, cp2.getEndPos().x );
		cpy.elem( segIndex * 2 + 1 ).setValue( 0, cp2.getEndPos().y );
	}

	private void setInsertControlPointPlaces( int index )
	{
		int previous = index - 1;
		int next = index + 1;
		if( next == px.size() )
			next = 0;
		if( previous < 0 )
			previous = px.size() - 1;

		Point2D.Float prevP = new Point2D.Float( px.elem( previous ).getValue( 0 ),
							py.elem( previous ).getValue( 0 ) );
		Point2D.Float nextP = new Point2D.Float( px.elem( next ).getValue( 0 ),
							py.elem( next ).getValue( 0 ) );
		Point2D.Float p = new Point2D.Float( px.elem( index ).getValue( 0 ),
							py.elem( index ).getValue( 0 ) );

		SVec forward = new SVec( p, nextP );
		SVec back = new SVec( p, prevP );
		SVec cp2 = forward.getMultipliedSVec( DEFAULT_TENSION );
		SVec cp1 = back.getMultipliedSVec( DEFAULT_TENSION );
		//--- cp 1
		cpx.elem( index * 2  - 1 ).setValue( 0, cp1.getEndPos().x );
		cpy.elem( index * 2  - 1 ).setValue( 0, cp1.getEndPos().y );
		//--- cp 2
		cpx.elem( index * 2 ).setValue( 0, cp2.getEndPos().x );
		cpy.elem( index * 2 ).setValue( 0, cp2.getEndPos().y );
	}

	private Point2D.Float getControlPoint( int seqindex, boolean startCP, float frame )
	{
		int index = seqindex * 2;
		if( !startCP ) index += 1;
		return new Point2D.Float( cpx.elem( index ).getValue( frame ), cpy.elem( index ).getValue( frame ) );
	}

}//end class
