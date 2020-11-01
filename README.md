# Android-In-App-Review
Basic android in app review example

#Nasıl Entegre Ederiz
#### build.gradle(app düzeyi) Impelement
``` implementation 'com.google.android.play:core:1.8.3' ```
<br><br>
Bu noktadan sonra biraz süslememiz gerekecek. Google kullanıcı deneyimini önemsediği için giriş sayıları vb dataları baz alan bir yapı oluşturdum ben. Bu localde tuttuğum datalara göre dialog gösteriyorum.

#### Tanımlamalar
```
int girissayisi;
ReviewInfo reviewInfo;
ReviewManager reviewManager;
```

#### Giriş Sayısını Locale Yazan Classlar
```
private int getEntrainceInt()
{
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    girissayisi = preferences.getInt("girisayisi", 1);
    return girissayisi;
}
private void setEntrainceInt()
{
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt("girisayisi",(girissayisi+1));
    editor.commit();
}
```

### Oylamayı Check Eden Classlar
```
private Boolean getVoteUs()
{
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    return preferences.getBoolean("oylamadurumu",false);
}
private void setVoteUs(Boolean durum)
{

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean("oylamadurumu",durum);
    editor.commit();
}
```
### Kullanıcıya Göstereceğimiz Dialog Classımız
```
private void showVoteUsDialog()
{
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Bizi oyla");
    builder.setMessage("Bizi oylayarak destek olabilir, görüşlerinizi bizimle paylaşabilirsiniz. Bunu yapmak için uygulamadan çıkmanıza gerek yok!");
    builder.setCancelable(false);
    builder.setNegativeButton("Daha sonra", null);
    builder.setPositiveButton("Oyla", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            reviewAPICall();
        }
    });
    builder.show();
}
```
### API İsteği Yapan Classımız
```
private void reviewAPICall()
{
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
```

#### Giriş sayısını, sdk versiyonunu ve oylama durumunu kontrol ettiğimiz classımız
```
private void setInAppReviewDeclaration()
{
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
         //3 saniye sonra gösteriyoruz
        }
        else
        {
            //kullandığınız bizi yorumlayın kütüphanesini buraya yazabilirsiniz.
            //Ben kullanmamayı tercih ettiğim için boş bırakıyorum.
        }
    }
}
```
#### En son OnCreate’de Topluyoruz
```
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setInAppReviewDeclaration();
}
```

#### Sonuç
<img src="https://miro.medium.com/max/700/1*WuCl_lf6EUQjVGfwsMtNGA.png">