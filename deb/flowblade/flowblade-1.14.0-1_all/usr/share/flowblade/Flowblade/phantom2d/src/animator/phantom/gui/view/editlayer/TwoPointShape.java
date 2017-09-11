package animator.phantom.gui.view.editlayer;

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

import java.awt.geom.Point2D;

import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.EditPointShape;
import animator.phantom.renderer.param.AnimatedValue;

/**
* A editable shape made of of two <code>EditPoints</code>.
*/
public class TwoPointShape extends EditPointShape
{
	//--- Edit target parameters
	private AnimatedValue px1;
	private AnimatedValue py1;
	private AnimatedValue px2;
	private AnimatedValue py2;
	/**
	* Constructor with edit target parameters for two points.
	*/
	public TwoPointShape( AnimatedValue px1, AnimatedValue py1,  AnimatedValue px2,  AnimatedValue py2 )
	{
		//--- value for frame 0 because frame position at creation time is unrelevant
		editPoints.add( new EditPoint( px1.getValue( 0 ), py1.getValue( 0 ) ) );
		editPoints.add( new EditPoint( px2.getValue( 0 ), py2.getValue( 0 ) ) );

		this.px1 = px1;
		this.py1 = py1;
		this.px2 = px2;
		this.py2 = py2;
	}
	/**
	* Moves edit points to positions determined by values of edit target parameters in frame.  
	*/
	public void movePoints( int frame )
	{
		editPoints.elementAt( 0 ).setPos( px1.getValue( frame ), py1.getValue( frame ) );
		editPoints.elementAt( 1 ).setPos( px2.getValue( frame ), py2.getValue( frame ) );
	}
	/**
	* Set edit target values in frame from edit point positions.
	*/
	public void updateValues( int frame )
	{
		px1.setValue( frame, editPoints.elementAt( 0 ).x );
		py1.setValue( frame, editPoints.elementAt( 0 ).y );
		px2.setValue( frame, editPoints.elementAt( 1 ).x  );
		py2.setValue( frame, editPoints.elementAt( 1 ).y );
	}
	/**
	* Registers undos for all edit target parameters.
	*/
	public void registerUndos()
	{
		px1.registerUndo();
		py1.registerUndo( false );//to pack all as single undo;
		px2.registerUndo( false );
		py2.registerUndo( false );
	}
	/**
	* Returns always false, this has no area, just edit points.
	*/
	public boolean pointInArea( Point2D.Float p ){ return false; }

}//end class