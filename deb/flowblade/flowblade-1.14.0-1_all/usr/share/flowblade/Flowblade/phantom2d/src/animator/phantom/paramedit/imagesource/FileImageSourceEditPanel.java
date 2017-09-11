package animator.phantom.paramedit.imagesource;

/*
    Copyright Janne Liljeblad

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

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import animator.phantom.controller.GUIComponents;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.CoordsEditComponents;
import animator.phantom.paramedit.FlipSelect;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.imagesource.FileImageSource;

public class FileImageSourceEditPanel extends ParamEditPanel implements ItemListener
{
	FileImageSource fileImageSource;
	CheckBoxEditor asCanvas;

	public FileImageSourceEditPanel( FileImageSource fileImageSource )
	{
		initParamEditPanel();

		this.fileImageSource = fileImageSource;

		CoordsEditComponents coords = new CoordsEditComponents( fileImageSource );
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", fileImageSource.opacity );
		IntegerComboBox blendSelect = 
			new IntegerComboBox( fileImageSource.blendMode,
						"Blend mode",
						FileImageSource.blendModes );//so that blendmodes can be changed without recompilation.
		blendSelect.setMaxComboRows( FileImageSource.blendModes.length );
	
		FlipSelect flip = new FlipSelect( fileImageSource.flipTrans );
		//CheckBoxEditor aOverB = new CheckBoxEditor( fileImageSource.useOverRule, "Alpha combine", true );
		asCanvas = new CheckBoxEditor( fileImageSource.asCanvas, "Send as canvas", true );
		asCanvas.addItemListener( this );
		String[] maskInputOps = { "blend mask", "output alpha" };
		IntegerComboBox maskOpSelect = new IntegerComboBox( 	fileImageSource.inputMaskOp,
									"Mask input",
									maskInputOps );

		addComponentsVector( coords.getEditComponents() );
		add( opacityEdit );
		add( new RowSeparator() );
		add( blendSelect );
		add( new RowSeparator() );
		add( flip );
		add( new RowSeparator() );
		add( maskOpSelect );
		//add( new RowSeparator() );
		//add( aOverB );
		add( new RowSeparator() );
		add( asCanvas );

		//--- HACK TO GET EDITOS UNABLED AFTRE LOAD IF SEND AS CANVAS SET 
		itemStateChanged(new ItemEvent( new Checkbox(), 0, new Object(), -1000 ) );
	}

	public void itemStateChanged(ItemEvent e)
	{
		boolean newVal = fileImageSource.asCanvas.get();
		setAllEnabled( newVal == false );
		setEnabledRecursively( asCanvas, true );
		GUIComponents.viewEditor.setLayerControlsEnabled( fileImageSource, !newVal );
		GUIComponents.viewEditor.setLayerDrawingEnabled( fileImageSource, !newVal );
	}

}//end class