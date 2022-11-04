package com.abogados.proyectog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FormsAdapter extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<FormObject> forms;

    public FormsAdapter(Activity activity, ArrayList<FormObject> forms) {
        this.activity = activity;
        this.forms = forms;
    }

    @Override
    public int getCount() {
        return forms.size();
    }

    @Override
    public Object getItem(int i) {
        return forms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return forms.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewForm = view;
        if (viewForm == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewForm = layoutInflater.inflate(R.layout.item_form, null);
        }

        FormObject item = forms.get(i);

        ImageView imageView = viewForm.findViewById(R.id.ivIcon);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        imageBytes = Base64.decode(item.getImage(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        RoundRectShape roundRectShapeImage = new RoundRectShape(new float[]{
                30, 30, 30, 30,
                30, 30, 30, 30}, null, null);
        ShapeDrawable shapeDrawableImage = new ShapeDrawable(roundRectShapeImage);
        shapeDrawableImage.getPaint().setColor(Color. parseColor(item.getColor()));
        shapeDrawableImage.setPadding(20, 20, 20, 20);
        imageView.setBackground(shapeDrawableImage);
        imageView.setImageBitmap(decodedImage);

        LinearLayout llContainer = viewForm.findViewById(R.id.llContainer);
        RoundRectShape roundRectShapeContainer = new RoundRectShape(new float[]{
                0, 0, 30, 30,
                30, 30, 0, 0}, null, null);
        ShapeDrawable shapeDrawableContainer = new ShapeDrawable(roundRectShapeContainer);
        shapeDrawableContainer.getPaint().setColor(Color.parseColor("#eeeeee"));
        shapeDrawableContainer.setPadding(10, 10, 10, 10);
        llContainer.setBackground(shapeDrawableContainer);

        TextView tvTitle = viewForm.findViewById(R.id.tvTitle);
        tvTitle.setText(item.getName().toUpperCase());

        return viewForm;
    }
}
