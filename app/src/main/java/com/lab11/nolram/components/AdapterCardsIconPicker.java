package com.lab11.nolram.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lab11.nolram.cadernocamera.R;

/**
 * Created by nolram on 16/09/15.
 */
public class AdapterCardsIconPicker extends RecyclerView.Adapter<AdapterCardsIconPicker.ViewHolder> {
    private final Integer[] mDataset = {R.drawable.book_2, R.drawable.book_4, R.drawable.book_5,
            R.drawable.book_6, R.drawable.bookmark_18, R.drawable.calculator_2,
            R.drawable.calculator_4, R.drawable.calculator_6, R.drawable.calendar_4,
            R.drawable.certificate_4, R.drawable.checkout_2, R.drawable.code_fork_7,
            R.drawable.coffee_11, R.drawable.coffee_2, R.drawable.connection_3,
            R.drawable.cpu_6, R.drawable.crown_2, R.drawable.crown_6,
            R.drawable.cube_2, R.drawable.currency_brazilian_real_2, R.drawable.currency_dollar_2,
            R.drawable.danger_3, R.drawable.danger_6, R.drawable.diamond_10,
            R.drawable.direction_7, R.drawable.disk_2, R.drawable.drop_2,
            R.drawable.drop_3, R.drawable.easter_24, R.drawable.favorite_3,
            R.drawable.female, R.drawable.male, R.drawable.fingerprint_20,
            R.drawable.flag_6, R.drawable.flask, R.drawable.flask_10,
            R.drawable.flip_chart_3, R.drawable.font_size, R.drawable.gamepad_2,
            R.drawable.gear_10, R.drawable.glasses_7, R.drawable.glasses_8,
            R.drawable.globe_4, R.drawable.headphones_2, R.drawable.html_code,
            R.drawable.infinity_4, R.drawable.javascript_code, R.drawable.keyboard_2,
            R.drawable.language_4, R.drawable.language_8, R.drawable.light_bulb_7,
            R.drawable.light_bulb_8, R.drawable.line_chart_3, R.drawable.magnet_2,
            R.drawable.medal_5, R.drawable.megaphone_3, R.drawable.microphone_2,
            R.drawable.newspaper, R.drawable.paintbrush_7, R.drawable.paper_plane_2,
            R.drawable.pen_2, R.drawable.photo_camera_2, R.drawable.picture,
            R.drawable.pin_11, R.drawable.puzzle_2, R.drawable.ruler_15,
            R.drawable.quote, R.drawable.quote_2, R.drawable.quote_15,
            R.drawable.radio_7, R.drawable.recycling, R.drawable.retweet_6,
            R.drawable.rocket_2, R.drawable.script_4, R.drawable.sitemap_12,
            R.drawable.smiley_2, R.drawable.smiley_cool, R.drawable.smiley_cry,
            R.drawable.smiley_dead, R.drawable.smiley_excited, R.drawable.smiley_geek,
            R.drawable.smiley_happy, R.drawable.smiley_lol, R.drawable.smiley_neutral,
            R.drawable.smiley_sad, R.drawable.smiley_scared, R.drawable.smiley_shocked,
            R.drawable.smiley_surprised, R.drawable.smiley_wink_2, R.drawable.sound_wave_2,
            R.drawable.special_symbol, R.drawable.star_4, R.drawable.superscript,
            R.drawable.tablet, R.drawable.target_4, R.drawable.tools_2,
            R.drawable.tv, R.drawable.tv_4, R.drawable.user_14,
            R.drawable.video_3, R.drawable.virus_2, R.drawable.adhesive_bandage_3,
            R.drawable.adhesive_bandage_5, R.drawable.ruler_5, R.drawable.ruler_30};

    private View layoutView;

    public AdapterCardsIconPicker() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_icon, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int img = mDataset[position];
        holder.mImgPicker.setImageResource(img);
        ;
    }

    public int getItem(int position) {
        return mDataset[position];
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImgPicker;

        public ViewHolder(View v) {
            super(v);
            mImgPicker = (ImageView) v.findViewById(R.id.img_picker);
        }
    }
}
