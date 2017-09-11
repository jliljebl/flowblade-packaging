package animator.phantom.project;

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

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import animator.phantom.controller.Application;

public class MovieFormat
{
	private String name;
	private int framesPerSecond;
	private float framesPerSecondAccurate;
	private Dimension screenSize;
	
	public static Vector<MovieFormat> formats = new Vector<MovieFormat>();

	public final static MovieFormat DEFAULT = new MovieFormat( "default", 25,  384, 288 );
	public final static MovieFormat PAL_SQUARE = new MovieFormat( "PAL Square", 25, 768, 576 );
	public final static MovieFormat NTSC_SQUARE = new MovieFormat( "NTSC Square", 30, 720, 540 );

	static
	{
		formats.add( DEFAULT );
		formats.add( PAL_SQUARE );
		formats.add( NTSC_SQUARE );

		String formatPath = Application.getFormatPath();
		File[] files = new File(formatPath).listFiles();
		for (File file : files) 
		{
			
			try 
			{
				Vector<String> lines = new Vector<String>();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();

				while (line != null) 
				{
					if ( line.contains("=") )
						lines.add( line.split("=")[1] );

					line = br.readLine();
				}
				br.close();
				formats.add( new MovieFormat( lines ) );
			}
			catch (Exception e)
			{
				System.out.println( "Formats load error!");
				System.out.println( "Error:" + e.getMessage() );
			} 
		}

		Collections.sort(formats, 
			new Comparator<MovieFormat>() 
			{
				@Override
				public int compare(final MovieFormat object1, final MovieFormat object2) 
				{
					return object1.getName().compareTo(object2.getName());
				}
			} 
		);
  
   
	}

	public MovieFormat(){}

	public MovieFormat( Vector<String> lines )
	{
		String description = "";
		int frame_rate_num = 0;
		int frame_rate_den  = 0;
		int width  = 0;
		int height  = 0;
		int progressive = 0;
		int sample_aspect_num  = 0;
		int sample_aspect_den  = 0;
		int display_aspect_num  = 0;// not used currently
		int display_aspect_den = 0;// not used currently
			
		for( int i = 0; i < lines.size(); i++ )
		{
			String line = lines.elementAt( i );
			switch( i )
			{
				case 0:
					description = line;
					break;
				case 1:
					frame_rate_num = Integer.parseInt(line);
					break;
				case 2:
					frame_rate_den = Integer.parseInt(line);
					break;
				case 3:
					width = Integer.parseInt(line);
					break;
				case 4:
					height = Integer.parseInt(line);
					break;
				case 5:
					progressive = Integer.parseInt(line);
					break;
				case 6:
					sample_aspect_num = Integer.parseInt(line);
					break;
				case 7:
					sample_aspect_den = Integer.parseInt(line);
					break;
				case 8:
					display_aspect_num = Integer.parseInt(line);
					break;
				case 9:
					display_aspect_den = Integer.parseInt(line);
					break;
			}
		}
		this.name = description;
		this.framesPerSecond = Math.round( ((float) frame_rate_num) / ((float) frame_rate_den) );
		this.framesPerSecondAccurate = ((float) frame_rate_num) / ((float) frame_rate_den);
		this.screenSize = new Dimension( width, height );
	}

	public MovieFormat( String name, int framesPerSecond, int width, int height )
	{
		this.name = name;
		this.framesPerSecond = framesPerSecond;
		this.framesPerSecondAccurate = (float) framesPerSecond;
		this.screenSize = new Dimension( width, height );
	}

	public String getName(){ return name; }
	public int getFPS(){ return framesPerSecond; }
	public float getFPSAccurate(){ return framesPerSecondAccurate; }
	public Dimension getScreenSize(){ return screenSize; }
        public String getUnderscrorName(){ return name.replaceAll("\\s+", "_"); }

}//end class
