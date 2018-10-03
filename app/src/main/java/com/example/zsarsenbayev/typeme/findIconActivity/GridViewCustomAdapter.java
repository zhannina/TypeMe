package com.example.zsarsenbayev.typeme.findIconActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zsarsenbayev.typeme.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by zsarsenbayev on 11/14/17.
 */

public class GridViewCustomAdapter extends ArrayAdapter {

    Context context;
    ArrayList<FindIconActivity.CellContent> icons;
    HashMap<Integer, FindIconActivity.CellContent> iconsMap = new HashMap<>();
    int positionToFind;

    public GridViewCustomAdapter(Context context, ArrayList<FindIconActivity.CellContent> nIcons, int posToFind, FindIconActivity.CellContent cc) {
        super(context, 0);
        this.context = context;
        this.icons = new ArrayList<>(nIcons);
        this.positionToFind = posToFind;
        iconsMap.put(posToFind, cc);
        this.icons.remove(icons.indexOf(cc));

        Collections.shuffle(icons, new Random(System.nanoTime()));

        for (int i = 0; i < 24; i++) {
            if (i != posToFind) {
                iconsMap.put(i, icons.get(0));
                icons.remove(0);
            }
        }

    }

    public int getCount() {
        return 24;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;

        if (cell == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            cell = inflater.inflate( R.layout.grid_cell, parent, false);


            TextView textViewTitle = (TextView) cell.findViewById(R.id.textView);
            ImageView imageViewItem = (ImageView) cell.findViewById(R.id.imageView);

            textViewTitle.setText(iconsMap.get(position).getName());
            imageViewItem.setImageResource(iconsMap.get(position).getDrawableID());

        }

        return cell;

    }
}
