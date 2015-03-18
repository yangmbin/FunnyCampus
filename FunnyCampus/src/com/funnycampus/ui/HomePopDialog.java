package com.funnycampus.ui;

import com.yangmbin.funnycampus.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomePopDialog extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.home_pop_dialog);
    final TextView[] arrayOfTextView = new TextView[5];
    arrayOfTextView[1] = ((TextView)findViewById(R.id.top_dialog_textview1));
    arrayOfTextView[2] = ((TextView)findViewById(R.id.top_dialog_textview2));
    arrayOfTextView[3] = ((TextView)findViewById(R.id.top_dialog_textview3));
    arrayOfTextView[4] = ((TextView)findViewById(R.id.top_dialog_textview4));
    //对四个条目的监听
    arrayOfTextView[1].setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("title", arrayOfTextView[1].getText().toString());
        HomePopDialog.this.setResult(101, localIntent);
        HomePopDialog.this.finish();
      }
    });
    arrayOfTextView[2].setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("title", arrayOfTextView[2].getText().toString());
        HomePopDialog.this.setResult(101, localIntent);
        HomePopDialog.this.finish();
      }
    });
    arrayOfTextView[3].setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("title", arrayOfTextView[3].getText().toString());
        HomePopDialog.this.setResult(101, localIntent);
        HomePopDialog.this.finish();
      }
    });
    arrayOfTextView[4].setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent();
        localIntent.putExtra("title", arrayOfTextView[4].getText().toString());
        HomePopDialog.this.setResult(101, localIntent);
        HomePopDialog.this.finish();
      }
    });
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    finish();
    return true;
  }
}