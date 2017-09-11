package giotto2D.libcolor;

//--- Color in Hue-Saturation-Luminance space
public class GiottoHSLInt
{
	public int h = 0;
	public int s = 0;
	public int l = 0;
	public int a = 255;

	public GiottoHSLInt(){}

	public GiottoHSLInt( int h, int s, int l, int a )
	{
		this.h  = h;
		this.s  = s;
		this.l  = l;
		this.a  = a;
	}

}//end class