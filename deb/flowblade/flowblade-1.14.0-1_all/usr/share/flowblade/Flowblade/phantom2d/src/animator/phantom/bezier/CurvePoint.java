package animator.phantom.bezier;

/*
    Copyright Janne Liljeblad

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

//--- Used to describe Catmull-rom curves
public class CurvePoint implements Comparable<Object>
{
	public int x = -1;
	public int y = -1;

	public CurvePoint(){}
	public CurvePoint( int x, int y )
	{
		this.x = x;
		this.y = y;
	}

	public int compareTo( Object o )
	{
		CurvePoint cp2 = ( CurvePoint ) o;
		if( cp2.x == x ) return 0;
		if( cp2.x > x ) return -1;
		return 1;
	}	

}//end class