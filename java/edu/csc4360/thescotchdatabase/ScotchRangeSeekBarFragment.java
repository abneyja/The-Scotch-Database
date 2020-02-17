package edu.csc4360.thescotchdatabase;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;



/**
 * A simple {@link Fragment} subclass.
 */
public class ScotchRangeSeekBarFragment extends Fragment {
    private ScotchRangeSeekBarFragment.ScotchRangeSeekBarAdapterListener listener;
    private RangeSeekBar rangeBar;

    public ScotchRangeSeekBarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_scotch_range_seek_bar, container, false);
        rangeBar = view.findViewById(R.id.rangeSeekBar);

        if(getFromSp("left") > -1 && getFromSp("right") < 6){
            rangeBar.setProgress(getFromSp("left"), getFromSp("right"));
        }else
            rangeBar.setProgress(0f, 5f);
        rangeBar.setTickMarkTextMargin(20);
        rangeBar.setIndicatorTextDecimalFormat("0.0");
        rangeBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            float leftValue;
            float rightValue;

            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                //leftValue is left seekbar value, rightValue is right seekbar value
                this.leftValue = leftValue;
                this.rightValue = rightValue;
                saveInSp("left", leftValue);
                saveInSp("right", rightValue);
                listener.onScotchRangeSeekBarAdapterSent(leftValue, rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //start tracking touch

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //stop tracking touch
                Toast.makeText(view.getContext(), "Displaying " + leftValue + " to " + rightValue + " rated scotches!",Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private float getFromSp(String key){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("SEEK_BAR", android.content.Context.MODE_PRIVATE);
        return preferences.getFloat(key, -1);
    }

    private void saveInSp(String key, float value){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("SEEK_BAR", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScotchRangeSeekBarFragment.ScotchRangeSeekBarAdapterListener) {
            listener = (ScotchRangeSeekBarFragment.ScotchRangeSeekBarAdapterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ScotchRangeSeekBarAdapterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ScotchRangeSeekBarAdapterListener {
        void onScotchRangeSeekBarAdapterSent(float leftValue, float rightValue);
    }
}
