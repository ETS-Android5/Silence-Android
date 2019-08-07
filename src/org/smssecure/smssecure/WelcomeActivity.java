package org.smssecure.smssecure;

import android.annotation.TargetApi;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.smssecure.smssecure.BaseActionBarActivity;
import org.smssecure.smssecure.R;
import org.smssecure.smssecure.permissions.Permissions;
import org.smssecure.smssecure.util.SilencePreferences;

public class WelcomeActivity extends BaseActionBarActivity {

  private int backgroundColor = 0xFF7568AE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStatusBarColor();
    setContentView(R.layout.welcome_activity);
    findViewById(R.id.welcome_continue_button).setOnClickListener(v -> onContinueClicked());
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Permissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }

  private void onContinueClicked() {
    Permissions.with(this)
        .request(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)
        .ifNecessary()
        .withRationaleDialog(getString(R.string.WelcomeActivity_silence_needs_access_to_your_contacts_media_and_phone),
          R.drawable.ic_contacts_white_36dp, R.drawable.ic_folder_white_36dp, R.drawable.ic_phone_white_36dp)
        .onAnyResult(() -> {
          SilencePreferences.setFirstRun((Context)WelcomeActivity.this);

          Intent nextIntent = getIntent().getParcelableExtra("next_intent");

          if (nextIntent == null) {
            throw new IllegalStateException("Was not supplied a next_intent.");
          }

          startActivity(nextIntent);
          overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out);
          finish();
        })
        .execute();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void setStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(backgroundColor);
    }
  }
}
