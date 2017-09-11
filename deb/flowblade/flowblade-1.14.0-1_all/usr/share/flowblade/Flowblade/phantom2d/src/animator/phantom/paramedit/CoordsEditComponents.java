package animator.phantom.paramedit;

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

import java.awt.Component;
import java.util.Vector;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

/**
* A <code>Vector</code> of <code>AnimValueNumberEditor</code> editors for a <code>Vector</code> 
* of <code>AnimatedValue</code> parameters packed in a <code>AnimatedImageCoordinates</code> object.
* <p>
* When creating plugins of type MOVING_SOURCE this is created automatically. Use <code>PhantomPlugin.addCoordsEditors()</code>
* to explicitly create these editors.
*/
public class CoordsEditComponents
{
	//--- Components 
	private Vector <Component> editComponents = new Vector<Component>();
	/**
	* A constructor with <code>ImageOperation</code>. <code>ImageOperation</code>
	* object has to have an initialized <code>AnimatedImageCoordinates</code>. or this will 
	* fail with null exception. 
	* @param editIOP ImageOperation with instantiated <code>AnimatedImageCoordinates</code> member.
	*/ 
	public CoordsEditComponents( ImageOperation editIOP )
	{
		//--- Create GUI editor components.
		AnimatedImageCoordinates animCoords = editIOP.getCoords();
		AnimValueNumberEditor xEdit = new AnimValueNumberEditor( "X Position", animCoords.x );
		AnimValueNumberEditor yEdit = new AnimValueNumberEditor( "Y Position", animCoords.y );
		AnimValueNumberEditor xScaleEdit = new AnimValueNumberEditor( "X Scale", animCoords.xScale );
		AnimValueNumberEditor yScaleEdit = new AnimValueNumberEditor( "Y Scale", animCoords.yScale );
		AnimValueNumberEditor xAnchorEdit = new AnimValueNumberEditor( "Anchor X", animCoords.xAnchor);
		AnimValueNumberEditor yAnchorEdit = new AnimValueNumberEditor( "Anchor Y",animCoords.yAnchor );
		AnimValueNumberEditor rotationEdit= new AnimValueNumberEditor("Rotation",animCoords.rotation );

		//--- Put components in vector.
		editComponents.add( xEdit );
		editComponents.add( yEdit );
		editComponents.add( xScaleEdit );
		editComponents.add( yScaleEdit );
		editComponents.add( xAnchorEdit );
		editComponents.add( yAnchorEdit );
		editComponents.add( rotationEdit );
	}
	
	/**
	* Returns all editor components
	* @return All created editor components. 
	*/
	public Vector <Component> getEditComponents(){ return editComponents; }
	/**
	* Returns all editor components cast into <code>FrameChangeListener</code> components.
	* @return All created editor components as <code>FrameChangeListener</code> components.
	*/
	public Vector <FrameChangeListener> getEditorAsFrameChangeListeners()
	{
		Vector <FrameChangeListener>  retVec = new Vector <FrameChangeListener>();
		for( int i = 0; i < editComponents.size(); i++ )
		{
			retVec.add( (FrameChangeListener ) editComponents.elementAt( i ) );
		}
		return retVec;
	}

}//end class