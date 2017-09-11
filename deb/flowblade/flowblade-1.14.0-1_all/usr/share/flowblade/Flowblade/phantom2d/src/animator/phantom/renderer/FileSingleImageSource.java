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
import java.awt.image.DataBuffer;
import java.io.File;

import javax.swing.ImageIcon;

import animator.phantom.gui.GUIResources;
import animator.phantom.gui.GUIUtils;

//--- Objects of this class are used to access media files.
public class FileSingleImageSource extends FileSource
{
	//--- Image data
	private BufferedImage img = null;
	//--- file type icon
	private static ImageIcon bitmapIcon = GUIResources.getIcon( GUIResources.bmicon );

	//--- Dummy for loading.
	public FileSingleImageSource()
	{
		type = IMAGE_FILE;
	}

	//----------------------------------------- CONSTRUCTOR
	public FileSingleImageSource( File  f )
	{
		this.file = f;
		type = IMAGE_FILE;
	}

	public BufferedImage getBufferedImage()
	{
		return img;
	}

	public void cacheOrClearData()
	{
		if( inCache )
			if( img == null ) img = getBufferedImageFromFile( file );
		else
			img = null;
	}
	
	public void firstLoadData()
	{
		img = getBufferedImageFromFile( file );
		if( img == null )
		{
			hasResourceAvailable = false;
			return;
		}
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
        	DataBuffer db = img.getRaster().getDataBuffer();
		int bp = (int)Math.ceil( DataBuffer.getDataTypeSize(db.getDataType()) / 8f);
        	sizeEstimate = bp * db.getSize();
	}

	public void loadData(){ if( img == null ) img = getBufferedImageFromFile( file ); }
	public void clearData(){ img = null; }
	public boolean dataInMemory(){ return ( img != null ); }
	public ImageIcon getThumbnailIcon()
	{
		if( !fileAvailable() ) return noFileIcon;
		loadData();
		return GUIUtils.getThumbnailFromImage( img, 40, 30 );

	}
	public ImageIcon getFileTypeIcon()
	{
		return bitmapIcon;
	}

}//end class