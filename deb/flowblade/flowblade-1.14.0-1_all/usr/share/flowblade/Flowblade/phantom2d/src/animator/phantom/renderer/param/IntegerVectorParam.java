package animator.phantom.renderer.param;

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

import java.util.Vector;

/**
* An integer <code>Vector</code> parameter. Does not have a GUI editor component.
* This is used for example to store color samples when creating a color range key.
*/
public class IntegerVectorParam extends Param
{
	private Vector<Integer> vec = new Vector<Integer>();

	/**
	* No params constructor.
	*/
	public IntegerVectorParam(){}

	/**
	* Constructor with parameter name.
	* @param pName Paramter name.
	*/
	public IntegerVectorParam( String paramName)
	{
		setParamName( paramName );
	}

	/**
	* Returns parameter value.
	* @return <code>Vector</code> value
	*/
	public Vector<Integer> get(){ return vec; }

	/**
	* Sets parameter value.
	* @param newVec New <code>Vector</code> value for parameter. 
	*/
	public void set( Vector<Integer> newVec ){ vec = newVec; }

}//end class