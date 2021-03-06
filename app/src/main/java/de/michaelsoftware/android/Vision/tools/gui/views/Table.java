package de.michaelsoftware.android.Vision.tools.gui.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import de.michaelsoftware.android.Vision.tools.gui.GUIHelper;
import de.michaelsoftware.android.Vision.tools.gui.listener.CustomOnClickListener;

/**
 * Creates a TableView with the specified HashMap
 * Created by Michael on 10.05.2016.
 */
/*
public class Table extends TableLayout {
    private final GUIHelper gui;
    private final Context context;

    public Table(Context context) {
        super(context);

        this.context = context;
        this.gui = null;
    }

    public Table(Context context, GUIHelper guiHelper) {
        super(context);

        this.context = context;
        this.gui = guiHelper;
    }

    public void parseHashMap(HashMap<Object, Object> hashMap) {
        LinearLayout.LayoutParams view_params = new
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(view_params);
        this.setStretchAllColumns(true);
        this.setColumnShrinkable(0, true);
        Object rows = hashMap.get("rows");
        Object click = hashMap.get("click");
        Object longclick = hashMap.get("longclick");

        if (rows instanceof HashMap)
            for (int j = 0; j < ((HashMap) rows).size(); j++) {
                Object row = ((HashMap) rows).get(j);
                TableRow newRow = new TableRow(this.context);

                if (row instanceof HashMap)
                    for (int k = 0; k < ((HashMap) row).size(); k++) {
                        Object element = ((HashMap) row).get(k);

                        if (element instanceof String) {
                            TextView tv = new TextView(this.context);
                            tv.setText((String) element);
                            newRow.addView(tv);
                        } else if (element instanceof HashMap) {
                            LinearLayout tableLin = new LinearLayout(this.context);
                            tableLin.setOrientation(LinearLayout.VERTICAL);
                            for (int i = 0; i < ((HashMap) element).size(); i++) {
                                if (((HashMap) element).get(i) instanceof HashMap) {
                                    if(this.gui != null) {
                                        View tableEl = this.gui.parseElement((HashMap) ((HashMap) element).get(i), false);

                                        if (tableEl != null) {
                                            tableLin.addView(tableEl);
                                        }
                                    }
                                }
                            }

                            newRow.addView(tableLin);
                        }
                    }

                if (click instanceof HashMap && ((HashMap) click).containsKey(j) && ((HashMap) click).get(j) instanceof String) {
                    String clickString = (String) ((HashMap) click).get(j);
                    newRow.setOnClickListener(new CustomOnClickListener(context, clickString));
                }

                if (longclick instanceof HashMap && ((HashMap) longclick).containsKey(j) && ((HashMap) longclick).get(j) instanceof String) {
                    String longclickString = (String) ((HashMap) longclick).get(j);
                    newRow.setOnClickListener(new CustomOnClickListener(context, longclickString));
                }

                this.addView(newRow);
            }
    }
}*/
