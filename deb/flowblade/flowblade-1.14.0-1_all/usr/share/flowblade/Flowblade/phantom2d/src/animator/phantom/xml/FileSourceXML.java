package animator.phantom.xml;

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

import java.io.File;
import java.util.Vector;

import org.w3c.dom.Element;

import animator.phantom.controller.AppUtils;
import animator.phantom.controller.ProjectController;
import animator.phantom.renderer.FileSequenceSource;
import animator.phantom.renderer.FileSingleImageSource;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.VideoClipSource;
//import animator.phantom.renderer.MovieSource;
import animator.phantom.renderer.SequencePlaybackSource;

public class FileSourceXML extends AbstractXML
{
	public static String ELEMENT_NAME = "filesource";

	public static FileSource getObject( Element e )
	{
		//--- Creae object
		FileSource fs = null;
		int type = getInt( e, "type" );
		if( type == FileSource.IMAGE_FILE ) fs = new FileSingleImageSource();
		if( type == FileSource.IMAGE_SEQUENCE ) fs = new FileSequenceSource();
		if( type == FileSource.VIDEO_FILE ) fs = new VideoClipSource();

		//--- Get basic data
		fs.setID( getInt( e, "id" ) );
		fs.setType( type );
		String path = e.getAttribute( "file" );
		path = getFromSubFolderIfMissing( path );
		fs.setFile( new File( path ) );
		if( !fs.getFile().exists() )
		{
			System.out.println("FileSource file does not exist, path" + fs.getFile().getAbsolutePath() );
			fs.setResourceAvailable( false );
		}
		String path2 = e.getAttribute( "file2" );
		if( !path2.equals("null") ) 
			fs.setFile2( new File( path2 ) );

		//--- Get file info
		fs.setSizeEstimate( getLong( e, "memsize" ) );
		fs.setImageWidth( getInt( e, "imgwidth" ) );
		fs.setImageHeight( getInt( e, "imgheight" ) );

		if( type == FileSource.IMAGE_SEQUENCE ) 
			((SequencePlaybackSource )fs).loadInit();

		if( type == FileSource.VIDEO_FILE ) 
		{
			VideoClipSource vcs = (VideoClipSource ) fs;
			vcs.setLength( getInt( e, "length" ) );	
			vcs.setMD5id( e.getAttribute( "MD5id" ) );
			vcs.loadInit();
		}

		return fs;
	}

	public static Element getElement( FileSource fileSource )
	{
		Element e = doc.createElement( ELEMENT_NAME );
		e.setAttribute( "id", intStr( fileSource.getID() ) );
		e.setAttribute( "type", intStr( fileSource.getType() ) );
		e.setAttribute( "file", fileSource.getFile().getAbsolutePath() );
		if( fileSource.getFile2() == null ) 
			e.setAttribute( "file2", "null" );
		else 
			e.setAttribute( "file2", fileSource.getFile2().getAbsolutePath()  );
		e.setAttribute( "memsize",  longStr( fileSource.getSizeEstimate() ) );
		e.setAttribute( "imgwidth", intStr( fileSource.getImageWidth() ) );
		e.setAttribute( "imgheight", intStr( fileSource.getImageHeight() ) );
		
		if (fileSource.getType() == FileSource.VIDEO_FILE)
		{
			VideoClipSource vcs = (VideoClipSource ) fileSource;
			e.setAttribute( "length", intStr( vcs.getLength() ) );
			e.setAttribute( "MD5id",  vcs.getMD5id() );
		}
		 
		return e;
	}

	private static String getFromSubFolderIfMissing( String path )
	{
		File f = new File(path);
		if(f.exists() && !f.isDirectory())
			return path;

		File projectFile = new File( ProjectController.getLoadPath() );
		String parentDir = projectFile.getParentFile().getAbsolutePath();
		String fileName = f.getName();
		
		Vector<File> dirFiles = AppUtils.getAllFilesRecursively( parentDir );
		for (File lookUpFile : dirFiles)
		{
			System.out.println(lookUpFile.getName() );
			if ( !lookUpFile.isDirectory() && lookUpFile.getName().equals(fileName))
			{
				return lookUpFile.getAbsolutePath();
			}
		}
		return path;
	}

}//end class