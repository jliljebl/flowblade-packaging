package animator.phantom.gui.view.editlayer;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.EditPointShape;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.plugin.PolyCurvePlugin;
//import animator.phantom.renderer.imagesource.PolyCurvePathIOP;

//--- A shape made of editpoints and control points
public class PolyCurveShape extends EditPointShape
{
	protected AnimatedValueVectorParam px;//--- curve end points
	protected AnimatedValueVectorParam py;

	protected AnimatedValueVectorParam cpx;//--- curve edit points
	protected AnimatedValueVectorParam cpy;

	//protected PolyCurvePlugin plm;
	protected PolyCurveEditLayer layer;

	//--- Editpoint identifying help vectors.
	public Vector<EditPoint> controlPoints = new Vector<EditPoint>();

	public PolyCurveShape( 	PolyCurvePlugin plm,
				PolyCurveEditLayer layer )
	{
		this.px = plm.px;
		this.py = plm.py;
		this.cpx = plm.cpx;
		this.cpy = plm.cpy;
		//this.plm = plm;
		this.layer = layer;
		loadEditPoints();
	}

	public void loadEditPoints()
	{
		editPoints.clear();
		controlPoints.clear();
		//--- value for frame 0 because frame position at creation time is unrelevant
		for( int i = 0; i < px.size(); i++ )
			editPoints.add( new EditPoint( px.elem( i ).getValue( 0 ), py.elem( i ).getValue( 0 ) ) );
		//--- Control points
		for( int i = 0; i < cpx.size(); i++ )
		{
			EditPoint cp = new EditPoint( cpx.elem( i ).getValue( 0 ), cpy.elem( i ).getValue( 0 ) );
			editPoints.add( cp );
			controlPoints.add( cp );
			cp.setDisplayType( EditPoint.CONTROL_POINT );
		}
	}

	//--- We are not using super's translation methods since we don't have anchor point.
	public void movePoints( int frame )
	{
		//--- edit(curve) points
		for( int i = 0; i < px.size(); i++ )
			editPoints.elementAt( i ).setPos( px.elem(i).getValue( frame ), py.elem(i).getValue( frame ) );
		//--- control points
		for( int i = px.size(); i < px.size() + cpx.size(); i++ )
			editPoints.elementAt( i ).setPos( cpx.elem( i - px.size() ).getValue( frame ), cpy.elem( i - px.size() ).getValue( frame ) );
	}

	public void updateValues( int frame )
	{
		//--- edit(curve) points
		for( int i = 0; i < px.size(); i++ )
		{
			px.elem( i ).setValue( frame, editPoints.elementAt( i ).x );
			py.elem( i ).setValue( frame, editPoints.elementAt( i ).y );
		}
		//--- control points
		for( int i = 0; i < cpx.size(); i++ )
		{
			cpx.elem( i ).setValue( frame, editPoints.elementAt( i + px.size() ).x );
			cpy.elem( i ).setValue( frame, editPoints.elementAt( i + px.size() ).y );
		}
	}

	//--- Used to create detection order, small are on top. ( or big ones will mask all. active is tested first)
	public float getAreaSize(){ return 0; }
	
	//--- This is used to determine if mouse event is passed. This shape wants all mouse events when active.
	public boolean pointInArea( Point2D.Float p )
	{ 
		if( layer.isActive() ) return true;
			
		return false;
	}

}//end class