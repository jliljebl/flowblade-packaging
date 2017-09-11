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

import animator.phantom.bezier.CRCurve;
import animator.phantom.paramedit.SingleCurveEditor;

/**
* A Catmull-Rom curve value parameter. Curve is expressed as a <code> int[256]</code> array of values in range 0 - 255.
* @see SingleCurveEditor
*/
public class CRCurveParam extends Param
{
	public CRCurve curve = new CRCurve();

	/**
	* Constructor for persistance use.
	*/
	public CRCurveParam(){};

	/**
	* Constructor with user displayed name.
	* @param pName Paramter name.
	*/
	public CRCurveParam( String paramName )
	{
		String name = getXMLName( paramName );
		setParamName( name );
	}

}//end class
