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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import animator.phantom.controller.AppUtils;
import animator.phantom.gui.GUIUtils;

//--- Objects extending this class are used to access media files.
public abstract class FileSource implements Comparable<Object>
{
	protected File file;
	protected File file2;
	protected int type;
	private int id = -1;

	protected int imgWidth = -1;
	protected int imgHeight = -1;

	protected boolean inCache = false;
	protected long sizeEstimate = -1;

	public static final int IMAGE_FILE = 0;
	public static final int IMAGE_SEQUENCE = 1;
	public static final int VIDEO_FILE = 2;

	private static String [] descs = { "BITMAP IMAGE","BITMAP IMAGE SEQUENCE","VIDEO FILE" };
	protected static ImageIcon noFileIcon;
	
	protected boolean hasResourceAvailable = true;

	static
	{
		//--- Make no files icon
 		//--- make bigger then needed to get some softening from scaling
		BufferedImage tmp = new BufferedImage(  40 * 2,
							30 * 2,
							BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = tmp.createGraphics();
		g.setColor( Color.black );
		g.fillRect( 0,0,40 * 2,30 * 2 );
		Line2D.Float l1 = new Line2D.Float( 35, 25, 85, 65 );
		Line2D.Float l2 = new Line2D.Float( 35, 65, 85, 25 );
		g.setColor( Color.red );
		g.setStroke( new BasicStroke(10));
		g.draw( l1 );
		g.draw( l2 );
		noFileIcon = GUIUtils.getThumbnailFromImage( tmp, 40, 30);
	}

	//-------------------------------------------- INTERFACE
	public int getID(){ return id; }
	public void setID( int newID ){ id = newID; }
	public int getType(){ return type; }
	public void setType( int t ){ type = t; }
	public String getTypeDesc(){ return descs[ type ]; }
	public String getName(){ return file.getName(); }
	public File getFile(){ return file; }
	public void setFile( File f ){ file = f; }
	public File getFile2(){ return file2; }
	public void setFile2( File f ){ file2 = f; }
	public boolean fileAvailable()
	{
		if( file == null )
			return false;

		return file.exists(); 
	}
	//--- This is an optimization for bin display 
	public boolean hasResourceAvailable(){ return hasResourceAvailable; }
	public void setResourceAvailable( boolean value ){ hasResourceAvailable = value; }
	public String getSizeString()
	{
		String ret;
		int kbs = (int)(sizeEstimate / (long) 1024);
		if( kbs < 1024 ) ret = Integer.toString( kbs ) + "kB";
		else ret = AppUtils.getMBString( sizeEstimate );
		return ret;
	}

	//------------------------------------- IMAGE
	public abstract BufferedImage getBufferedImage();
	public abstract ImageIcon getThumbnailIcon();
	public abstract ImageIcon getFileTypeIcon();
	public int getImageWidth(){ return imgWidth; }
	public int getImageHeight(){ return imgHeight; }	
	public void setImageWidth( int w ){ imgWidth = w; }
	public void setImageHeight( int h ){ imgHeight = h; }
	protected BufferedImage getBufferedImageFromFile( File f )
	{
		try
		{
			BufferedImage loadImg = ImageIO.read( f );

			//--- Force alpha if missing. Keep if exists.
			BufferedImage img = new BufferedImage( 	loadImg.getWidth(),
								loadImg.getHeight(),
								BufferedImage.TYPE_INT_ARGB );
			Graphics2D g2 = img.createGraphics();
			g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC, 1.0f ) );
			g2.drawRenderedImage( loadImg, null );
			g2.dispose();
			
			return img;
		}
		catch( Exception e )
		{
			System.out.println("BITMAP IMAGE LOAD FAILED FOR:"+ f.getAbsolutePath() );
			hasResourceAvailable = false;
			return null;
		}
	}

	//----------------------------------- DATA AND CACHE MANAGEMENT
	//--- loads data into memory and does some initialization if needed, e.g picks up dimensions
	public abstract void firstLoadData();
	//--- Loads data into memory
	public abstract void loadData();
	//--- true if file source loaded and in memory
	public abstract boolean dataInMemory();
	//--- Releases memory representaion of data
	public abstract void clearData();
	//--- This is called after flag for being in cache is set by a MemoryManager run.
	//--- It releases or loads data.
	public abstract void cacheOrClearData();
	public void setInCache( boolean val ){ inCache = val; }
	//public boolean inCache(){ return inCache; }
	public long getSizeEstimate()
	{
		return sizeEstimate;
	}
	public void setSizeEstimate( long est ){ sizeEstimate = est; }
	//--- Used to sort by memory footprint size
	public int compareTo( Object compareFS )
	{
		FileSource cfs = ( FileSource ) compareFS;
		if( cfs.getSizeEstimate() == getSizeEstimate() ) return 0;
		if( cfs.getSizeEstimate() > getSizeEstimate() ) return -1;
		return 1;
	}

	public static String getPaddedNumberString( int frameNumber, int numpartLength )
	{
		StringBuffer buf = new StringBuffer();
		String num = Integer.toString( frameNumber );
		//--- Add starting zeroes
		for( int i = 0; i < numpartLength - num.length(); i++ )
			buf.insert( 0, "0" );
		buf.append( num );
		return buf.toString();
	}
	
	//------------------------------------ DEBUG
	public void printInfo()
	{
		System.out.println( "FileSource:" + file.getAbsolutePath() + 
			", id: " + id + ", type:" + type + ", size est:" + getSizeEstimate() );
	}

}//end class