package animator.phantom.renderer.param;

/*
    Copyright Janne Liljeblad.

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

import java.util.Vector;

/**
* A parameter with a <code>Vector</code> of <code>AnimatedValue</code> parameters. 
* <p>
* This parameter can for example be used to describe a shape
* with unknown number of animated points.
*/
public class AnimatedValueVectorParam extends Param 
{

	private Vector<AnimatedValue> animVals = new Vector<AnimatedValue>();

	/**
	* No params constructor.
	*/
	public AnimatedValueVectorParam(){}

	/**
	* Constructor with user displayed name.
	* @param pName Paramter name.
	*/
	public AnimatedValueVectorParam( String pName )
	{
		String name = getXMLName( pName );
		setParamName( name );
	}

	/**
	* Returns AnimatedValue at index.
	*/
	public AnimatedValue elem( int index ){ return animVals.elementAt( index ); }

	/**
	* Returns Vector of AnimatedValues.
	*/
	public Vector<AnimatedValue> get(){ return animVals; }
	/**
	* Replaces value Vector.
	*/
	public void set( Vector<AnimatedValue> newVec ){ animVals = newVec; }

	/**
	* Returns size of value Vector.
	*/
	public int size(){ return animVals.size(); }

}//end class