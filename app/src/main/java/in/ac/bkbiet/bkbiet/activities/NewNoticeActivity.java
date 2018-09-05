package in.ac.bkbiet.bkbiet.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.Notice;
import in.ac.bkbiet.bkbiet.utils.Statics;

public class NewNoticeActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 21903;
    Button vPublish;
    TextInputEditText vTitle, vDesc;
    ImageView vImage;
    Bitmap currBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice);

        initViews();
    }

    private void initViews() {
        vImage = findViewById(R.id.iv_ann_image);
        vTitle = findViewById(R.id.tiet_ann_title);
        vDesc = findViewById(R.id.tiet_ann_desc);
        vPublish = findViewById(R.id.b_ann_publish);

        vImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        vPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = vTitle.getText().toString();
                String desc = vDesc.getText().toString();

                if (title.isEmpty()) {
                    ((TextInputLayout) vTitle.getParentForAccessibility()).setError("Title is empty");
                    return;
                }

                if (desc.isEmpty()) {
                    ((TextInputLayout) vDesc.getParentForAccessibility()).setError("Description is empty");
                    return;
                }

                uploadNotice(new Notice(title, desc));
            }
        });
    }

    private void getImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadNotice(final Notice notice) {
        Bitmap imageBitmap = currBitmap;
        if (imageBitmap != null) {
            vImage.setImageBitmap(imageBitmap);
            //encodeBitmapAndSaveToFirebase(imageBitmap);

            // Get the data from an ImageView as bytes
            //imageView.setDrawingCacheEnabled(true);
            //imageView.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bImage = baos.toByteArray();

            String random = Statics.generateRandomString(4);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("notices/" + Statics.getTimeStamp("dd_mm_yy_hh_mm_ss_") + random);
            UploadTask uploadTask = imageRef.putBytes(bImage);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long total = taskSnapshot.getTotalByteCount();
                    long progress = taskSnapshot.getBytesTransferred();

                    int kTotal = (int) (total / 1024);
                    int kProgress = (int) (progress / 1024);

                    findViewById(R.id.rl_ann_uploader).setVisibility(View.VISIBLE);
                    ProgressBar progressBar = findViewById(R.id.pb_ann_progress);
                    progressBar.setIndeterminate(false);
                    progressBar.setMax(kTotal);
                    progressBar.setProgress(kProgress);
                    TextView vProgress = findViewById(R.id.tv_ann_uploaded);
                    vProgress.setText(kProgress + " kB");
                    TextView vTotal = findViewById(R.id.tv_ann_total);
                    vTotal.setText(kTotal + " kB");

                    Log.e("UploaderDebug", "Progress : " + progress + "/" + total);
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    assert downloadUrl != null;
                    notice.setImgUrl(downloadUrl.toString());

                    DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notices").push();
                    notice.setUid(noticeRef.getKey());
                    noticeRef.setValue(notice);

                    Statics.showToast(NewNoticeActivity.this, "Uploaded Notice");
                    finish();
                }
            });
        } else {
            notice.setImgUrl(null);

            DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("notices").push();
            notice.setUid(noticeRef.getKey());
            noticeRef.setValue(notice);
            Statics.showToast(NewNoticeActivity.this, "Uploaded Notice without image.");
            //finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras == null) return;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                vImage.setImageBitmap(imageBitmap);
                currBitmap = imageBitmap;
            }
        }
    }
}