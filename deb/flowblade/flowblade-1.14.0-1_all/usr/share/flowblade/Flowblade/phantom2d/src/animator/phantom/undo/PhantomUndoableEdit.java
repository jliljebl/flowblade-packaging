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

//--- All extending are signifigant non-compound edits that are always placed inside a compound edit.
public abstract class PhantomUndoableEdit
{
	protected Element beforeState;
	protected Element afterState;
	protected ImageOperation iop;
	private boolean isSignificant = true;

	public boolean isSignificant(){ return isSignificant; }
	public void setSignificant( boolean val){ isSignificant = val; }

	public abstract void undo();
	public abstract void redo();

	public ImageOperation getIOP(){ return iop; }

	//--- Just to look nice
	public void doEdit(){ redo(); }

}//end class
