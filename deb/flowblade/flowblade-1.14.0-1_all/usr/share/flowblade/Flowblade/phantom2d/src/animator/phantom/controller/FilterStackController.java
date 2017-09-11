package animator.phantom.controller;

/*
    Copyright Janne Liljeblad

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

import animator.phantom.gui.modals.FilterStackEdit;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

//--- Contains logic for manipulating pre-transform filter stacks and related GUI update code.
public class FilterStackController
{
	private static ImageOperation editTarget;

	public static final int STACK_MAX_SIZE = 7;

	public static void displayEditor( ImageOperation iop )
	{
		if( GUIComponents.filterStackEdit != null )
			return;
		GUIComponents.filterStackEdit = new FilterStackEdit( iop );
	}

	public static void closeEditor()
	{
		if( editTarget == GUIComponents.filterStackEdit.getIop() )
			ParamEditController.reBuildEditFrame();
		else
		{
			GUIComponents.filterStackEdit.getIop().reCreateEditPanel();
			editTarget = null;
			GUIComponents.filterStackEdit.getIop();
			UpdateController.editTargetIOPChangedFromStackEditor( null );
			ParamEditController.displayEditFrame( GUIComponents.filterStackEdit.getIop() );
		}

		GUIComponents.filterStackEdit.setVisible( false );
		GUIComponents.filterStackEdit.dispose();
		GUIComponents.filterStackEdit = null;
	}

	public static void addFilter( ImageOperation iop, ImageOperation filterIOP )
	{
		if( iop.getFilterStack().size() < STACK_MAX_SIZE )
		{
			ImageOperation addFilter;
			if( filterIOP.getPlugin() == null )
			{
				addFilter = IOPLibrary.getNewInstance( filterIOP.getClass().getName() );
				iop.getFilterStack().add( addFilter );
			}
			else//is plugin, not raw iop
			{
				String pluginName = filterIOP.getPlugin().getClass().getName();
				addFilter = IOPLibrary.getNewInstance( pluginName );
				iop.getFilterStack().add( addFilter );
			}
			addFilter.setFilterStackIOP( true );
			addFilter.copyTimeParams( iop );
			
			UpdateController.updateCurrentFrameDisplayers( false );
		}
	}

	public static void removeFilter( ImageOperation iop, int index )
	{
		if(  index == -1 )
			return;

		iop.getFilterStack().removeElementAt( index );

 		UpdateController.valueChangeUpdate();
	}

	public static void moveFilterDown( ImageOperation iop, int index )
	{
		if( index == iop.getFilterStack().size() - 1 || index == -1 )
			return;

		ImageOperation filter = (ImageOperation) iop.getFilterStack().remove( index );
		index++;
		iop.getFilterStack().insertElementAt( filter, index );
		GUIComponents.filterStackEdit.initFilterStack( index );

		UpdateController.valueChangeUpdate();
	}

	public static void moveFilterUp( ImageOperation iop, int index )
	{
		if( index < 1 )
			return;
		ImageOperation filter = (ImageOperation) iop.getFilterStack().remove( index );
		index--;
		iop.getFilterStack().insertElementAt( filter, index );
		GUIComponents.filterStackEdit.initFilterStack( index );

		UpdateController.valueChangeUpdate();
	}

	public static void setStackIOPEditTarget( ImageOperation iop, int index )
	{
		if(  index == -1 )
			return;

		ImageOperation stackFilter = iop.getFilterStack().elementAt( index );
		UpdateController.editTargetIOPChangedFromStackEditor( stackFilter );
		ParamEditController.displayEditFrame( stackFilter );
		//--- repaint happens in setEdittarget() below which is called from ParamEditController
	}

	public static void setEditTarget( ImageOperation iop )
	{
		editTarget = iop;
		if( GUIComponents.filterStackEdit != null )
		{
			GUIComponents.filterStackEdit.initFilterStack();
			GUIComponents.filterStackEdit.repaint();
		}
	}

	public static ImageOperation getEditTarget(){ return editTarget; }

}//end class
