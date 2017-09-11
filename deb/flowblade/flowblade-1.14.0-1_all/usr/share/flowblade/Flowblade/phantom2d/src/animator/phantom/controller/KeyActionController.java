package animator.phantom.controller;

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

import java.util.Vector;

//import animator.phantom.gui.ListFileSourcePanel;
//import animator.phantom.project.Bin;
import animator.phantom.renderer.RenderNode;

//--- handles copy-paste, delete actions.
public class KeyActionController
{
	private static Object lastSource;
	private static Vector<Object> lastItems;

	//@SuppressWarnings("unchecked")
	public static void copyItems( Object source )
	{
		/*
		lastSource = source;
		Vector<Object> items = new Vector<Object>();

		if( source == GUIComponents.renderFlowPanel )
		{
			Vector<?> nodes = GUIComponents.renderFlowPanel.getSelectedNodes();
			if( nodes.size() > 0 )
			{
				items.add( nodes.elementAt( 0 ) );
			}
		}

		if( source == GUIComponents.keyEditorContainerPanel )
		{
			if( EditorsController.getCurrentKeyFrame() != null )
				items.add( new Float( EditorsController.getCurrentKeyFrame().getValue() ) );
		}
		
		/*
		if( source == GUIComponents.binsPanel )
		{
			items = (Vector<Object>) GUIComponents.binsPanel.currentSelectPanel().getSelectedPanels().clone();
		}
		
		
		lastItems = items;
		*/
	}

	public static void setSinglePasteItem( Object source, Object item )
	{
		lastSource = source;
		Vector<Object> items = new Vector<Object>();
		items.add( item );
		lastItems = items;
	}

	public static void pasteItems( Object source )
	{
		if( lastSource != source )
			return;

		if( lastItems == null || lastItems.size() < 1 )
			return;

		if( source == GUIComponents.renderFlowPanel )
		{
			//MenuActions.cloneNode((RenderNode) lastItems.elementAt( 0 ) );
		}

		if( source == GUIComponents.keyEditorContainerPanel )
		{
			EditorsController.addKeyFrameForValue( ((Float) lastItems.elementAt( 0 )).floatValue() );
		}
	}
		
	public static void deleteItems( Object source )
	{
		if( source == GUIComponents.keyEditorContainerPanel )
		{
			EditorsController.deleteKeyFrame();
		}

		if( source == GUIComponents.renderFlowPanel )
		{
			//FlowController.deleteSelected(); 
		}

	}

	public static void selectAllItems( Object source )
	{

		if( source == GUIComponents.renderFlowPanel )
		{
			//FlowController.selectAll(); 
		}

		/*
		if( source == GUIComponents.binsPanel )
		{
			GUIComponents.binsPanel.currentSelectPanel().selectAll();
		}
		*/
	}

	public static void deSelectAllItems( Object source )
	{

		if( source == GUIComponents.renderFlowPanel )
		{
			//FlowController.clearSelection(); 
		}

		/*
		if( source == GUIComponents.binsPanel )
		{
			GUIComponents.binsPanel.deselectAll();
		}
		*/
	}

}//end class