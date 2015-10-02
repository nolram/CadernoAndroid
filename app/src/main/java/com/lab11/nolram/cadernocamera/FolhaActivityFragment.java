package com.lab11.nolram.cadernocamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.components.TouchImageView;
import com.lab11.nolram.database.Database;

import java.io.File;
import java.lang.ref.WeakReference;


/**
 * A placeholder fragment containing a simple view.
 */
public class FolhaActivityFragment extends Fragment {

    private String localImagem;
    private TouchImageView imgFoto;
    private ProgressBar progressBar;


    public FolhaActivityFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            Bitmap newBitmap;
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(data);
                float scalingFactor = getBitmapScalingFactor(myBitmap);
                newBitmap = BitmapHelper.ScaleBitmap(myBitmap, scalingFactor);
            }catch (NullPointerException e){
                newBitmap = null;
            }
            return newBitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(View.GONE);
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_folha, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imgFoto = (TouchImageView) view.findViewById(R.id.img_foto);
        Bundle bundle = getArguments();
        localImagem = bundle.getString(Database.FOLHA_LOCAL_IMAGEM);
        File imgFile = new File(localImagem);
        //Log.d("local", mCurrentPhotoPath);
        if(imgFile.exists()){
            loadBitmap(localImagem, imgFoto);
            /*Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            float scalingFactor = this.getBitmapScalingFactor(myBitmap);
            Bitmap newBitmap = BitmapHelper.ScaleBitmap(myBitmap, scalingFactor);
            imgFoto.setImageBitmap(newBitmap);*/
        }else{
            Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.txt_mensage_remove_image), Toast.LENGTH_LONG).show();
            imgFoto.setImageResource(R.drawable.picture_remove);
        }
        return view;
    }

    public void loadBitmap(String localImagem, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(localImagem);
    }

    private float getBitmapScalingFactor(Bitmap bm) {
        // Get display width from device
        WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int displayWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            displayWidth = size.x;
        }else {
            displayWidth = display.getWidth();  // deprecated
        }
        // Get margin to use it for calculating to max width of the ImageView
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) this.imgFoto.getLayoutParams();
        int leftMargin = layoutParams.leftMargin;
        int rightMargin = layoutParams.rightMargin;

        // Calculate the max width of the imageView
        int imageViewWidth = displayWidth - (leftMargin + rightMargin);

        // Calculate scaling factor and return it
        return ( (float) imageViewWidth / (float) bm.getWidth() );
    }
}
