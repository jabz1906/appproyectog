package com.abogados.proyectog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OptionsAdapter extends BaseAdapter {
    protected Activity activity;
    protected ArrayList<OptionObject> options;

    public OptionsAdapter(Activity activity, ArrayList<OptionObject> options) {
        this.activity = activity;
        this.options = options;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int i) {
        return options.get(i);
    }

    @Override
    public long getItemId(int i) {
        return options.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewForm = view;
        if (viewForm == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewForm = layoutInflater.inflate(R.layout.item_spinner, null);
        }

        OptionObject item = options.get(i);

        TextView tvText = viewForm.findViewById(R.id.tvText);
        tvText.setText(item.getName().toUpperCase());

        return viewForm;
    }
}
