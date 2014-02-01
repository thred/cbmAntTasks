package org.cbm.ant.util;

/**
 * The Java Color Space is incredibly slow! Great thanks to
 * http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm
 * 
 * @author thred
 */
public abstract class CBMColorConversion
{

	public static final CBMColorConversion RGB2RGB = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			to[0] = ((from >> 16) & 0xff) / 255f;
			to[1] = ((from >> 8) & 0xff) / 255f;
			to[2] = (from & 0xff) / 255f;

			return to;
		}
	};

	public static final CBMColorConversion RGB2YCbCr = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			int R = (from >> 16) & 0xff;
			int G = (from >> 8) & 0xff;
			int B = from & 0xff;

			to[0] = ((0.299f * R) + (0.587f * G) + (0.114f * B)) / 255f;
			to[1] = (((-0.16874f * R) - (0.33126f * G)) + (0.50000f * B)) / 255f;
			to[2] = ((0.50000f * R) - (0.41869f * G) - (0.08131f * B)) / 255f;

			return to;
		}
	};

	public static final CBMColorConversion RGB2YUV = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			int R = (from >> 16) & 0xff;
			int G = (from >> 8) & 0xff;
			int B = from & 0xff;

			float y = ((0.299f * R) + (0.587f * G) + (0.114f * B));
			float u = ((B - y) * 0.492f);
			float v = ((R - y) * 0.877f);

			to[0] = y / 255f;
			to[1] = u / 255f;
			to[2] = v / 255f;

			return to;
		}
	};

	public static final CBMColorConversion RGB2HSL = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			float var_R = (((from >> 16) & 0xff) / 255f);
			float var_G = (((from >> 8) & 0xff) / 255f);
			float var_B = ((from & 0xff) / 255f);

			float var_Min; //Min. value of RGB
			float var_Max; //Max. value of RGB
			float del_Max; //Delta RGB value

			if (var_R > var_G)
			{
				var_Min = var_G;
				var_Max = var_R;
			}
			else
			{
				var_Min = var_R;
				var_Max = var_G;
			}

			if (var_B > var_Max)
			{
				var_Max = var_B;
			}
			if (var_B < var_Min)
			{
				var_Min = var_B;
			}

			del_Max = var_Max - var_Min;

			float H = 0, S, L;
			L = (var_Max + var_Min) / 2f;

			if (del_Max == 0)
			{
				H = 0;
				S = 0;
			} // gray
			else
			{ //Chroma
				if (L < 0.5)
				{
					S = del_Max / (var_Max + var_Min);
				}
				else
				{
					S = del_Max / (2 - var_Max - var_Min);
				}

				float del_R = (((var_Max - var_R) / 6f) + (del_Max / 2f)) / del_Max;
				float del_G = (((var_Max - var_G) / 6f) + (del_Max / 2f)) / del_Max;
				float del_B = (((var_Max - var_B) / 6f) + (del_Max / 2f)) / del_Max;

				if (var_R == var_Max)
				{
					H = del_B - del_G;
				}
				else if (var_G == var_Max)
				{
					H = ((1 / 3f) + del_R) - del_B;
				}
				else if (var_B == var_Max)
				{
					H = ((2 / 3f) + del_G) - del_R;
				}
				if (H < 0)
				{
					H += 1;
				}
				if (H > 1)
				{
					H -= 1;
				}
			}

			to[0] = (360 * H) / 255f;
			to[1] = (S * 100) / 255f;
			to[2] = (L * 100) / 255f;

			return to;
		}
	};

	public static final CBMColorConversion RGB2HSV = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			int r = (from >> 16) & 0xff;
			int g = (from >> 8) & 0xff;
			int b = from & 0xff;

			int min; //Min. value of RGB
			int max; //Max. value of RGB
			int delMax; //Delta RGB value

			if (r > g)
			{
				min = g;
				max = r;
			}
			else
			{
				min = r;
				max = g;
			}
			if (b > max)
			{
				max = b;
			}
			if (b < min)
			{
				min = b;
			}

			delMax = max - min;

			float H = 0, S;
			float V = max;

			if (delMax == 0)
			{
				H = 0;
				S = 0;
			}
			else
			{
				S = delMax / 255f;
				if (r == max)
				{
					H = ((g - b) / (float) delMax) * 60;
				}
				else if (g == max)
				{
					H = (2 + ((b - r) / (float) delMax)) * 60;
				}
				else if (b == max)
				{
					H = (4 + ((r - g) / (float) delMax)) * 60;
				}
			}

			to[0] = H / 255f;
			to[1] = (S * 100) / 255f;
			to[2] = (V * 100) / 255f;

			return to;
		}
	};

	public static final CBMColorConversion RGB2xyY = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			//http://www.brucelindbloom.com

			float r, g, b, X, Y, Z;

			// RGB to XYZ
			r = ((from >> 16) & 0xff) / 255.f; //R 0..1
			g = ((from >> 8) & 0xff) / 255.f; //G 0..1
			b = (from & 0xff) / 255.f; //B 0..1

			if (r <= 0.04045)
			{
				r = r / 12;
			}
			else
			{
				r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
			}

			if (g <= 0.04045)
			{
				g = g / 12;
			}
			else
			{
				g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
			}

			if (b <= 0.04045)
			{
				b = b / 12;
			}
			else
			{
				b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
			}

			X = (0.436052025f * r) + (0.385081593f * g) + (0.143087414f * b);
			Y = (0.222491598f * r) + (0.71688606f * g) + (0.060621486f * b);
			Z = (0.013929122f * r) + (0.097097002f * g) + (0.71418547f * b);

			float x;
			float y;

			float sum = X + Y + Z;
			if (sum != 0)
			{
				x = X / sum;
				y = Y / sum;
			}
			else
			{
				float Xr = 0.964221f; // reference white
				float Yr = 1.0f;
				float Zr = 0.825211f;

				x = Xr / (Xr + Yr + Zr);
				y = Yr / (Xr + Yr + Zr);
			}

			to[0] = x;
			to[1] = y;
			to[2] = Y;

			return to;
		}
	};

	public static final CBMColorConversion RGB2XYZ = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			float r, g, b, X, Y, Z;

			r = ((from >> 16) & 0xff) / 255.f; //R 0..1
			g = ((from >> 8) & 0xff) / 255.f; //G 0..1
			b = (from & 0xff) / 255.f; //B 0..1

			if (r <= 0.04045)
			{
				r = r / 12;
			}
			else
			{
				r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
			}

			if (g <= 0.04045)
			{
				g = g / 12;
			}
			else
			{
				g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
			}

			if (b <= 0.04045)
			{
				b = b / 12;
			}
			else
			{
				b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
			}

			X = (0.436052025f * r) + (0.385081593f * g) + (0.143087414f * b);
			Y = (0.222491598f * r) + (0.71688606f * g) + (0.060621486f * b);
			Z = (0.013929122f * r) + (0.097097002f * g) + (0.71418547f * b);

			to[0] = Y;
			to[1] = X;
			to[2] = Z;

			return to;
		}
	};

	public static final CBMColorConversion RGB2LAB = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			//http://www.brucelindbloom.com

			float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
			float Ls, as, bs;
			float eps = 216.f / 24389.f;
			float k = 24389.f / 27.f;

			float Xr = 0.964221f; // reference white D50
			float Yr = 1.0f;
			float Zr = 0.825211f;

			// RGB to XYZ
			r = ((from >> 16) & 0xff) / 255.f; //R 0..1
			g = ((from >> 8) & 0xff) / 255.f; //G 0..1
			b = (from & 0xff) / 255.f; //B 0..1

			// assuming sRGB (D65)
			if (r <= 0.04045)
			{
				r = r / 12;
			}
			else
			{
				r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
			}

			if (g <= 0.04045)
			{
				g = g / 12;
			}
			else
			{
				g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
			}

			if (b <= 0.04045)
			{
				b = b / 12;
			}
			else
			{
				b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
			}

			X = (0.436052025f * r) + (0.385081593f * g) + (0.143087414f * b);
			Y = (0.222491598f * r) + (0.71688606f * g) + (0.060621486f * b);
			Z = (0.013929122f * r) + (0.097097002f * g) + (0.71418547f * b);

			// XYZ to Lab
			xr = X / Xr;
			yr = Y / Yr;
			zr = Z / Zr;

			if (xr > eps)
			{
				fx = (float) Math.pow(xr, 1 / 3.);
			}
			else
			{
				fx = (float) (((k * xr) + 16.) / 116.);
			}

			if (yr > eps)
			{
				fy = (float) Math.pow(yr, 1 / 3.);
			}
			else
			{
				fy = (float) (((k * yr) + 16.) / 116.);
			}

			if (zr > eps)
			{
				fz = (float) Math.pow(zr, 1 / 3.);
			}
			else
			{
				fz = (float) (((k * zr) + 16.) / 116);
			}

			Ls = (116 * fy) - 16;
			as = 500 * (fx - fy);
			bs = 200 * (fy - fz);

			to[0] = (int) ((2.55 * Ls) + .5) / 255f;
			to[1] = (int) (as + .5) / 255f;
			to[2] = (int) (bs + .5) / 255f;

			return to;
		}
	};

	public static final CBMColorConversion RGB2LUV = new CBMColorConversion()
	{
		@Override
		public float[] convert(int from, float[] to)
		{
			if (to == null)
			{
				to = new float[3];
			}

			//http://www.brucelindbloom.com

			float r, g, b, X, Y, Z, yr;
			float L;
			float eps = 216.f / 24389.f;
			float k = 24389.f / 27.f;

			float Xr = 0.964221f; // reference white D50
			float Yr = 1.0f;
			float Zr = 0.825211f;

			// RGB to XYZ

			r = ((from >> 16) & 0xff) / 255.f; //R 0..1
			g = ((from >> 8) & 0xff) / 255.f; //G 0..1
			b = (from & 0xff) / 255.f; //B 0..1

			// assuming sRGB (D65)
			if (r <= 0.04045)
			{
				r = r / 12;
			}
			else
			{
				r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
			}

			if (g <= 0.04045)
			{
				g = g / 12;
			}
			else
			{
				g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
			}

			if (b <= 0.04045)
			{
				b = b / 12;
			}
			else
			{
				b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
			}

			X = (0.436052025f * r) + (0.385081593f * g) + (0.143087414f * b);
			Y = (0.222491598f * r) + (0.71688606f * g) + (0.060621486f * b);
			Z = (0.013929122f * r) + (0.097097002f * g) + (0.71418547f * b);

			// XYZ to Luv

			float u, v, u_, v_, ur_, vr_;

			u_ = (4 * X) / (X + (15 * Y) + (3 * Z));
			v_ = (9 * Y) / (X + (15 * Y) + (3 * Z));

			ur_ = (4 * Xr) / (Xr + (15 * Yr) + (3 * Zr));
			vr_ = (9 * Yr) / (Xr + (15 * Yr) + (3 * Zr));

			yr = Y / Yr;

			if (yr > eps)
			{
				L = (float) ((116 * Math.pow(yr, 1 / 3.)) - 16);
			}
			else
			{
				L = k * yr;
			}

			u = 13 * L * (u_ - ur_);
			v = 13 * L * (v_ - vr_);

			to[0] = (int) ((2.55 * L) + .5) / 255f;
			to[1] = (int) (u + .5) / 255f;
			to[2] = (int) (v + .5) / 255f;

			return to;
		}
	};

	public abstract float[] convert(int from, float[] to);

	protected static int to(int a, int b, int c)
	{
		return (range(a) << 16) + (range(b) << 8) + range(c);
	}

	protected static int range(int value)
	{
		//		if (value < 0)
		//		{
		//			return 0;
		//		}
		//
		//		if (value > 255)
		//		{
		//			return 255;
		//		}
		//
		return value;
	}

}
