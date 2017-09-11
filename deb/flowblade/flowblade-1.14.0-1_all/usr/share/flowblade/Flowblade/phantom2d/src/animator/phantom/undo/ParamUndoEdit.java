package animator.phantom.undo;

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

import org.w3c.dom.Element;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.Param;
import animator.phantom.xml.ParamXML;

//--- Undo/redo for param value change
public class ParamUndoEdit extends PhantomUndoableEdit 
{
	private Param p;//--- parameter

	public ParamUndoEdit( ImageOperation iop, Param p, Element before, Element after )
	{
		this.iop = iop;
		this.p = p;
		this.beforeState = before;
		this.afterState = after;
	}
	public void undo()
	{
		ParamXML.readParamValue( beforeState, p );
		p.initCurrentValue();
	}

	public void redo()
	{
		ParamXML.readParamValue( afterState, p );
		p.initCurrentValue();
	}

}//--- end class