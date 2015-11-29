package com.supertask.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.supertask.R;
import com.supertask.util.Constant;

import java.io.File;

public class CardsDataAdapter extends ArrayAdapter<String> {

    public CardsDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, final View contentView, ViewGroup parent) {
        ImageView imgMain = (ImageView) (contentView.findViewById(R.id.imgMain));

        File imgFile = new File(Environment.getExternalStorageDirectory()
                + "/" + Constant.folder + "/" + getItem(position));
        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


            imgMain.setImageBitmap(myBitmap);
        }
            // v.setText(getItem(position));
            return contentView;
        }

    }

