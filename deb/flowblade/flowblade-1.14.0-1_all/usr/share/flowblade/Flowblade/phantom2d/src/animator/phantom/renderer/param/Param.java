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

import java.util.StringTokenizer;

import org.w3c.dom.Element;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.undo.ParamUndoEdit;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.xml.ParamXML;

/**
* Base class for all persistant user editable parameters in plugins.
*/
public class Param
{
	/**
	* Reference to the iop this is part of.
	*/
	protected ImageOperation iop = null;
	/**
	* Set by iop when buildin plugin / iop
	*/
	private String id;
	/**
	* User displayed name, ususlly set by editor from given label
	*/
	private String paramName = "param name not set";
	/**
	* Used as before state when creating undos, see registerUndo( boolean isSignificant )
	*/
	private Element currentValue;
	/**
	* This is used when param is registered to set reference.
	*/
	public void setIOP( ImageOperation iop ){ this.iop = iop; }

	/**
	* Used to set currentValue after undo/redo to correspond to state.
	*/
	public void initCurrentValue()
	{ 
		currentValue = ParamXML.getElement( this );
	}

	/**
	* Used when registering paramater to set persistance id. 
	*/
	public void setID( String idStr){ id = idStr; }
	/**
	* Returns id.
	*/
	public String getID(){ return id; }
	/**
	* Used to get current value when doing load/save or undo/redo.
	* @return Value as DOM element
	*/
	public Element getCurrentValue(){ return currentValue; }
	/**
	* Used to set current value when doing load/save or undo/redo.
	* @param e Value as DOM element.
	*/
	public void setCurrentValue( Element e ){ currentValue = e; }
	/**
	* Sets param name.
	* @param name User visisble name.
	*/
	public void setParamName( String name ){ paramName = name; }
	/**
	* Sets param user visible name.
	* @return User visisble name.
	*/
	public String getParamName(){ return paramName; }
	/**
	* This should probably go away.
	*/
	public static String getXMLName( String pname )
	{
		String lcase = pname.toLowerCase();
		StringTokenizer t = new StringTokenizer( lcase, " ");
        	StringBuffer result = new StringBuffer("");
		while (t.hasMoreTokens()) { result.append(t.nextToken()); }
        	return result.toString();
	}
	/**
	* This is called to push significant packet into undo stack AFTER the value has already been set by editor.
	* Not calling this after value change makes edits non-undoable. If setting value produces a lot of
	* values because of for example a mouse drag then register only the last one. 
	*/
	public void registerUndo()
	{
		registerUndo( true );
	}
	/**
	* This is called to push a usually unsignificant packet into undo stack AFTER the value has already been set by editor.
	* Not calling this after value change makes edits non-undoable. If setting value produces a lot of
	* values because of for example a mouse drag then register only the last one. 
	* Setting parameter <code>isSignificant</code> false makes registerd undo happen atomically with the last significant registered undo.
	* Typical use of unsignicant undos is packing x and y values to do undos simultaniously after setting position in ViewEditor.
	*/
	public void registerUndo( boolean isSignificant )
	{
		Element newVal = ParamXML.getElement( this );
		ParamUndoEdit undoEdit = new ParamUndoEdit( iop, this, currentValue, newVal );
		undoEdit.setSignificant( isSignificant );
		PhantomUndoManager.addUndoEdit( undoEdit );
		currentValue = newVal;
	}

}//end class
