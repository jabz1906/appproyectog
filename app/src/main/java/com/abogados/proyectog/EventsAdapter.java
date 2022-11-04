package com.abogados.proyectog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EventsAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<EventObject> events;

    public EventsAdapter(Activity activity, ArrayList<EventObject> events) {
        this.activity = activity;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return events.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewEvent = view;
        if (viewEvent == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewEvent = layoutInflater.inflate(R.layout.item_event, null);
        }

        EventObject item = events.get(i);

        LinearLayout llContainer = viewEvent.findViewById(R.id.llContainer);
        RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                80, 80, 360, 360,
                360, 360, 80, 80}, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(Color.parseColor(item.getColor()));
        shapeDrawable.setPadding(10, 10, 10, 10);
        llContainer.setBackground(shapeDrawable);


        TextView tvId = viewEvent.findViewById(R.id.tvId);
        tvId.setText("" + item.getId());

        TextView tvRegistered = viewEvent.findViewById(R.id.tvRegistered);
        tvRegistered.setText(item.getRegistered());

        TextView tvFormName = viewEvent.findViewById(R.id.tvFormName);
        tvFormName.setText(item.getFormName());

        TextView tvUserName = viewEvent.findViewById(R.id.tvUserName);
        tvUserName.setText(item.getUserName());

        ImageView ivIcon = viewEvent.findViewById(R.id.ivIcon);

        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(60);
        biggerCircle.setIntrinsicWidth(60);
        biggerCircle.setBounds(new Rect(0, 0, 60, 60));
        biggerCircle.getPaint().setColor(Color.parseColor(item.getColor()));
        biggerCircle.setPadding(15, 15, 15, 15);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(10);
        smallerCircle.setIntrinsicWidth(10);
        smallerCircle.setBounds(new Rect(0, 0, 10, 10));
        smallerCircle.getPaint().setColor(Color.WHITE);

        smallerCircle.setPadding(5, 5, 5, 5);
        Drawable[] d = {smallerCircle, biggerCircle};
        LayerDrawable composite1 = new LayerDrawable(d);

        ivIcon.setBackground(composite1);
        ivIcon.setImageResource(R.drawable.ic_baseline_create_24);

        return viewEvent;
    }
}
