package animator.phantom.gui.modals;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.GUIUtils;
import animator.phantom.gui.PHButtonFactory;

public class MFileSelect extends MInputField implements ActionListener 
{
	private String title;
	private JLabel fileLabel;
	public File file;
	private String[] acceptedExtensions;
	private int type = JFileChooser.FILES_ONLY;

	public MFileSelect( String msg, String title, int cutColumnPix, File defFile, String[] exts )
	{
		if( defFile != null ) this.file = defFile;
		this.acceptedExtensions = exts;
		this.title = title;

		//--- 
		JButton selectButton = PHButtonFactory.getButton( msg );
		selectButton.addActionListener( this );
	
		//--- Set up edit field.
		fileLabel = new JLabel();
		setFileText();

		this.leftComponent = selectButton;
		this.rightComponent = fileLabel;

		initPanels();
	}
	
	public void setType( int type_ )
	{ 
		this.type = type_;
		setFileText();
	}

	public void actionPerformed(ActionEvent e)
	{
		File newf = getFile();
		if( newf != null ) file = newf;
		setFileText();
		repaint();
	}

	private void setFileText()
	{
		String fileText;

		if( file != null )
		{
			if( type == JFileChooser.DIRECTORIES_ONLY )
			{
				if( file.getParentFile().getParentFile() != null )
					fileText = ".../" + file.getName();
				else
					fileText = "/" + file.getName();
			}
			else fileText = file.getName();
		}
		else 
			fileText = "<not selected>";

		fileLabel.setText( fileText );
	}

	private File getFile()
	{
		//--- Folder select
		if( type == JFileChooser.DIRECTORIES_ONLY )
		{
			JFileChooser fileChoose = new JFileChooser();
			fileChoose.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			fileChoose.setDialogTitle( title );

			int retVal = fileChoose.showOpenDialog( GUIComponents.getAnimatorFrame() );
			if( retVal == JFileChooser.APPROVE_OPTION )
				return fileChoose.getSelectedFile();
			else 
				return null;
		}
		
		//--- File select
		File loadFile = GUIUtils.selectFilteredFile( 
				GUIComponents.getAnimatorFrame(),
				acceptedExtensions,
				title );
		return loadFile;
	}

	public File getSelectedFile(){ return file; }

}//end class
