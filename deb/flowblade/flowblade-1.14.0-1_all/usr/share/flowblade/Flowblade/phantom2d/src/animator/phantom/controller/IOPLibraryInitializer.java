package animator.phantom.controller;

import java.util.Collections;
import java.util.Vector;

import javax.swing.JMenu;

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

import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.OutputIOP;
import animator.phantom.renderer.coordtransformer.NullIOP;
import animator.phantom.renderer.imagefilter.ColorCorrectorIOP;
import animator.phantom.renderer.imagefilter.CurvesIOP;
import animator.phantom.renderer.imagemerge.BasicTwoMergeIOP;
import animator.phantom.renderer.imagesource.FileImageSource;
import animator.phantom.renderer.imagesource.ImageSequenceIOP;
import animator.phantom.renderer.imagesource.VideoClipIOP;
import animator.phantom.renderer.plugin.AlphaMergePlugin;
import animator.phantom.renderer.plugin.AlphaReplacePlugin;
import animator.phantom.renderer.plugin.AlphaToImagePlugin;
import animator.phantom.renderer.plugin.AnimatedColorRGBPlugin;
import animator.phantom.renderer.plugin.BoxBlurPlugin;
import animator.phantom.renderer.plugin.BrushedMetalPlugin;
import animator.phantom.renderer.plugin.BumpPlugin;
import animator.phantom.renderer.plugin.CanvasPlugin;
import animator.phantom.renderer.plugin.CausticsPlugin;
import animator.phantom.renderer.plugin.CheckerboardPlugin;
import animator.phantom.renderer.plugin.ChromePlugin;
import animator.phantom.renderer.plugin.ColorDifferenceKeyPlugin;
import animator.phantom.renderer.plugin.ColorHalfTonePlugin;
import animator.phantom.renderer.plugin.ColorSampleKeyPlugin;
import animator.phantom.renderer.plugin.ColorSolidPlugin;
import animator.phantom.renderer.plugin.ColorizePlugin;
import animator.phantom.renderer.plugin.ContrastBrightnessPlugin;
import animator.phantom.renderer.plugin.CrystallizePlugin;
import animator.phantom.renderer.plugin.DesaturatePlugin;
import animator.phantom.renderer.plugin.DespecklePlugin;
import animator.phantom.renderer.plugin.DitherPlugin;
import animator.phantom.renderer.plugin.DivideByAlphaPlugin;
import animator.phantom.renderer.plugin.EdgesPlugin;
import animator.phantom.renderer.plugin.EmbossPlugin;
import animator.phantom.renderer.plugin.FBMPlugin;
import animator.phantom.renderer.plugin.FileImagePatternMergePlugin;
import animator.phantom.renderer.plugin.FlipPlugin;
import animator.phantom.renderer.plugin.FourColorPlugin;
import animator.phantom.renderer.plugin.GaussianBlurPlugin;
import animator.phantom.renderer.plugin.GradientMaskPlugin;
import animator.phantom.renderer.plugin.GradientPlugin;
import animator.phantom.renderer.plugin.GreenKeyPlugin;
import animator.phantom.renderer.plugin.GreyscalePlugin;
import animator.phantom.renderer.plugin.HalfTonePlugin;
import animator.phantom.renderer.plugin.HighPassPlugin;
import animator.phantom.renderer.plugin.HueSatBrightPlugin;
import animator.phantom.renderer.plugin.ImageToAlphaMergePlugin;
import animator.phantom.renderer.plugin.ImageToAlphaPlugin;
import animator.phantom.renderer.plugin.InvertPlugin;
import animator.phantom.renderer.plugin.KaleidoscopePlugin;
import animator.phantom.renderer.plugin.LensBlurPlugin;
import animator.phantom.renderer.plugin.LevelsPlugin;
import animator.phantom.renderer.plugin.LumaKeyPlugin;
import animator.phantom.renderer.plugin.MarblePlugin;
import animator.phantom.renderer.plugin.MaskJoinPlugin;
import animator.phantom.renderer.plugin.MatteModifyPlugin;
import animator.phantom.renderer.plugin.MaxRGBPlugin;
import animator.phantom.renderer.plugin.MotionBlurPlugin;
import animator.phantom.renderer.plugin.OilifyPlugin;
import animator.phantom.renderer.plugin.PatternGradientPlugin;
import animator.phantom.renderer.plugin.PerspectivePlugin;
import animator.phantom.renderer.plugin.PinchPlugin;
import animator.phantom.renderer.plugin.PixelizePlugin;
import animator.phantom.renderer.plugin.PlasmaPlugin;
import animator.phantom.renderer.plugin.PointillizePlugin;
import animator.phantom.renderer.plugin.PolarPlugin;
import animator.phantom.renderer.plugin.PolyCurveMaskPlugin;
import animator.phantom.renderer.plugin.PolyCurveShapePlugin;
import animator.phantom.renderer.plugin.PolyLineMaskPlugin;
import animator.phantom.renderer.plugin.PolyLineShapePlugin;
import animator.phantom.renderer.plugin.PosterizePlugin;
import animator.phantom.renderer.plugin.PredatorPlugin;
import animator.phantom.renderer.plugin.QuantizePlugin;
import animator.phantom.renderer.plugin.QuickSketchPlugin;
import animator.phantom.renderer.plugin.RipplePlugin;
import animator.phantom.renderer.plugin.ScatterRGBPlugin;
import animator.phantom.renderer.plugin.ScratchPlugin;
import animator.phantom.renderer.plugin.ShapeGridMergePlugin;
import animator.phantom.renderer.plugin.ShapeGridPlugin;
import animator.phantom.renderer.plugin.ShapeMaskPlugin;
import animator.phantom.renderer.plugin.ShapeMergePlugin;
import animator.phantom.renderer.plugin.ShapePlugin;
import animator.phantom.renderer.plugin.SharpenPlugin;
import animator.phantom.renderer.plugin.ShearPlugin;
import animator.phantom.renderer.plugin.SmearPlugin;
import animator.phantom.renderer.plugin.SolarizePlugin;
import animator.phantom.renderer.plugin.SolidNoisePlugin;
import animator.phantom.renderer.plugin.SpherePlugin;
import animator.phantom.renderer.plugin.SpillSuppressPlugin;
import animator.phantom.renderer.plugin.SplitScreenPlugin;
import animator.phantom.renderer.plugin.SpreadPlugin;
import animator.phantom.renderer.plugin.StampPlugin;
import animator.phantom.renderer.plugin.StripesPlugin;
import animator.phantom.renderer.plugin.SwimPlugin;
import animator.phantom.renderer.plugin.ThresholdPlugin;
import animator.phantom.renderer.plugin.TransparentBGPlugin;
import animator.phantom.renderer.plugin.TritonePlugin;
import animator.phantom.renderer.plugin.TwirlPlugin;
import animator.phantom.renderer.plugin.TwotonePlugin;
import animator.phantom.renderer.plugin.UnsharpPlugin;
import animator.phantom.renderer.plugin.WoodPlugin;

