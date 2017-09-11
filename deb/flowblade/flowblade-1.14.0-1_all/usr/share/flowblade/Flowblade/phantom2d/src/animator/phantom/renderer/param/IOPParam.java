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


/**
* A parameter that has an instance of RenderNode in render flow as value. The name of the class comes from the fact 
* the ImageOperation wrapped in a RenderNode is the functional part and the one used when
* parameter is used. ImageOperations do not have ids. 
* <p>
* Parameter is used to describe parent child releationships between nodes.
*/
public class IOPParam extends Param
{
	private int iopNodeID = -1;

	/**
	* No parameter constructor.
	*/
	public IOPParam(){}
	/**
	* Constructor that uses ImageOperation instance to get node id.
	*/
	/*
	public IOPParam( ImageOperation defaultValue )
	{
		RenderNode iopNode = EditorRendererInterface.getNode( defaultValue );
		if( iopNode == null ) iopNodeID = -1;
		else iopNodeID = iopNode.getID();
	}
	*/
	/**
	* Returns the ImageOperation wrapped in node that has its id saved here. 
	*/
	/*
	public ImageOperation get()
	{
		RenderNode iopNode = EditorRendererInterface.getNode( iopNodeID );
		if( iopNode != null ) return iopNode.getImageOperation();
		else return null;
	}
	*/
	/**
	* Sets value using ImageOperation instance to get node id.
	*/
	/*
	public void set( ImageOperation newValue )
	{
		RenderNode iopNode = EditorRendererInterface.getNode( newValue );
		if( iopNode == null ) iopNodeID = -1;
		else iopNodeID = iopNode.getID();
	}
	*/
	/**
	* Sets value using node id.
	*/
	public void setNodeID( int newId )
	{
		iopNodeID = newId;
	}
	/**
	* Returns value as node id.
	*/
	public int getNodeID(){ return iopNodeID; }

}//end class