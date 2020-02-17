package edu.csc4360.thescotchdatabase;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ScotchListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int layout;
    private ArrayList<Scotch> scotchList;
    private ArrayList<Scotch> originalList;

    public ScotchListAdapter(Context context, int layout, ArrayList<Scotch> scotchList) {
        this.context = context;
        this.layout = layout;
        this.scotchList = scotchList;
        this.originalList = new ArrayList<>();
        this.originalList = scotchList;
    }

    @Override
    public int getCount() {
        return scotchList.size();
    }

    @Override
    public Object getItem(int position) {
        return scotchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtName;
        RatingBar ratingBar;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.imageView = (ImageView) row.findViewById(R.id.imgScotch);
            holder.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Scotch scotch = scotchList.get(position);

        holder.txtName.setText(scotch.getName());
        holder.ratingBar.setRating(scotch.getStars());
        String scotchImage = scotch.getImage();
        //Bitmap bitmap = BitmapFactory.decodeByteArray(gameImage, 0, gameImage.length);
        Bitmap bitmap = ImageUtil.convert(scotchImage);
        holder.imageView.setImageBitmap(bitmap);
        holder.checkBox.setChecked(scotch.getFavorite());

        return row;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                scotchList = (ArrayList<Scotch>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Scotch> FilteredArrayScotch = new ArrayList<Scotch>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < originalList.size(); i++) {
                    String scotchNames = originalList.get(i).getName();
                    if (scotchNames.toLowerCase().contains(constraint.toString()))  {
                        FilteredArrayScotch.add(originalList.get(i));
                    }
                }

                results.count = FilteredArrayScotch.size();
                results.values = FilteredArrayScotch;

                return results;
            }
        };

        return filter;
    }

}

