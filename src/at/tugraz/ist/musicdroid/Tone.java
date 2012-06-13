package at.tugraz.ist.musicdroid;

import java.util.Dictionary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class Tone extends View {
	private Resources res = getResources();
	private int r;
	private int x;
	private int y;
	private int midiVal;
	private int help_value;
	private boolean cross = false;
	private Paint paint;
	private int y_line;

	public Tone(Context context, int midiVal, int x, int y, Paint paint) {
		// bekomme y = oberste Notenzeile
		super(context);
		r = res.getInteger(R.integer.radius);
		this.midiVal = midiVal;
		this.x = x;
		this.y = y + 4 * r;
		this.y_line = y;
		calculateCoordinates();
		this.paint = paint;
		invalidate();
	}

	private void calculateCoordinates() {
		// g2=43 x+r, y-r
		int octave = midiVal / 12 - 3;
		//int octave = midiVal / 12 - 1; ->  Korrektur um 2 Oktaven
		int value = midiVal % 12; // 0=C

		int distance[] = { 6, 6, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0 };

		help_value = distance[value];

		if (value == 1 || value == 3 || value == 6 || value == 8 || value == 10)
			cross = true;

		y += help_value * r - (octave - 1) * 2 * r * 3.5;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		RectF oval = new RectF(x - r - r / 4, y - r, x + r + r / 4, y + r);

		canvas.drawOval(oval, paint);
		if (this.midiVal < 60)  //36 ->  Korrektur um 2 Oktaven
			canvas.drawLine(x + r + r / 4, y, x + r + r / 4, y - 5 * r, paint);// nach
		// unten
		// klappen
		else {
			canvas.drawLine(x - r - r / 4, y, x - r - r / 4, y + 5 * r, paint);
		}
		int helpline = y_line + 10 * r;
		while (helpline <= y) {
			canvas.drawLine(x - 2 * r, helpline, x + 2 * r, helpline, paint);
			helpline += 2 * r;
		}
		helpline = y_line - 2 * r;
		while (helpline >= y) {
			canvas.drawLine(x - r * 2, helpline, x + r * 2, helpline, paint);
			helpline -= 2 * r;
		}

		if (cross) {
			canvas.drawLine(x - 3 * r, y - 2 * r, x - 3 * r, y + 2 * r - 1,
					paint);
			canvas.drawLine(x - 2 * r, y - 2 * r, x - 2 * r, y + 2 * r - 1,
					paint);

			canvas.drawLine(x - 4 * r, y, x - 3 / 2 * r, y - r, paint);

			canvas.drawLine(x - 4 * r, y + r, x - 3 / 2 * r, y, paint);
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int new_x) {
		x = new_x;
	}
}
