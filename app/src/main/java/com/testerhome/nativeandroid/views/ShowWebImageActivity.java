package com.testerhome.nativeandroid.views;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;
import com.testerhome.nativeandroid.views.widgets.photodraweeview.PhotoDraweeView;

import butterknife.BindView;

/**
 * Created by libin on 16/5/15.
 */
public class ShowWebImageActivity extends BackBaseActivity {

    @BindView(R.id.photo_drawee_view)
    PhotoDraweeView mPhotoDraweeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web_image);

        if (getIntent().hasExtra("image")){

            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(getIntent().getStringExtra("image")));
            controller.setOldController(mPhotoDraweeView.getController());
            // You need setControllerListener
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null || mPhotoDraweeView == null) {
                        return;
                    }
                    mPhotoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            mPhotoDraweeView.setController(controller.build());
        }
    }
}
