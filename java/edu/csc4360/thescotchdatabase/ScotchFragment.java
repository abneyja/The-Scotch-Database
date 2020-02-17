package edu.csc4360.thescotchdatabase;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScotchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScotchFragment#} factory method to
 * create an instance of this fragment.
 */
public class ScotchFragment extends Fragment {

    public ScotchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.scotch_fragment, container, false);
        view.setBackgroundColor(Color.WHITE);

        String sName = this.getArguments().getString("name");
        String sNotes = this.getArguments().getString("notes");
        float sRating = this.getArguments().getFloat("rating");
        boolean sFavorite = this.getArguments().getBoolean("favorite");
        String sImage = this.getArguments().getString("image");

        TextView name = view.findViewById(R.id.edtName);
        name.setText("Name: " + sName);

        TextView notes = view.findViewById(R.id.edtPrice);
        notes.setText("Tasting Notes: " + sNotes);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating(sRating);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(ImageUtil.convert(sImage));

        CheckBox checkbox = view.findViewById(R.id.checkBox2);
        checkbox.setChecked(sFavorite);

        return view;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
