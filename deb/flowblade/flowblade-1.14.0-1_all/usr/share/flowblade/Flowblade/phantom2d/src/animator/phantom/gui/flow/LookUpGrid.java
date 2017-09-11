package animator.phantom.gui.flow;

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

import java.awt.Rectangle;
import java.util.Vector;

//--- Grid used to access graphic objects in render flow.
// This is probably a very unnecessary optimization.
public class LookUpGrid
{
	//--- Work area is divided into grid. Vectors in lookUpGrid[x][y] have references 
	//--- to all flow boxes that have cornerpoints in given grid cell.
	private Vector<FlowGraphic>[][] lookUpGrid;
	//--- Size of lookUPGrid cell in pix.
	private static final int GRID_CELL_SIDE = 100;
	//--- Width of lookUpGrid.
	private int gridWidth;
	//--- Height of lookUpGrid
	private int gridHeight;

	
	//--------------------------------------------- CONSTRUCTOR
	@SuppressWarnings("unchecked")
	public LookUpGrid( int width, int height )
	{
		//--- Calculate lookUpGrid width and height;
		gridWidth = ( width / GRID_CELL_SIDE ) + 1;
		gridHeight = ( height / GRID_CELL_SIDE ) + 1;

		//--- Create lookUpGrid
		lookUpGrid = new Vector[ gridWidth ][ gridHeight ];

		//--- Create cell vectors.
		for( int i = 0; i < gridWidth; i++ )
			for( int j = 0; j < gridHeight; j++ )
				lookUpGrid[ i ][ j ] = new Vector<FlowGraphic>();
	}

	//--- Adds FlowGraphic to cells of lookUpGrid its's area intersects or contains.
	public void addFlowGraphicToGrid( FlowGraphic fGraphic )
	{
		Rectangle area = fGraphic.getArea();
		int x = area.x / GRID_CELL_SIDE;
		int y = area.y / GRID_CELL_SIDE;
		int xEnd =  ( area.x + area.width ) / GRID_CELL_SIDE;
		int yEnd = ( area.y + area.height ) / GRID_CELL_SIDE;
		
		for( int i = x; i <= xEnd; i++ )
		{
			for( int j = y; j <= yEnd; j++ )
			{
				Vector<FlowGraphic> cell = lookUpGrid[ i ][ j ];
				if( !cell.contains( fGraphic ) )
					cell.add( fGraphic );
			}
		}
	}

	//--- Removes FlowGraphics form cell in area.
	public void removeFlowGraphicFromGridInArea( FlowGraphic fGraphic, Rectangle area )
	{
		int x = area.x / GRID_CELL_SIDE;
		int y = area.y / GRID_CELL_SIDE;
		int xEnd =  ( area.x + area.width ) / GRID_CELL_SIDE;
		int yEnd = ( area.y + area.height ) / GRID_CELL_SIDE;
		
		for( int i = x; i <= xEnd; i++ )
		{
			for( int j = y; j <= yEnd; j++ )
			{
				Vector<FlowGraphic> cell = lookUpGrid[ i ][ j ];
				cell.remove( fGraphic );
			}
		}	

	}

	//--- Returns box from given point.(if exists)
	public FlowBox getBox( int x, int y )
	{
		Vector<FlowGraphic> cell = getLookUpCell( x, y );
		for( FlowGraphic graphic : cell )
		{
			if( graphic instanceof FlowBox )
			{
				FlowBox box = (FlowBox) graphic;
				if( box.pointInBoxArea( x, y ) )
					return box;
			}
		}
		//--- Given point is not in any box
		return null;
	}

	//--- Return lookUpGrid cell Vector for given point.
	public Vector<FlowGraphic> getLookUpCell( int x, int y )
	{
		int gridX = x / GRID_CELL_SIDE;
		int gridY = y / GRID_CELL_SIDE;
		//System.out.println("Getting box from cell:" + gridX + "," + gridY );
		try
		{
			return lookUpGrid[ gridX ][ gridY ];
		}
		catch (Exception e)
		{
			return lookUpGrid[ 2 ][ 2 ];
		}
	}

	//--- Returns all FlowGraphisc in grid cells in or partly in in the given area.
	public Vector<FlowGraphic> getFlowGraphicsFromArea( Rectangle rect )
	{
		Vector<FlowGraphic> areaGraphics = new Vector<FlowGraphic>();
		int gridX = rect.x / GRID_CELL_SIDE;
		int gridY = rect.y / GRID_CELL_SIDE;
		int gridXEnd = ( rect.x + rect.width ) / GRID_CELL_SIDE;
		int gridYEnd = ( rect.y + rect.height ) / GRID_CELL_SIDE;
	
		Vector<FlowGraphic> cell;
		FlowGraphic testGraphic;
		for( int i = gridX; i <= gridXEnd; i++ )
		{
			for( int j = gridY; j <= gridYEnd; j++ )
			{
				cell = lookUpGrid[ i ][ j ];
				
				for( int k = 0; k < cell.size(); k++ )
				{
					testGraphic = cell.elementAt( k );
					if( !areaGraphics.contains( testGraphic ) )
						if( testGraphic instanceof FlowBox) areaGraphics.add( testGraphic );
						else areaGraphics.add( 0, testGraphic );
				}
			}
		}
		return areaGraphics;
	}

}//end class

