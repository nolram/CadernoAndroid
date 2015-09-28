package com.lab11.nolram.cadernocamera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.components.TouchImageView;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Folha;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.lang.ref.WeakReference;


/**
 * A placeholder fragment containing a simple view.
 */
public class FolhaActivityFragment extends Fragment {

    private String localImagem;
    private TouchImageView imgFoto;


    public FolhaActivityFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*IProfile profile;
        int id_badge = getResources().getIdentifier(badge, "drawable",
                getActivity().getPackageName());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(titulo);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(cor_principal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile = new ProfileDrawerItem().withName(caderno_titulo).withIcon(
                    getResources().getDrawable(id_badge, getActivity().getTheme()));
        }else{
            profile = new ProfileDrawerItem().withName(caderno_titulo).withIcon(
                    getResources().getDrawable(id_badge));
        }
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(getActivity())
                .addProfiles(profile)
                .withHeaderBackground(id_cor_principal)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        menu = new DrawerBuilder(getActivity())
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withActivity(getActivity())
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.txt_info),
                        new PrimaryDrawerItem().withName(titulo).withIcon(FontAwesome.Icon.faw_font),
                        new PrimaryDrawerItem().withName(data).withIcon(FontAwesome.Icon.faw_calendar),
                        new PrimaryDrawerItem().withName(tags).withIcon(FontAwesome.Icon.faw_tags)
                )
                .withAccountHeader(headerResult)
                .withSavedInstance(savedInstanceState)
                .build();

        //toolbar.setTitle(titulo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Integer.valueOf(cor_secundaria));
        }*/
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
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
