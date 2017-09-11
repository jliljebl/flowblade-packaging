package animator.phantom.gui.timeline;

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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import animator.phantom.gui.AnimFrameGUIParams;

public class TimeLineColumnRowLayout implements LayoutManager
{
	//--- Components
	private static final int START_GAP = 2;
	private static final int TOP_GAP = 2;
	private static final int ID_WIDTH = 24;
	private static final int NAME_WIDTH = 150;
	private static final int ACTIVE_WIDTH = 18;
	private static final int BLEND_WIDTH = AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH - ID_WIDTH - NAME_WIDTH - ACTIVE_WIDTH - START_GAP;
	
	//--- Constructor
	public TimeLineColumnRowLayout() 
	{

	}

	//--- Layout is performed here
    	public void layoutContainer(Container cont) 
	{
    		synchronized ( cont.getTreeLock() )
    		{
    			Dimension containerSize = cont.getSize();
    			int comps = cont.getComponentCount();
    			for (int i = 0 ; i < comps ; i++)
    			{
    				Component c = cont.getComponent(i);
    				switch( i )
    				{
    					//--- ID label
    					case 0:
    						c.setBounds( 	START_GAP,
    										0,
		    								ID_WIDTH,
		    								AnimFrameGUIParams.TE_ROW_HEIGHT);
    						break;
    					//--- Name label
    					case 1:
    						c.setBounds( 	ID_WIDTH,
    										0 ,
		    								NAME_WIDTH,
		    								AnimFrameGUIParams.TE_ROW_HEIGHT);
    						break;
        				//--- checkbox
        				case 2:
    						c.setBounds( 	ID_WIDTH + NAME_WIDTH,
    										0,
		    								ACTIVE_WIDTH,
		    								AnimFrameGUIParams.TE_ROW_HEIGHT);
    						break;
        				//--- blend combo box
        				case 3:
    						c.setBounds( 	ID_WIDTH + NAME_WIDTH + ACTIVE_WIDTH,
    										TOP_GAP,
		    								BLEND_WIDTH,
		    								AnimFrameGUIParams.TE_ROW_HEIGHT - 4);
    						break;	
    					default:
    						break;

    				}
    			}
    		}
    	}

	//--- noop
	public void addLayoutComponent(String name, Component comp) {}
	//--- noop
	public void removeLayoutComponent(Component comp) {}

	public Dimension preferredLayoutSize(Container cont ){ return new Dimension( AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH , AnimFrameGUIParams.TE_ROW_HEIGHT  ); }

	public Dimension minimumLayoutSize( Container cont ){ return new Dimension( AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH , AnimFrameGUIParams.TE_ROW_HEIGHT  ); }

}//end class
