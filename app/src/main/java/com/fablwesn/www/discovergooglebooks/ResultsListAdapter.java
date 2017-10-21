package com.fablwesn.www.discovergooglebooks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter for the Recycler view, displays the results in a list and styles the views
 */
class ResultsListAdapter extends RecyclerView.Adapter<ResultsListAdapter.MyViewHolder> {

    // list with {@link BookModel}s
    private List<BookModel> bookList;

    // characters added to correct android's cutting of the last letter when the text-style is italic
    private final static String ITALIC_CORRECTION_SPACE = " ";

    // single list item, get everything we need to display the results correctly
    class MyViewHolder extends RecyclerView.ViewHolder {
        Context context;

        TextView title, author, publisher, releaseYear;
        ImageView cover, link;

        MyViewHolder(View listItem, Context context) {
            super(listItem);
            this.context = context;
            // get views to change
            title = listItem.findViewById(R.id.list_item_txt_title);
            author = listItem.findViewById(R.id.list_item_txt_author);
            publisher = listItem.findViewById(R.id.list_item_txt_publisher);
            releaseYear = listItem.findViewById(R.id.list_item_txt_published_date);
            cover = listItem.findViewById(R.id.list_item_img_cover);
            link = listItem.findViewById(R.id.list_item_img_internet);
        }
    }

    ResultsListAdapter(List<BookModel> list) {
        bookList = list;
    }

    /* onCreateViewHolder
    *   - assign the layout to use
    *******************************************/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_results, parent, false);

        return new MyViewHolder(itemView,parent.getContext());
    }

    /* onBindViewHolder
    *   - set correct data
    *******************************************/
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BookModel bookModel = bookList.get(position);

        // add space to the views displayed in italic style
        String correctedPublisherText = bookModel.getBookPublisher() + ITALIC_CORRECTION_SPACE;
        String correctedReleaseText = bookModel.getBookReleaseYear() + ITALIC_CORRECTION_SPACE;

        // update text views
        holder.title.setText(bookModel.getBookTitle());
        holder.author.setText(bookModel.getBookAuthor());
        holder.publisher.setText(correctedPublisherText);
        holder.releaseYear.setText(correctedReleaseText);

        // load images with external library Picasso {@link http://square.github.io/picasso/}
        Picasso.with(holder.context)
                .load(bookModel.getBookCover())
                .fit()
                .placeholder(R.drawable.img_list_cover_placeholder)
                .into(holder.cover);

        // set a click listener to the internet icon, opening the
        // books Google Books web-page
        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View imageView) {
                Uri bookInfoUri = Uri.parse(bookModel.getBookUrl());

                imageView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, bookInfoUri));
            }
        });
    }

    /* getItemCount
    *   - return the item quantity of the list
    *******************************************/
    @Override
    public int getItemCount() {
        return bookList.size();
    }
}