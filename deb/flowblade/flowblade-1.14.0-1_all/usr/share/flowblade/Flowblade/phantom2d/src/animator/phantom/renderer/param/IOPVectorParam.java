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
* A parameter that has a Vector of RenderNode instances in render flow as value. The name of the class comes from the fact 
* the ImageOperation wrapped in a RenderNode is the functional part and the one used when
* parameter is used. ImageOperations do not have ids. 
* <p>
* Parameter is often used to describe parent child releationships between nodes.
*/
public class IOPVectorParam extends Param
{
	//--- Value object.
	private Vector<Integer> iopNodeIDs = new Vector<Integer>();

	/**
	* No parameter constructor.
	*/
	public IOPVectorParam(){}
	/**
	* Sets value using Vector of node ids.
	*/
	public void setNodeIDs( Vector<Integer> iopNodeIDs_ ){ iopNodeIDs = iopNodeIDs_; }
	/**
	* Returns value as Vector of node ids.
	*/
	public Vector<Integer> getNodeIDs(){ return iopNodeIDs; }

}//end class