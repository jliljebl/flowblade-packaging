package animator.phantom.paramedit.imagemerge;

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

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.CoordsEditComponents;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.paramedit.RowSeparator;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.imagemerge.BasicTwoMergeIOP;
import animator.phantom.renderer.ImageOperation;

public class BasicTwoMergeEditPanel extends ParamEditPanel
{
	public BasicTwoMergeEditPanel( BasicTwoMergeIOP btm   )
	{
		initParamEditPanel();

		CoordsEditComponents coords = new CoordsEditComponents( btm );
		CheckBoxEditor aOverB = new CheckBoxEditor( btm.useOverRule, "Alpha combine", true );
		IntegerComboBox blendSelect = new IntegerComboBox( 	btm.blendMode,
									"Blend mode",
									ImageOperation.blendModes);
		AnimValueNumberEditor opacityEdit = new AnimValueNumberEditor( "Opacity", btm.opacity );
		addComponentsVector( coords.getEditComponents() );
		add( opacityEdit );
		add( new RowSeparator() );
		add( blendSelect );
		add( new RowSeparator() );
		add( aOverB );
	}

}//end class
