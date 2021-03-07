# Secure-Development-Mobile-applications
The lab3 pratical course project


The goal of the project is to create the most secure bank application possible.

I gathered information about the security of Android on their developer website and source website : 

https://developer.android.com/

https://source.android.com/


I used Java for this project. A great way to learn how to develop Android applications with Java is through their Codelabs:
https://developer.android.com/courses/fundamentals-training/toc-v2


The requirements for this project are : 

### Requirements
- This application must be available offline.
- A refresh button allows the user to update its accounts.
- Access to the application is restricted 
- Exchanges with API must be secure ( with TLS)


I also have to answer some questions : 

### README.md content

- Explain how you ensure user is the right one starting the app
- How do you securely save user's data on your phone ?
- How did you hide the API url ?
- Screenshots of your application 



The implementation of the project : 

I USED THE API 29 : Android 10.0 (Q) (because of the AndroidX Biometric Library)

TESTED ON A GOOGLE PIXEL 4A ANDROID 11 API 30

To begin with, one of the key element in securing applications is how to hide the API URLs.
You could be paying a private API with an API key, so you don't want to have the API URLs in plain text.
It is not possible to hide it completely because at the end of the day, you have to write the information somewhere.
But one of the best solution today is using Native Development Kit (NDK).
The Android Native Devlopment Kit (NDK) allows you to write code in C/C++, and can be very useful when you’re trying to hide things like API keys.
The good news is that NDK libraries can’t be decompiled making the information harder to find.
The NDK compiled code can still be opened with a hexidecimal editor but by the nature of the API key they are harder to pick out in a hexidecimal editor.


![Template C++](/screenshots/1.png)

I 'hid' the API URLs here.

![API URLs](/screenshots/2.png)


Another attack to discover the API URLs could be through a MITM attack. But I used HTTPS.
About the safety of the implementation of HTTPS : It is secure because inside Android are lists of Certificate Authorities (CA) such as Let's Encrypt,
that are in the phone by default. So it is not possible to send a fake certificate because an error would be thrown because it is not from a known CA.


![Certificate HTTPS](/screenshots/3.png)


Thank's to HTTPS, the URL of the API is hidden. No MITM attack is possible to find the API URLs.
I used HttpsURLConnection.

![HttpsURLConnection](/screenshots/4.png)


I just answered the question on how I hide the API url.

Let's now talk about how I ensured that the user is the right one starting the app.
To do so, I used fingerprint recognition. Thank's to the latests Android versions, the process is really simple.
We no longer need to create our own authentication UI. The library provides a standard and familiar UI that matches a user’s biometric authentication form factor.
I used the library "androidx.biometric:biometric:1.1.0" and especially the object BiometricPrompt. 

![login page](/screenshots/screen1.png)

![authenticated](/screenshots/screen2.png)


I cannot take a screenshot while the BiometricPrompt is launched.


Another key element of the app is how to securely store data to be able to access the app without Internet.
I used SharedPreferences because this is what you should use when you have key-value data.
The probleme is that it is not encrypted. 

![EncryptedSharedPreferences](/screenshots/5.png)


But an implementation of SharedPreferences that encrypts keys and values exists : EncryptedSharedPreferences.
It uses the Android Keystore which is a system that lets you store cryptographic keys in a container to make it more difficult to extract from the device.

![EncryptedSharedPreferences](/screenshots/6.png)


After that, the SharedPreferences file is encrypted and you cannot read from it unless you are in the app.

![Encrypted file](/screenshots/7.png)


I also obfuscated my code with gradle and the R8 compiler. 
The R8 compiler does : 

-Code shrinking

-Resource shrinking

-Obfuscation

-Optimization

![R8 compiler](/screenshots/8.png)


To finish, I submitted to apk in my Github. One which is the debug apk and the release apk which is more secure because it is signed.
Because when you are generating signed APK files, it is secured by a Keystore credential made by the developer and includes a password for the security purpose.
Therefore, Signed APK cannot be easily unzipped and mainly used for production purposes.

![Key store](/screenshots/9.png)

![Release APK](/screenshots/10.png)


Apart from the security issues, I had the opportunity through this project to improve enormously on my abilities to develop. 
I had to use the AsyncTask and understand the notions around threads to make requests to the API,
I also implemented a spinner with a listener to facilitate the choice of users.
I also had to implement a scroll view to display all the different bank accounts of each person.

![Home page](/screenshots/screen3.png)

![Home page updated](/screenshots/screen4.png)



To use the app, you can imagine that you are a banker and you have all the accounts of your clients. 
That is why ,once you are authenticated, you can select multiple users.


To sum up :


### Requirements
- This application must be available offline.
Yes it is, thanks to EncryptedSharedPreferences.

- A refresh button allows the user to update its accounts.
Yes, it refreshes all the users and their accounts and put the data in the EncryptedSharedPreferences file.

- Access to the application is restricted 
Yes, through the fingerprint recognition. Only the user of the phone can use the app.

- Exchanges with API must be secure ( with TLS)
Yes, I used HttpsURLConnection and the certificate is from a known CA.


### README.md content

- Explain how you ensure user is the right one starting the app
Through the fingerprint recognition.

- How do you securely save user's data on your phone ?
With the EncryptedSharedPreferences file.

- How did you hide the API url ?
I used NDK and put the URLs on a cpp file.


Thank you for this project, it was interesting !
