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

import animator.phantom.controller.AppUtils;
import animator.phantom.gui.GUIUtils;

//--- Abstract base class for file sources that display numbered file sequences.
public abstract class SequencePlaybackSource extends FileSource
{
	private String extension;
	private String numpart;
	private String namePart;
	private String pathPart;
	private int firstNumber;
	private int lastNumber;//inclusive

	private static final int NAME_FIRST_PADDED = 0;
	private static final int NAME_FIRST_NOT_PADDED = 1;
	private static final int NO_NAME_PADDED = 2;
	private int frameNameType = NAME_FIRST_PADDED;

	public void init( File firstFramefile )
	{
		this.file = firstFramefile;//First file in sequence.

		if( this.file == null )
		{
			setResourceAvailable( false );
			return;
		}

		this.extension = AppUtils.getExtension( file );
		this.numpart = getNumpart( file );

		String fileName = file.getName();
		int numpartIndex = fileName.lastIndexOf( numpart );
		int numPartValue = Integer.parseInt( numpart );
		this.namePart = fileName.substring( 0, numpartIndex );

		if( namePart.length() == 0 ) frameNameType = NO_NAME_PADDED;
		else if( numpartIndex > 0 && numpart.length() == Integer.toString( numPartValue ).length() )
			frameNameType = NAME_FIRST_NOT_PADDED;
		else frameNameType = NAME_FIRST_PADDED;

		String absolutePath = file.getAbsolutePath();
		int pathPartIndex = absolutePath.lastIndexOf( File.separator );
		this.pathPart = absolutePath.substring( 0, pathPartIndex + 1 );

		this.firstNumber = Integer.parseInt( numpart );
		findLastInSequence();
	}

	public abstract void loadInit();

	public BufferedImage getClipImage( int clipFrame )
	{
		//--- This fails if file is longer there
		try
		{
			int frameNumber = Integer.parseInt( numpart ) + clipFrame;
			String frameNamePart = null;
			if( frameNameType == NO_NAME_PADDED )
				frameNamePart = getPaddedNumber( frameNumber );
			if( frameNameType == NAME_FIRST_PADDED )
				frameNamePart = namePart + getPaddedNumber( frameNumber );
			if( frameNameType == NAME_FIRST_NOT_PADDED )
				frameNamePart =  namePart + Integer.toString( frameNumber );
			StringBuffer buf = new StringBuffer( frameNamePart );
			buf.insert( 0, pathPart );
			buf.append("." );
			buf.append( extension );
			String framePath = buf.toString();
			return getBufferedImageFromFile( new File( framePath ) );
		}
		catch( Exception e )
		{
			setResourceAvailable( false );
			System.out.println("SequencePlaybackSource.getClipImage() failed");
			return null;
		}
	}

	public int getProgramLength()
	{
		return lastNumber - firstNumber + 1;//end frame inclusive
	}

	public void firstLoadData()
	{
		BufferedImage firstframe  = getBufferedImageFromFile( file );
		imgWidth = firstframe.getWidth();
		imgHeight = firstframe.getHeight();
        	DataBuffer db = firstframe.getRaster().getDataBuffer();
		int bp = (int)Math.ceil( DataBuffer.getDataTypeSize(db.getDataType()) / 8f);
        	sizeEstimate = bp * db.getSize();
		firstframe = null;
	}

	private void findLastInSequence()
	{
		boolean FIND_NEXT = true;
		int frameIndex = firstNumber;

		while( FIND_NEXT )
		{
			try
			{
				//--- Find out if exists.
				File f = getFileForNumber( frameIndex );
				if( f == null ) FIND_NEXT = false;
				else frameIndex++;
			}
			catch( Exception e )
			{
				System.out.println( "error" + e.getMessage() );
				FIND_NEXT = false;
			}
		}
		this.lastNumber = frameIndex - 1;
	}

	private File getFileForNumber( int num )
	{
		String path = getPaddedFilePath( num );
		File f = new File( path );
		//--- try non padded if padded not found
		if( !f.exists() )
		{
			path = getFilePath( Integer.toString( num ) );
			f = new File( path );
		}
		if( !f.exists() ) return null;
		return f;
	}

	private String getPaddedFilePath( int frameNumber )
	{
		return getFilePath( getPaddedNumber( frameNumber ) );
	}

	private String getPaddedNumber( int frameNumber )
	{
		return getPaddedNumberString(frameNumber, numpart.length() );
	}

	private String getFilePath( String numStr )
	{
		StringBuffer buf = new StringBuffer( numStr );
		buf.append("." );
		buf.append( extension );
		buf.insert( 0, namePart );
		buf.insert( 0, pathPart );
		return buf.toString();
	}

	public static String getNumpart( File f )
	{
		if(f == null) return null;

		String filename = f.getName();
		int pointIndex = filename.lastIndexOf('.');

		//--- get number part
		boolean next = true;
		int index = pointIndex;
		while( next )
		{
			try
			{
				index--;
				String sub = filename.substring( index, index + 1 );
				Integer.parseInt( sub );// if not number, catch and out
			}
			catch( Exception e )
			{
				next = false;
			}
		}
		
		index++;//take back failed increase
		if( index == pointIndex ) return null;
		String npart = filename.substring( index, pointIndex );
		return npart;
	}

	public ImageIcon getThumbnailIcon()
	{
		if( !fileAvailable() )
			return noFileIcon;
		if( !hasResourceAvailable() )
			return noFileIcon;

		BufferedImage firstframe = getBufferedImageFromFile( file );
		return GUIUtils.getThumbnailFromImage( firstframe, 40, 30);
	}

}//end class
