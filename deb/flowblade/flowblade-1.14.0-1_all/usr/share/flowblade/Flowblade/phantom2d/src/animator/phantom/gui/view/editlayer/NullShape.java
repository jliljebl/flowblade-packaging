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
import animator.phantom.renderer.ImageOperation;

/**
* A cross like shape for editing a position. Displays rotation too, but cannot be used to edit rotation.
*/
public class NullShape extends AnimCoordsEditShape
{
	private static final int ARM_LENGTH = 10;
	private static final int X_OFF = 0;
	public static final int HANDLE_POINT = 0;
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 0;

	/**
	* Constructor with <code>ImageOperation</code>.
	*/
	public NullShape( ImageOperation iop )
	{
		//--- places edit points in vector to correspond to hard coded indexes HANDLE_POINT, UP, DOWN...
		for(int i = 0; i < 5 ; i++)
		{
			if( i == 0 )
 				editPoints.add( new EditPoint( EditPoint.INVISIBLE_POINT ) );
			else
			{
				EditPoint p = new EditPoint( EditPoint.INVISIBLE_POINT );
				p.setHittable( false );
				editPoints.add( p );
			}
		}

		this.iop = iop;
	}

	/**
	* Sets edit points into untransformed positions in a shape of cross and a hittable editpoint in the middle.
	*/
	public void resetEditPoints()
	{
		editPoints.elementAt( HANDLE_POINT ).setPos( X_OFF,0 );
		editPoints.elementAt( UP ).setPos( 0,-ARM_LENGTH );
		editPoints.elementAt( DOWN ).setPos( 0,ARM_LENGTH );
		editPoints.elementAt( LEFT ).setPos( -ARM_LENGTH,0 );
		editPoints.elementAt( RIGHT ).setPos( ARM_LENGTH,0 );
	}

	/**
	* Returns always false because this has no area, just single hittable edit point in the middle.
	*/
	public boolean pointInArea( Point2D.Float p ){ return false; }

}//end class