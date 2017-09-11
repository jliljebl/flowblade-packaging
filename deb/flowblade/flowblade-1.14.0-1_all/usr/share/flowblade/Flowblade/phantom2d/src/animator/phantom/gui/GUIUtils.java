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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import animator.phantom.controller.AppUtils;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.renderer.FileSource;


public class GUIUtils
{

 	public static final int FILE_CHOOSE_WITDH = 600;
	public static final int FILE_CHOOSE_HEIGHT = 500;

	//--- Constants for drawing panel frame lines.
	private static int MAX_FRAME_LINES = 5;

	public static final File selectFilteredFile(  	Component parent,
							String[] filters,
							String dialogTitle )
	{
		File[] sFiles = getSelectFile( parent, filters, dialogTitle, false );
		if( sFiles == null ) return null;
		else return sFiles[ 0 ];
	}

	public static final File[] selectFilteredFiles( Component parent,
							String[] filters,
							String dialogTitle )
	{
		return getSelectFile( parent, filters, dialogTitle, true );
	}

	private static final File[] getSelectFile( 	Component parent,
							String[] filters,
							String dialogTitle,
							boolean acceptMultiple )
	{
		JFileChooser fileChoose = new JFileChooser();
		SelectFileFilter filter = new SelectFileFilter();
		for( int i = 0; i < filters.length; i++ ) filter.addExtension( filters[ i ] );
		fileChoose.setFileFilter( filter );
		fileChoose.setDialogTitle( dialogTitle );
		fileChoose.setMultiSelectionEnabled( acceptMultiple );
		fileChoose.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChoose.setPreferredSize( new Dimension( FILE_CHOOSE_WITDH, FILE_CHOOSE_HEIGHT ) );

		int retVal = fileChoose.showOpenDialog( parent );
		if( retVal == JFileChooser.APPROVE_OPTION )
		{
			if( acceptMultiple ) return fileChoose.getSelectedFiles();
			else
			{
				File files[] = { fileChoose.getSelectedFile() };
				return files;
			}
		}
		else return null;
	}//end selectFilteredFile
	
	public static File selectSaveFile( Component parent,
					String[] filters,
					String dialogTitle,
					File defaultSaveFile )
	{
		JFileChooser fileChoose = new JFileChooser();
		SelectFileFilter filter = new SelectFileFilter();
		for( int i = 0; i < filters.length; i++ ) filter.addExtension( filters[ i ] );
		fileChoose.setFileFilter( filter );
		fileChoose.setDialogTitle( dialogTitle );
		fileChoose.setMultiSelectionEnabled( false );
		fileChoose.setFileSelectionMode( JFileChooser.FILES_ONLY );

		if( defaultSaveFile != null ) fileChoose.setSelectedFile( defaultSaveFile );

		int retVal = fileChoose.showSaveDialog( parent );
		if( retVal == JFileChooser.APPROVE_OPTION ) return fileChoose.getSelectedFile();
		else return null;
	}	

	public static File addISingleImageFile( Component parent, String title )
	{
		String[] filters = AppUtils.getImageExtensions();
	    File file = selectFilteredFile(  parent,
										 filters,
										 title );
		return file;
	}

	public static File[] addFiles( Component parent, String title, int fileType  )
	{
		String[] filters = null;
		if (fileType == FileSource.IMAGE_FILE)
			filters = AppUtils.getImageExtensions();
		else
			filters = AppUtils.getMovieExtensions();

		File[] files = GUIUtils.selectFilteredFiles( parent, filters, title );
		return files;
	}

	public static void centralizeWindow( JFrame frame )
	{
		Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();
	        Dimension windowSize = frame.getPreferredSize();
	        frame.setBounds( (SCREENSIZE.width - windowSize.width )/ 2,
				(SCREENSIZE.height - windowSize.height)/2,
					windowSize.width,
						windowSize.height );
	}//end cetralizeWindow
	
	public static BufferedImage getBufferedImageFromFile( File f )
	{
		try
		{
			//BufferedImage img;
			BufferedImage loadImg = ImageIO.read( f );

			//--- Force alpha if missing. Keep if exists.
			BufferedImage	img = new BufferedImage( 	loadImg.getWidth(),
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
			System.out.println("IMAGE LOAD FAILED FOR"+ f.getAbsolutePath() );
			return null;
		}

	}
	/*
	public static ImageIcon getThumbnailFromFile( File imageFile, int type )
	{

		//--- Load and scale image
		BufferedImage iconImg = GUIUtils.getBufferedImageFromFile( imageFile );
		Image scaled = iconImg.getScaledInstance( ICON_WIDTH , ICON_HEIGHT, Image.SCALE_FAST );
		//--- scaled holds reference to original instance and must be left behind
		//--- so that both can be carbage collected.?????????????
		BufferedImage noReferenceImg = new BufferedImage( 	scaled.getWidth(null),
									 scaled.getHeight(null),
									 BufferedImage.TYPE_INT_ARGB );
		Graphics g = noReferenceImg.getGraphics();
		g.drawImage( scaled, 0, 0, null );

		//--- Draw icon on FileSourceSequence
		if( type == FileSource.IMAGE_SEQUENCE )
		{
			g.setColor( Color.white );
			g.drawOval(0,0,10,10);
		}
		
		return new ImageIcon( noReferenceImg );
	}
	*/
	public static ImageIcon getThumbnailFromImage( BufferedImage iconImg, int ICON_WIDTH, int ICON_HEIGHT )
	{
		if( iconImg == null ) System.out.println("iconimg null" );
		
		Image scaled = iconImg.getScaledInstance( ICON_WIDTH, ICON_HEIGHT, Image.SCALE_FAST );
		//--- scaled holds reference to original instance and must be left behind
		//--- so that both can be carbage collected.
		BufferedImage noReferenceImg = new BufferedImage( 	scaled.getWidth(null),
									scaled.getHeight(null),
									BufferedImage.TYPE_INT_ARGB );
		Graphics g = noReferenceImg.getGraphics();
		g.drawImage( scaled, 0, 0, null );
		return new ImageIcon( noReferenceImg );
	}

	public static void drawFrameLines( Graphics2D g, int panelHeight )
	{
		//--- Get displayed frame range.
		float pixPerFrame = TimeLineController.getCurrentScaleMultiplier();
		int pos = TimeLineController.getTimeLinePosition();

		int columnWidth = AnimFrameGUIParams.getTimeEditRightColWidth();
		int framesInPanel = Math.round( columnWidth / pixPerFrame );
		int endDrawFrame = pos + framesInPanel;

		//--- Get line draw step
		int fps = ProjectController.getFramesPerSecond();
		int sec = framesInPanel / fps;
		int drawStep = 5;	
		if( sec > 1 && sec <= 4  )
			drawStep = fps;
		if( sec > 4 )
			drawStep = (sec / MAX_FRAME_LINES) * fps;

		//--- Draw frame lines
		Line2D.Float line = new Line2D.Float();
		int startDrawFrame = ( pos / fps ) * fps;
		endDrawFrame = (( endDrawFrame / fps) + 1 ) * fps;
		g.setColor( GUIColors.KF_LINES_COLOR );
		for( int i = startDrawFrame; i < endDrawFrame; i += drawStep )
		{
			float drawX = i * pixPerFrame - pos * pixPerFrame;
			line.setLine(drawX, 0, drawX, panelHeight );
			g.draw( line );
		}
	}

}//end class
