package com.abogados.proyectog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Generator {
    private Activity activity;
    private LinearLayout llContainer;
    private Integer parent_id;
    private Integer form_id;
    private Integer user_id;

    private String textDate = "Haga clic para obtener la Fecha";
    private String textHour = "Haga clic para obtener la Hora";
    private String required = "Obligatorio";

    private ArrayList<EditText> editable = new ArrayList<>();
    private ArrayList<EditText> editableMultiline = new ArrayList<>();
    private ArrayList<Spinner> selections = new ArrayList<>();
    private ArrayList<TextView> dates = new ArrayList<>();
    private ArrayList<TextView> hours = new ArrayList<>();
    private ArrayList<ImageObject> images = new ArrayList<>();
    private ArrayList<ImageObject> signatures = new ArrayList<>();
    private ArrayList<Spinner> selectionsSpecial = new ArrayList<>();

    public int imageId;
    public ImageObject imageObjectSelected;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public String currentPhotoPath;

    public JSONObject eventForm = new JSONObject();

    public Generator(Activity activity, LinearLayout llContainer, Integer parent_id, Integer form_id, Integer user_id) {
        this.activity = activity;
        this.llContainer = llContainer;
        this.parent_id = parent_id;
        this.form_id = form_id;
        this.user_id = user_id;
    }

    public void createdTitle(String text) {
        TextView textView = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        layoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setTextSize(30);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);

        llContainer.addView(textView);
    }

    public void createdQuestion(String text, int mandatory) {
        TextView textView = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        layoutParams.gravity = Gravity.LEFT;
        textView.setLayoutParams(layoutParams);
        if (mandatory == 1)
        {
            textView.setText(text+ " (*)");
        } else {
            textView.setText(text);
        }
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        llContainer.addView(textView);
    }

    public void createdText(String text) {
        TextView textView = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        layoutParams.gravity = Gravity.LEFT;
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(Color.GRAY);
        textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);

        llContainer.addView(textView);
    }

    public void createdEditText(int question_id, int mandatory) {
        EditText editText = new EditText(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        editText.setLayoutParams(layoutParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextSize(16);
        editText.setTextColor(Color.BLACK);
        if (mandatory == 1) {
            editText.setHint(required);
        } else {
            editText.setHint("");
        }
        editText.setId(question_id);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(200);
        editText.setFilters(inputFilters);

        llContainer.addView(editText);

        editable.add(editText);
    }

    public void createdEditTextMultiline(int question_id, int mandatory) {
        EditText editText = new EditText(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        editText.setLayoutParams(layoutParams);
        editText.setSingleLine(false);
        editText.setTextSize(16);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLines(1);
        editText.setMaxLines(10);
        editText.setVerticalScrollBarEnabled(true);
        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        if (mandatory == 1) {
            editText.setHint(required);
        } else {
            editText.setHint("");
        }
        editText.setId(question_id);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(1000);
        editText.setFilters(inputFilters);

        llContainer.addView(editText);

        editableMultiline.add(editText);
    }

    public void createdSpinner(int question_id, JSONArray data) {
        ArrayList<OptionObject> items = new ArrayList<>();

        Spinner spinner = new Spinner(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        spinner.setLayoutParams(layoutParams);
        spinner.setId(question_id);
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject option = data.getJSONObject(i);
                Integer id = option.getInt("id");
                String name = option.getString("name");
                items.add(new OptionObject(id, name));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        OptionsAdapter optionsAdapter = new OptionsAdapter(activity, items);
        spinner.setAdapter(optionsAdapter);

        llContainer.addView(spinner);

        selections.add(spinner);
    }

    public void createdSpinnerSpecial(int question_id, JSONArray data) {
        ArrayList<OptionObject> items = new ArrayList<>();

        Spinner spinner = new Spinner(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        spinner.setLayoutParams(layoutParams);
        spinner.setId(question_id);
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject option = data.getJSONObject(i);
                Integer id = option.getInt("id");
                String input_data = option.getString("input_data");
                items.add(new OptionObject(id, input_data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        OptionsAdapter optionsAdapter = new OptionsAdapter(activity, items);
        spinner.setAdapter(optionsAdapter);

        llContainer.addView(spinner);

        selectionsSpecial.add(spinner);
    }

    public void createdTextViewDate(int question_id, int mandatory) {
        TextView textView = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        textView.setLayoutParams(layoutParams);
        textView.setId(question_id);
        textView.setTextSize(16);
        textView.setText(textDate);
        if (mandatory == 1) {
            textView.setHint(required);
        } else {
            textView.setHint("");
        }

        llContainer.addView(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(textView);
            }
        });

        dates.add(textView);
    }

    public void createdTextViewHour(int question_id, int mandatory) {
        TextView textView = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        textView.setLayoutParams(layoutParams);
        textView.setId(question_id);
        textView.setTextSize(16);
        textView.setText(textHour);
        if (mandatory == 1) {
            textView.setHint(required);
        }else {
            textView.setHint("");
        }

        llContainer.addView(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHour(textView);
            }
        });

        hours.add(textView);
    }

    public void createdImageView(int question_id, int mandatory) {
        ImageView imageView = new ImageView(activity);
        imageView.setId(question_id);
        imageView.setImageResource(R.drawable.ic_baseline_photo_camera_24);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(0, 20, 0, 0);
        imageView.setLayoutParams(layoutParams);

        ImageObject imageObject = new ImageObject(question_id, null, mandatory);

        llContainer.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageId = question_id;
                imageObjectSelected = imageObject;
                captureImage();
            }
        });

        images.add(imageObject);
    }

    public void createdSignature(int question_id, int mandatory) {
        ImageView imageView = new ImageView(activity);
        imageView.setId(question_id);
        imageView.setImageResource(R.drawable.ic_baseline_create_24);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(0, 20, 0, 0);
        imageView.setLayoutParams(layoutParams);

        ImageObject imageObject = new ImageObject(question_id, null, mandatory);

        llContainer.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureSignature(imageView, imageObject);
            }
        });

        signatures.add(imageObject);
    }

    public void createdImage(String image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        imageBytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        ImageView imageView = new ImageView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(550, 200);
        layoutParams.setMargins(0, 20, 0, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(decodedImage);

        llContainer.addView(imageView);
    }

    public boolean validate() {
        boolean validated = true;

        JSONArray answers = new JSONArray();

        for (Iterator iterator = editable.iterator(); iterator.hasNext(); ) {
            EditText editText = (EditText) iterator.next();
            int question_id =  editText.getId();
            String input_data = editText.getText().toString().trim();
            String mandatory = editText.getHint().toString().trim();
            editText.setTextColor(Color.BLACK);

            if (input_data.equals("") && mandatory.equals(required)) {
                validated = false;
                editText.setTextColor(Color.RED);
            } else {
                try {
                    JSONObject answer = new JSONObject();
                    answer.put("question_id", question_id);
                    answer.put("option_id", 0);
                    answer.put("input_data", input_data);
                    answer.put("media_file", 0);
                    answers.put(answer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator iterator = editableMultiline.iterator(); iterator.hasNext(); ) {
            EditText editText = (EditText) iterator.next();
            int question_id =  editText.getId();
            String input_data = editText.getText().toString().trim();
            String mandatory = editText.getHint().toString().trim();
            editText.setTextColor(Color.BLACK);

            if (input_data.equals("") && mandatory.equals(required)) {
                validated = false;
                editText.setTextColor(Color.RED);
            } else {
                try {
                    JSONObject answer = new JSONObject();
                    answer.put("question_id", question_id);
                    answer.put("option_id", 0);
                    answer.put("input_data", input_data);
                    answer.put("media_file", 0);
                    answers.put(answer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator iterator = selections.iterator(); iterator.hasNext(); ) {
            Spinner spinner = (Spinner) iterator.next();
            int position = spinner.getSelectedItemPosition();
            OptionObject selected = (OptionObject) spinner.getItemAtPosition(position);
            int question_id =  spinner.getId();
            int option_id = selected.getId();

            try {
                JSONObject answer = new JSONObject();
                answer.put("question_id", question_id);
                answer.put("option_id", option_id);
                answer.put("input_data", 0);
                answer.put("media_file", 0);
                answers.put(answer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (Iterator iterator = dates.iterator(); iterator.hasNext(); ) {
            TextView textView = (TextView) iterator.next();
            int question_id =  textView.getId();
            String input_data = textView.getText().toString().trim();
            String mandatory = textView.getHint().toString().trim();
            textView.setTextColor(Color.GRAY);

            if (input_data.equals(textDate) && mandatory.equals(required)) {
                validated = false;
                textView.setTextColor(Color.RED);
            } else {
                if (input_data.equals(textDate)) {
                    input_data = "";
                }

                try {
                    JSONObject answer = new JSONObject();
                    answer.put("question_id", question_id);
                    answer.put("option_id", 0);
                    answer.put("input_data", input_data);
                    answer.put("media_file", 0);
                    answers.put(answer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator iterator = hours.iterator(); iterator.hasNext(); ) {
            TextView textView = (TextView) iterator.next();
            int question_id =  textView.getId();
            String input_data = textView.getText().toString().trim();
            String mandatory = textView.getHint().toString().trim();
            textView.setTextColor(Color.GRAY);

            if (input_data.equals(textHour) && mandatory.equals(required)) {
                validated = false;
                textView.setTextColor(Color.RED);
            } else {
                if (input_data.equals(textHour)) {
                    input_data = "";
                }

                try {
                    JSONObject answer = new JSONObject();
                    answer.put("question_id", question_id);
                    answer.put("option_id", 0);
                    answer.put("input_data", input_data);
                    answer.put("media_file", 0);
                    answers.put(answer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator iterator = images.iterator(); iterator.hasNext(); ) {
            ImageObject imageObject = (ImageObject) iterator.next();
            int question_id =  imageObject.getId();
            Bitmap file = imageObject.getImage();
            int mandatory = imageObject.getMandatory();
            ImageView imageView = activity.findViewById(question_id);

            if (file == null && mandatory == 1) {
                validated = false;
                imageView.setImageResource(R.drawable.ic_baseline_photo_camera_24_red);
            } else {
                String media_file = "";

                if (file != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    file.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    media_file = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }

                try {
                    JSONObject answer = new JSONObject();
                    answer.put("question_id", question_id);
                    answer.put("option_id", 0);
                    answer.put("input_data", 0);
                    answer.put("media_file", media_file);
                    answers.put(answer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator iterator = signatures.iterator(); iterator.hasNext(); ) {
            ImageObject imageObject = (ImageObject) iterator.next();
            int question_id =  imageObject.getId();
            Bitmap file = imageObject.getImage();
            int mandatory = imageObject.getMandatory();
            ImageView imageView = activity.findViewById(question_id);

            if (file == null && mandatory == 1) {
                validated = false;
                imageView.setImageResource(R.drawable.ic_baseline_create_24_red);
            } else {
                String media_file = "";

                if (file != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    file.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    media_file = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }

                try {
                    JSONObject answer = new JSONObject();
                    answer.put("question_id", question_id);
                    answer.put("option_id", 0);
                    answer.put("input_data", 0);
                    answer.put("media_file", media_file);
                    answers.put(answer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Iterator iterator = selectionsSpecial.iterator(); iterator.hasNext(); ) {
            Spinner spinner = (Spinner) iterator.next();
            int position = spinner.getSelectedItemPosition();
            OptionObject selected = (OptionObject) spinner.getItemAtPosition(position);
            int question_id =  spinner.getId();
            int option_id = selected.getId();

            try {
                JSONObject answer = new JSONObject();
                answer.put("question_id", question_id);
                answer.put("option_id", 0);
                answer.put("input_data", option_id);
                answer.put("media_file", 0);
                answers.put(answer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Util util = new Util(activity);

        try {
            eventForm.put("parent_id", parent_id);
            eventForm.put("form_id", form_id);
            eventForm.put("user_id", user_id);
            eventForm.put("registered", util.getDateTime());
            eventForm.put("answers", answers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("json", "" + eventForm);
        return validated;
    }

    private void getDate(TextView textView) {
        int Year, Month, Day;

        Calendar calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int aux = month + 1;
                String date = "" + year + "-" + aux + "-" + dayOfMonth;
                textView.setText(date);

            }
        }, Year, Month, Day);
        datePickerDialog.setTitle("seleccione la fecha");
        datePickerDialog.show();
    }

    private void getHour(TextView textView) {
        int Hour, Minute;

        Calendar calendar = Calendar.getInstance();
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                textView.setText(hourOfDay + ":" + minute);
            }
        }, Hour, Minute, false);
        timePickerDialog.setTitle("seleccione la Hora");
        timePickerDialog.show();
    }

    protected void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, "com.abogados.proyectog.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = "file://" + image.getAbsolutePath();

        return image;
    }

    private void captureSignature(ImageView imageView, ImageObject imageObject) {
        LinearLayout llWindow = new LinearLayout(activity);

        LinearLayout llBody = new LinearLayout(activity);
        LinearLayout.LayoutParams paramsBody = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsBody.setMargins(20, 10, 20, 10);
        llBody.setLayoutParams(paramsBody);
        llBody.setOrientation(LinearLayout.VERTICAL);
        llBody.setPadding(10, 10, 10, 10);
        llWindow.addView(llBody);

        LinearLayout llSignature = new LinearLayout(activity);
        LinearLayout.LayoutParams paramsSignature = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
        llSignature.setLayoutParams(paramsSignature);
        llSignature.setOrientation(LinearLayout.HORIZONTAL);
        RoundRectShape roundRectShapeBody = new RoundRectShape(new float[]{
                30, 30, 30, 30,
                30, 30, 30, 30}, null, null);
        ShapeDrawable shapeDrawableImage = new ShapeDrawable(roundRectShapeBody);
        shapeDrawableImage.getPaint().setColor(Color.BLACK);
        shapeDrawableImage.setPadding(20, 20, 20, 20);
        llSignature.setBackground(shapeDrawableImage);
        llSignature.setGravity(Gravity.CENTER);
        llBody.addView(llSignature);

        LinearLayout llContent = new LinearLayout(activity);
        LinearLayout.LayoutParams paramsContent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llContent.setLayoutParams(paramsContent);
        llContent.setOrientation(LinearLayout.HORIZONTAL);
        llContent.setPadding(30, 30, 30, 30);
        llContent.setGravity(Gravity.CENTER);

        CaptureBitmapView captureBitmapView;
        captureBitmapView = new CaptureBitmapView(activity, null);
        llContent.addView(captureBitmapView);

        llSignature.addView(llContent);

        Button btnAccept = new Button(activity);
        LinearLayout.LayoutParams paramsAccept = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsAccept.setMargins(0, 10, 0, 0);
        btnAccept.setLayoutParams(paramsAccept);
        btnAccept.setText("Guardar");
        llBody.addView(btnAccept);

        Button btnClear = new Button(activity);
        LinearLayout.LayoutParams paramsClear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsClear.setMargins(0, 10, 0, 0);
        btnClear.setLayoutParams(paramsClear);
        btnClear.setText("Limpiar");
        llBody.addView(btnClear);

        Button btnExit = new Button(activity);
        LinearLayout.LayoutParams paramsExit = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsExit.setMargins(0, 10, 0, 0);
        btnExit.setLayoutParams(paramsExit);
        btnExit.setText("Salir");
        llBody.addView(btnExit);

        final AlertDialog optionDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen).create();
        optionDialog.setTitle("Firma");
        optionDialog.setView(llWindow);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureBitmapView.ClearCanvas();

                imageObject.setImage(null);
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = captureBitmapView.getBitmap();

                LinearLayout.LayoutParams paramsImageview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
                imageView.setLayoutParams(paramsImageview);
                imageView.setImageBitmap(bitmap);

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = 457;
                int newHeight = 305;

                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        width, height, matrix, true);

                imageObject.setImage(resizedBitmap);

                optionDialog.dismiss();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionDialog.dismiss();
            }
        });

        optionDialog.show();
    }
}
