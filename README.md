# Android-In-App-Review
Basic android in app review example

Nasıl Entegre Ederiz
build.gradle(app düzeyi) Impelement
implementation 'com.google.android.play:core:1.8.3'
Bu noktadan sonra biraz süslememiz gerekecek. Google kullanıcı deneyimini önemsediği için giriş sayıları vb dataları baz alan bir yapı oluşturdum ben. Bu localde tuttuğum datalara göre dialog gösteriyorum.
Tanımlamalar
int girissayisi;
ReviewInfo reviewInfo;
ReviewManager reviewManager;
Giriş Sayısını Locale Yazan Classlar
{
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
}