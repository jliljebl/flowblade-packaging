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

import java.util.Vector;

import javax.swing.JPanel;

import animator.phantom.project.Bin;
import animator.phantom.renderer.FileSource;

//--- Interface of a panel for selecting and manipulating FileSources in a bin.
//--- This was used to interface deifferent media select panels, only exists currently
public abstract class FileSourceSelectPanel extends JPanel
{
	//--- Initializes for given bin.
	public abstract void init( Bin bin );

	public abstract void initSelectPanel();

	//--- Creates and adds fileSourcePanels.
	public abstract void addFileSources( Vector<FileSource> addFileSources );

	public abstract void addFileSource( FileSource fs );
	
	//--- Deletes selected filesources from bin.
	public abstract void deleteSelected();

	//--- Returns FileSource that is in the last selected panel or null if nothinf selected.
	public abstract FileSource getLastSelectionFileSource();

	//--- Returns vector of selected filesources.
	public abstract Vector<FileSource> getSelected();
	//--- 
	@SuppressWarnings("rawtypes")
	public abstract Vector getSelectedPanels();

	public abstract void addPanel( Object panel );
	//--- 
	public abstract void removePanel( Object panel );

	//--- Return true if panel is empty.
	public abstract boolean isEmpty();

	public abstract void deselectAll();

	public abstract void selectAll();

	public abstract void reInitSelectPanel();

 	public abstract void reInitFromBinContents();

	public abstract void updatePanelName( FileSource panelFS );

}//end class
