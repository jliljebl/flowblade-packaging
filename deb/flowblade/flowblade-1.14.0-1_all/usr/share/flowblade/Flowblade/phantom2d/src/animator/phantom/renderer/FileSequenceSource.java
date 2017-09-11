package animator.phantom.renderer;

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

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import animator.phantom.gui.GUIResources;

public class FileSequenceSource extends SequencePlaybackSource
{
	private static ImageIcon seqIcon = GUIResources.getIcon( GUIResources.bmseriesicon );

	//--- for loading.
	public FileSequenceSource(){}

	public FileSequenceSource( File  f )
	{
		type = IMAGE_SEQUENCE;
		init( f );
	}

	public void loadInit()
	{
		init( file );
	}

	public void loadData(){}//--- not cached
	public boolean dataInMemory(){ return false; }//--- not cached
	public void clearData(){}//--- not cached
	public void cacheOrClearData(){}//--- not cached

	public BufferedImage getBufferedImage(){ return null; }
	public ImageIcon getFileTypeIcon(){ return seqIcon; }

}//end class
