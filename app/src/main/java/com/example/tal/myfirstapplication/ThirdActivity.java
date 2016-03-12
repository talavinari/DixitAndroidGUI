package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by tal on 3/11/2016.
 */
public class ThirdActivity extends Activity {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity_layout);

        initGrid();
    }

    private void initGrid() {
        tableLayout = (TableLayout) findViewById(R.id.tableLayout1);
        for (int i = 0; i <2; i++) {

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView id = new TextView(this);
            TextView name = new TextView(this);
            name.setText("Tal " + i);
            id.setText(String.valueOf(i));
            row.addView(id);
            row.addView(name);
            tableLayout.addView(row, i);
        }
    }
}
