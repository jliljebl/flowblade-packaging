package animator.phantom.paramedit.imagesource;

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
import animator.phantom.renderer.imagesource.ImageSequenceIOP;
import animator.phantom.renderer.ImageOperation;

public class ImageSequenceEditPanel extends ParamEditPanel  implements ItemListener
{
	ImageSequenceIOP iss;
	CheckBoxEditor asCanvas;

	public ImageSequenceEditPanel( ImageSequenceIOP iss )
	{
		this.iss = iss;

		initParamEditPanel();

		CoordsEditComponents coords = new CoordsEditComponents( iss );
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", iss.opacity );
		IntegerComboBox blendSelect = new IntegerComboBox( iss.blendMode,
									"Blend mode",
									ImageOperation.blendModes );//so that blenders can be changed without recompiling
		blendSelect.setMaxComboRows( ImageOperation.blendModes.length );
	
		FlipSelect flip = new FlipSelect( iss.flipTrans );
		CheckBoxEditor aOverB = new CheckBoxEditor( iss.useOverRule, "Alpha combine", true );
		asCanvas = new CheckBoxEditor( iss.asCanvas, "Send as canvas", true );
		asCanvas.addItemListener( this );

		//--- Put GUI together 
		addComponentsVector( coords.getEditComponents() );
		add( opacityEdit );
		add( new RowSeparator() );
		add( blendSelect );
		add( new RowSeparator() );
		add( flip );
		add( new RowSeparator() );
		add( aOverB );
		add( new RowSeparator() );
		add( asCanvas );

		//--- HACK TO GET EDITORS UNABLED AFTRE LOAD IF SEND_AS_CANVAS IS TRUE  
		itemStateChanged(new ItemEvent( new Checkbox(), 0, new Object(), -1000 ) );
	}

	public void itemStateChanged(ItemEvent e)
	{
		boolean newVal = iss.asCanvas.get();
		setAllEnabled( newVal == false );
		setEnabledRecursively( asCanvas, true );
		GUIComponents.viewEditor.setLayerControlsEnabled( iss, !newVal );
		GUIComponents.viewEditor.setLayerDrawingEnabled( iss, !newVal );
	}

}//end class