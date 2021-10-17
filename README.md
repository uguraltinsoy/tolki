# Tolki
- Tolki Firebase altyapısı kullanılarak yapılan bir sesli sohbet uygulamasıdır
- Kullanılan yardımcı araçlar
	- Firebase Analytics
	- Firebase Auth
	- Firebase Firestore
	- Firebase Storage
	- Firebase Messaging
	- Firebase Ui Firestore
	- [Volley](https://developer.android.com/training/volley)
	- [Circleimageview](https://github.com/hdodenhof/CircleImageView)
	- [Picasso](https://square.github.io/picasso)
- Send Notification Android  Firebase Messaging bildirim göndermeyi desteklemediği için FCMSend.java isimli bir HTTP POST JSON sınıfı oluşturup Volley yardımıyla JSON şeklinde bildirimleri karşı tarafa token yardımı ile gönderiyorum.
- Ses değiştirme işlemini ise "PlaybackParams" sınıfının "setPitch" methodunu kullanarak sağlıyorum
	```Java
	PlaybackParams params = new PlaybackParams();  
	params.setPitch(0.75f);  
	mediaPlayer.setPlaybackParams(params);
	```
## Geliştiren
- [Uğur Altınsoy](https://www.linkedin.com/in/uguraltnsy/)
## APK
- [Tolki İndir](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2Fapp-debug.apk?alt=media&token=5c442ab5-7065-41d7-8955-9e7c3687dbf3)
## Galeri
| Image| Image | Image
|------------|-------------|-------------|
|![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476074.png?alt=media&token=334ea649-73e9-42c1-9398-dc82983d53f4)|![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476221.png?alt=media&token=f6d98698-03ee-4de0-8b30-56945681f4a5) |![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476230.png?alt=media&token=e75e1db1-c38c-487d-ba11-465bb2b72c51) |
|![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476400.png?alt=media&token=5289cdda-4dd1-4475-8b11-be23b565ea5e) |![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476403.png?alt=media&token=a822bacc-02b3-4f24-b60f-7cd29185d521) |![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476407.png?alt=media&token=63c19c1f-96ee-45cb-971b-d8e055bb00bb) |
|![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476412.png?alt=media&token=5f88fc9f-ecb7-4947-b8ba-7e878829febf) |![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476418.png?alt=media&token=eb0449ba-2c92-4eee-85e1-6e341286a65c) |![alt text](https://firebasestorage.googleapis.com/v0/b/tolki-53493.appspot.com/o/Github%2FScreenshot_1634476429.png?alt=media&token=ca8ae4ec-36c9-4578-890e-653b32852f65) |