public class IOPLibraryInitializer
{

	public static void init()
	{
		System.out.print("INITIALIZING IOPS LIBRARY..." );

		//--- This method may be called many times.
		IOPLibrary.clear();

		//--- Create groups.
		IOPLibrary.registerGroup( "Color" );
		IOPLibrary.registerGroup( "Noise" );
		IOPLibrary.registerGroup( "Blur" );
		IOPLibrary.registerGroup( "Sharpen" );
		IOPLibrary.registerGroup( "Artistic" );
		IOPLibrary.registerGroup( "Source" );
		IOPLibrary.registerGroup( "Render" );
		//IOPLibrary.registerGroup( "Merge" );
		IOPLibrary.registerGroup( "Key" );
		IOPLibrary.registerGroup( "Alpha" );
		IOPLibrary.registerGroup( "Mask" );
		IOPLibrary.registerGroup( "Distort" );
		IOPLibrary.registerGroup( "Animation" );
		//IOPLibrary.registerGroup( "Output" );

		//--- File sources.
		IOPLibrary.registerIOP( new FileImageSource(), null );
		IOPLibrary.registerIOP( new ImageSequenceIOP(), null );
		IOPLibrary.registerIOP( new VideoClipIOP(), null );
		//IOPLibrary.registerIOP( new OutputIOP(), "Output" );
		
		//--- Color
		IOPLibrary.registerPlugin( new InvertPlugin(), "Color" );
		IOPLibrary.registerPlugin( new DesaturatePlugin(), "Color" );
		IOPLibrary.registerPlugin( new ThresholdPlugin(), "Color" );
		IOPLibrary.registerPlugin( new ContrastBrightnessPlugin(), "Color" );
		IOPLibrary.registerPlugin( new HueSatBrightPlugin(), "Color" );
		IOPLibrary.registerPlugin( new LevelsPlugin(), "Color" );
		IOPLibrary.registerPlugin( new QuantizePlugin(), "Color" );
		IOPLibrary.registerPlugin( new GreyscalePlugin(), "Color" );
		IOPLibrary.registerPlugin( new PosterizePlugin(), "Color" );
		IOPLibrary.registerPlugin( new SolarizePlugin(), "Color" );
		IOPLibrary.registerPlugin( new TritonePlugin(), "Color" );
		IOPLibrary.registerPlugin( new TwotonePlugin(), "Color" );
		IOPLibrary.registerIOP( new CurvesIOP(), "Color" );
		IOPLibrary.registerIOP( new ColorCorrectorIOP(), "Color" );
		IOPLibrary.registerPlugin( new ColorizePlugin(), "Color" );
		IOPLibrary.registerPlugin( new MaxRGBPlugin(), "Color" );
		//IOPLibrary.registerPlugin( new TestPlugin(),  "Color" );
		
		//--- Noise
		IOPLibrary.registerPlugin( new DespecklePlugin(), "Noise" );
		IOPLibrary.registerPlugin( new DitherPlugin(), "Noise" );
		IOPLibrary.registerPlugin( new ScatterRGBPlugin(), "Noise" );
		IOPLibrary.registerPlugin( new SpreadPlugin(), "Noise" );
		IOPLibrary.registerPlugin( new HighPassPlugin(), "Noise" );

		//--- Sharpen
		IOPLibrary.registerPlugin( new SharpenPlugin(), "Sharpen" );
		IOPLibrary.registerPlugin( new UnsharpPlugin(), "Sharpen" );

		//--- Blur
		IOPLibrary.registerPlugin( new BoxBlurPlugin(), "Blur" );
		IOPLibrary.registerPlugin( new GaussianBlurPlugin(), "Blur" );
		IOPLibrary.registerPlugin( new LensBlurPlugin(), "Blur" );
		IOPLibrary.registerPlugin( new MotionBlurPlugin(), "Blur" );
		IOPLibrary.registerPlugin( new PixelizePlugin(), "Blur" );

		//--- Artistic
		IOPLibrary.registerPlugin( new EmbossPlugin(), "Artistic" ); 
		IOPLibrary.registerPlugin( new EdgesPlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new CrystallizePlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new ChromePlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new StampPlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new MarblePlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new PointillizePlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new QuickSketchPlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new PredatorPlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new SmearPlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new ColorHalfTonePlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new HalfTonePlugin(), "Artistic" );
		IOPLibrary.registerPlugin( new OilifyPlugin(), "Artistic" );

		//--- Source
		IOPLibrary.registerPlugin( new ColorSolidPlugin(), "Source" );
		IOPLibrary.registerPlugin( new GradientPlugin(), "Source" );
		IOPLibrary.registerPlugin( new CanvasPlugin(), "Source" );
		IOPLibrary.registerPlugin( new PolyLineShapePlugin(), "Source" );
		IOPLibrary.registerPlugin( new PolyCurveShapePlugin(), "Source" );
		IOPLibrary.registerPlugin( new ShapePlugin(), "Source" );
		IOPLibrary.registerPlugin( new ShapeGridPlugin(), "Source" );
		IOPLibrary.registerPlugin( new StripesPlugin(), "Source" );
		//IOPLibrary.registerPlugin( new AnimatedColorSolidPlugin(), "Source" );
		IOPLibrary.registerPlugin( new AnimatedColorRGBPlugin(), "Source" );
		IOPLibrary.registerPlugin( new PatternGradientPlugin(), "Source" );
		IOPLibrary.registerPlugin( new TransparentBGPlugin(), "Source" );

		//--- Rendered Source
		IOPLibrary.registerPlugin( new CausticsPlugin(), "Render" );
		IOPLibrary.registerPlugin( new FourColorPlugin(), "Render" );
		IOPLibrary.registerPlugin( new PlasmaPlugin(), "Render" );
		IOPLibrary.registerPlugin( new BrushedMetalPlugin(), "Render" );
		IOPLibrary.registerPlugin( new CheckerboardPlugin(),  "Render" );
		IOPLibrary.registerPlugin( new SolidNoisePlugin(),  "Render" );
		IOPLibrary.registerPlugin( new WoodPlugin(),  "Render" );
		IOPLibrary.registerPlugin( new ScratchPlugin(), "Render" );
		IOPLibrary.registerPlugin( new FBMPlugin(), "Render" ); 

		//--- Merge
		/*
		IOPLibrary.registerIOP( new BasicTwoMergeIOP(), "Merge" );
		IOPLibrary.registerPlugin( new ShapeMergePlugin(), "Merge" );
		IOPLibrary.registerPlugin( new ShapeGridMergePlugin(), "Merge" );
		IOPLibrary.registerPlugin( new SplitScreenPlugin(), "Merge" );
		IOPLibrary.registerNonUserPlugin( new FileImagePatternMergePlugin() );
		*/
		//--- Key 
		IOPLibrary.registerPlugin( new LumaKeyPlugin(), "Key" );
		IOPLibrary.registerPlugin( new MatteModifyPlugin(), "Key" );
		IOPLibrary.registerPlugin( new GreenKeyPlugin(), "Key" );
		IOPLibrary.registerPlugin( new ColorDifferenceKeyPlugin(), "Key" );
		IOPLibrary.registerPlugin( new ColorSampleKeyPlugin(), "Key" );
		IOPLibrary.registerPlugin( new SpillSuppressPlugin(), "Key" );

		//--- Alpha 
		IOPLibrary.registerPlugin( new ImageToAlphaPlugin(), "Alpha" );
		IOPLibrary.registerPlugin( new AlphaToImagePlugin(), "Alpha" );
		IOPLibrary.registerPlugin( new DivideByAlphaPlugin(), "Alpha" );
		//IOPLibrary.registerPlugin( new AlphaReplacePlugin(), "Alpha" );
		//IOPLibrary.registerPlugin( new AlphaMergePlugin(), "Alpha" );
		//IOPLibrary.registerPlugin( new ImageToAlphaMergePlugin(), "Alpha" );

		//--- Mask
		IOPLibrary.registerPlugin( new PolyLineMaskPlugin(), "Mask" );
		IOPLibrary.registerPlugin( new PolyCurveMaskPlugin(), "Mask" );
		IOPLibrary.registerPlugin( new GradientMaskPlugin(), "Mask" );
		IOPLibrary.registerPlugin( new ShapeMaskPlugin(), "Mask" );
		IOPLibrary.registerPlugin( new MaskJoinPlugin(), "Mask" );

		//--- Distort
		IOPLibrary.registerPlugin( new BumpPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new FlipPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new PolarPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new TwirlPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new RipplePlugin(), "Distort" );
		IOPLibrary.registerPlugin( new KaleidoscopePlugin(),"Distort" );
		IOPLibrary.registerPlugin( new PinchPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new SwimPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new SpherePlugin(), "Distort" );
		IOPLibrary.registerPlugin( new ShearPlugin(), "Distort" );
		IOPLibrary.registerPlugin( new PerspectivePlugin(), "Distort" );

		//--- Animation
		IOPLibrary.registerIOP( new NullIOP(), "Animation" );

		System.out.println("DONE, " + IOPLibrary.getGroupKeys().size() + " groups and " + IOPLibrary.libSize() + " iops created." );
		
		Vector<String> groups = IOPLibrary.getGroupKeys();
		for( String group : groups )
			System.out.println("//------------ " + group);

	}
	
	public static Vector<String> getLayerEffectGroups()
	{
		Vector<String> groups = new Vector<String>();
		groups.add( "Color" );
		groups.add( "Noise" );
		groups.add( "Blur" );
		groups.add( "Sharpen" );
		groups.add( "Artistic" );
		groups.add( "Source" );
		groups.add( "Render" );
		groups.add( "Distort" );
		Collections.sort(groups);
		return groups;
	}
	
	public static Vector<String> getMasktGroups()
	{
		Vector<String> groups = new Vector<String>();
		groups.add( "Key" );
		groups.add( "Alpha" );
		groups.add( "Mask" );
		Collections.sort(groups);
		return groups;
	}
}//end class