package com.prod.inappreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {

    int girissayisi;
    ReviewInfo reviewInfo;
    ReviewManager reviewManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInAppReviewDeclaration();
    }
    private int getEntrainceInt()
    {
        //giriş sayısını localde kontrol ettiğimiz sınıf, eğer veri yoksa ilk girişte 1 olarak çekiyoruz veriyi
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        girissayisi = preferences.getInt("girisayisi", 1);
        return girissayisi;
    }
    private void setEntrainceInt()
    {
        //giriş sayısını her girişte arttırarak locale yazdığımız sınıf
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("girisayisi",(girissayisi+1));
        editor.commit();
    }
    private Boolean getVoteUs()
    {
        // oylama durumunu localde kontrol ettiğimiz sınıfımız
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getBoolean("oylamadurumu",false);
    }
    private void setVoteUs(Boolean durum)
    {
        // oylama durumunu locale boolean olarak kaydedebildiğimiz sınıfımız
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("oylamadurumu",durum);
        editor.commit();
    }
    private void showVoteUsDialog()
    {
        //kullanıcıya popupta gösterdiğimiz alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Bizi oyla");
        builder.setMessage("Bizi oylayarak destek olabilir, görüşlerinizi bizimle paylaşabilirsiniz. Bunu yapmak için uygulamadan çıkmanıza gerek yok!");
        builder.setCancelable(false);
        builder.setNegativeButton("Daha sonra", null);
        builder.setPositiveButton("Oyla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reviewAPICall();
                //oyla seçeneğini seçerse reviewapicall classını çağırıp kullanıcıya oyla olarak gösterecek
            }
        });
        builder.show();
    }
    private void reviewAPICall()
    {
        //google in-app-review kütüphanesini kullandığımız class
        reviewManager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if(task.isSuccessful()){
                    reviewInfo = task.getResult();
                    reviewManager.launchReviewFlow(MainActivity.this, reviewInfo).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(MainActivity.this, "Oylama başarısız, sonra deneyiniz..", Toast.LENGTH_SHORT).show();
                            setVoteUs(false);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "İnceleme tamamlandı, desteğiniz için teşekkür ederiz..", Toast.LENGTH_SHORT).show();
                            setVoteUs(true);
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Uygulama inceleme isteği gönderilirken sorun oluştu..", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setInAppReviewDeclaration()
    {
        //giriş sayısı 3 ise, oylama yapılmadıysa daha önce ve sdk versionu 21 üstündeyse in app review gösterilir, 21 altındaysa apprate kütüphanesi kullanılır
        getEntrainceInt();
        setEntrainceInt();
        reviewManager = ReviewManagerFactory.create(this);
        if(girissayisi>=3 && !getVoteUs())
        {
            if(Build.VERSION.SDK_INT>=21) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showVoteUsDialog();
                    }
                }, 3000);
            }
            else
            {
                AppRate.with(this)
                        //indirildikten en az kaç gün sonra gösterilsin
                        .setInstallDays(3)
                        //bi gün içinde kaç defa uygulamayı açarsa oy ver sorulsun?
                        .setLaunchTimes(2)
                        //daha sonra hatırlat derse kaç gün sonra hatırlatılsın
                        .setRemindInterval(30)
                        .setCancelable(true)
                        .setTitle("Oy Ver")
                        .setMessage("Uygulamamıza oy ver")
                        .setTextLater("Daha sonra")
                        .setTextNever("Asla")
                        .setTextRateNow("inş başka zaman")
                        .monitor();

                AppRate.showRateDialogIfMeetsConditions(this);
            }
        }
    }

}