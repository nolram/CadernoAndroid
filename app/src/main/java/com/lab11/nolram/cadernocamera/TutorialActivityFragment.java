package com.lab11.nolram.cadernocamera;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class TutorialActivityFragment extends Fragment {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int mBackgroundColor, mPage;

    public static TutorialActivityFragment newInstance(int backgroundColor, int page) {
        TutorialActivityFragment frag = new TutorialActivityFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, backgroundColor);
        b.putInt(PAGE, page);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(BACKGROUND_COLOR))
            throw new RuntimeException("Fragment must contain a \"" + BACKGROUND_COLOR + "\" argument!");
        mBackgroundColor = getArguments().getInt(BACKGROUND_COLOR);

        if (!getArguments().containsKey(PAGE))
            throw new RuntimeException("Fragment must contain a \"" + PAGE + "\" argument!");
        mPage = getArguments().getInt(PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
        int layoutResId;
        switch (mPage) {
            case 0:
                layoutResId = R.layout.fragment_tutorial_01;
                break;
            case 1:
                layoutResId = R.layout.fragment_tutorial_02;
                break;
            case 2:
                layoutResId = R.layout.fragment_tutorial_03;
                break;
            case 3:
                layoutResId = R.layout.fragment_tutorial_04;
                break;
            case 4:
                layoutResId = R.layout.fragment_tutorial_05;
                break;
            default:
                layoutResId = R.layout.fragment_tutorial_06;
                break;
        }

        // Inflate the layout resource file
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        //View background = view.findViewById(R.id.intro_background);
        //background.setBackgroundColor(mBackgroundColor);
    }

}
