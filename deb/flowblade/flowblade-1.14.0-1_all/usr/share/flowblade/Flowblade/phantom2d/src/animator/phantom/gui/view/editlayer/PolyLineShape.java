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

import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.EditPointShape;
import animator.phantom.renderer.param.AnimatedValueVectorParam;
import animator.phantom.renderer.plugin.PolyLinePlugin;

public class PolyLineShape extends EditPointShape
{
	protected AnimatedValueVectorParam px;
	protected AnimatedValueVectorParam py;
	//protected PolyLinePlugin plm;
	protected PolyLineEditLayer layer;

	public PolyLineShape( 	AnimatedValueVectorParam px,
				AnimatedValueVectorParam py,
				PolyLinePlugin plm,
				PolyLineEditLayer layer )
	{
		this.px = px;
		this.py = py;
		//this.plm = plm;
		this.layer = layer;
		//--- value for frame 0 because frame position at creation time is unrelevant
		for( int i = 0; i < px.size(); i++ )
			editPoints.add( new EditPoint( px.elem( i ).getValue( 0 ), py.elem( i ).getValue( 0 ) ) );
	}

	//--- We are not using super's translation methods since we don't have anchor point.
	//--- note this method moves handle points to places corresponding to frame, does not change values.
	public void movePoints( int frame )
	{
		for( int i = 0; i < px.size(); i++ )
			editPoints.elementAt( i ).setPos( px.elem(i).getValue( frame ), py.elem(i).getValue( frame ) );
	}

	public void updateValues( int frame )
	{
		for( int i = 0; i < editPoints.size(); i++ )
		{
			px.elem( i ).setValue( frame, editPoints.elementAt( i ).x );
			py.elem( i ).setValue( frame, editPoints.elementAt( i ).y );
		}
	}

	//--- Used to create detection order, small are on top. ( or big ones will mask all. active is tested first)
	public float getAreaSize(){ return 0; }

	public boolean pointInArea( Point2D.Float p )
	{ 
		if( layer.isActive() ) return true;
			
		return false;
	}

}//end class