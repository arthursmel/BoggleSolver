package com.domain.mel.solver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * List adapter that displays the found words
 * the score of the word, and a link to search
 * the work for the definition
 */
public class ListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "ListAdapter";

    private Context context;
    // The array of found words by the solver
    private String[] foundWords;
    // The array of the corresponding scores
    private Integer[] foundWordsScores;
    private ListAdapterListener listener;
    // Represents which words have been selected by the checkbox
    private boolean[] selectedFoundWords;

    /**
     * Constructor
     * @param context context
     * @param foundWords the words found by the solver
     * @param foundWordsScores the respective scores of the words
     * @param listener used to listen for check box clicks, list item
     *                 clicks, search button clicks, for each word
     */
    public ListAdapter(Context context,
                       String[] foundWords,
                       Integer[] foundWordsScores,
                       ListAdapterListener listener) {
        super(context, R.layout.list_row, foundWords);

        this.context = context;
        this.foundWords = foundWords;
        this.foundWordsScores = foundWordsScores;
        this.listener = listener;
        this.selectedFoundWords = new boolean[this.foundWords.length];
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(this.context);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_row, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
            viewHolder.wordScoreTextView = convertView.findViewById(R.id.wordScore);
            viewHolder.foundWordTextView = convertView.findViewById(R.id.foundWord);
            viewHolder.searchButton = convertView.findViewById(R.id.searchButton);
            viewHolder.listItem = convertView.findViewById(R.id.listItem);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.wordScoreTextView.setText(String.valueOf(foundWordsScores[position]));
        viewHolder.foundWordTextView.setText(this.foundWords[position]);
        viewHolder.checkBox.setId(position);
        viewHolder.checkBox.setChecked(this.selectedFoundWords[position]);

        viewHolder.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSearch(position);
            }
        });

        viewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position);
            }
        });

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox checkBox = (CheckBox) view;
                int id = checkBox.getId();

                // Update the item's selected state
                checkBox.setChecked(!selectedFoundWords[id]);
                selectedFoundWords[id] = !selectedFoundWords[id];

                listener.onCheck(id, selectedFoundWords[id]);

            }
        });

        return convertView;
    }

    private class ViewHolder {
        private CheckBox checkBox;
        private TextView wordScoreTextView;
        private TextView foundWordTextView;
        private ImageButton searchButton;
        private RelativeLayout listItem;
    }

    public interface ListAdapterListener {
        void onSearch(int position);
        void onClick(int position);
        void onCheck(int position, boolean isSelected);
    }

}
