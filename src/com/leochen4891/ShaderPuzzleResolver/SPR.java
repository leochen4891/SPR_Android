package com.leochen4891.ShaderPuzzleResolver;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.leochen4891.ShaderPuzzleResolver.*;

public class SPR extends Activity {
	
	static final String verDesc4 = "2-2.1-1.1-1.1";
	static final String horDesc4 = "3-1-1-4";
	/*
	N	Y	Y	Y
	N	Y	N	N
	Y	N	N	N
	Y	Y	Y	Y
	*/
	
	static final String verDesc5 = "2.1-4-4-1-3";
	static final String horDesc5 = "1-3.1-3.1-3-3";
	/*
	N	N	N	N	Y
	Y	Y	Y	N	Y
	Y	Y	Y	N	Y
	N	Y	Y	Y	N
	Y	Y	Y	N	N
	*/

	static final String verDesc7 = "3.1-1.1.1-1.3-0-5-1.1-3";
	static final String horDesc7 = "0-3.3-1.1.1-3.3-1.1-3.1-0";
	/*
	N	N	N	N	N	N	N
	Y	Y	Y	N	Y	Y	Y
	Y	N	N	N	Y	N	Y
	Y	Y	Y	N	Y	Y	Y
	N	N	Y	N	Y	N	N
	Y	Y	Y	N	Y	N	N
	N	N	N	N	N	N	N
	*/

	static final String verDesc9 = "1.1.1.1-3.1.1-1.3-1-1.1.1-2.1-1.1.3-1.1.2-4.1";
	static final String horDesc9 = "1.1.2-1-2.3-1.1.1.1-2.2.1-1.1-3.2-1.1.3-2.1";
	/*
	Y	N	Y	N	N	N	Y	Y	N
	N	N	N	N	Y	N	N	N	N
	Y	Y	N	N	N	N	Y	Y	Y
	N	Y	N	Y	N	Y	N	N	Y
	Y	Y	N	N	Y	Y	N	N	Y
	N	N	N	N	N	N	Y	N	Y
	Y	Y	Y	N	N	Y	Y	N	N
	N	N	Y	N	Y	N	Y	Y	Y
	N	Y	Y	N	N	N	N	Y	N
	*/
	
	
	private EditText editTextVerRule = null; // on the top 
	private EditText editTextHorRule = null;  // on the left side
	private Button btnStart = null;
	private Button btnAbout = null;
	private ImageView[][] images = new ImageView[MAX_LENGTH][MAX_LENGTH];
	
	private static final int MAX_LENGTH = 9;
	
	private static ShaderPuzzle mPuzzle = null;
	private static Resolver mResolver = null; 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spr);
		
		InitalControls();
		
		if (null == mPuzzle || null == mResolver) {
			mPuzzle = new ShaderPuzzle(null, null);
			mResolver = new Resolver(mPuzzle);
			
			if (editTextHorRule.getText().length() <= 0) {
				editTextHorRule.setText(horDesc5);
			}
			if (editTextVerRule.getText().length() <= 0) {
				editTextVerRule.setText(verDesc5);
			}
		}
		
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Resolve();
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(btnStart.getWindowToken(), 0);
			}
		});
		
		btnAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(SPR.this);
				builder.setMessage("Shader Puzzle Resolver\n" +
						"(test version)\n" +
						"by Leo Chen\n" +
						"(leochen4891@gmail.com)\n");
				builder.setTitle("About");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
	}
	
	private void Resolve() {
		String verRule = editTextVerRule.getText().toString();
		String horRule = editTextHorRule.getText().toString();
		mPuzzle.Initialize(horRule, verRule);
		mResolver.SetPuzzle(mPuzzle);
		ResolveTask task = new ResolveTask();
		task.execute(mResolver);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_spr, menu);
		return true;
	}
	
	private void InitalControls() {
		editTextVerRule = (EditText) this.findViewById(R.id.editTextVerRule);
		editTextHorRule = (EditText) this.findViewById(R.id.editTextHorRule);
		btnStart = (Button) this.findViewById(R.id.buttonStart);
		btnAbout = (Button) this.findViewById(R.id.btnAbout);
		
		int id;
		int index;
		String str = "";
		for (int y = 0; y < MAX_LENGTH; y++) {
			for (int x = 0; x < MAX_LENGTH; x++) {
				index = y*MAX_LENGTH + x;
				str = index < 10?("0" + index):""+index;
				id = getResources().getIdentifier("ImageView" + str , "id", this.getPackageName());
				images[y][x] = (ImageView) findViewById(id);
			}
		}
	}
	
	private void HideImages() {
		for (int y = 0; y < MAX_LENGTH; y++) {
			for (int x = 0; x < MAX_LENGTH; x++) {
				images[y][x].setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void ShowImages(int h, int w) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				images[y][x].setVisibility(View.VISIBLE);
			}
		}
	}
	
	class ResolveTask extends AsyncTask<Resolver, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Resolver... params) {
			if (params.length != 1)
				return false;
			boolean found = params[0].Resolve();
			return found;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			HideImages();
			ShowPuzzle();
			ShowImages(mPuzzle.mY, mPuzzle.mX);
		}
	}
	
	private void ShowPuzzle() {
		if (null != mPuzzle)
		for (int y = 0; y < mPuzzle.mY; y++) {
			for (int x = 0; x < mPuzzle.mX; x++) {
				if (mPuzzle.Get(y, x) == mPuzzle.STATUS_YES) {
					images[y][x].setImageResource(R.drawable.yes);
				} else {
					images[y][x].setImageResource(R.drawable.no);
				}
			}
		}
	}

}
