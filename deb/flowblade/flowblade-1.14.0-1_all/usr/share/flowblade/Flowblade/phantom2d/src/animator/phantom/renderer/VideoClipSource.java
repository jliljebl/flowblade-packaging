package animator.phantom.renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;

import javax.swing.ImageIcon;

import animator.phantom.controller.MLTFrameServerController;
import animator.phantom.gui.GUIResources;

public class VideoClipSource extends FileSource
{
	private static ImageIcon movieIcon = GUIResources.getIcon( GUIResources.movieicon );
	
	private static String FRAME_NAME = "frame_";
	
	private String MD5id = null;
	private int length = -1;

	//--- for loading.
	public VideoClipSource(){}

	public VideoClipSource( File  f )
	{
		type = VIDEO_FILE;
		setFile( f );
	}

	public void loadInit()
	{
		loadClipIntoServer();
	}

	public void loadClipIntoServer()
	{
		String answer = MLTFrameServerController.loadClipIntoServer( this );
		if ( answer != null )
		{
            String[] tokens = answer.split(" ");
            MD5id = tokens[0];
            length = Integer.parseInt( tokens [1] );
		}
	}

	public void firstLoadData()
	{
		MLTFrameServerController.renderFrame( this, 0 );
		BufferedImage firstframe = getClipImage( 0 );
		imgWidth = firstframe.getWidth();
		imgHeight = firstframe.getHeight();
        DataBuffer db = firstframe.getRaster().getDataBuffer();
		int bp = (int)Math.ceil( DataBuffer.getDataTypeSize(db.getDataType()) / 8f);
        sizeEstimate = bp * db.getSize();
		firstframe = null;
	}
	
	public BufferedImage getClipImage( int clipFrame )
	{
		String framePath = getFrameFilePath( clipFrame );
		File frameFile = new File( framePath );
		if( frameFile.exists() && !frameFile.isDirectory()) 
		{ 
		    return getBufferedImageFromFile( frameFile );
		}
		else
		{
			MLTFrameServerController.renderFrame( this, clipFrame );
		    return getBufferedImageFromFile( frameFile );
		}
	}
	
	private String getFrameFilePath( int clipFrame )
	{
		String fileName = FRAME_NAME + getPaddedNumberString(clipFrame, 5 ) + ".png";
		return MLTFrameServerController.getSourceFramesFolder(this) + "/" + fileName;
	}
	
	public int getProgramLength()
	{
		return length;
	}

	public ImageIcon getThumbnailIcon()
	{
		return null;
	}

	public int getLength(){ return length; }
	public void setLength( int newlength) { length = newlength; }			
	public String getMD5id(){ return MD5id;}
	public void setMD5id( String newID) { MD5id = newID; }

	public void loadData(){}//--- not cached
	public boolean dataInMemory(){ return false; }//--- not cached
	public void clearData(){}//--- not cached
	public void cacheOrClearData(){}//--- not cached

	public BufferedImage getBufferedImage(){ return null; }
	public ImageIcon getFileTypeIcon(){ return movieIcon; }
	
}//end class
