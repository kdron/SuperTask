package com.supertask.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.supertask.R;
import com.supertask.adapter.CardsDataAdapter;
import com.supertask.util.ConstantClass;
import com.supertask.util.DatabaseHelper;
import com.wenchao.cardstack.CardStack;

/**
 * Created by pradipk on 11/25/2015.
 */
public class BookmarkFragment extends Fragment implements CardStack.CardEventListener, OnClickListener {
    private CardStack mCardStack;
    private CardsDataAdapter mCardAdapter;
    ConstantClass constantClass;
    Context context;
    Button btnShow;

    public BookmarkFragment() {
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
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        context = getActivity();
        init(view);

        return view;
    }

    private void init(View view) {
        mCardStack = (CardStack) view.findViewById(R.id.container);
        btnShow = (Button) view.findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);
        constantClass = new ConstantClass(context);
        mCardStack.setListener(this);

        //
        mCardStack.setContentResource(R.layout.card_content);
        mCardAdapter = new CardsDataAdapter(context, 0);
     //   setCardStack();


    }

    public void setCardStack() {
        Cursor cursor = constantClass.getCloths();
        if (cursor.getCount() > 0) {

            for (int i = 0; i < cursor.getCount(); i++) {
                String isBookmark = cursor.getString(cursor.getColumnIndex(DatabaseHelper.bookmark));
                if (isBookmark.equalsIgnoreCase("1")) {
                    mCardAdapter.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.img_name)));
                }
                cursor.moveToNext();
            }
            if (mCardAdapter.getCount() == 0) {
                mCardStack.setVisibility(View.GONE);
                btnShow.setVisibility(View.VISIBLE);

                btnShow.setText("No bookmark found");
            } else {
                btnShow.setVisibility(View.GONE);
                mCardStack.setVisibility(View.VISIBLE);
                btnShow.setText("Show Archive");
                mCardStack.setAdapter(mCardAdapter);
            }
        } else {
            mCardStack.setVisibility(View.GONE);
            btnShow.setVisibility(View.VISIBLE);
            btnShow.setText("No bookmark found");

        }
    }

    @Override
    public boolean swipeEnd(int section, float distance) {

       /* Log.d("click1", "end " + section + "  " + distance);
        if (distance > 350) {
            mCardStack.discardTop(section);
        }*/
        return (distance > 300) ? true : false;
    }

    @Override
    public boolean swipeStart(int section, float distance) {
        Log.d("click1", "Start " + section + "  " + distance);

        return true;
    }

    @Override
    public boolean swipeContinue(int section, float distanceX, float distanceY) {
        Log.d("click1", "Continue " + section + "  " + distanceX + "  " + distanceY);

        return true;
    }

    @Override
    public void discarded(int mIndex, int direction) {
        Log.d("click1", "discarded " + mIndex + "  " + direction);

        if (direction == 1 || direction == 3) {

            constantClass.bookmarkCloth(mCardAdapter.getItem(mIndex - 1).toString(), "0");
            Toast.makeText(context, "Bookmark Removed", Toast.LENGTH_SHORT).show();

        }

        if(mIndex==mCardAdapter.getCount())
        {
            btnShow.setVisibility(View.VISIBLE);
            mCardStack.setVisibility(View.GONE);

        }
    }

    @Override
    public void topCardTapped() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShow:
                setCardStack();

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setCardStack();
    }
}
