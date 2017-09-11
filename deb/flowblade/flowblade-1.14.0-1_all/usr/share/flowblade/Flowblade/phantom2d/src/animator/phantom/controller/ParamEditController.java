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

import animator.phantom.gui.ParamEditFrame;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

//--- Logic for param editing in window.
public class ParamEditController
{
	public static ParamEditFrame paramEditFrame = null;// is set via GUIComponents
	private static ImageOperation editTarget = null;

	//--- Display or create new paramedit window for ImageOperation.
	//--- Param edit window can handle null input.	
	public static void displayEditFrame( ImageOperation iop )
	{
		//--- These need to have own references
		//FlowController.setEditTargetNode( iop );
		FilterStackController.setEditTarget( iop );
		editTarget = iop;
		paramEditFrame.display( iop );
	}
	//--- Called when no iop selected.
	public static void clearEditframe()
	{ 
		displayEditFrame( null );
		//FlowController.setEditTargetNode( null );
	}
	//--- Returns ImageOperation being edited in parameditFrame.
	public static ImageOperation getParamEditIOP()
	{
		return paramEditFrame.getIOP();
	}
	//--- Called after current frame changed. Displays values for new frame.
	public static void updateEditFrame()
	{
		paramEditFrame.currentFrameChanged();
	}
	//--- Called after undo done.
	public static void undoUpdate()
	{
		paramEditFrame.undoDone();
	}

	public static ImageOperation getEditTarget(){ return editTarget; }
	
	//--- Called after parent or filter stack update
	public static void reBuildEditFrame()
	{
		if( paramEditFrame == null )
			return;

		paramEditFrame.displayGUI();
	}

	public static void addSelectedIOPToFilterStack( ImageOperation selIOP )
	{
		//ImageOperation selIOP = GUIComponents.nodesPanel.getSelectedIOP();
		if( editTarget.getFilterStack().size() < ImageOperation.STACK_MAX_SIZE )
		{
			ImageOperation addFilter;
			if( selIOP.getPlugin() == null )
			{
				addFilter = IOPLibrary.getNewInstance( selIOP.getClass().getName() );
			}
			else//is plugin, not raw iop
			{
				String pluginName = selIOP.getPlugin().getClass().getName();
				addFilter = IOPLibrary.getNewInstance( pluginName );
			}
			
			editTarget.getFilterStack().add( addFilter );
			addFilter.setFilterStackIOP( true );
			addFilter.copyTimeParams( editTarget );
			
			UpdateController.updateCurrentFrameDisplayers( false );
			GUIComponents.filterStackPanel.initFilterStack( editTarget.getFilterStack().size() - 1 );
		}
	}
	
}//end class