## Verwendung

Für die Verwendung der Library muss im ersten Schritt ein Intent erzeugt werden, welcher den Klassennamen ```VoucherActivity``` enthält. 
Anschließend kann die Library mit dem Befehl ```startIntent``` gestartet werden.

```android
	Intent intent = new Intent(this, VoucherActivity.class);
	startActivity(intent);
```

## Konfiguration

Die Konfiguration erfolgt über die Konstantenklasse ```Config```, welche die konfigurierbaren Parameter enthält.
Um Zugriff auf diese zu haben, muss die Klasse importiert werden.

```android
	import static de.cmc.android.config.Config.*;
```


Für die Nutzung der Library, sind der API-Key und das dazugehörige Passwort notwendig.
Diese zwei Werte werden als Extras in dem zuvor erzeugten Intent an die Library übergeben.

Konstantentnamen der Extras:
* API_KEY
* API_SECRET

```android
		intent.putExtra(API_KEY, "android-sdk-123456789");
		intent.putExtra(API_SECRET, "123456789");
```

## Metadaten

Die Metadaten sind optional und werden ebenfalls als Extras an den Intent übergeben.
Falls in den Metadaten keine E-Mail-Adresse hinterlegt wird, so muss diese vom Nutzer für den Gutschein selber eingegeben werden.

Konstantentnamen der Extras:
* EMAIL
* ZIP_CODE
* FIRST_NAME
* LAST_NAME
* GENDER
* BIRTHDAY

```android
  intent.putExtra(EMAIL, "mustermann@gmx.de");
		intent.putExtra(ZIP_CODE, "12489");
		intent.putExtra(FIRST_NAME, "Max");
		intent.putExtra(LAST_NAME, "Mustermann");
		intent.putExtra(GENDER, Metadata.Gender.Male);
		intent.putExtra(BIRTHDAY, "01.01.1985");
```

Hinweis: Für das Geschlecht wir das Enum ```Gender``` aus der Klasse ```Metadata``` verwendet. Hier kann entweder der Typ **Male** oder **Female** 
ausgewählt und als Extra mitgegeben werden.

## Entwicklung

Für die Entwicklung muss in Android Studio die BuildVariant von auf dev gestellt werden. Damit die Application im Emulator gestartet werden kann muss in der build.gradle `apply plugin: 'com.android.library'` durch '`apply plugin: 'com.android.application'` ersetzt werden. Sobald die Entwicklung abgeschlossen ist muss diese Änderung wieder Rückgänig gemacht werden!

## Author

NetScaleNow, support@netscalenow.com

## License

NetScaleNow is available under the MIT license. See the LICENSE file for more info.