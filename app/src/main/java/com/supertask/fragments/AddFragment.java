package com.supertask.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.supertask.R;
import com.supertask.adapter.CardsDataAdapter;
import com.supertask.util.Constant;
import com.supertask.util.ConstantClass;
import com.wenchao.cardstack.CardStack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by pradipk on 11/25/2015.
 */
public class AddFragment extends Fragment implements View.OnClickListener {
    protected int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    ConstantClass constantClass;

    Bitmap thumbnail;
    String imageName = "";
    Context context;
    ImageButton imgAdd;
    ImageButton imgDone;
    ImageButton imgCancle;
    private ImageView imgMain;

    public static final int RESULT_OK = -1;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        init(view);

        return view;
    }


    private void init(View view) {
        imgMain = (ImageView) view.findViewById(R.id.imgMain);
        imgAdd = (ImageButton) view.findViewById(R.id.imgAdd);
        imgCancle = (ImageButton) view.findViewById(R.id.imgCancle);
        imgDone = (ImageButton) view.findViewById(R.id.imgDone);
        context = getActivity();
        constantClass = new ConstantClass(context);
        imgAdd.setOnClickListener(this);
        imgDone.setOnClickListener(this);

        imgCancle.setOnClickListener(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //in fragment class callback

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {

                File dir = new File(
                        Environment.getExternalStorageDirectory()
                                + "/" + Constant.folder + "/");
                File output = new File(dir, "temp.jpeg");
                try {

                    thumbnail = getscaledImage(output.toString());


                    imgMain.setImageBitmap(thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageName = SavedImages(thumbnail);


            } else if (requestCode == GALLERY_PICTURE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                imageName = SavedImages(bm);

                imgMain.setImageBitmap(bm);


            }
            setVsible();

        }
    }

    public static Bitmap getscaledImage(String filePath) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = (float) (actualWidth / actualHeight);
        float maxRatio = 612.0f / 800.0f;
        Log.v("Pictures", "Before scaling Width and height are " + actualWidth + "--" + actualHeight);
        if (((float) actualHeight) > 800.0f || ((float) actualWidth) > 612.0f) {
            if (imgRatio < maxRatio) {
                actualWidth = (int) (((float) actualWidth) * (800.0f / ((float) actualHeight)));
                actualHeight = (int) 800.0f;
            } else if (imgRatio > maxRatio) {
                actualHeight = (int) (((float) actualHeight) * (612.0f / ((float) actualWidth)));
                actualWidth = (int) 612.0f;
            } else {
                actualHeight = (int) 800.0f;
                actualWidth = (int) 612.0f;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[AccessibilityNodeInfoCompat.ACTION_COPY];
        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception2) {
            exception2.printStackTrace();
        }
        float ratioX = ((float) actualWidth) / ((float) options.outWidth);
        float ratioY = ((float) actualHeight) / ((float) options.outHeight);
        float middleX = ((float) actualWidth) / 2.0f;
        float middleY = ((float) actualHeight) / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - ((float) (bmp.getWidth() / 2)), middleY - ((float) (bmp.getHeight() / 2)), new Paint(2));
        Log.v("Pictures", "After scaling Width and height are " + scaledBitmap.getWidth() + "--" + scaledBitmap.getHeight());
        try {
            int orientation = new ExifInterface(filePath).getAttributeInt("Orientation", 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90.0f);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                // matrix.postRotate(BitmapDescriptorFactory.HUE_CYAN);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                // matrix.postRotate(BitmapDescriptorFactory.HUE_VIOLET);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Resized - ", scaledBitmap.getHeight() + " - " + scaledBitmap.getWidth());
        return scaledBitmap;
    }


    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(((float) height) / ((float) reqHeight));
            int widthRatio = Math.round(((float) width) / ((float) reqWidth));
            if (heightRatio < widthRatio) {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }
        }
        while (((float) (width * height)) / ((float) (inSampleSize * inSampleSize)) > ((float) ((reqWidth * reqHeight) * 2))) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public String SavedImages(Bitmap bm) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + Constant.folder);
        myDir.mkdirs();

        String random = generateRandom(16) + System.currentTimeMillis();
        String fname = random + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return fname;

    }

    public String generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    public void setVsible() {
        imgDone.setVisibility(View.VISIBLE);
        imgCancle.setVisibility(View.VISIBLE);
        imgAdd.setVisibility(View.GONE);
    }

    public void setGone() {
        imgDone.setVisibility(View.GONE);
        imgCancle.setVisibility(View.GONE);
        imgAdd.setVisibility(View.VISIBLE);
        imgMain.setImageDrawable(context.getResources().getDrawable(R.drawable.img_default));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAdd:

                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setContentHolder(new ViewHolder(R.layout.bottom_menu))

                        .setCancelable(true)
                        .setGravity(Gravity.BOTTOM)
                                //       .setAdapter(adapter)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialog, View view) {
                                switch (view.getId()) {
                                    case R.id.lnrCamera:
                                        Log.d("click1", "in " + view.getId());

                                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        File dir = new File(
                                                Environment.getExternalStorageDirectory()
                                                        + "/" + Constant.folder + "/");


                                        File output = new File(dir, "temp.jpeg");
                                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

                                        getActivity().startActivityForResult(i, CAMERA_REQUEST);
                                        dialog.dismiss();
                                        break;

                                    case R.id.lnrGallary:
                                        Log.d("click1", "in " + view.getId());
                                        Intent pictureActionIntent;
                                        pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        pictureActionIntent.setType("image/*");
                                        pictureActionIntent.putExtra("return-data", true);
                                        getActivity().startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                                        dialog.dismiss();

                                        break;


                                }
                            }
                        })


                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

                        .create();
                dialog.show();
                break;

            case R.id.imgDone:
                Cursor cursorCloth = constantClass.getCloths();
                Cursor cursor = constantClass.getCloths(imageName);
                if (imageName.length() > 0 && cursor.getCount() == 0) {
                    constantClass.insertCloths(imageName);
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                    imgMain.setImageDrawable(getResources().getDrawable(R.drawable.img_default));

                } else {
                    Toast.makeText(context, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
                setGone();

                break;
            case R.id.imgCancle:
                setGone();

                break;
        }
    }
}
