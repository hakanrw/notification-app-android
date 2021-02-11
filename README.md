# notification-app-android
## TR
Bildirim uygulaması Android (Client) kodu.
## Geliştirici Ortamını Hazırlama
Android Studio programı kullanarak projeyi açabilirsiniz
## Classlar
| Class | Açıklama |
| --- | --- |
| MainActivity | Uygulama açıldığı zaman çalışan ana aktivite sınıfı |
| LoginActivity | `Giriş yap` veya `Kayıt ol` tuşuna basıldığı zaman açılan aktivite |
| MenuActivity | Giriş yapıldığı zaman açılan aktivite |
| DataStore | Uygulama kapatılsa bile silinmeyen verilerin depolanmasına yardımcı olan sınıf |
| AuthHandler | Giriş yapıldığı zaman `mail` ve `session token`lerinin `DataStore` arayıcılığı ile depolanmasına yarayan sınıf |
| Notification | Sunucu üzerinden çekilen bildirimler JSON türünden bu sınıfa çevirilir |
| NotificationService | Eğer kullanıcı giriş yapmış ise her 5 saniyede bir sunucuya HTTP GET isteği gönderen sınıf. <br /> **NOT**: Background process'te çalışır |
| BootReceiver | Telefon açıldığı zaman `NotificationService` servisini çalıştırmaya yarayan sınıf. |
| PreferenceSingleton | İçine `AppPreference` alarak kalıcı Anahtar-Değer saklamaya yardımcı olan sınıf |
| Utils | Metni x-www-urlencoded haline çevirmek gibi fonksiyonları olan sınıf |
## DataStore
`PreferenceSingleton` classı arayıcılığı ile Anahtar-Değer saklamaya yarayan sınıf. 
Aynı zamanda background process'te çalışan `NotificationService` servisi ile ana process arasında veri transferi yapmak için de kullanılmıştır.  
| Anahtar | Açıklama |
| --- | --- |
| token | Sunucuya gönderilen `GET /notifications/get` istekleri için gerekli olan token. `Cookie: SESSION_M=<token>` şeklinde kullanılır. Giriş yapıldığı zaman sunucu tarafından verilir. |
| mail | Kullanıcının giriş yapmak için kullandığı e-posta adresi. |
| receiveDate | `NotificationService` servisinin sunucuya en son başarılı HTTP isteği attığı tarih. |
| newReceived | `NotificationService` sunucu tarafından yeni bildirim alırsa bu anahtar null olmayan bir değer yapılır, bu sayede eğer kullanıcı `MenuActivity` aktivitesinde ise ekran güncellenmiş olur |
