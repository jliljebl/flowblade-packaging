package animator.phantom.gui;

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

public class BigEditorsLayout implements LayoutManager
{
	//public static int MID_Y = EditorPersistance.getIntPref( EditorPersistance.LAYOUT_MID );

	private int STRIP_HEIGHT = 35;
	private int STRIP_INSET = 0;
	private int BIG_EDITOR_INSET = 0;
	private int BIG_EDITOR_UP_INSET = 4;
	private int LEFT_GAP = 0;
	private int RIGHT_GAP = 0;
	private int TOP_GAP = 0;
	private int VIEW_EDITOR_HEIGHT_REDUCTION = 0;
	private int PARAM_EDIT_WIDTH = 360;


	public BigEditorsLayout(){}

	//--- This all depends on order that components are added into container
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
					//--- View Editor
					case 0:
						c.setBounds(	LEFT_GAP,
										TOP_GAP,
										containerSize.width - LEFT_GAP - RIGHT_GAP - PARAM_EDIT_WIDTH,
										containerSize.height - STRIP_HEIGHT - TOP_GAP - VIEW_EDITOR_HEIGHT_REDUCTION );
						Container c1 = (Container) c;
						Component e1 = c1.getComponent( 0 );//--- There will be one.
						e1.setPreferredSize(
							new Dimension(  containerSize.width - BIG_EDITOR_INSET - LEFT_GAP - RIGHT_GAP - PARAM_EDIT_WIDTH ,
									containerSize.height - STRIP_HEIGHT - BIG_EDITOR_UP_INSET - TOP_GAP - VIEW_EDITOR_HEIGHT_REDUCTION) );

						break;
					//--- ActionButtons
					case 1:
						c.setBounds( 	LEFT_GAP + STRIP_INSET,
								containerSize.height - STRIP_HEIGHT + 2,
								containerSize.width - RIGHT_GAP,
								STRIP_HEIGHT);
						break;
					//--- Param edit frame
					case 2:
						c.setBounds( 	containerSize.width - LEFT_GAP - RIGHT_GAP - PARAM_EDIT_WIDTH,
								TOP_GAP,
								PARAM_EDIT_WIDTH,
								containerSize.height - STRIP_HEIGHT - TOP_GAP - VIEW_EDITOR_HEIGHT_REDUCTION);
					default:
						break;

				}
			}
		}
    	}

	//---------------------------------------------------- LAYOUT MANAGER METHODS
	//--- noop
	public void addLayoutComponent(String name, Component comp) {}
	//--- noop
	public void removeLayoutComponent(Component comp) {}
	public Dimension preferredLayoutSize(Container cont ){ return new Dimension( 100, 100 ); }
	public Dimension minimumLayoutSize(Container cont ){ return new Dimension( 100, 100 ); }

}//end class
