package animator.phantom.paramedit;

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
* GUI editor components must implement this to update view after undo / redo actions. 
* Editors that implement <code>FrameChangeListener</code> are notified using
* that interface for undo events so they don't need to implement this. 
* 
*/
public interface UndoListener
{
	/**
	* Called after undo/redo action. Editors implement method to update gui to display new state.
	*/
	public void undoDone();

}//end class