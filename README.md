
# NFC Tools TKI

NFC Tools TKI is a tool used to read NFC cards based on PT TKI's rules. Each time an NFC card is tapped, it reads the first 8 characters of the NFC ID.

## How to use


### 1. Add Repositories

Add this following code:

```

maven { url 'https://jitpack.io' }

```

`settings.gradle` Will be looks like this

```
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}

```


### 2. Add the Dependency

Add this dependency

```

  implementation 'com.github.ibnunaufal:nfc-tool-tki:1.7'

```
`app/build.gradle` will be looks like this

```
dependencies {
  implementation 'com.github.ibnunaufal:nfc-tool-tki:1.7'
}
```

### 3. Implement NFC Reading

For every activity that needs to read NFC, inherit from `ClassNfc()`. In the `onNewIntent()` method, call `getValidNfcFromIntent()`, which will return the NFC ID as a string. Here the usage example below:


```
class CardCheckActivity : ClassNfc() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_check)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val nfc = getValidNfcFromIntent(intent)
        val tvNfc = findViewById<TextView>(R.id.nfc)
        tvNfc.text = nfc
    }
}


```

In `CardCheckActivity()`, which inherits from `ClassNfc()`, every time a card is tapped, it will return the NFC ID as a string from `getValidNfcFromIntent()`.


## Authors

- [@ibnunaufal](https://www.github.com/ibnunaufal)
